package org.neo4j.io.nvmfs;



import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class DelegatingFileSystem extends FileSystem
{
    private final FileSystem delegate;
    private final FileSystemProvider provider;

    public DelegatingFileSystem( final FileSystem delegate )
    {
        this.delegate = delegate;
        FileSystemProvider delegateProvider = delegate.provider();
        this.provider = ( delegateProvider == null ) ? null : createDelegate( delegateProvider );
    }

    public FileSystem getDelegate()
    {
        return delegate;
    }

    @Override
    public FileSystemProvider provider()
    {
        return provider;
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }

    @Override
    public boolean isOpen()
    {
        return delegate.isOpen();
    }

    @Override
    public boolean isReadOnly()
    {
        return delegate.isReadOnly();
    }

    @Override
    public String getSeparator()
    {
        return delegate.getSeparator();
    }

    @Override
    public Iterable<Path> getRootDirectories()
    {
        final Iterable<Path> rootDirectories = delegate.getRootDirectories();
        return new Iterable<Path>()
        {
            @Override
            public Iterator<Path> iterator()
            {
                final Iterator<Path> iterator = rootDirectories.iterator();
                return new Iterator<Path>()
                {
                    @Override
                    public boolean hasNext()
                    {
                        return iterator.hasNext();
                    }

                    @Override
                    public Path next()
                    {
                        return createDelegate( iterator.next() );
                    }

                    @Override
                    public void remove()
                    {
                        iterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<FileStore> getFileStores()
    {
        return delegate.getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews()
    {
        return delegate.supportedFileAttributeViews();
    }

    @Override
    public Path getPath( String first, String... more )
    {
        return createDelegate( delegate.getPath( first, more ) );
    }

    @Override
    public PathMatcher getPathMatcher( String syntaxAndPattern )
    {
        final PathMatcher matcher = delegate.getPathMatcher( syntaxAndPattern );
        return new PathMatcher()
        {
            @Override
            public boolean matches( Path path )
            {
                return matcher.matches( DelegatingPath.getDelegate( path ) );
            }
        };
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return delegate.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService()
    {
        throw new UnsupportedOperationException();
    }

    protected Path createDelegate( Path path )
    {
        return new WrappedPath( path, this );
    }

    protected DelegatingFileSystemProvider createDelegate( FileSystemProvider provider )
    {
        return new WrappedProvider( provider, this );
    }

    public static class WrappedPath extends DelegatingPath
    {
        private final DelegatingFileSystem fileSystem;

        WrappedPath( Path delegate, DelegatingFileSystem fileSystem )
        {
            super( delegate, fileSystem );
            this.fileSystem = fileSystem;
        }

        @Override
        protected Path createDelegate( Path path )
        {
            return fileSystem.createDelegate( path );
        }
    }

    public static class WrappedProvider extends DelegatingFileSystemProvider
    {
        private final DelegatingFileSystem fileSystem;

        public WrappedProvider( FileSystemProvider delegate, DelegatingFileSystem fileSystem )
        {
            super( delegate );
            this.fileSystem = fileSystem;
        }

        @Override
        public FileSystem newFileSystem( URI uri, Map<String, ?> env ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileSystem getFileSystem( URI uri )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Path createDelegate( Path path )
        {
            return fileSystem.createDelegate( path );
        }
    }
}
