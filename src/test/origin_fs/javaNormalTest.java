package test.origin_fs;

import java.util.HashSet;

public class javaNormalTest {
    public static void testJavaNormal(){
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("F1");
        hashSet.add("A1");
        hashSet.add("B2");
        hashSet.add("C3");
        hashSet.remove("B2");
        for(String i: hashSet){
            System.out.println(i);
        }
    }
}
