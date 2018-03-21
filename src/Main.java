import org.neo4j.io.fs.StoreChannel;
import test.origin_fs.Utiltest;

import java.nio.ByteBuffer;
import java.nio.channels.FileLock;





class StoreNvmFileChannel implements StoreChannel{

    @Override
    public FileLock tryLock() {
        return null;
    }

    @Override
    public int write(ByteBuffer src, long position) {
        return 0;
    }

    @Override
    public void writeAll(ByteBuffer src, long position) {

    }

    @Override
    public void writeAll(ByteBuffer src) {

    }

    @Override
    public int read(ByteBuffer dst, long position) {
        return 0;
    }

    @Override
    public void force(boolean metaData) {

    }

    @Override
    public int read(ByteBuffer byteBuffer) {
        return 0;
    }

    @Override
    public int write(ByteBuffer byteBuffer) {
        return 0;
    }

    @Override
    public long position() {
        return 0;
    }

    @Override
    public StoreChannel position(long newPosition) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public StoreChannel truncate(long size) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public long write(ByteBuffer[] byteBuffers, int i, int i1) {
        return 0;
    }

    @Override
    public long write(ByteBuffer[] byteBuffers) {
        return 0;
    }

    @Override
    public long read(ByteBuffer[] byteBuffers, int i, int i1) {
        return 0;
    }

    @Override
    public long read(ByteBuffer[] byteBuffers) {
        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }
}







public class Main {
    public static void main(String[] agrs)
    {

        Utiltest.testAll();





    }


}

