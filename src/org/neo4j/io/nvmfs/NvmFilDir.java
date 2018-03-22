package org.neo4j.io.nvmfs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.types.BooleanField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;

import java.io.File;
import java.io.IOException;

import static lib.util.persistent.Util.persistent;

public class NvmFilDir  extends PersistentObject{
    private static final StringField LOCALINDEX = new StringField();
    private static final StringField FILECONTENT = new StringField();
    private static final BooleanField ISFILE = new BooleanField();
    private static final BooleanField ISDIRECTORY = new BooleanField();

    private static final ObjectType<NvmFilDir> TYPE = ObjectType.withFields(NvmFilDir.class, LOCALINDEX, FILECONTENT, ISFILE, ISDIRECTORY);

    public NvmFilDir(File file, boolean isFile, boolean isDirectory) throws IOException{
        super(TYPE);
        setLocalIndex();
        setFileContent();
        setIsFile(isFile);
        setIsDirectory(isDirectory);
        ObjectDirectory.put(file.getCanonicalPath(),this);
    }

    private NvmFilDir(ObjectPointer<NvmFilDir> p){
        super(p);
    }

    //private NvmFilDir(NvmFilDir nvmFilDir){

    //}

    private void setLocalIndex(){
        setObjectField(LOCALINDEX, persistent(""));
    }

    private String getLocalIndex(){
        return getObjectField(LOCALINDEX).toString();
    }

    private void setFileContent(){
        setObjectField(FILECONTENT, persistent(""));
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


}
