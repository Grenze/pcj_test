/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.io.fs;

import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardOpenOption.*;

public class FileUtils
{
    private static final int WINDOWS_RETRY_COUNT = 5;

    //delete the directory(file)'s content including itself
    public static void deleteRecursively( File directory ) throws IOException
    {
        //exist check
        if ( ! directory.exists() )
        {
            return;
        }
        Path path = directory.toPath();
        deletePathRecursively( path );
    }

    //delete the path(file)'s content including itself
    public static void deletePathRecursively( Path path ) throws IOException
    {
        //the path should be valid or will throw IOException
        Files.walkFileTree( path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
            {
                deleteFileWithRetries( file, 0 );
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory( Path dir, IOException e ) throws IOException
            {
                if ( e != null )
                {
                    throw e;
                }
                Files.delete( dir );
                return FileVisitResult.CONTINUE;
            }
        } );
    }

    //can delete a single file or an empty directory(return true)
    public static boolean deleteFile( File file )
    {
        if ( !file.exists() )
        {
            return true;
        }
        int count = 0;
        boolean deleted;
        do
        {
            deleted = file.delete();
            if ( !deleted )
            {
                count++;
                waitAndThenTriggerGC();
            }
        }
        while ( !deleted && count <= WINDOWS_RETRY_COUNT );
        return deleted;
    }

    /**
     * Utility method that moves a file from its current location to the
     * new target location. If rename fails (for example if the target is
     * another disk) a copy/delete will be performed instead. This is not a rename,
     * use {@link #renameFile(File, File)} instead.
     *
     * @param toMove The File object to move.
     * @param target Target file to move to.
     * @throws IOException
     */
    public static void moveFile( File toMove, File target ) throws IOException
    {
        /*Source file or directory should exist*/
        if ( !toMove.exists() )
        {
            throw new FileNotFoundException( "Source file[" + toMove.getAbsolutePath()
                    + "] not found" );
        }
        /*Target file or directory should not exist*/
        if ( target.exists() )
        {
            throw new IOException( "Target file[" + target.getAbsolutePath()
                    + "] already exists" );
        }
        /*rename the final file(directory) name with the existed path prefix
        * example: file/file1 to file/file5/0 and file5 already existed
        * if there is no file5, toMove.isDirectory if dir, copy and delete if file
        * */
        if ( toMove.renameTo( target ) )
        {
            System.out.println("renameTo");
            return;
        }

        if ( toMove.isDirectory() )
        {
            System.out.println("isDirectory");
            Files.createDirectories( target.toPath() );
            copyRecursively( toMove, target );
            deleteRecursively( toMove );
        }
        else
        {
            System.out.println("copy delete");
            copyFile( toMove, target );
            deleteFile( toMove );
        }
    }

