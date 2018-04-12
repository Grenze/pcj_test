package org.neo4j.io.nvmfs;

import java.nio.ByteBuffer;
import java.nio.channels.FileLock;


public class NvmStoreFileChannel implements NvmStoreChannel
{
    private NvmFilDir nvmFile;
    private sharePosition locate;


    private class sharePosition{
        public int position;
        sharePosition(){
            position=0;
        }
    }

    //new channel connect to nvmFilDir with position 0
    public NvmStoreFileChannel(NvmFilDir file, String mode)
    {
        this.nvmFile = file;
        this.locate = new sharePosition();
    }

    //copy and new channel connect to nvmFilDir with copied's position
    public NvmStoreFileChannel(NvmStoreFileChannel nvmchannel)
    {
        this.nvmFile = nvmchannel.nvmFile;
        this.locate = nvmchannel.locate;
    }


    private static byte[] combineBytes(byte[] bts1, byte[] bts2){
        byte[] btsCom = new byte[bts1.length+bts2.length];
        System.arraycopy(bts1,0,btsCom,0,bts1.length);
        System.arraycopy(bts2,0,btsCom,bts1.length,bts2.length);
        return btsCom;
    }

    //convert ByteBuffer to Bytes
    private byte[] byteBufferToBytes(ByteBuffer buf){
        byte[] bts = new byte[buf.remaining()];
        buf.get(bts, 0, bts.length);
        return bts;
    }

    //convert ByteBuffers to Bytes
    private byte[] byteBufferToBytes(ByteBuffer[] bufs, int offset, int length){
        byte[] btsCom = new byte[0];
        for(int i=0; i<length; i++){
            btsCom = combineBytes(btsCom, byteBufferToBytes(bufs[offset+i]));
        }
        return btsCom;
    }

    //convert ByteBuffer to String
    private String byteBufferToString( ByteBuffer buf ){
        byte[] bts = new byte[buf.remaining()];
        buf.get(bts, 0, bts.length);
        return new String(bts);
    }

    //convert ByteBuffers to String
    private String  byteBuffersToString( ByteBuffer[] bufs, int offset, int length ){
        String bufsStr = "";
        for(int i=0; i<length; i++){
            bufsStr += byteBufferToString(bufs[offset+i]);
        }
        return bufsStr;
    }

    /*write the content of ByteBuffer to the position of channel, position init 0*/
    @Override
    public int write( ByteBuffer src ) {
        return write(src, locate.position);
    }

    /*write the content of every ByteBuffer to the position of channel in order*/
    @Override
    public long write( ByteBuffer[] srcs ) {
        return write(srcs, 0, srcs.length);
    }

    /*write the content of ByteBuffer to the position of channel, position required*/
    @Override
    public int write( ByteBuffer src, long position ) {
        int temp = nvmFile.write( byteBufferToBytes(src), Math.toIntExact(position));
        locate.position = Math.toIntExact(position) + temp;
        return temp;
    }

    /*write the content of ByteBuffer[offset:offset+length(<=ByteBuffer.length)] to the position of channel, params>=0, nothing will be written if length == 0*/
    @Override
    public long write( ByteBuffer[] srcs, int offset, int length ) {
        int temp = nvmFile.write( byteBufferToBytes(srcs, offset, length ), locate.position);
        locate.position += temp;
        return temp;
    }

    /*guarantee all bytes will be written*/
    @Override
    public void writeAll( ByteBuffer src, long position ) {
        write( src, position );
    }

    @Override
    public void writeAll( ByteBuffer src ) {
        write(src);
    }

    /*truncate from the position*/
    @Override
    public NvmStoreFileChannel truncate( long size ) {
        nvmFile.truncate( Math.toIntExact(size), true );
        if(locate.position>Math.toIntExact(size)){
            locate.position = Math.toIntExact(size);
        }
        return this;
    }

    @Override
    public int read( ByteBuffer dst ) {
        return read(dst, locate.position);
    }

    @Override
    public long read( ByteBuffer[] dsts ) {
        return read(dsts, 0, dsts.length);
    }

    @Override
    public int read( ByteBuffer dst, long position ) {
        if(dst.remaining()==0){
            return 0;
        }
        byte[] getBts = nvmFile.read(dst.remaining(), Math.toIntExact(position), true);
        if(getBts.length==0){
            return -1;
        }
        locate.position = Math.toIntExact(position) + getBts.length;
        dst.put(getBts);
        return getBts.length;
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length ) {
        int sumCapacity = 0;
        for(int i=0; i<length; i++){
            sumCapacity += dsts[offset+i].remaining();
        }
        if(sumCapacity==0){
            return 0;
        }
        int positionMark = locate.position;
        for(int j=0; j<length;j++){
            if(read(dsts[offset+j])==-1){break;}
        }
        if(positionMark==locate.position){
            return -1;
        }
        else{
            return locate.position-positionMark;
        }


        /*int sumCapacity = 0;
        for(int i=0; i<length; i++){
            sumCapacity += dsts[offset+i].remaining();
        }
        if(sumCapacity==0){
            return 0;
        }
        String getString = nvmFile.read(sumCapacity, locate.position);
        if(getString.length()==0){
            return -1;
        }
        locate.position += getString.length();
        int len = getString.length();
        int bufOrder = offset;
        while(getString.length()>dsts[bufOrder].remaining() && bufOrder<offset+length){
            dsts[bufOrder].put(getString.substring(0, dsts[bufOrder].remaining()).getBytes());
            //sub the current bufString then change order
            getString = getString.substring(dsts[bufOrder].remaining());
            bufOrder++;
        }
        dsts[bufOrder].put(getString.getBytes());
        return len;*/
    }

    /*position init at 0 when open the channel
     *and move to the newPosition using position method may cause hole in file
     * support method chain
     */
    @Override
    public NvmStoreFileChannel position( long newPosition ) {
        locate.position = Math.toIntExact(newPosition);
        return this;
    }

    /*return the current position*/
    @Override
    public long position() {
        return locate.position;
    }

    /*pcj Transaction provide lock in lower layer, delete the tryLock()
    *    public void addMovie(PersistentString movie) {
         Transaction.run(() -> {
             movies.add(movie);
             movieIndex.add(movie);
         });
        }
    *
    *
    * */
    @Override
    public FileLock tryLock() {

        return null;
    }

    @Override
    public boolean isOpen()
    {
        return nvmFile != null;
    }

    @Override
    public void close() {
        if(this.nvmFile == null){return;}
        nvmFile.force(true);
        this.nvmFile = null;
        this.locate = null;
    }

    /*size of file*/
    @Override
    public long size() {
        return nvmFile.getSize(true);
    }

    /*sync memory to disk*/
    @Override
    public void force( boolean metaData ) {
        nvmFile.force( metaData );
    }

    @Override
    public void flush() {
        force( false );
    }

    /*only used in StoreFileChannelUnwrapper.java*/
    static NvmStoreFileChannel unwrap( NvmStoreChannel channel )
    {
        NvmStoreFileChannel sfc = (NvmStoreFileChannel) channel;
        return sfc;
    }
}
