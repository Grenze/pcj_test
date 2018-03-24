import org.neo4j.io.nvmfs.NvmFilDir;
import org.neo4j.io.nvmfs.NvmFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class Main {
    public static void main(String[] agrs) throws IOException {

        //Utiltest.testAll();
        NvmFileUtils.writeToFile(new File("dir_test"), "Violate", true);
        //NvmFileUtils.moveFile(new File("dir_test"), new File("MFile/M1/file1"));
        //NvmFileUtils.moveFileToDirectory(new File("Dir_test"), new File("moved"));
        NvmFileUtils.printDirectory();
        System.out.println(NvmFilDir.exists(new File("/home")));
        System.out.println(NvmFileUtils.readTextFile(new File("MFile/M1/file1"), Charset.forName("utf8")));
        System.out.print("----bottom line----");


    }


}

