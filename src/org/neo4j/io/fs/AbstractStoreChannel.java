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

import java.nio.ByteBuffer;
import java.nio.channels.FileLock;

public class AbstractStoreChannel implements StoreChannel
{
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
    public StoreChannel position( long newPosition )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long size()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public StoreChannel truncate( long size )
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
