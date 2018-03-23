package org.neo4j.io.nvmfs;

import java.nio.ByteBuffer;
import java.nio.channels.FileLock;


public class NvmStoreFileChannel implements NvmStoreChannel
{
    private final NvmFilDir nvmFile;
    private int localPosition;

    public NvmStoreFileChannel(NvmFilDir file)
    {
        this.nvmFile = file;
        this.localPosition = 0;
    }
    //copy
    public NvmStoreFileChannel(NvmStoreFileChannel nvmchannel)
    {
        this.nvmFile = nvmchannel.nvmFile;
        this.localPosition = nvmchannel.localPosition;
    }
    //convert ByteBuffer to String
    private String byteBufferToString( ByteBuffer buf ){
        String bufStr = "";
        while (buf.hasRemaining()){
            bufStr += (char)buf.get();
        }
        return bufStr;
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
        int temp = nvmFile.write( byteBufferToString(src) , localPosition);
        localPosition += temp;
        return temp;
    }

    /*write the content of every ByteBuffer to the position of channel in order*/
    @Override
    public long write( ByteBuffer[] srcs ) {
        int temp = nvmFile.write( byteBuffersToString(srcs, 0, srcs.length),  localPosition);
        localPosition += temp;
        return temp;
    }

    /*write the content of ByteBuffer to the position of channel, position required*/
    @Override
    public int write( ByteBuffer src, long position ) {
        int temp = nvmFile.write( byteBufferToString(src), Math.toIntExact(position));
        localPosition = Math.toIntExact(position) + temp;
        return temp;
    }

    /*write the content of ByteBuffer[offset:offset+length(<=ByteBuffer.length)] to the position of channel, params>=0, nothing will be written if length == 0*/
    @Override
    public long write( ByteBuffer[] srcs, int offset, int length ) {
        int temp = nvmFile.write( byteBuffersToString(srcs, offset, length ), localPosition);
        localPosition += temp;
        return temp;
    }

    /*guarantee all bytes will be written*/
    @Override
    public void writeAll( ByteBuffer src, long position ) {
        write( src, Math.toIntExact(position) );
        /*long filePosition = position;
        //be sure ByteBuffer.flip() executed
        long expectedEndPosition = filePosition + src.limit() - src.position();
        int bytesWritten;
        while((filePosition += (bytesWritten = write( src, filePosition ))) < expectedEndPosition)
        {
            if( bytesWritten < 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }*/
    }

    @Override
    public void writeAll( ByteBuffer src ) {
        write(src);
        /*long bytesToWrite = src.limit() - src.position();
        int bytesWritten;
        while((bytesToWrite -= (bytesWritten = write( src ))) > 0)
        {
            if( bytesWritten < 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }*/
    }

    /*truncate from the position*/
    @Override
    public NvmStoreFileChannel truncate( long size ) {
        nvmFile.truncate( Math.toIntExact(size) );
        return this;
    }

    @Override
    public int read( ByteBuffer dst ) {
        String getString = nvmFile.read(dst.capacity(),localPosition);
        localPosition += getString.length();
        dst.put(getString.getBytes());
        return getString.length();
    }

    @Override
    public long read( ByteBuffer[] dsts ) {
        return read(dsts, 0, dsts.length);
    }

    @Override
    public int read( ByteBuffer dst, long position ) {
        String getString = nvmFile.read(dst.capacity(), Math.toIntExact(position));
        localPosition = Math.toIntExact(position) + getString.length();
        dst.put(getString.getBytes());
        return getString.length();
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length ) {
        int sumCapacity = 0;
        for(int i=0; i<length; i++){
            sumCapacity += dsts[offset+i].capacity();
        }
        String getString = nvmFile.read(sumCapacity, localPosition);
        localPosition += getString.length();
        int bufOrder = offset;
        while(getString.length()>dsts[bufOrder].capacity() && bufOrder<offset+length){
            dsts[bufOrder].put(getString.substring(0, dsts[bufOrder].capacity()).getBytes());
            //sub the current bufString then change order
            getString = getString.substring(dsts[bufOrder].capacity());
            bufOrder++;
        }
        dsts[bufOrder].put(getString.getBytes());
        return getString.length();
    }

    /*position init at 0 when open the channel
     *and move to the newPosition using position method may cause hole in file
     * support method chain
     */
    @Override
    public NvmStoreFileChannel position( long newPosition ) {
        localPosition = Math.toIntExact(newPosition);
        return this;
    }

    /*return the current position*/
    @Override
    public long position() {
        return localPosition;
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
        return;
    }

    /*size of file*/
    @Override
    public long size() {
        return nvmFile.getSize();
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
