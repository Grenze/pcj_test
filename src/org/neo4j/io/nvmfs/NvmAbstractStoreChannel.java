package org.neo4j.io.nvmfs;

import java.nio.ByteBuffer;
import java.nio.channels.FileLock;

public class NvmAbstractStoreChannel implements NvmStoreChannel{
    @Override
    public FileLock tryLock()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int write( ByteBuffer src, long position )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAll( ByteBuffer src, long position )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAll( ByteBuffer src )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read( ByteBuffer dst, long position )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void force( boolean metaData )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long write( ByteBuffer[] srcs, int offset, int length )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long write( ByteBuffer[] srcs )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long read( ByteBuffer[] dsts, int offset, int length )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long read( ByteBuffer[] dsts )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read( ByteBuffer dst )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int write( ByteBuffer src )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public NvmStoreChannel position(long newPosition )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long size()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public NvmStoreChannel truncate( long size )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        force( false );
    }
}
