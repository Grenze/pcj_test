package test.nvm_fs;

import org.neo4j.io.nvmfs.NvmDefaultFileSystemAbstraction;
import org.neo4j.io.nvmfs.NvmFileUtils;
import org.neo4j.io.nvmfs.NvmStoreFileChannel;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class NvmUtilsTest {

    public static void testAll() throws IOException{

    NvmFileUtils.writeToFile(new File("dir1/file1"), "Violate 123456789", false);
    NvmFileUtils.truncateFile(new File("dir1/file1"), 10);


    NvmStoreFileChannel nvmsfc = NvmFileUtils.open(new File("dir1/file1").toPath(), "");
    //NvmStoreFileChannel nvmsfc1 = new NvmStoreFileChannel(nvmsfc);
    //System.out.println(nvmsfc.position());
    //System.out.println(nvmsfc1.position());
    //nvmsfc.position(1);
    //System.out.println(nvmsfc.position());
    //System.out.println(nvmsfc1.position());
    //NvmStoreFileChannel nvmsfc2 = NvmFileUtils.open(new File("dir1/file1").toPath(), "");
    //nvmsfc2.position(100);
    //System.out.println(nvmsfc.position());
    //System.out.println(nvmsfc1.position());
    //System.out.println(nvmsfc2.position());

    ByteBuffer buf0 = ByteBuffer.allocate(8);
    ByteBuffer buf1 = ByteBuffer.allocate(8);
    ByteBuffer[] bufs = new ByteBuffer[2];
    bufs[0] = buf0;
    bufs[1] = buf1;
    buf0.put("Hello!".getBytes());
    buf0.flip();
    buf1.put("World!".getBytes());
    buf1.flip();
    nvmsfc.write(bufs[0], nvmsfc.size());
    System.out.print(NvmFileUtils.readTextFile(new File("dir1/file1"), Charset.defaultCharset()));
    buf0.clear();
    buf1.clear();
    nvmsfc.read(bufs[0], 0);
    buf0.flip();
    nvmsfc.position(10);
    nvmsfc.read(bufs, 1, 1);
    //System.out.println(nvmsfc.position());
    buf1.flip();
    printBuffer(buf0);
    printBuffer(buf1);

    NvmDefaultFileSystemAbstraction fs = new NvmDefaultFileSystemAbstraction();
    fs.mkdirs(new File("dirA/dirB/file"));

    NvmFileUtils.writeToFile(new File("dir2/file2"), "Violate 123456789", false);
    NvmFileUtils.writeToFile(new File("dir3/file3"), "Violate 123456789", false);
    NvmFileUtils.printDirectory();

    NvmFileUtils.copyFile(new File("dir1/file1"), new File("dir2/fileCopyFromDir1_To_Dir2"));
    NvmFileUtils.printDirectory();

    NvmFileUtils.moveFile(new File("dir1/file1"), new File("dir3/fileMoveFromDir1_To_Dir3"));
    NvmFileUtils.printDirectory();

    NvmFileUtils.moveFileToDirectory(new File("dir3/file3"), new File("dir1/dir11"));
    NvmFileUtils.printDirectory();

    NvmFileUtils.renameFile(new File("dir1"),new File("dirX"));

    NvmFileUtils.copyRecursively(new File("dir2"),new File("dir3"));
    NvmFileUtils.printDirectory();
    NvmFileUtils.deleteFile(new File("dir3/dir2/fileCopyFromDir1_To_Dir2"));
    NvmFileUtils.printDirectory();

    NvmFileUtils.deleteRecursively(new File("."));


    //NvmFileUtils.printDirectory();

    //NvmFileUtils.copyRecursively(new File("dir2"), new File("dir3"));
    //NvmFileUtils.printDirectory();

    //NvmFileUtils.copyRecursively(new File("dir3"), new File("dir3/dir2"));
    //NvmFileUtils.printDirectory();

    /*NvmFileUtils.deleteFile(new File("dir1"));
    NvmFileUtils.printDirectory();
    NvmFileUtils.deleteFile(new File("dir1/file1"));
    NvmFileUtils.deleteFile(new File("dir1"));
    NvmFileUtils.printDirectory();*/



    //NvmFileUtils.truncateFile(new File("file1"), 15);
    //NvmFileUtils.moveFile(new File("dir1/file1"), new File("dir1/dir2/file1"));
    //NvmFileUtils.copyFile(new File("file1"), new File("MFile/X1/file1"));
    //NvmFileUtils.moveFileToDirectory(new File("file1"), new File("MFile/M1"));
    //NvmFileUtils.copyRecursively(new File("dir1"), new File("dir1/dir2"));

    //System.out.println(NvmFilDir.removeNvmFilDir(new File("MFile/M1/file2")).getSize());

    //NvmFileUtils.renameFile(new File("MFile/M1/"), new File("MFile/F1"));
    //NvmDefaultFileSystemAbstraction ndfs = new NvmDefaultFileSystemAbstraction();
    //printFiles(ndfs.listFiles(new File("dir1")));
    //NvmFileUtils.printDirectory();
    //NvmFileUtils.copyRecursively(new File("MFile"),new File("MFile/X1"));

    //NvmFileUtils.deleteRecursively(new File("MFile"));
    //NvmFileUtils.printDirectory();

    //System.out.println(new File("../../../../..").getCanonicalFile().getParentFile());
    //System.out.println(NvmFilDir.exists(new File("../../../../")));
    //System.out.println(NvmFileUtils.readTextFile(new File("dir1/file1"), Charset.forName("utf8")));
    System.out.print("----bottom line----");


}

    public static void printFiles(File[] files){
        for(File file: files){
            System.out.println(file.toString());
        }
    }

    private static void printBuffer(ByteBuffer buf) {
        while (buf.hasRemaining()) {
            System.out.print((char)buf.get());
        }
    }
}
