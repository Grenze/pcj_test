package org.neo4j.io.nvmfs;

import java.io.File;
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




}