    /**
     * Utility method that moves a file from its current location to the
     * provided target directory. If rename fails (for example if the target is
     * another disk) a copy/delete will be performed instead. This is not a rename,
     * use {@link #renameFile(File, File)} instead.
     *
     * @param toMove The File object to move.
     * @param targetDirectory the destination directory
     * @return the new file, null iff the move was unsuccessful
     * @throws IOException
     */
    /*specified moveFile, targetDirectory must exist, so renameTo only, keep its origin name*/
    public static File moveFileToDirectory( File toMove, File targetDirectory ) throws IOException
    {
        if ( !targetDirectory.isDirectory() )
        {
            throw new IllegalArgumentException(
                    "Move target must be a directory, not " + targetDirectory );
        }

        File target = new File( targetDirectory, toMove.getName() );
        moveFile( toMove, target );
        return target;
    }
    /*bound to call renameTo, but possible locked file, return true when succeeded*/
    public static boolean renameFile( File srcFile, File renameToFile ) throws IOException
    {
        if ( !srcFile.exists() )
        {
            throw new FileNotFoundException( "Source file[" + srcFile.getName() + "] not found" );
        }
        if ( renameToFile.exists() )
        {
            throw new FileNotFoundException( "Target file[" + renameToFile.getName() + "] already exists" );
        }
        if ( !renameToFile.getParentFile().isDirectory() )
        {
            throw new FileNotFoundException( "Target directory[" + renameToFile.getParent() + "] does not exists" );
        }
        int count = 0;
        boolean renamed;
        do
        {
            renamed = srcFile.renameTo( renameToFile );
            if ( !renamed )
            {
                count++;
                waitAndThenTriggerGC();
            }
        }
        while ( !renamed && count <= WINDOWS_RETRY_COUNT );
        return renamed;
    }
    /*realised by nvm file channel*/
    public static void truncateFile( SeekableByteChannel fileChannel, long position )
            throws IOException
    {
        int count = 0;
        boolean success = false;
        IOException cause = null;
        do
        {
            count++;
            try
            {
                fileChannel.truncate( position );
                success = true;
            }
            catch ( IOException e )
            {
                cause = e;
            }

        }
        while ( !success && count <= WINDOWS_RETRY_COUNT );
        if ( !success )
        {
            throw cause;
        }
    }
    /*realised by nvm FileChannel*/
    public static void truncateFile( File file, long position ) throws IOException
    {
        try ( RandomAccessFile access = new RandomAccessFile( file, "rw" ) )
        {
            truncateFile( access.getChannel(), position );
        }
    }

    /*
     * See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154.
     * JDK-4715154 : (fs) Cannot delete file if memory mapped with FileChannel.map (windows)
     * Ubuntu has no such a problem, a mapped file can change its name concurrently
     */
    private static void waitAndThenTriggerGC()
    {
        try
        {
            Thread.sleep( 500 );
        }
        catch ( InterruptedException ee )
        {
            Thread.interrupted();
        } // ok
        System.gc();
    }

    /*
     * fixSeparatorsInPath for Settings.java --remained
     */
    public static String fixSeparatorsInPath( String path )
    {
        String fileSeparator = System.getProperty( "file.separator" );
        if ( "\\".equals( fileSeparator ) )
        {
            path = path.replace( '/', '\\' );
        }
        else if ( "/".equals( fileSeparator ) )
        {
            path = path.replace( '\\', '/' );
        }
        return path;
    }
    /*copy srcFile to the path of dstFile.getParentFile(), name changed*/
    public static void copyFile( File srcFile, File dstFile ) throws IOException
    {
        //noinspection ResultOfMethodCallIgnored
        dstFile.getParentFile().mkdirs();
        try ( FileInputStream input = new FileInputStream( srcFile );
              FileOutputStream output = new FileOutputStream( dstFile ))
        {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ( (bytesRead = input.read( buffer )) != -1 )
            {
                output.write( buffer, 0, bytesRead );
            }
        }
        catch ( IOException e )
        {
            // Because the message from this cause may not mention which file it's about
            throw new IOException( "Could not copy '" + srcFile + "' to '" + dstFile + "'", e );
        }
    }
    /*keep its origin name, so there should not be the same name file(Dir) under the toDirectory, or throw IOException*/
    public static void copyRecursively( File fromDirectory, File toDirectory ) throws IOException
    {
        copyRecursively( fromDirectory, toDirectory, null );
    }
    /*design a filter for nvm FileFilter, read the listFiles source code*/
    public static void copyRecursively( File fromDirectory, File toDirectory, FileFilter filter) throws IOException
    {
        for ( File fromFile : fromDirectory.listFiles( filter ) )
        {
            File toFile = new File( toDirectory, fromFile.getName() );
            if ( fromFile.isDirectory() )
            {
                System.out.print("CopyRecursively");
                Files.createDirectories( toFile.toPath() );
                copyRecursively( fromFile, toFile, filter );
            }
            else
            {
                copyFile( fromFile, toFile );
            }
        }
    }
    /*override or append, create if not exist, createNewFile need its ParentFile exist*/
    public static void writeToFile( File target, String text, boolean append ) throws IOException
    {
        if ( !target.exists() )
        {
            Files.createDirectories( target.getParentFile().toPath() );
            //noinspection ResultOfMethodCallIgnored
            target.createNewFile();
        }

        try ( Writer out = new OutputStreamWriter( new FileOutputStream( target, append ), StandardCharsets.UTF_8 ) )
        {
            out.write( text );
        }
    }
    /*only used in StartClient.java to call readLine() to read command from file,
    * so change this method means change executeCommandStream() in StartClient,
    * readLine() return the content of a line without "\n"
    **/
    public static BufferedReader newBufferedFileReader( File file, Charset charset ) throws FileNotFoundException
    {
        return new BufferedReader( new InputStreamReader( new FileInputStream( file ), charset) );
    }
    /*only used in AnnotationProcessor.java to call append() to write Strings,
    * do not forget flush it to the disk
    **/
    public static PrintWriter newFilePrintWriter( File file, Charset charset ) throws FileNotFoundException
    {
        return new PrintWriter( new OutputStreamWriter( new FileOutputStream( file, true ), charset) );
    }
    /*to make dirs or createNewFile*/
    public static File path( String root, String... path )
    {
        return path( new File( root ), path );
    }
    /*just different root type*/
    public static File path( File root, String... path )
    {
        for ( String part : path )
        {
            root = new File( root, part );
        }
        return root;
    }
    /*let it go*/
    public interface FileOperation
    {
        void perform() throws IOException;
    }
    /*let it go*/
    public static void windowsSafeIOOperation( FileOperation operation ) throws IOException
    {
        IOException storedIoe = null;
        for ( int i = 0; i < 10; i++ )
        {
            try
            {
                operation.perform();
                return;
            }
            catch ( IOException e )
            {
                storedIoe = e;
                System.gc();
            }
        }
        throw storedIoe;
    }

