import org.neo4j.io.nvmfs.NvmFileUtils;

import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] agrs) throws IOException {

        //Utiltest.testAll();
        //NvmFileUtils.writeToFile(new File("dir1/file1"), "Violate 123456789", false);
        //NvmFileUtils.truncateFile(new File("file1"), 15);
        //NvmFileUtils.moveFile(new File("dir1/file1"), new File("dir1/dir2/file1"));
        //NvmFileUtils.copyFile(new File("file1"), new File("MFile/X1/file1"));
        //NvmFileUtils.moveFileToDirectory(new File("file1"), new File("MFile/M1"));
        NvmFileUtils.copyRecursively(new File("dir1"), new File("dir1/dir2"));

        //System.out.println(NvmFilDir.removeNvmFilDir(new File("MFile/M1/file2")).getSize());

        //NvmFileUtils.renameFile(new File("MFile/M1/"), new File("MFile/F1"));
        //NvmDefaultFileSystemAbstraction ndfs = new NvmDefaultFileSystemAbstraction();
        //printFiles(ndfs.listFiles(new File("dir1")));
        NvmFileUtils.printDirectory();
        //NvmFileUtils.copyRecursively(new File("MFile"),new File("MFile/X1"));

        //NvmFileUtils.deleteRecursively(new File("MFile"));
        //NvmFileUtils.printDirectory();

        //System.out.println(new File("../../../../..").getCanonicalFile().getParentFile());
        //System.out.println(NvmFilDir.exists(new File("../../../../")));
        //System.out.println(NvmFileUtils.readTextFile(new File("MFile/F1/file1"), Charset.forName("utf8")));
        System.out.print("----bottom line----");


    }

    public static void printFiles(File[] files){
        for(File file: files){
            System.out.println(file.toString());
        }
    }

}

