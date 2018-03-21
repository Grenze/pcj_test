package test.origin_fs;


import org.neo4j.io.fs.DefaultFileSystemAbstraction;

import java.io.File;

import static test.origin_fs.fileChannelTest.testStoreFileChannel;
import static test.origin_fs.fileUtilsTest.testFileUtils;
import static test.origin_fs.javaNormalTest.testJavaNormal;
import static test.origin_fs.pcjTest.testpcj;

public class Utiltest {
    public static void testAll(){
        testStoreFileChannel();
        testpcj();
        testFileUtils();
        testDefaultFileSystemAbstraction();
        testJavaNormal();
    }

    public static void testDefaultFileSystemAbstraction(){
        DefaultFileSystemAbstraction fs = new DefaultFileSystemAbstraction();
        for(File i : fs.listFiles(new File("Dir_test"))){
            System.out.println(i.toString());
            //Dir_test/test, Dir_test/copy ......
        }
    }

}
