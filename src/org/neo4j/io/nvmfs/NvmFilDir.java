package org.neo4j.io.nvmfs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.PersistentString;
import lib.util.persistent.types.BooleanField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static lib.util.persistent.Util.persistent;

public class NvmFilDir  extends PersistentObject{
    private static final StringField GLOBALID = new StringField();
    private static final StringField LOCALINDEX = new StringField();
    private static final StringField FILECONTENT = new StringField();
    private static final BooleanField ISFILE = new BooleanField();
    private static final BooleanField ISDIRECTORY = new BooleanField();

    private static final ObjectType<NvmFilDir> TYPE = ObjectType.withFields(NvmFilDir.class, GLOBALID, LOCALINDEX, FILECONTENT, ISFILE, ISDIRECTORY);
    //only this param about File is String, convert to canonicalPath before pass in
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



    /*above set/get method*/


    public int write(String str, int position){
        if(str.length() == 0 || position < 0){return 0;}
        String originContent = getFileContent();
        int offset = position - originContent.length();
        if(offset > 0){
            //offset should be filled with 00 space is 20h
            setFileContent(originContent + new String(new byte[offset])+ str);
        }
        else{
            if(position+str.length()<=originContent.length()){
                setFileContent(originContent.substring(0, position) + str + originContent.substring(position + str.length()));
            }
            else{
                setFileContent(originContent.substring(0, position) + str);
            }
        }
        return str.length();
    }

    public void write(String text, boolean append){
        if(append){
            write(text, getFileContent().length());
        }
        else{
            write(text, 0);
        }
    }

    public String readAll(){
        return getFileContent();
    }



    public void truncate(int size){
        String originContent = getFileContent();
        setFileContent(originContent.substring(0, size));
    }

    public String read(int length, int position){
        String originContent = getFileContent();
        if(position >= originContent.length() || position < 0 || length<=0 ){return "";}
        if(position+length<=originContent.length()){
            return originContent.substring(position, position+length);
        }
        else {
            return originContent.substring(position);
        }
    }

    public long getSize(){
        return getFileContent().length();
    }
    //remained to complete
    public void force(boolean metadata){
        if(ObjectDirectory.get(getGlobalId(), NvmFilDir.class)==null){
            ObjectDirectory.put(getGlobalId(), this);
        }
    }

    //"/"not used in file's name
    public void increaseLocalIndex(File newLocalSub){
        setLocalIndex(getLocalIndex()+"/"+newLocalSub.getName());
    }

    public void decreaseLocalIndex(File oldLocalSub){
        setLocalIndex(getLocalIndex().replace(("/"+oldLocalSub.getName()),""));
    }
    //remove the first "/"
    private String[] getSubList(){
        if(getLocalIndex().length()==0){
            return null;
        }
        return getLocalIndex().substring(1).split("/");
    }

    public void renameSelf(File src, File dst) {
        setGlobalId(convertFile(dst));
        NvmFilDir.putNvmFilDir(dst,NvmFilDir.removeNvmFilDir(src));
    }


    /*below are static methods*/
    //less IOException
    private static String convertFile(File file){
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean exists(File file)  {
        return (ObjectDirectory.get(convertFile(file), NvmFilDir.class)!=null);
    }


    public static NvmFilDir removeNvmFilDir(File file) {
        return ObjectDirectory.remove(convertFile(file), NvmFilDir.class);
    }

    public static NvmFilDir getNvmFilDir(File file) {
        return ObjectDirectory.get(convertFile(file), NvmFilDir.class);
    }

    public static void putNvmFilDir(File file, NvmFilDir nvmFilDir) {
        ObjectDirectory.put(convertFile(file), nvmFilDir);
    }

    public static void copyNvmFilDir(File src, File dst){
        NvmFilDir dstNvmFilDir = new NvmFilDir(NvmFilDir.getNvmFilDir(src));
        dstNvmFilDir.setGlobalId(convertFile(dst));
        NvmFilDir.putNvmFilDir(dst, dstNvmFilDir);
    }

    public static boolean isEmpty(File file)  {
        return ObjectDirectory.get(convertFile(file), NvmFilDir.class).getLocalIndex().length() == 0;
    }

    public static boolean isFile(File file) {
        return ObjectDirectory.get(convertFile(file), NvmFilDir.class) != null
                && ObjectDirectory.get(convertFile(file), NvmFilDir.class).getIsFile();
    }

    public static boolean isDirectory(File file) {
        return ObjectDirectory.get(convertFile(file), NvmFilDir.class) == null
                || ObjectDirectory.get(convertFile(file), NvmFilDir.class).getIsDirectory();
    }


    public static File[] listLocalFiles(File directory, FilenameFilter filter ) {
        String[] subs = NvmFilDir.getNvmFilDir(directory).getSubList();
        if(subs == null){
            return null;
        }
        ArrayList temp = new ArrayList();
        for(String sub: subs){
            if(filter == null || filter.accept(directory, sub)){
                temp.add(new File(directory, sub));
            }
        }
        return (File[])temp.toArray(new File[temp.size()]);
    }


    //ObjectDirectory's HashMap's key format: mark + class.getName()
    public static List<String> getNvmFilDirDirectory(String reMove){
        List<String> keyList = new ArrayList<>();
        String nvmClass = NvmFilDir.class.getName();
        for(PersistentString key: ObjectDirectory.getDirectory()){
            if(key.toString().endsWith(nvmClass)){
                keyList.add(key.toString().replace(nvmClass,""));
            }
        }
        keyList.remove(reMove);//remove safely
        return keyList;
    }

    //Print ObjectDirectory's HashMap's key Set
    public static void PrintDirectory(Class cls){
        List<PersistentString> keyList = new ArrayList<>(ObjectDirectory.getDirectory());
        keyList.sort(Comparator.comparing(s -> s.toString().replace(cls.getName(), "")));
        System.out.println("\n------"+cls.getName()+"------\n");
        for(PersistentString key: keyList){
            System.out.println(key.toString().replace(cls.getName(),""));
        }
        System.out.println("\n------"+cls.getName()+"------\n");
    }




}
