import org.neo4j.io.nvmfs.NvmDefaultFileSystemAbstraction;
import org.neo4j.io.nvmfs.NvmFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class Main {
    public static void main(String[] agrs) throws IOException {

        //Utiltest.testAll();
        NvmFileUtils.writeToFile(new File("file1"), "Violate 123456789", false);
        NvmFileUtils.truncateFile(new File("file1"), 15);
        //NvmFileUtils.moveFile(new File("file2"), new File("MFile/M1/file2"));
        NvmFileUtils.copyFile(new File("file1"), new File("MFile/X1/file1"));
        NvmFileUtils.moveFileToDirectory(new File("file1"), new File("MFile/M1"));
        NvmFileUtils.copyRecursively(new File("MFile/X1"), new File("MFile/M1"));

        //System.out.println(NvmFilDir.removeNvmFilDir(new File("MFile/M1/file2")).getSize());

        NvmFileUtils.renameFile(new File("MFile/M1/"), new File("MFile/F1"));
        NvmDefaultFileSystemAbstraction ndfs = new NvmDefaultFileSystemAbstraction();
        printFiles(ndfs.listFiles(new File("MFile")));
        NvmFileUtils.printDirectory();
        //System.out.println(new File("../../../../..").getCanonicalFile().getParentFile());
        //System.out.println(NvmFilDir.exists(new File("../../../../")));
        System.out.println(NvmFileUtils.readTextFile(new File("MFile/F1/file1"), Charset.forName("utf8")));
        System.out.print("----bottom line----");


    }

    public static void printFiles(File[] files){
        for(File file: files){
            System.out.println(file.toString());
        }
    }

}

