package org.neo4j.io.nvmfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class NvmFileUtils {
    private static final  int WINDOWS_RETRY_COUNT = 5;

    private void deleteNvmFilDir(File file) throws IOException {
        if ( !NvmFilDir.exists(file) )
        {
            return;
        }
    }

    //delete the directory(file)'s content including itself
    public static void deleteRecursively( File directory ) throws IOException
    {

        Path path = directory.toPath();
        deletePathRecursively( path );
    }
    //delete the path(file)'s content including itself
    public static void deletePathRecursively( Path path ) {

    }



}
