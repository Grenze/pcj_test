package test.nvm_fs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.PersistentHashMap;

import java.io.File;
import java.nio.ByteBuffer;

import static lib.util.persistent.Util.persistent;

public class NvmUtilsTest {

    public static void testAll() {
        long loopTimes = 10;

        PersistentHashMap pHm = new PersistentHashMap();
        ObjectDirectory.put("pHm", pHm);



        //ObjectDirectory.put(String.valueOf(20), PersistentByteBuffer.copyWrap(new byte[20]));
        //ObjectDirectory.put(String.valueOf(20), persistent("20"));

        //NvmFilDir testFilDir = new NvmFilDir("testFilDir",true,false);
        //ObjectDirectory.put("testFilDir", testFilDir);


        long Start = System.currentTimeMillis();
        //for(int i=0;i<loopTimes;i++){
            //ObjectDirectory.put(String.valueOf(i), PersistentByteBuffer.copyWrap(new byte[i]));//put the origin class costs 5s, init heap costs 5s
            //ObjectDirectory.put(String.valueOf(i), persistent(String.valueOf(i)));
            //ObjectDirectory.put(String.valueOf(i),new NvmFilDir(String.valueOf(i), true, false));//18s
        //}
        long End = System.currentTimeMillis();
        //System.out.println(loopTimes + " Puts cost "+(End - Start)+" ms");


        Start = System.currentTimeMillis();
        for(int j=0;j<loopTimes;j++){
            //testFilDir.setFileContent(String.valueOf(j));
            //pHm.put(persistent(j), new NvmFilDir(String.valueOf(j), true, false));//17s
            pHm.put(persistent(j), persistent(String.valueOf(j)));
        }
        End = System.currentTimeMillis();
        System.out.println(loopTimes + " Sets cost "+(End - Start)+" ms");



        /*


        NvmFileUtils.writeToFile(new File("dir1/file1"), "Violate 123456789", false);

        NvmFileUtils.truncateFile(new File("dir1/file1"), 10);


        NvmStoreFileChannel nvmsfc = NvmFileUtils.open(new File("dir1/file1").toPath(), "");

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

        /*

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

        NvmFileUtils.printDirectory();*/

        //System.out.print("----bottom line----");


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
