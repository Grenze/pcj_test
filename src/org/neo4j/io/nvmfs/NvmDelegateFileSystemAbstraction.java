package org.neo4j.io.nvmfs;


import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class NvmDelegateFileSystemAbstraction implements NvmFileSystemAbstraction{
    private final FileSystem fs;

    public NvmDelegateFileSystemAbstraction( FileSystem fs )
    {
        this.fs = fs;
    }

    @Override
    public NvmStoreChannel open(File fileName, String mode ) throws IOException
    {
        return new NvmStoreFileChannel( NvmFileUtils.open( path( fileName ), mode ) );
    }

    private Path path(File fileName )
    {
        return path( fileName.getPath() );
    }

    private Path path( String fileName )
    {
        return fs.getPath( fileName );
    }

    @Override
    public OutputStream openAsOutputStream( File fileName, boolean append ) throws IOException
    {
        return NvmFileUtils.openAsOutputStream( path( fileName ), append );
    }

    @Override
    public InputStream openAsInputStream( File fileName ) throws IOException
    {
        return NvmFileUtils.openAsInputStream( path( fileName ) );
    }

    @Override
    public Reader openAsReader( File fileName, Charset charset ) throws IOException
    {
        return new InputStreamReader( openAsInputStream( fileName ), charset );
    }

    @Override
    public Writer openAsWriter( File fileName, Charset charset, boolean append ) throws IOException
    {
        return new OutputStreamWriter( openAsOutputStream( fileName, append ), charset );
    }

    @Override
    public NvmStoreChannel create( File fileName ) throws IOException
    {
        return open( fileName, "rw" );
    }

    @Override
    public boolean fileExists( File fileName )
    {
        return Files.exists( path( fileName ) );
    }

    @Override
    public boolean mkdir( File fileName )
    {
        if ( !fileExists( fileName ) )
        {
            try
            {
                Files.createDirectory( path( fileName ) );
                return true;
            }
            catch ( IOException ignore )
            {
            }
        }
        return false;
    }

    @Override
    public void mkdirs( File fileName ) throws IOException
    {
        Files.createDirectories( path( fileName ) );
    }

    @Override
    public long getFileSize( File fileName )
    {
        try
        {
            return Files.size( path( fileName ) );
        }
        catch ( IOException e )
        {
            return 0;
        }
    }

    @Override
    public boolean deleteFile( File fileName )
    {
        try
        {
            Files.delete( path( fileName ) );
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    @Override
    public void deleteRecursively( File directory ) throws IOException
    {
        if ( fileExists( directory ) )
        {
            NvmFileUtils.deletePathRecursively( path( directory ) );
        }
    }

    @Override
    public boolean renameFile( File from, File to ) throws IOException
    {
        Files.move( path( from ), path( to ) );
        return true;
    }

    @Override
    public File[] listFiles( File directory )
    {
        try ( Stream<Path> listing = Files.list( path( directory ) ) )
        {
            return listing.map( Path::toFile ).toArray( File[]::new );
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    @Override
    public File[] listFiles( File directory, final FilenameFilter filter )
    {
        try ( Stream<Path> listing = Files.list( path( directory ) ) )
        {
            return listing
                    .filter( entry -> filter.accept( entry.getParent().toFile(), entry.getFileName().toString() ) )
                    .map( Path::toFile )
                    .toArray( File[]::new );
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    @Override
    public boolean isDirectory( File file )
    {
        return Files.isDirectory( path( file ) );
    }

    @Override
    public void moveToDirectory( File file, File toDirectory ) throws IOException
    {
        Files.move( path( file ), path( toDirectory ).resolve( path( file.getName() ) ) );
    }

    @Override
    public void copyFile( File from, File to ) throws IOException
    {
        Files.copy( path( from ), path( to ) );
    }

    @Override
    public void copyRecursively( File fromDirectory, File toDirectory ) throws IOException
    {
        Path target = path( toDirectory );
        Path source = path( fromDirectory );
        copyRecursively( source, target );
    }

    private void copyRecursively( Path source, Path target ) throws IOException
    {
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream( source ) )
        {
            for ( Path sourcePath : directoryStream )
            {
                Path targetPath = target.resolve( sourcePath.getFileName() );
                if ( Files.isDirectory( sourcePath ) )
                {
                    Files.createDirectories( targetPath );
                    copyRecursively( sourcePath, targetPath );
                }
                else
                {
                    Files.copy( sourcePath, targetPath,
                            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES );
                }
            }
        }
    }

    private final Map<Class<?>,Object> thirdPartyFs = new HashMap<>();

    @Override
    public synchronized <K extends ThirdPartyFileSystem> K getOrCreateThirdPartyFileSystem(
            Class<K> clazz, Function<Class<K>,K> creator )
    {
        // what in the ever-loving mother of the lake is this!?
        K otherFs = (K) thirdPartyFs.get( clazz );
        if ( otherFs == null )
        {
            otherFs = creator.apply( clazz );
            thirdPartyFs.put( clazz, otherFs );
        }
        return otherFs;
    }

    @Override
    public void truncate( File path, long size ) throws IOException
    {
        try ( FileChannel channel = FileChannel.open( path( path ) ) )
        {
            channel.truncate( size );
        }
    }
}
