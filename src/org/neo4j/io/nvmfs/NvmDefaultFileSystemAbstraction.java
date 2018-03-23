package org.neo4j.io.nvmfs;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Function;

public class NvmDefaultFileSystemAbstraction implements NvmFileSystemAbstraction {

    //mode not used yet
    @Override
    public NvmStoreFileChannel open(File fileName, String mode) throws IOException {
        return new NvmStoreFileChannel(NvmFilDir.getNvmFilDir(fileName));
    }

    /*method below not supported yet, supported by NvmFileUtils*/
    @Override
    public OutputStream openAsOutputStream(File fileName, boolean append) {
        return null;
    }

    @Override
    public InputStream openAsInputStream(File fileName) {
        return null;
    }

    @Override
    public Reader openAsReader(File fileName, Charset charset) {
        return null;
    }

    @Override
    public Writer openAsWriter(File fileName, Charset charset, boolean append) {
        return null;
    }

    @Override
    public NvmStoreFileChannel create(File fileName) throws IOException {
        return new NvmStoreFileChannel(NvmFilDir.getNvmFilDir(fileName));
    }

    @Override
    public boolean fileExists(File fileName) throws IOException {
        return NvmFilDir.exists(fileName);
    }

    @Override
    public boolean mkdir(File fileName) throws IOException {
        NvmFileUtils.nvmMkDirs(fileName, false, true);
        return true;
    }

    @Override
    public void mkdirs(File fileName) throws IOException {
        NvmFileUtils.nvmMkDirs(fileName, false, true);
    }

    @Override
    public long getFileSize(File fileName) throws IOException {
        return NvmFilDir.getNvmFilDir(fileName).getSize();
    }

    @Override
    public boolean deleteFile(File fileName) throws IOException {
        return NvmFileUtils.deleteFile(fileName);
    }

    @Override
    public void deleteRecursively(File directory) throws IOException {
        NvmFileUtils.deleteRecursively(directory);
    }

    @Override
    public boolean renameFile(File from, File to) throws IOException {
        return NvmFileUtils.renameFile(from, to);
    }

    @Override
    public File[] listFiles(File directory) {
        return new File[0];
    }

    @Override
    public File[] listFiles(File directory, FilenameFilter filter) {
        return new File[0];
    }

    @Override
    public boolean isDirectory(File file) {
        return false;
    }

    @Override
    public void moveToDirectory(File file, File toDirectory) {

    }

    @Override
    public void copyFile(File from, File to) {

    }

    @Override
    public void copyRecursively(File fromDirectory, File toDirectory) {

    }

    @Override
    public <K extends ThirdPartyFileSystem> K getOrCreateThirdPartyFileSystem(Class<K> clazz, Function<Class<K>, K> creator) {
        return null;
    }

    @Override
    public void truncate(File path, long size) {

    }
}
