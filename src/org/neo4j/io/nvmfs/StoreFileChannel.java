package org.neo4j.io.nvmfs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;

public class StoreFileChannel implements StoreChannel
{
    private final File nvmFile;

    public StoreFileChannel(File file)
    {
        this.nvmFile = file;
    }
    public StoreFileChannel(StoreFileChannel nvmchannel)
    {
        this.nvmFile = nvmchannel.nvmFile;
    }

    /*write the content of ByteBuffer to the position of channel, position init 0*/
    @Override
    public int write( ByteBuffer src ) {
        return channel.write( src );
    }

    /*write the content of every ByteBuffer to the position of channel in order*/
    @Override
    public long write( ByteBuffer[] srcs ) {
        return channel.write( srcs );
    }

    /*write the content of ByteBuffer to the position of channel, position required*/
    @Override
    public int write( ByteBuffer src, long position ) {
        return channel.write( src, position );
    }

    /*write the content of ByteBuffer[offset:offset+length(<=ByteBuffer.length)] to the position of channel, params>=0, nothing will be written if length == 0*/
    @Override
    public long write( ByteBuffer[] srcs, int offset, int length ) {
        return channel.write( srcs, offset, length );
    }

    /*guarantee all bytes will be written*/
    @Override
    public void writeAll( ByteBuffer src, long position ) throws IOException
    {
        long filePosition = position;
        //be sure ByteBuffer.flip() executed
        long expectedEndPosition = filePosition + src.limit() - src.position();
        int bytesWritten;
        while((filePosition += (bytesWritten = write( src, filePosition ))) < expectedEndPosition)
        {
            if( bytesWritten < 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }
    }

    @Override
    public void writeAll( ByteBuffer src ) throws IOException
    {
        long bytesToWrite = src.limit() - src.position();
        int bytesWritten;
        while((bytesToWrite -= (bytesWritten = write( src ))) > 0)
        {
            if( bytesWritten < 0 )
            {
                throw new IOException( "Unable to write to disk, reported bytes written was " + bytesWritten );
            }
        }
    }

    /*truncate from the position*/
    @Override
    public StoreFileChannel truncate( long size ) {
        channel.truncate( size );
        return this;
    }

    @Override
    public int read( ByteBuffer dst ) {
        return channel.read( dst );
    }

    @Override
    public long read( ByteBuffer[] dsts ) {
        return channel.read( dsts );
    }

    @Override
    public int read( ByteBuffer dst, long position ) {
        return channel.read( dst, position );
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length ) {
        return channel.read( dsts, offset, length );
    }

    /*position init at 0 when open the channel
     *and move to the newPosition using position method may cause hole in file
     * support method chain
     */
    @Override
    public StoreFileChannel position( long newPosition ) {
        channel.position( newPosition );
        return this;
    }

    /*return the current position*/
    @Override
    public long position() {
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
    public FileLock tryLock() {
        return channel.tryLock();
    }

    @Override
    public boolean isOpen()
    {
        return channel.isOpen();
    }

    @Override
    public void close() {
        channel.close();
    }

    /*size of file*/
    @Override
    public long size() {
        return channel.size();
    }

    /*sync memory to disk*/
    @Override
    public void force( boolean metaData ) {
        channel.force( metaData );
    }

    @Override
    public void flush() throws IOException
    {
        force( false );
    }

    /*only used in StoreFileChannelUnwrapper.java*/
    static FileChannel unwrap( StoreChannel channel )
    {
        StoreFileChannel sfc = (StoreFileChannel) channel;
        return sfc.channel;
    }
}
