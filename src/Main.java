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
    public static final ObjectType<Engineer> TYPE = Employee.TYPE.extendWith(Engineer.class, PROJECT);  //extend type

    public Engineer(int id, String name, String company, String project){
        this (TYPE, id, name, company, project);
    }

    protected  Engineer(ObjectType<? extends Engineer> type, int id, String name, String company, String project){
        super(type, id, name, company);
        setProject(project);
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


}





public class Main {
    public static void main(String[] agrs)
    {
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

        //assert(strings1.get(1).equals(persistent("world")));

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


        //Engineer eng0 = new Engineer(0,"ghost","clear","umbrella");
        //ObjectDirectory.put("eng0",eng0);

        Engineer eng = ObjectDirectory.get("eng0",Engineer.class);
        System.out.println(eng.getName()+eng.getProject());



    }
}
