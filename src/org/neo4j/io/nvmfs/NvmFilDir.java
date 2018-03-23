package org.neo4j.io.nvmfs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.PersistentString;
import lib.util.persistent.types.BooleanField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static lib.util.persistent.Util.persistent;

public class NvmFilDir  extends PersistentObject{
    private static final StringField GLOBALID = new StringField();
    private static final StringField LOCALINDEX = new StringField();
    private static final StringField FILECONTENT = new StringField();
    private static final BooleanField ISFILE = new BooleanField();
    private static final BooleanField ISDIRECTORY = new BooleanField();

    private static final ObjectType<NvmFilDir> TYPE = ObjectType.withFields(NvmFilDir.class, GLOBALID, LOCALINDEX, FILECONTENT, ISFILE, ISDIRECTORY);

    public NvmFilDir(String uniqueFileName, boolean isFile, boolean isDirectory){
        super(TYPE);
        setGlobalId(uniqueFileName);
        setLocalIndex("");
        setFileContent("");
        setIsFile(isFile);
        setIsDirectory(isDirectory);
    }

    private NvmFilDir(ObjectPointer<NvmFilDir> p){
        super(p);
    }
    //copy
    public NvmFilDir(NvmFilDir nvmFilDir){
        super(TYPE);
        setGlobalId(nvmFilDir.getGlobalId());
        setLocalIndex(nvmFilDir.getLocalIndex());
        setFileContent(nvmFilDir.getFileContent());
        setIsFile(nvmFilDir.getIsFile());
        setIsDirectory(nvmFilDir.getIsDirectory());
    }

    private void setGlobalId(String globalId){
        setObjectField(GLOBALID, persistent(globalId));
    }

    private String getGlobalId(){
        return getObjectField(GLOBALID).toString();
    }


    private void setLocalIndex(String localIndex){
        setObjectField(LOCALINDEX, persistent(localIndex));
    }

    private String getLocalIndex(){
        return getObjectField(LOCALINDEX).toString();
    }


    private void setFileContent(String fileContent){
        setObjectField(FILECONTENT, persistent(fileContent));
    }

    private String getFileContent(){
        return getObjectField(FILECONTENT).toString();
    }


    private void setIsFile(boolean isFile){
        setBooleanField(ISFILE, isFile);
    }

    private boolean getIsFile(){
        return getBooleanField(ISFILE);
    }


    private void setIsDirectory(boolean isDirectory){
        setBooleanField(ISDIRECTORY, isDirectory);
    }

    private boolean getIsDirectory(){
        return getBooleanField(ISDIRECTORY);
    }


    public int write(String src, int position){
        if(src.length() == 0 || position < 0){return 0;}
        String originContent = getFileContent();
        long offset = position - originContent.length();
        if(offset > 0){
            setFileContent(String.format("%1$-"+offset+"s", originContent) + src);
        }
        else{
            if(position+src.length()<=originContent.length()){
                setFileContent(originContent.substring(0, position) + src + originContent.substring(position + src.length()));
            }
            else{
                setFileContent(originContent.substring(0, position) + src);
            }
        }
        return src.length();
    }

    public void truncate(int size){
        String originContent = getFileContent();
        setFileContent(originContent.substring(0, size));
    }

    public String read(int length, int position){
        String originContent = getFileContent();
        if(position >= originContent.length() || position < 0 || length <= 0){return "";}
        if(position+length<=originContent.length()){
            return originContent.substring(position, position+length);
        }
        else {
            return originContent.substring(position);
        }
    }

    public long getSize(){
        return getFileContent().toString().length();
    }
    //remained to complete
    public void force(boolean metadata){
        if(ObjectDirectory.get(getGlobalId(), NvmFilDir.class)==null){
            ObjectDirectory.put(getGlobalId(), this);
        }
    }

    //"/"not used in file's name
    public void increaseLocalIndex(String newLocalSub){
        setLocalIndex(getLocalIndex()+"/"+newLocalSub);
    }

    public void decreaseLocalIndex(String oldLocalSub){
        setLocalIndex(getLocalIndex().replace(("/"+oldLocalSub),""));
    }




    /*below are static methods*/


    public static boolean exists(File file) throws IOException {
        return ObjectDirectory.get(file.getCanonicalPath(), NvmFilDir.class)!=null;
    }


    //ObjectDirectory's HashMap's key format: mark + class.getName()
    public static Set<String> getNvmFilDirDirectory(){
        Set<String> nvmFilDirDirectory = null;
        String nvmClass = NvmFilDir.class.getName();
        for(PersistentString key: ObjectDirectory.getDirectory()){
            if(key.toString().endsWith(nvmClass)){
                nvmFilDirDirectory.add(key.toString().replace(nvmClass,""));
            }
        }
        return nvmFilDirDirectory;
    }

    public static void removeNvmFilDir(File file) throws IOException{
        removeNvmFilDir(file.getCanonicalPath());
    }

    public static void removeNvmFilDir(String fileString) {
        ObjectDirectory.remove(fileString, NvmFilDir.class);
    }

    public static NvmFilDir getNvmFilDir(File file) throws IOException{
        return ObjectDirectory.get(file.getCanonicalPath(), NvmFilDir.class);
    }

    public static boolean isEmpty(File file) throws IOException {
        return ObjectDirectory.get(file.getCanonicalPath(), NvmFilDir.class).getLocalIndex().length() == 0;
    }


}
