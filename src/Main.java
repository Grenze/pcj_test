import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] agrs) {



        //Utiltest.testAll();
        //NvmUtilsTest.testAll();
    }

    public static void testNormalFileUtils(){
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
    }
}

