package test.origin_fs;


import org.neo4j.io.fs.DefaultFileSystemAbstraction;

import java.io.File;

public class Utiltest {
    public static void testAll() {
        //testStoreFileChannel();
        //testpcj();
        //testFileUtils();
        //testDefaultFileSystemAbstraction();
    }

    public static void testDefaultFileSystemAbstraction() {
        DefaultFileSystemAbstraction fs = new DefaultFileSystemAbstraction();
        for(File i : fs.listFiles(new File("Dir_test"))){
            System.out.println(i.toString());
            //Dir_test/test, Dir_test/copy ......
        }
        System.out.println(fs.mkdir(new File("/123")));
    }

}
