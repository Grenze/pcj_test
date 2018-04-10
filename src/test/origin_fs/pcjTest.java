package test.origin_fs;

import lib.util.persistent.ObjectDirectory;
import lib.util.persistent.ObjectPointer;
import lib.util.persistent.PersistentObject;
import lib.util.persistent.types.LongField;
import lib.util.persistent.types.ObjectType;
import lib.util.persistent.types.StringField;

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

    public synchronized void  setName(String name){
        setObjectField(NAME, persistent(name));
    }

    public String getName(){
        return getObjectField(NAME).toString();
    }

    public void  setCompany(String company){
        setObjectField(COMPANY, persistent(company));
    }

    public String getCompany(){
        return getObjectField(COMPANY).toString();
    }

}



class Thread1 extends Thread{

    public void run() {
        ObjectDirectory.get("1",Employee.class).setName("thread1");
        System.out.println(ObjectDirectory.get("1", Employee.class).getName());

    }
}

class Thread2 extends Thread{

    public void run() {
        ObjectDirectory.get("1",Employee.class).setName("thread2");
        System.out.println(ObjectDirectory.get("1", Employee.class).getName());

    }

}


public class pcjTest {
    public static void testpcj(){

        /*Transaction.run(() -> {

        });*/


        Employee stuff = new Employee(0, "lin", "umbrella");
        ObjectDirectory.put("1",stuff);
        ObjectDirectory.get("1",Employee.class).setName("main");
        System.out.println(ObjectDirectory.get("1", Employee.class).getName());


        Thread1 th1 = new Thread1();
        Thread2 th2 = new Thread2();
        th1.start();
        th2.start();

        System.out.println(ObjectDirectory.get("1", Employee.class).getName());


    }




}
