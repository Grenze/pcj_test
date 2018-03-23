package test.origin_fs;

import org.neo4j.io.fs.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public class fileUtilsTest {
    public static void testFileUtils(){
        //File dir = new File("Dir_test/File_test");
        try {
            //FileUtils.deleteRecursively(dir);
            //FileUtils.deletePathRecursively(dir.toPath());
            //FileUtils.moveFile(new File("Dir_test/File/File1"), new File("Dir_test/ex/F"));
            //FileUtils.truncateFile(new File("Dir_test/File/File1"), 20L);
            //FileChannel testchannel = new RandomAccessFile("Dir_test/File/File1","rw").getChannel();
            //FileUtils.renameFile(new File("Dir_test/File/File1"),new File("Dir_test/F2"));
            //FileUtils.renameFile(new File("Dir_test/File/File1"),new File("Dir_test/F2"));
            //testchannel.truncate(10L);
            FileUtils.copyFile(new File("Dir_test/File/123"),new File("Dir_test/copy1/F"));
            //FileUtils.copyRecursively(new File("Dir_test/File/"),new File("Dir_test/copy/"));
            //FileUtils.writeToFile(new File("Dir_test/File/File1"),"Franxx",false);
            //BufferedReader bufread = FileUtils.newBufferedFileReader(new File("Dir_test/File/File1"), StandardCharsets.UTF_8);
            //System.out.println(bufread.readLine()+bufread.readLine());
            //FileUtils.path(new File("Dir_test/test/"),"0").mkdir();
            //System.out.println(FileUtils.readTextFile(new File("Dir_test/File/File1"),StandardCharsets.UTF_8));
            //System.out.println(FileUtils.relativePath(new File("Dir_test"),new File("Dir_test/File/F")));

            /*
             * getPath() and getParent() return the path when constructed
             * getAbsolutePath() return current path + constructed path
             * getCanonicalPath() return path unique without any . or .. or other symbol
             * */
            //System.out.println(new File("./Dir_test").getCanonicalPath());
            /*"\/" at the end will be ignored by new File()*/
            //System.out.println(new File("Dir_test/"));

            //FileChannel testchannel = new RandomAccessFile("Dir_test/File/File1","rw").getChannel();
            //ByteBuffer testbuf = ByteBuffer.allocate(30);
            //testbuf.put("Franx----\n".getBytes());
            //testbuf.flip();
            //FileUtils.writeAll(testchannel,testbuf);
            /*between two append if there is a modify executed not by jvm,
             * it will start at a new line
             * */

            System.out.println(new File("Dir_test/ex1").toPath().toFile().getName());
            System.out.println(new File("Dir_test/ex1").getParentFile().getName());
            System.out.println(new File("Dir_test/ex").getParentFile().getCanonicalPath());
            System.out.println(new File("Dir_test/ex").getParentFile().getCanonicalPath());
            //System.out.println(FileUtils.isEmptyDirectory(new File("Dir_test")));
            FileUtils.newFilePrintWriter(new File("Dir_test/File/File1"), StandardCharsets.UTF_8).append("Franxx ").flush();
            //Channels.newOutputStream()
            //System.out.println(new File("").getCanonicalPath());
            //FileUtils.openAsOutputStream(new File("Dir_test/File/File1").toPath(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(FileUtils.deleteFile(new File("Dir_test/File5")));



    }



    public static Reader nvmOpenAsReader(File fileName, Charset charset) {
        return new Reader() {
            @Override
            public int read(char[] chars, int i, int i1) {
                return 0;
            }

            @Override
            public void close() {

            }
        };
    }

    /*with nvm FileChannel support I/O put stream*/
    public static InputStream nvmOpenAsInputStream(Path path) {
        return new InputStream() {
            @Override
            public int read() {
                //
                return 0;
            }
        };
    }

    public static OutputStream nvmOpenAsOutputStream(Path path, boolean append) {
        OpenOption[] options;
        if ( append )
        {
            options = new OpenOption[] {CREATE, WRITE, APPEND};
        }
        else
        {
            options = new OpenOption[] {CREATE, WRITE};
        }
        return new OutputStream() {

            @Override
            public void write(int intToWrite) {
                //this.write(intToWrite);
                //file.write or nvmchannel.write
            }
            @Override
            public void write(byte[] bytesToWrite, int offset, int length) {
                if(bytesToWrite == null){
                    throw new NullPointerException();
                }else if(offset >= 0 && offset <= bytesToWrite.length && length >= 0 && offset+length <= bytesToWrite.length){
                    //file.write or nvmchannel.write
                }else{
                    throw new IndexOutOfBoundsException();
                }
            }
        };
    }
}