    public interface LineListener
    {
        void line( String line );
    }
    /*see method beneath*/
    public static LineListener echo( final PrintStream target )
    {
        return new LineListener()
        {
            @Override
            public void line( String line )
            {
                target.println( line );
            }
        };
    }
    /*see method beneath*/
    public static void readTextFile( File file, LineListener listener ) throws IOException
    {
        try(BufferedReader reader = new BufferedReader( new FileReader( file ) ))
        {
            String line;
            while ( (line = reader.readLine()) != null )
            {
                listener.line( line );
            }
        }
    }
    /*used in HTTPLoggingIT.java and Fixtures.java, return with the content of file extend with a "\n"*/
    public static String readTextFile( File file, Charset charset ) throws IOException
    {
        StringBuilder out = new StringBuilder();
        for ( String s : Files.readAllLines( file.toPath(), charset ) )
        {
            out.append( s ).append( "\n" );
        }
        return out.toString();
    }
    /*Private method, lock is provided by pcj and pmdk*/
    private static void deleteFileWithRetries( Path file, int tries ) throws IOException
    {
        try
        {
            Files.delete( file );
        }
        catch ( IOException e )
        {
            if ( SystemUtils.IS_OS_WINDOWS && mayBeWindowsMemoryMappedFileReleaseProblem( e ) )
            {
                if ( tries >= WINDOWS_RETRY_COUNT )
                {
                    throw new MaybeWindowsMemoryMappedFileReleaseProblem(e);
                }
                waitAndThenTriggerGC();
                deleteFileWithRetries( file, tries + 1 );
            }
            else
            {
                throw e;
            }
        }
    }
    /*let it go*/
    private static boolean mayBeWindowsMemoryMappedFileReleaseProblem( IOException e )
    {
        return e.getMessage().contains( "The process cannot access the file because it is being used by another process." );
    }
    /*let it go*/
    public static class MaybeWindowsMemoryMappedFileReleaseProblem extends IOException
    {
        public MaybeWindowsMemoryMappedFileReleaseProblem( IOException e )
        {
            super(e);
        }
    }

