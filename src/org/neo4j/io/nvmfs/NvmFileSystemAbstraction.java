package org.neo4j.io.nvmfs;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.zip.ZipOutputStream;

public interface NvmFileSystemAbstraction {
    NvmStoreFileChannel open(File fileName, String mode ) throws IOException;

    OutputStream openAsOutputStream(File fileName, boolean append ) throws IOException;

    InputStream openAsInputStream(File fileName ) throws IOException;

    Reader openAsReader(File fileName, Charset charset ) throws IOException;

    Writer openAsWriter( File fileName, Charset charset, boolean append ) throws IOException;

    NvmStoreFileChannel create(File fileName ) throws IOException;

    boolean fileExists( File fileName ) throws IOException ;

    boolean mkdir( File fileName ) throws IOException;

    void mkdirs( File fileName ) throws IOException;

    long getFileSize( File fileName ) throws IOException;

    boolean deleteFile( File fileName ) throws IOException;

    void deleteRecursively( File directory ) throws IOException;

    boolean renameFile( File from, File to ) throws IOException;

    File[] listFiles( File directory ) throws IOException;

    File[] listFiles( File directory, FilenameFilter filter ) throws IOException;

    boolean isDirectory( File file ) throws IOException;

    void moveToDirectory( File file, File toDirectory ) throws IOException;

    void copyFile( File from, File to ) throws IOException;

    void copyRecursively( File fromDirectory, File toDirectory ) throws IOException;

    <K extends NvmFileSystemAbstraction.ThirdPartyFileSystem> K getOrCreateThirdPartyFileSystem(Class<K> clazz, Function<Class<K>, K> creator );

    void truncate( File path, long size ) throws IOException;

    interface ThirdPartyFileSystem extends Closeable
    {
        void close();

        void dumpToZip(ZipOutputStream zip, byte[] scratchPad ) throws IOException;
    }

}
