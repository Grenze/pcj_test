package test.origin_fs;

import org.neo4j.io.fs.StoreFileChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class fileChannelTest {
    public static void testStoreFileChannel (){


        ByteBuffer buf0 = ByteBuffer.allocate(50);
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        String newData0 = "New String to write to file..."+System.currentTimeMillis();
        String newData1 = "Franxx String to write to file..."+System.currentTimeMillis();
        bufs[0].put("".getBytes());
        buf0.flip();
        System.out.println(buf0.limit());
        System.out.println(buf0.capacity());
        printBuffer(buf0);
        //buf1.put(newData1.getBytes());
        //bufs[1].flip();
        //System.out.println(bufs.length);*/

        try {
            FileChannel testchannel = new RandomAccessFile("text1","rw").getChannel();
            testchannel.position(1);
            testchannel.truncate(50);
            Files.delete(new File("text1").toPath());
            testchannel.write(buf0);
            //System.out.println(testchannel.tryLock().isValid());
            StoreFileChannel testchannel1 = new StoreFileChannel(testchannel);
            System.out.println(testchannel1.position());
            String a = null;
            System.out.println(a == null);

            //System.out.println(testchannel1.tryLock());


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
        String s = "";
        while (buf.hasRemaining()){
            s += (char)buf.get();
            //System.out.print((char)buf.get());
        }
        /*System.out.println(s);
        String t = "aa";
        //t.substring(-1);
        String t1 = String.format("%1$-"+7+"s",t);
        System.out.println(t1);
        String test = "0123456789";
        System.out.println(test.substring(0,4)+test.substring(10));
        //System.out.println(Math.toIntExact(100000000000000L));*/

    }
}
