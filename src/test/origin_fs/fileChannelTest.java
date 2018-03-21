package test.origin_fs;

import org.neo4j.io.fs.StoreFileChannel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class fileChannelTest {
    public static void testStoreFileChannel (){

        /*
        ByteBuffer buf0 = ByteBuffer.allocate(100);
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        String newData0 = "New String to write to file..."+System.currentTimeMillis();
        String newData1 = "Franxx String to write to file..."+System.currentTimeMillis();
        //bufs[0].put(newData0.getBytes());
        //buf0.flip();
        //buf1.put(newData1.getBytes());
        //bufs[1].flip();
        //System.out.println(bufs.length);*/

        try {
            FileChannel testchannel = new RandomAccessFile("text","rw").getChannel();
            System.out.println(testchannel.tryLock().isValid());
            StoreFileChannel testchannel1 = new StoreFileChannel(testchannel);
            System.out.println(testchannel1.tryLock());


            //StoreFileChannel neo4jchannel = new StoreFileChannel(testchannel);
            //System.out.println(neo4jchannel.equals(new StoreFileChannel(neo4jchannel)));
            /*while(buf.hasRemaining()){
                try {
                    testchannel.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            /*System.out.println(neo4jchannel.size());
            //System.out.println(neo4jchannel.isOpen());
            //System.out.println(neo4jchannel.position());
            neo4jchannel.position(30);
            //System.out.println(neo4jchannel.position());
            neo4jchannel.read(bufs);
            buf0.flip();
            buf1.flip();
            printBuffer(buf0);
            printBuffer(buf1);

            //neo4jchannel.truncate(15);
            System.out.println(neo4jchannel.position());
            //System.out.println(neo4jchannel.size());
            neo4jchannel.flush();
            neo4jchannel.close();
            //System.out.println(neo4jchannel.isOpen());*/
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printBuffer(ByteBuffer buf){
        while (buf.hasRemaining()){
            System.out.print((char)buf.get());
        }
        System.out.println();
    }
}
