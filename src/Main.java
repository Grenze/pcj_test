import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentHashMap;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.types.LongField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.io.fs.StoreChannel;
import org.neo4j.io.fs.StoreFileChannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;
import static lib.util.persistent.Util.persistent;
/*
final class Employee extends PersistentObject{
    private static final LongField ID = new LongField();
    private static final StringField NAME = new StringField();
    private static final ObjectType<Employee> TYPE = ObjectType.withFields(Employee.class, ID, NAME);

    public Employee(int id, String name){
        super(TYPE);    //base class accept TYPE for persistent fields specification
        setLongField(ID, id);   //setter in PersistentObject base class
        setName(name);  //setObjectName(NAME, name) works too
    }

    private Employee(ObjectPointer<Employee> p){    //Required boilerplate "reconstructor" passes pointer
        super(p);                                   //to base class. This is a non-allocating reconstruction path
    }                                              //used e.g. after restart

    public void setName(String name){     //limiting the use of Field objects to constructors and accessors
        setObjectField(NAME, persistent(name));                 //hides meta-programming aspect and can make maintenance easier
    }
    public String getName(){
        return getObjectField(NAME).toString();
    }

    public long getId(){
        return getLongField(ID);
    }
}
*/

interface people{
    void setName(String name);
}

interface shakaijin{
    void setCompany(String company);
}

class myfile extends File{
    myfile(String var1){
        super(var1);
    }
}


class Employee extends PersistentObject implements people, shakaijin{
    private static final LongField ID = new LongField();
    private static final StringField NAME = new StringField();
    private static final StringField COMPANY = new StringField();
    public static final ObjectType<Employee> TYPE = ObjectType.withFields(Employee.class, ID, NAME, COMPANY);//now public

    public Employee(int id, String name, String company){
        this (TYPE, id, name, company);
    }

    //add a subclassing constructor that forwards type argument to base class
    protected Employee(ObjectType<? extends  Employee> type, int id, String name, String company) {
        super(type);
        setLongField(ID, id);
        setName(name);
        setCompany(company);
    }

    protected Employee(ObjectPointer<? extends Employee> p){     //type bounds allow subtypes
        super(p);
    }

    public long getId(){
        return getLongField(ID);
    }

    public void setName(String name){
        setObjectField(NAME, persistent(name));
    }

    public String getName(){
        return getObjectField(NAME).toString();
    }

    public void setCompany(String company){
        setObjectField(COMPANY, persistent(company));
    }

    public String getCompany(){
        return getObjectField(COMPANY).toString();
    }

}

class Engineer extends  Employee{
    private static final StringField PROJECT = new StringField();   //new field
    public static final StringField GROUPNAME = new StringField();
    //don't forget extendWith or will out of index
    public static final ObjectType<Engineer> TYPE = Employee.TYPE.extendWith(Engineer.class, PROJECT, GROUPNAME);  //extend type

    public Engineer(int id, String name, String company, String project){
        this (TYPE, id, name, company, project);
    }

    protected  Engineer(ObjectType<? extends Engineer> type, int id, String name, String company, String project){
        super(type, id, name, company);
        setProject(project);
        setGroupname("root");
        ObjectDirectory.put(name,this);
    }

    protected  Engineer(ObjectPointer<? extends Engineer> p){
        super(p);
    }


    public void setProject(String project){
        setObjectField(PROJECT, persistent(project));
    }

    public String getProject(){
        return getObjectField(PROJECT).toString();
    }

    public void changeProject(String project){
        setObjectField(PROJECT, persistent(project));
    }


    public void setGroupname(String groupname){setObjectField(GROUPNAME, persistent(groupname));}

    public String getGroupname(){return getObjectField(GROUPNAME).toString();}

    public void changGroupname(String groupname){setObjectField(GROUPNAME, persistent(groupname));}

    public Engineer createGroup(String groupname){
        setGroupname(groupname);
        PersistentHashMap group = new PersistentHashMap();

        Engineer var1 = new Engineer(1,"lin",getCompany(),getProject());
        group.put(persistent("lin"),var1);
        ObjectDirectory.put(getGroupname(),group);
        var1 = (Engineer) ObjectDirectory.get(getGroupname(),PersistentHashMap.class).get(persistent("lin"));
        System.out.println(var1.getGroupname());
        return var1;
    }

}



class StoreNvmFileChannel implements StoreChannel{

    @Override
    public FileLock tryLock() {
        return null;
    }

    @Override
    public int write(ByteBuffer src, long position) {
        return 0;
    }

    @Override
    public void writeAll(ByteBuffer src, long position) {

    }

    @Override
    public void writeAll(ByteBuffer src) {

    }

    @Override
    public int read(ByteBuffer dst, long position) {
        return 0;
    }

    @Override
    public void force(boolean metaData) {

    }

    @Override
    public int read(ByteBuffer byteBuffer) {
        return 0;
    }

    @Override
    public int write(ByteBuffer byteBuffer) {
        return 0;
    }

    @Override
    public long position() {
        return 0;
    }

