package test.origin_fs;


import org.neo4j.io.fs.DefaultFileSystemAbstraction;

import java.io.File;
import java.io.IOException;

import static test.origin_fs.fileChannelTest.testStoreFileChannel;

public class Utiltest {
    public static void testAll() throws IOException {
        testStoreFileChannel();
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
