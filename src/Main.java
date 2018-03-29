import org.apache.commons.io.FileUtils;
import test.origin_fs.Utiltest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;


public class Main {
    public static void main(String[] agrs) {

        /*FileChannel channel = new RandomAccessFile( new File("/home/lingo/Desktop/neo4j_temp/LOCK"), "r" ).getChannel();
        channel.tryLock();
        FileChannel channel2 = new RandomAccessFile( new File("/home/lingo/Desktop/neo4j_temp/LOCK"), "r" ).getChannel();
        channel2.tryLock();*/

        //String s ="123";
        //System.out.println(Charset.defaultCharset());
       // System.out.print(new String(s.getBytes("UTF-8"),"GBK"));
        //System.out.print("");
        Utiltest.testAll();
        //NvmUtilsTest.testAll();
        //PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("text"), true), StandardCharsets.UTF_8));
        //pw.append("hello fan!").flush();

        /*BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream("text"), StandardCharsets.UTF_8));
        String s = null;
        while ((s = rd.readLine()) != null){
            System.out.println(s);
        }*/
        //Writer wr = new OutputStreamWriter( new FileOutputStream( new File("text"), false ), StandardCharsets.UTF_8 );
        //wr.write("123123132131231");wr.flush();




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

    public static void testNormalFileUtils(){
        try {
            //FileUtils.copyDirectory used in AbstractInProcessServerBuilder
            FileUtils.copyDirectory(new File("12"), new File("23"));
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally {

        }
    }
}

