package org.neo4j.io.nvmfs;

import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.types.BooleanField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;

import java.io.File;

import static lib.util.persistent.Util.persistent;

public class NvmFilDir  extends PersistentObject{
    private static final StringField LOCALINDEX = new StringField();
    private static final StringField FILECONTENT = new StringField();
    private static final BooleanField ISFILE = new BooleanField();
    private static final BooleanField ISDIRECTORY = new BooleanField();

    private static final ObjectType<NvmFilDir> TYPE = ObjectType.withFields(NvmFilDir.class, LOCALINDEX, FILECONTENT, ISFILE, ISDIRECTORY);

    public NvmFilDir(File file, boolean isFile, boolean isDirectory) {
        super(TYPE);
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
        setLocalIndex(nvmFilDir.getLocalIndex());
        setFileContent(nvmFilDir.getFileContent());
        setIsFile(nvmFilDir.getIsFile());
        setIsDirectory(nvmFilDir.getIsDirectory());
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

    public

}