    /**
    * Given a directory and a path under it, return filename of the path
    * relative to the directory.
    *
    * @param baseDir The base directory, containing the storeFile
    * @param storeFile The store file path, must be contained under
    * <code>baseDir</code>
    * @return The relative path of <code>storeFile</code> to
    * <code>baseDir</code>
    * @throws IOException As per {@link File#getCanonicalPath()}
    */
    /*example: Dir_test, Dir_test/File/F, File/F returned*/
    public static String relativePath( File baseDir, File storeFile )
            throws IOException
    {
        String prefix = baseDir.getCanonicalPath();
        String path = storeFile.getCanonicalPath();
        if ( !path.startsWith( prefix ) )
        {
            throw new FileNotFoundException();
        }
        path = path.substring( prefix.length() );
        if ( path.startsWith( File.separator ) )
        {
            return path.substring( 1 );
        }
        return path;
    }
    /*only used in StoreCopyServer.java, just let it go*/
    // TODO javadoc what this one does. It comes from Serverutil initially.
    public static File getMostCanonicalFile( File file )
    {
        try
        {
            return file.getCanonicalFile().getAbsoluteFile();
        }
        catch ( IOException e )
        {
            return file.getAbsoluteFile();
        }
    }
    /*reuse*/
    public static void writeAll( FileChannel channel, ByteBuffer src, long position ) throws IOException
    {
        new StoreFileChannel(channel).writeAll(src, position);
        /*long filePosition = position;
        long expectedEndPosition = filePosition + src.limit() - src.position();
        int bytesWritten;
        while((filePosition += (bytesWritten = channel.write( src, filePosition ))) < expectedEndPosition)
        {
            if( bytesWritten <= 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }*/
    }
    /*reuse*/
    public static void writeAll( FileChannel channel, ByteBuffer src ) throws IOException
    {
        new StoreFileChannel(channel).writeAll(src);
        /*long bytesToWrite = src.limit() - src.position();
        int bytesWritten;
        while((bytesToWrite -= (bytesWritten = channel.write( src ))) > 0)
        {
            if( bytesWritten <= 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }*/
    }
    /*let it go*/
    public static OpenOption[] convertOpenMode( String mode )
    {
        OpenOption[] options;
        switch ( mode )
        {
        case "r": options = new OpenOption[]{READ}; break;
        case "rw": options = new OpenOption[] {CREATE, READ, WRITE}; break;
        case "rws": options = new OpenOption[] {CREATE, READ, WRITE, SYNC}; break;
        case "rwd": options = new OpenOption[] {CREATE, READ, WRITE, DSYNC}; break;
        default: throw new IllegalArgumentException( "Unsupported mode: " + mode );
        }
        return options;
    }

    /*only used in DelegateFileSystemAbstraction.java, supported by nvm FileChannel*/
    public static FileChannel open( Path path, String mode ) throws IOException
    {
        return FileChannel.open( path, convertOpenMode( mode ) );
    }
    /*final used in BufferedReader.readLine()*/
    public static InputStream openAsInputStream( Path path ) throws IOException
    {
        return Files.newInputStream( path, READ );
    }

    /**
     * Check if directory is empty.
     * @param directory - directory to check
     * @return false if directory exists and not empty, true otherwise.
     * @throws IllegalArgumentException if specified directory represent a file
     * @throws IOException if some problem encountered during reading directory content
     */
    public static boolean isEmptyDirectory( File directory ) throws IOException
    {
        if ( directory.exists() )
        {
            if ( !directory.isDirectory() )
            {
                throw new IllegalArgumentException( "Expected directory, but was file: " + directory );
            }
            else
            {
                try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream( directory.toPath() ) )
                {
                    return !directoryStream.iterator().hasNext();
                }
            }
        }
        return true;
    }

    public static OutputStream openAsOutputStream( Path path, boolean append ) throws IOException
    {
        OpenOption[] options;
        if ( append )
        {
            options = new OpenOption[] {CREATE, WRITE, APPEND};
        }
        else
        {
            options = new OpenOption[] {CREATE, WRITE};
        }
        return Files.newOutputStream( path, options );
    }
}
