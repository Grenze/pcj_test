package org.neo4j.io.nvmfs;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public class NvmFileUtils {

    //parent itself child
    private static void deleteNvmFilDir(File file) throws IOException {
        if ( !NvmFilDir.exists(file) )
        {
            return;
        }
        File parentDirectory = file.getParentFile();
        NvmFilDir.getNvmFilDir(parentDirectory).decreaseLocalIndex(file);

        NvmFilDir.removeNvmFilDir(file);

        for(String key: NvmFilDir.getNvmFilDirDirectory()){
            if(key.startsWith(file.getCanonicalPath())){
                NvmFilDir.removeNvmFilDir(new File(key));
            }
        }

    }

    //delete the directory(file)'s content including itself
    public static void deleteRecursively( File directory ) throws IOException{
        deleteNvmFilDir( directory );
    }

    //delete the path(file)'s content including itself
    public static void deletePathRecursively( Path path ) throws IOException{
        deleteNvmFilDir(path.toFile());
    }

    //only able to delete file or empty directory
    public static boolean deleteFile(File file) throws  IOException{
        if ( !NvmFilDir.exists(file) )
        {
            return true;
        }
        if( NvmFilDir.isEmpty(file)){
            deleteNvmFilDir(file);
            return true;
        }
        else{
            return false;
        }
    }

    public static void moveFile( File toMove, File target ) throws IOException {
        /*Source file or directory should exist*/
        if (!NvmFilDir.exists(toMove)) {
            throw new FileNotFoundException("Source file[" + toMove.getAbsolutePath()
                    + "] not found");
        }
        /*Target file or directory should not exist*/
        if (NvmFilDir.exists(target)) {
            throw new IOException("Target file[" + target.getAbsolutePath()
                    + "] already exists");
        }
        renameNvmFilDir(toMove, target);
    }

    //parent itself child
    private static void renameNvmFilDir(File src, File dst) throws IOException {
        NvmFilDir.getNvmFilDir(src.getParentFile()).decreaseLocalIndex(src);
        nvmMkDirs(dst.getParentFile(), false, true);
        NvmFilDir.getNvmFilDir(dst.getParentFile()).increaseLocalIndex(src);

        NvmFilDir srcFilDir = NvmFilDir.getNvmFilDir(src);
        srcFilDir.renameNvmFilDir(src, dst);//index changed from src to dst, inner globalId changed too

        if(NvmFilDir.isFile(dst) || NvmFilDir.isEmpty(dst)){
            return;
        }
        for(String key: NvmFilDir.getNvmFilDirDirectory()){
            if(key.startsWith(src.getCanonicalPath())){
                NvmFilDir subFilDir = NvmFilDir.removeNvmFilDir(new File(key));
                subFilDir.renameNvmFilDir(new File(key), new File(dst, key.substring(src.getCanonicalPath().length())));
            }
        }
    }

    //mk current layer then make or prove higher and finally connect them
    private static void nvmMkDirs(File file, boolean isFile, boolean isDirectory) throws IOException {
        if(!nvmMkFilDir(file, isFile, isDirectory)){
            return;
        }
        File parentFile = file.getParentFile();
        if(NvmFilDir.exists(parentFile)){
            nvmMkDirs(parentFile, false, true);
        }
        NvmFilDir.getNvmFilDir(parentFile).increaseLocalIndex(file);
    }

    //if already exists, MkFilDir failed
    private static boolean nvmMkFilDir(File file, boolean isFile, boolean isDirectory) throws IOException{
        if(NvmFilDir.exists(file)){
            return false;
        }
        NvmFilDir nfd = new NvmFilDir(file.getCanonicalPath(), isFile, isDirectory);
        nfd.force(true);
        return true;
    }

    /*specified moveFile, targetDirectory must exist, so renameTo only, keep its origin name*/
    public static File moveFileToDirectory( File toMove, File targetDirectory ) throws IOException {
        if (!NvmFilDir.isDirectory(targetDirectory)) {
            throw new IllegalArgumentException(
                    "Move target must be a directory, not " + targetDirectory);
        }
        File target = new File( targetDirectory, toMove.getName() );
        moveFile(toMove, target);
        return target;
    }

    /*bound to call renameTo, but possible locked file, return true when succeed*/
    public static boolean renameFile( File srcFile, File renameToFile ) throws IOException {
        if (!NvmFilDir.exists(srcFile)) {
            throw new FileNotFoundException("Source file[" + srcFile.getName() + "] not found");
        }
        if (NvmFilDir.exists(renameToFile)) {
            throw new FileNotFoundException("Target file[" + renameToFile.getName() + "] already exists");
        }
        if (!NvmFilDir.isDirectory(renameToFile.getParentFile())) {
            throw new FileNotFoundException("Target directory[" + renameToFile.getParent() + "] does not exists");
        }
        renameNvmFilDir(srcFile, renameToFile);
        return true;
    }

    /*realised by NvmStoreFileChannel*/
    public static void truncateFile( NvmStoreFileChannel fileChannel, long position ) {
        fileChannel.truncate(position);
    }
    /*realised by NvmStoreFileChannel*/
    public static void truncateFile( File file, long position ) throws IOException
    {
         NvmStoreFileChannel access = new NvmStoreFileChannel(NvmFilDir.getNvmFilDir(file));
         truncateFile( access, position );
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

    private static void copyNvmFilDir(File fromDirectory, File toDirectory, FileFilter filter) throws IOException{
        nvmMkDirs(toDirectory, false, true);
        NvmFilDir.getNvmFilDir(toDirectory).increaseLocalIndex(fromDirectory);

        NvmFilDir.copyNvmFilDir(fromDirectory, new File(toDirectory, fromDirectory.getName()));

        if(NvmFilDir.isFile(fromDirectory) || NvmFilDir.isEmpty(fromDirectory)){
            return;
        }

        for(String key: NvmFilDir.getNvmFilDirDirectory()){
            if(key.startsWith(fromDirectory.getCanonicalPath()) && filter.accept(new File(key))){
                NvmFilDir.copyNvmFilDir(new File(key), new File(toDirectory, key.substring(fromDirectory.getParentFile().getCanonicalPath().length())) );
            }
        }
    }

    /*copy srcFile to the path of dstFile.getParentFile(), name changed*/
    public static void copyFile( File srcFile, File dstFile ) throws IOException{
        if(NvmFilDir.exists(dstFile)){
            return;
        }
        nvmMkDirs(dstFile.getParentFile(), false, true);
        NvmFilDir.getNvmFilDir(dstFile.getParentFile()).increaseLocalIndex(dstFile);

        NvmFilDir.copyNvmFilDir(srcFile, dstFile);
    }

    /*keep its origin name, so there should not be the same name file(Dir) under the toDirectory*/
    public static void copyRecursively( File fromDirectory, File toDirectory ) throws IOException
    {
        copyRecursively( fromDirectory, toDirectory, null );
    }

    public static void copyRecursively( File fromDirectory, File toDirectory, FileFilter filter) throws IOException
    {
        if(NvmFilDir.exists(toDirectory)){
            return;
        }
        copyNvmFilDir(fromDirectory, toDirectory, filter);
    }

    /*override or append, create if not exist, createNewFile need its ParentFile exist*/
    public static void writeToFile( File target, String text, boolean append ) throws IOException
    {
        if(!NvmFilDir.exists(target)){
            nvmMkDirs(target.getParentFile(), false, true);
            NvmFilDir.putNvmFilDir(target, new NvmFilDir(target.getCanonicalPath(), true, false));
        }
        if(NvmFilDir.isFile(target)){
            NvmFilDir.getNvmFilDir(target).write(text, append);
        }
    }

    //backward: read all of the file's content
    public static BufferedReader newBufferedFileReader(File file, Charset charset ) throws IOException
    {
        return new BufferedReader( new InputStreamReader( new ByteArrayInputStream(NvmFilDir.getNvmFilDir(file).readAll().getBytes(charset)), charset) );
    }

    /*public static PrintWriter newFilePrintWriter( File file, Charset charset ) throws FileNotFoundException{
        return null;
    }

    * replaced by writeToFile(file, text, true)
    */

    /*below method keep origin*/
    public static File path( String root, String... path )
    {
        return path( new File( root ), path );
    }

    public static File path( File root, String... path )
    {
        for ( String part : path )
        {
            root = new File( root, part );
        }
        return root;
    }

    public interface FileOperation
    {
        void perform() throws IOException;
    }

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

    public static LineListener echo(final PrintStream target )
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

    private static boolean mayBeWindowsMemoryMappedFileReleaseProblem( IOException e )
    {
        return e.getMessage().contains( "The process cannot access the file because it is being used by another process." );
    }

    public static class MaybeWindowsMemoryMappedFileReleaseProblem extends IOException
    {
        public MaybeWindowsMemoryMappedFileReleaseProblem( IOException e )
        {
            super(e);
        }
    }

    /*above method keep origin*/

    //return with the content of file extend with a "\n"*/
    public static String readTextFile( File file, Charset charset ) throws IOException
    {
        return (NvmFilDir.getNvmFilDir(file).readAll()+"\n");
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

    public static void writeAll(NvmStoreFileChannel nvmChannel, ByteBuffer src, long position ) {
        nvmChannel.writeAll(src, position);
    }

    public static void writeAll(NvmStoreFileChannel nvmChannel, ByteBuffer src) {
        nvmChannel.writeAll(src);
    }

    public static OpenOption[] convertOpenMode(String mode )
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

    /*only used in DelegateFileSystemAbstraction.java, supported by NvmStoreFileChannel
    * mode not used yet
    * */
    public static NvmStoreFileChannel open( Path path, String mode ) throws IOException
    {
        return new NvmStoreFileChannel(NvmFilDir.getNvmFilDir(path.toFile()));
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
        return NvmFilDir.isEmpty(directory);
    }







}