    @Override
    public StoreChannel position(long newPosition) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public StoreChannel truncate(long size) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public long write(ByteBuffer[] byteBuffers, int i, int i1) {
        return 0;
    }

    @Override
    public long write(ByteBuffer[] byteBuffers) {
        return 0;
    }

    @Override
    public long read(ByteBuffer[] byteBuffers, int i, int i1) {
        return 0;
    }

    @Override
    public long read(ByteBuffer[] byteBuffers) {
        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }
}







public class Main {
    public static void main(String[] agrs)
    {
        //testStoreFileChannel();
        testpcj();//chain create directories or files
        //testFileUtils();
        //testDefaultFileSystemAbstraction();
        //testJavaNormal();






    }

    public static void testJavaNormal(){

    }

    public static void testDefaultFileSystemAbstraction(){
        DefaultFileSystemAbstraction fs = new DefaultFileSystemAbstraction();
       for(File i : fs.listFiles(new File("Dir_test1"))){
           System.out.println(i.toString());
       }
    }

    public static void testFileUtils(){
        //File dir = new File("Dir_test/File_test");
        try {
            //FileUtils.deleteRecursively(dir);
            //FileUtils.deletePathRecursively(dir.toPath());
            //FileUtils.moveFileToDirectory(new File("Dir_test/test/File1"), new File("Dir_test/File"));
            //FileUtils.truncateFile(new File("Dir_test/File/File1"), 20L);
            //FileChannel testchannel = new RandomAccessFile("Dir_test/File/File1","rw").getChannel();
            //FileUtils.renameFile(new File("Dir_test/File/File1"),new File("Dir_test/F"));
            //testchannel.truncate(10L);
            //FileUtils.copyFile(new File("Dir_test/File/File1"),new File("Dir_test/copy/F"));
            //FileUtils.copyRecursively(new File("Dir_test/File/"),new File("Dir_test/copy/"));
            //FileUtils.writeToFile(new File("Dir_test/File/File1"),"Franxx",false);
            //BufferedReader bufread = FileUtils.newBufferedFileReader(new File("Dir_test/File/File1"), StandardCharsets.UTF_8);
            //System.out.println(bufread.readLine()+bufread.readLine());
            //FileUtils.path(new File("Dir_test/test/"),"0").mkdir();
            //System.out.println(FileUtils.readTextFile(new File("Dir_test/File/File1"),StandardCharsets.UTF_8));
            //System.out.println(FileUtils.relativePath(new File("Dir_test"),new File("Dir_test/File/F")));

            /*
            * getPath() and getParent() return the path when constructed
            * getAbsolutePath() return current path + constructed path
            * getCanonicalPath() return path unique without any . or .. or other symbol
            * */
            //System.out.println(new File("./Dir_test").getCanonicalPath());
            /*"\/" at the end will be ignored by new File()*/
            //System.out.println(new File("Dir_test/"));

            //FileChannel testchannel = new RandomAccessFile("Dir_test/File/File1","rw").getChannel();
            //ByteBuffer testbuf = ByteBuffer.allocate(30);
            //testbuf.put("Franx----\n".getBytes());
            //testbuf.flip();
            //FileUtils.writeAll(testchannel,testbuf);
            /*between two append if there is a modify executed not by jvm,
            * it will start at a new line
            * */

            //System.out.println(FileUtils.isEmptyDirectory(new File("Dir_test")));
            FileUtils.newFilePrintWriter(new File("Dir_test/File/File1"),StandardCharsets.UTF_8).append("Franxx ").flush();
            //Channels.newOutputStream()
            System.out.println(new File("").getCanonicalPath());
            //FileUtils.openAsOutputStream(new File("Dir_test/File/File1").toPath(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(FileUtils.deleteFile(new File("Dir_test/File5")));



    }



    public static Reader nvmOpenAsReader(File fileName, Charset charset) {
        return new Reader() {
            @Override
            public int read(char[] chars, int i, int i1) {
                return 0;
            }

            @Override
            public void close() {

            }
        };
    }

    /*with nvm FileChannel support I/O put stream*/
    public static InputStream nvmOpenAsInputStream(Path path) {
        return new InputStream() {
            @Override
            public int read() {
                //
                return 0;
            }
        };
    }

    public static OutputStream nvmOpenAsOutputStream(Path path, boolean append) {
        OpenOption[] options;
        if ( append )
        {
            options = new OpenOption[] {CREATE, WRITE, APPEND};
        }
        else
        {
            options = new OpenOption[] {CREATE, WRITE};
        }
        return new OutputStream() {

            @Override
            public void write(int intToWrite) {
                //this.write(intToWrite);
                //file.write or nvmchannel.write
            }
            @Override
            public void write(byte[] bytesToWrite, int offset, int length) {
                if(bytesToWrite == null){
                    throw new NullPointerException();
                }else if(offset >= 0 && offset <= bytesToWrite.length && length >= 0 && offset+length <= bytesToWrite.length){
                    //file.write or nvmchannel.write
                }else{
                    throw new IndexOutOfBoundsException();
                }
            }
        };
    }
    public static void testpcj(){
        /*System.out.println(System.getProperty("Java.library.path"));
        System.out.println("HelloWorld!");
        PersistentIntArray a = new PersistentIntArray(1024);
        a.set(0, 123);
        a = null;
        PersistentArray<PersistentString> strings = new PersistentArray<>(100);
        ObjectDirectory.put("data",strings);
        strings.set(0, new PersistentString("hello"));
        strings.set(1, persistent("world"));*/
        //ObjectDirectory.initialize();
        //ObjectCache.clear();
        //ObjectDirectory.remove("data",PersistentArray.class);
        //PersistentArray<PersistentString> strings1 = ObjectDirectory.get("data", PersistentArray.class);

        //assert(strings1.get(1).equals(persistent("wByteBufferorld")));

        //System.out.println(strings1.get(1));



        //Employee stuff = new Employee(0, "lin");

        //ObjectDirectory.put("lin",stuff);
        //ObjectDirectory.put("kan",stuff);
        //ObjectDirectory.initialize();
        //ObjectDirectory.remove("lin",Employee.class);

        //Employee stuff = ObjectDirectory.get("lin",Employee.class);
        //stuff.setName("kan");
        //Employee stuff1 = ObjectDirectory.get("hello",Employee.class);
        //System.out.println(stuff1.getName());
        //Employee stuff2 = new Employee(1,"hello");
        //ObjectDirectory.put("hello",stuff2);
        //System.out.println(stuff2.getName());

        /*Transaction.run(() -> {

        });*/


        //System.out.println(stuff.getId()+stuff.getName());

        /*attention!
        * the current directory should know direct name of files and directories in current directory
        * (these information should be included in it)
        * also it can find indirect name with path prefix of files and directories of all the
        * directories and files including the current layer
        *(these information should be got in ObjectDirectory)
        *
        *
        *
        *
        *
        * */
        Engineer eng0 = new Engineer(0,"ghost","umbrella","clear");
        //Engineer eng1 = new Engineer(1,"heaven","sun","fill");
        ObjectDirectory.put("1",eng0);
        ObjectDirectory.put("2",eng0);

        ObjectDirectory.remove("1",Engineer.class);
        Engineer eng2 = ObjectDirectory.get("2",Engineer.class);
        System.out.println(eng2.getName());
        //System.out.println(ObjectDirectory.get("123",Engineer.class)==null);//true


        //eng0.createGroup("littlebuster").createGroup("tinybuster");
        //Engineer eng1 = (Engineer) ObjectDirectory.get("tinybuster",PersistentHashMap.class).get(persistent("lin"));
        //System.out.println(eng1.getGroupname());

        //ObjectDirectory.put("eng0",eng0);
        //System.out.println(ObjectDirectory.get("root",PersistentHashMap.class).get(persistent(0)));

        //Engineer eng = ObjectDirectory.get("eng0",Engineer.class);
        //System.out.println(eng.getId()+" "+eng.getName()+" "+eng.getCompany()+" "+eng.getProject());

        //PersistentUUID a = PersistentUUID.randomUUID();
        //PersistentHashMap a =new PersistentHashMap();
        //a.put(persistent(0),persistent("bingo"));
        //ObjectDirectory.put("S",a);
        //PersistentHashMap<PersistentObject,PersistentString> a = ObjectDirectory.get("S",PersistentHashMap.class);
        //System.out.print(a.get(persistent(0)));
    }

    public static void testStoreFileChannel (){
        ByteBuffer buf0 = ByteBuffer.allocate(100);
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer[] bufs = new ByteBuffer[2];
        bufs[0] = buf0;
        bufs[1] = buf1;
        String newData0 = "New String to write to file..."+System.currentTimeMillis();
        String newData1 = "Franxx String to write to file..."+System.currentTimeMillis();
        //bufs[0].put(newData0.getBytes());
        //buf0.flip();
        //buf1.put(newData1.getBytes());
        //bufs[1].flip();
        //System.out.println(bufs.length);

        try {
            FileChannel testchannel = new RandomAccessFile("text","rw").getChannel();
            StoreFileChannel neo4jchannel = new StoreFileChannel(testchannel);
            System.out.println(neo4jchannel.equals(new StoreFileChannel(neo4jchannel)));
            /*while(buf.hasRemaining()){
                try {
                    testchannel.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            System.out.println(neo4jchannel.size());
            //System.out.println(neo4jchannel.isOpen());
            //System.out.println(neo4jchannel.position());
            neo4jchannel.position(30);
            //System.out.println(neo4jchannel.position());
            neo4jchannel.read(bufs);
            buf0.flip();
            buf1.flip();
            printBuffer(buf0);
            printBuffer(buf1);

            //neo4jchannel.truncate(15);
            System.out.println(neo4jchannel.position());
            //System.out.println(neo4jchannel.size());
            neo4jchannel.flush();
            neo4jchannel.close();
            //System.out.println(neo4jchannel.isOpen());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printBuffer(ByteBuffer buf){
        while (buf.hasRemaining()){
            System.out.print((char)buf.get());
        }
        System.out.println();
    }
}

