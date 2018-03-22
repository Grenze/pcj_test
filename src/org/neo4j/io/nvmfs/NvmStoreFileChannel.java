package org.neo4j.io.nvmfs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class NvmStoreFileChannel implements NvmStoreChannel
{
    private final NvmFilDir nvmFile;
    private  int localPosition;
    private final FileChannel channel;

    public NvmStoreFileChannel(NvmFilDir file)
    {
        this.nvmFile = file;
        this.localPosition = 0;
        this.channel = null;
    }
    //copy
    public NvmStoreFileChannel(NvmStoreFileChannel nvmchannel)
    {
        this.nvmFile = nvmchannel.nvmFile;
        this.localPosition = nvmchannel.localPosition;
        this.channel = null;

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
        return nvmFile.write( byteBufferToString(src), Math.toIntExact(position) );
    }

    /*write the content of ByteBuffer[offset:offset+length(<=ByteBuffer.length)] to the position of channel, params>=0, nothing will be written if length == 0*/
    @Override
    public long write( ByteBuffer[] srcs, int offset, int length ) {
        return nvmFile.write( byteBuffersToString(srcs, offset, length ), localPosition);
    }

    /*guarantee all bytes will be written*/
    @Override
    public void writeAll( ByteBuffer src, long position ) {
        nvmFile.write( byteBufferToString(src), Math.toIntExact(position) );
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
        nvmFile.write( byteBufferToString(src) , localPosition);
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

    private int stringToByteBuffer(ByteBuffer dst){

        return 0;
    }



    @Override
    public int read( ByteBuffer dst ) {
        dst.put(nvmFile.read(dst.capacity(),localPosition).getBytes());
        return dst.limit();
    }

    @Override
    public long read( ByteBuffer[] dsts )throws IOException  {
        int sumCapacity = 0;
        for(int i=0; i<dsts.length; i++){
            sumCapacity += dsts[i].capacity();
        }
        String getString = nvmFile.read(sumCapacity, localPosition);

        return channel.read( dsts );
    }

    @Override
    public int read( ByteBuffer dst, long position )throws IOException  {
        return channel.read( dst, position );
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length ) throws IOException {
        return channel.read( dsts, offset, length );
    }

    /*position init at 0 when open the channel
     *and move to the newPosition using position method may cause hole in file
     * support method chain
     */
    @Override
    public NvmStoreFileChannel position( long newPosition )throws IOException  {
        channel.position( newPosition );
        return this;
    }

    /*return the current position*/
    @Override
    public long position()throws IOException  {
        return channel.position();
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
    public FileLock tryLock()throws IOException  {
        return channel.tryLock();
    }

    @Override
    public boolean isOpen()
    {
        return channel.isOpen();
    }

    @Override
    public void close()throws IOException  {
        channel.close();
    }

    /*size of file*/
    @Override
    public long size()throws IOException  {
        return channel.size();
    }

    /*sync memory to disk*/
    @Override
    public void force( boolean metaData )throws IOException  {
        channel.force( metaData );
    }

    @Override
    public void flush() throws IOException
    {
        force( false );
    }

    /*only used in StoreFileChannelUnwrapper.java*/
    static NvmStoreFileChannel unwrap( NvmStoreChannel channel )
    {
        NvmStoreFileChannel sfc = (NvmStoreFileChannel) channel;
        return sfc;
    }
}
