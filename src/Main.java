import test.origin_fs.Utiltest;

import java.io.File;
import java.nio.ByteBuffer;


public class Main {
    public static void main(String[] agrs) {

        Utiltest.testAll();

        /*NvmFileUtils.writeToFile(new File("dir1/file1"), "Violate 123456789", false);
        NvmFileUtils.writeToFile(new File("dir2/file2"), "Violate 123456789", false);
        NvmFileUtils.writeToFile(new File("dir3/file3"), "Violate 123456789", false);
        NvmFileUtils.printDirectory();

        NvmFileUtils.copyFile(new File("dir1/file1"), new File("dir2/file123456"));
        NvmFileUtils.printDirectory();

        NvmFileUtils.copyRecursively(new File("dir2"), new File("dir3"));
        NvmFileUtils.printDirectory();

        NvmFileUtils.copyRecursively(new File("dir3"), new File("dir3/dir2"));
        NvmFileUtils.printDirectory();*/

        /*NvmFileUtils.deleteFile(new File("dir1"));
        NvmFileUtils.printDirectory();
        NvmFileUtils.deleteFile(new File("dir1/file1"));
        NvmFileUtils.deleteFile(new File("dir1"));
        NvmFileUtils.printDirectory();*/


        //NvmStoreFileChannel nvmsfc = NvmFileUtils.open(new File("dir1/file1").toPath(), "");
        /*NvmStoreFileChannel nvmsfc1 = new NvmStoreFileChannel(nvmsfc);
        System.out.println(nvmsfc.position());
        System.out.println(nvmsfc1.position());
        nvmsfc.position(1);
        System.out.println(nvmsfc.position());
        System.out.println(nvmsfc1.position());
        NvmStoreFileChannel nvmsfc2 = NvmFileUtils.open(new File("dir1/file1").toPath(), "");
        nvmsfc2.position(100);
        System.out.println(nvmsfc.position());
        System.out.println(nvmsfc1.position());
        System.out.println(nvmsfc2.position());*/

        /*ByteBuffer buf0 = ByteBuffer.allocate(6);
        ByteBuffer buf1 = ByteBuffer.allocate(6);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        buf0.put("Hello!".getBytes());
        buf0.flip();
        buf1.put("World!".getBytes());
        buf1.flip();
        nvmsfc.write(bufs, 1, 1);
        buf0.clear();
        buf1.clear();
        nvmsfc.position(0);
        nvmsfc.read(bufs);
        buf0.flip();
        printBuffer(buf0);*/
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

    public static void printBuffer(ByteBuffer buf) {
        while (buf.hasRemaining()) {
            System.out.print((char)buf.get());
        }
    }
}

