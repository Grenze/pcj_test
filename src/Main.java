import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] agrs) {

        try {
            //FileUtils.copyDirectory used in AbstractInProcessServerBuilder
            FileUtils.copyDirectory(new File("12"), new File("23"));
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally {

        }
        List<String> s = new ArrayList<>();
        s.add("1");
        s.add("12");
        s.remove("1");
        System.out.println(s);

        //Utiltest.testAll();
        //NvmUtilsTest.testAll();
    }
}

