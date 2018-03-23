package org.neo4j.io.nvmfs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.PersistentString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class NvmFileUtils {
    private static final  int WINDOWS_RETRY_COUNT = 5;

    private void deleteNvmFilDir(File file) throws IOException {
        File uniqueFile = file.getCanonicalFile();
        if ( !NvmFilDir.exists(uniqueFile.getCanonicalPath()) )
        {
            return;
        }
        String parentDirectory = uniqueFile.getParentFile().getCanonicalPath();
        ObjectDirectory.get(parentDirectory, NvmFilDir.class).decreaseLocalIndex(uniqueFile.getName());

        ObjectDirectory.remove(uniqueFile.getCanonicalPath(), NvmFilDir.class);

        for(PersistentString key: ObjectDirectory.map.keySet()){

        }

    }

    //delete the directory(file)'s content including itself
    public static void deleteRecursively( File directory ) {

        Path path = directory.toPath();
        deletePathRecursively( path );
    }
    //delete the path(file)'s content including itself
    public static void deletePathRecursively( Path path ) {

    }



}
