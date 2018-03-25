package org.neo4j.io.nvmfs;


import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NvmDefaultFileSystemAbstraction implements NvmFileSystemAbstraction {

    //mode not used yet
    @Override
    public NvmStoreFileChannel open(File fileName, String mode) throws IOException {
        NvmFileUtils.nvmMkDirs(fileName, true, false);//ensure there is NvmFilDir with fileName
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
        return open(fileName, "rw");
    }

    @Override
    public boolean mkdir(File fileName) throws IOException {
        if(fileName.toString().split("/").length>1){
            return false;
        }
        NvmFileUtils.nvmMkDirs(fileName, false, true);
        return true;
    }

    @Override
    public void mkdirs(File fileName) throws IOException {
        NvmFileUtils.nvmMkDirs(fileName, false, true);
    }

    @Override
    public boolean fileExists(File fileName) throws IOException {
        return NvmFilDir.exists(fileName);
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
    public File[] listFiles(File directory) throws IOException {
        return NvmFilDir.listLocalFiles(directory, null);
    }

    @Override
    public File[] listFiles(File directory, FilenameFilter filter) throws IOException {
        return NvmFilDir.listLocalFiles(directory, filter);
    }

    @Override
    public boolean isDirectory(File file) throws IOException {
        return NvmFilDir.isDirectory(file);
    }

    @Override
    public void moveToDirectory(File file, File toDirectory) throws IOException {
        NvmFileUtils.moveFileToDirectory(file, toDirectory);
    }

    @Override
    public void copyFile(File from, File to) throws IOException {
        NvmFileUtils.copyFile(from, to);
    }

    @Override
    public void copyRecursively(File fromDirectory, File toDirectory) throws IOException {
        NvmFileUtils.copyRecursively(fromDirectory, toDirectory);
    }

    private final Map<Class<? extends ThirdPartyFileSystem>, ThirdPartyFileSystem> thirdPartyFileSystems =
            new HashMap<>();

    @Override
    public synchronized <K extends ThirdPartyFileSystem> K getOrCreateThirdPartyFileSystem(Class<K> clazz, Function<Class<K>, K> creator )
    {
        ThirdPartyFileSystem fileSystem = thirdPartyFileSystems.get( clazz );
        if (fileSystem == null)
        {
            thirdPartyFileSystems.put( clazz, fileSystem = creator.apply( clazz ) );
        }
        return clazz.cast( fileSystem );
    }


    @Override
    public void truncate(File path, long size) throws IOException {
        NvmFileUtils.truncateFile(path, size);
    }

    /*used not only here*/
    protected NvmStoreFileChannel getStoreFileChannel( NvmStoreFileChannel channel )
    {
        return new NvmStoreFileChannel( channel );
    }

}
