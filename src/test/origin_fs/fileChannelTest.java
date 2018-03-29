package test.origin_fs;

import java.nio.ByteBuffer;

public class fileChannelTest {
    public static void testStoreFileChannel (){


        ByteBuffer buf0 = ByteBuffer.allocate(50);
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        String newData0 = "New String to write to file..."+System.currentTimeMillis();
        String newData1 = "Franxx String to write to file..."+System.currentTimeMillis();
        int i = 123456;
        buf0.putInt(i);
        buf0.flip();
        //System.out.println(buf0.limit());
        //System.out.println(buf0.capacity());
        buf1.put(printBuffer(buf0).getBytes());
        buf1.flip();
        System.out.println(buf1.getInt());
        //printBuffer(buf1);
        //buf1.put(newData1.getBytes());
        //bufs[1].flip();
        //System.out.println(bufs.length);*/

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

    public static String printBuffer(ByteBuffer buf){

        String s = "";
        //byte[] bytes = new byte[buf.remaining()];
        while (buf.hasRemaining()){
            //buf.get(bytes,0,bytes.length);
            s += String.valueOf(buf.get());
            //System.out.print((char)buf.get());
        }

        //s = bytes.toString();
        System.out.println(s);
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
