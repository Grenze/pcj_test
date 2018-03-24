import org.neo4j.io.nvmfs.NvmFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class Main {
    public static void main(String[] agrs) throws IOException {

        //Utiltest.testAll();
        NvmFileUtils.writeToFile(new File("file2"), "Violate 123456789", false);
        NvmFileUtils.truncateFile(new File("file2"), 10);
        //NvmFileUtils.moveFile(new File("file2"), new File("MFile/M1/file2"));
        //NvmFileUtils.moveFileToDirectory(new File("file2"), new File("MFile/M1/"));
        NvmFileUtils.printDirectory();
        //System.out.println(NvmFilDir.removeNvmFilDir(new File("MFile/M1/file2")).getSize());

        NvmFileUtils.renameFile(new File("MFile/M1/"), new File("MFile/X1"));
        //NvmDefaultFileSystemAbstraction ndfs = new NvmDefaultFileSystemAbstraction();
        //ndfs.listFiles(new File("MFile"));
        NvmFileUtils.printDirectory();
        //System.out.println(new File("../../../../..").getCanonicalFile().getParentFile());
        //System.out.println(NvmFilDir.exists(new File("../../../../")));
        System.out.println(NvmFileUtils.readTextFile(new File("MFile/M1/file2"), Charset.forName("utf8")));
        System.out.print("----bottom line----");


    }

}

