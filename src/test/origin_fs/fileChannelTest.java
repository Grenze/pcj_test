package test.origin_fs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Base64;

public class fileChannelTest {

    public static void testStoreFileChannel () throws IOException {

        FileChannel f1Channel = new RandomAccessFile("text","rw").getChannel();
        f1Channel.tryLock();
        RandomAccessFile raf = new RandomAccessFile("text","rw");
        FileChannel fChannel = raf.getChannel();
        //fChannel.tryLock();



        ByteBuffer buf0 = ByteBuffer.allocate(100);
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        String newData0 = "New String to write to file...";
        String newData1 = "Franxx String to write to file...";

        byte[] a = new byte[9];
        a[8] = 1;
        buf0.put(a);
        //buf0.putInt(123);
        buf0.putChar('中');
        buf0.put(newData0.getBytes());
        //buf0.flip();
        //byte[] bts1 = new byte[10];
        //bts1[0] = 10;

        //bts1[9] = 10;
        //byte[] bts2 = new byte[20];
        //bts2[0] = 20;
        //bts2[19] = 20;

        //buf0.put(new String(combineBytes(bts1, bts2)).getBytes());
        //buf0.put((" "+ new String(new byte[10])).getBytes());
        buf0.flip();


        buf1.put(conditional(buf0).getBytes());

        //buf1.put(bufferToString(buf0).getBytes());
        buf1.flip();
        System.out.println(fChannel.write(buf1));


        buf0.clear();
        //buf0.putInt(1);
        //buf0.position(11);
        //buf0.limit(9);
        //System.out.println(buf0.remaining());
        //buf1.clear();
        //buf1.limit(10);
        //buf1.position(9);
        //buf0.limit(10);
        //fChannel.position(100);

        //fChannel.truncate(20);
        //System.out.println(fChannel.position());
        //fChannel.position(10);
        System.out.println(fChannel.read(buf0));
        //buf0.clear();

        //fChannel.position(0);
        //fChannel.read(buf0);
        //buf0.flip();

        //System.out.println(buf0.getInt());
        //System.out.println(buf0.getChar());
        //System.out.println(buf0.get());
        //printBuffer(buf0);



        //printBufferInformation(buf1);

        //System.out.println(String.format("%1$-"+10+"s", "123456"));
        //System.out.println("123456789".substring(0,2)+"++"+"123456789".substring(4));



        //try {
            //RandomAccessFile raf = new RandomAccessFile("text","rw");
            //FileChannel fchannel = raf.getChannel();
            //raf.close();
            //fchannel.close();

            //FileUtils.moveFileToDirectory(new File("text"), new File("Dir_test"));
            //fchannel.truncate(50);
            //Files.delete(new File("text").toPath());
            //fchannel.write(buf0);
            //System.out.println(testchannel.tryLock().isValid());
            /*StoreFileChannel Schannel1 = new StoreFileChannel(fchannel);
            StoreFileChannel Schannel2 = new StoreFileChannel(Schannel1);
            //Files.delete(new File("text1").toPath());
            Schannel1.write(buf0);
            System.out.println(Schannel1.position());
            System.out.println(Schannel2.position());

            //fchannel.position(1);

            System.out.println(Schannel1.position());
            System.out.println(Schannel2.position());

            FileChannel fchannel1 = new RandomAccessFile("text","rw").getChannel();
            fchannel1.position(100);
            StoreFileChannel Schannel3 = new StoreFileChannel(fchannel1);


            System.out.println(Schannel1.position());
            System.out.println(Schannel2.position());
            System.out.println(Schannel3.position());*/






            //String a = null;
            //System.out.println(a == null);
            //String f = "123/456/789";
            //System.out.println(f.replace("/456",""));

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
        //} //catch (IOException e) {
            //e.printStackTrace();
        //}


    }

    private static void toHex(String str){
        String toHex = "";
        for(int i =0;i< str.length();i++){
            toHex += Integer.toHexString((int) str.charAt(i));
        }
        System.out.println(toHex);
    }



    private static String conditional(ByteBuffer buf){
        System.out.println("!!!!!"+bufferToBytes((buf)).length);

        String s = new String(bufferToBytes(buf));
        System.out.println("!!!!!"+s.getBytes().length);

        toHex(s);
        return s;
    }
    private static byte[] bufferToBytes(ByteBuffer buf){
        byte[] bts = new byte[buf.remaining()];
        buf.get(bts, buf.position(), buf.remaining());
        buf.flip();
        return bts;
    }

    private static  byte[] combineBytes(byte[] bts1, byte[] bts2){
        byte[] btsCom = new byte[bts1.length+bts2.length];
        System.arraycopy(bts1,0,btsCom,0,bts1.length);
        System.arraycopy(bts2,0,btsCom,bts1.length,bts2.length);
        return btsCom;
    }


    public static String byteBufferToString(ByteBuffer buf){

        String str = Base64.getEncoder().encodeToString(bufferToBytes(buf));
        System.out.println("Base64 encoded: "+str);
        return str;
    }

    public static void printBufferInformation(ByteBuffer buf){

        System.out.println(buf.toString());

    }

    public static byte[] stringToBytes(String str){
        byte[] bytes = Base64.getDecoder().decode(str);
        return bytes;
    }

    public static String bufferToString(ByteBuffer buf){

        String s = "";
        //byte[] bytes = new byte[buf.remaining()];
        while (buf.hasRemaining()){
            //buf.get(bytes,0,bytes.length);
            s += (char)buf.get();
            //System.out.print((char)buf.get());
        }

        //s = bytes.toString();
        //System.out.println(s);
        return s;
        //String t = "aa";
        //t.substring(-1);
        //String t1 = String.format("%1$-"+7+"s",t);
        //System.out.println(t1);
        //String test = "0123456789";
        //System.out.println(test.substring(0,4)+test.substring(10));
        //System.out.println(Math.toIntExact(100000000000000L));*/

    }
}
