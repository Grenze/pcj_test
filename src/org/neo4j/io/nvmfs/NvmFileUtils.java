package org.neo4j.io.nvmfs;

import java.io.File;
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
        NvmFilDir.getNvmFilDir(parentDirectory).decreaseLocalIndex(file.getName());

        NvmFilDir.removeNvmFilDir(file);

        for(String key: NvmFilDir.getNvmFilDirDirectory()){
            if(key.startsWith(file.getCanonicalPath())){
                NvmFilDir.removeNvmFilDir(key);
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
        NvmFilDir.getNvmFilDir(src.getParentFile()).decreaseLocalIndex(src.getName());
        nvmMkDirs(dst.getParentFile(), false, true);
        NvmFilDir.getNvmFilDir(dst.getParentFile()).increaseLocalIndex(src.getName());

        NvmFilDir srcFilDir = NvmFilDir.getNvmFilDir(src);
        srcFilDir.renameNvmFilDir(src, dst);//index changed from src to dst, inner globalId changed too

        if(NvmFilDir.isFile(dst) || NvmFilDir.isEmpty(dst)){
            return;
        }
        for(String key: NvmFilDir.getNvmFilDirDirectory()){
            if(key.startsWith(src.getCanonicalPath())){
                NvmFilDir subFilDir = NvmFilDir.removeNvmFilDir(key);
                subFilDir.renameNvmFilDir(new File(key), new File(dst.toString()+key.substring(src.getCanonicalPath().length())));
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
        NvmFilDir.getNvmFilDir(parentFile).increaseLocalIndex(file.getName());
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




}
