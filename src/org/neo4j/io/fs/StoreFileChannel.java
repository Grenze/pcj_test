/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.io.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class StoreFileChannel implements StoreChannel
{
    private final FileChannel channel;

    /*constructor, param:FileChannel*/
    public StoreFileChannel( FileChannel channel )
    {
        this.channel = channel;
    }

    /*duplicate a new StoreFileChannel to the same file*/
    public StoreFileChannel( StoreFileChannel channel )
    {
        this.channel = channel.channel;
    }

    /*write the content of ByteBuffer to the position of channel, position init 0*/
    @Override
    public int write( ByteBuffer src ) throws IOException
    {
        return channel.write( src );
    }

    /*write the content of every ByteBuffer to the position of channel in order*/
    @Override
    public long write( ByteBuffer[] srcs ) throws IOException
    {
        return channel.write( srcs );
    }

    /*write the content of ByteBuffer to the position of channel, position required*/
    @Override
    public int write( ByteBuffer src, long position ) throws IOException
    {
        return channel.write( src, position );
    }

    /*write the content of ByteBuffer[offset:offset+length(<=ByteBuffer.length)] to the position of channel, params>=0, nothing will be written if length == 0*/
    @Override
    public long write( ByteBuffer[] srcs, int offset, int length ) throws IOException
    {
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
    public StoreFileChannel truncate( long size ) throws IOException
    {
        channel.truncate( size );
        return this;
    }

    @Override
    public int read( ByteBuffer dst ) throws IOException
    {
        return channel.read( dst );
    }

    @Override
    public long read( ByteBuffer[] dsts ) throws IOException
    {
        return channel.read( dsts );
    }

    @Override
    public int read( ByteBuffer dst, long position ) throws IOException
    {
        return channel.read( dst, position );
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length ) throws IOException
    {
        return channel.read( dsts, offset, length );
    }

    /*position init at 0 when open the channel
     *and move to the newPosition using position method may cause hole in file
     * support method chain
     */
    @Override
    public StoreFileChannel position( long newPosition ) throws IOException
    {
        channel.position( newPosition );
        return this;
    }

    /*return the current position*/
    @Override
    public long position() throws IOException
    {
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
    public FileLock tryLock() throws IOException
    {
        return channel.tryLock();
    }

    @Override
    public boolean isOpen()
    {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException
    {
        channel.close();
    }

    /*size of file*/
    @Override
    public long size() throws IOException
    {
        return channel.size();
    }

    /*sync memory to disk*/
    @Override
    public void force( boolean metaData ) throws IOException
    {
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
