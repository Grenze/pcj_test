package org.neo4j.io.nvmfs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class NvmFileUtils {
    private static final  int WINDOWS_RETRY_COUNT = 5;

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











}
