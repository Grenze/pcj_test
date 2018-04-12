import org.apache.commons.io.FileUtils;
import test.nvm_fs.NvmUtilsTest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;


public class Main {
    public static void main(String[] args) throws IOException {


        //Utiltest.testAll();
        NvmUtilsTest.testAll();



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

