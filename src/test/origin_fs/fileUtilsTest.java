package test.origin_fs;

import org.neo4j.io.fs.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class fileUtilsTest {
    public static void testFileUtils(){
        //File dir = new File("Dir_test/File_test");
        try {
            //FileUtils.deleteRecursively(dir);
            //FileUtils.deletePathRecursively(dir.toPath());
            //FileUtils.moveFile(new File("Dir_test/File/File1"), new File("Dir_test/ex/F"));
            //FileUtils.truncateFile(new File("Dir_test/File/File1"), 20L);
            //FileChannel testchannel = new RandomAccessFile("Dir_test/File/File1","rw").getChannel();
            //FileUtils.renameFile(new File("Dir_test/File/File1"),new File("Dir_test/F2"));
            //FileUtils.renameFile(new File("Dir_test/File/File1"),new File("Dir_test/F2"));
            //testchannel.truncate(10L);
            //FileUtils.copyFile(new File("Dir_test/File/123"),new File("Dir_test/copy1/F"));
            //FileUtils.copyRecursively(new File("Dir_test/File/"),new File("Dir_test/copy/"));
            //FileUtils.writeToFile(new File("Dir_test/File/File1"),"Franxx",false);
            //BufferedReader bufread = FileUtils.newBufferedFileReader(new File("Dir_test/File/File1"), StandardCharsets.UTF_8);
            //System.out.println(bufread.readLine()+bufread.readLine());
            //FileUtils.path(new File("Dir_test/test/"),"0").mkdir();
            System.out.println(FileUtils.readTextFile(new File("Dir_test/File/File1"),StandardCharsets.UTF_8).length());
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
            String[] temp = "/1".substring(1).split("/");
            for(String key: temp){
                System.out.println(key);
            }
            String s="1\r\n2\r\n3\r\n";
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
            String line;
            StringBuffer strbuf=new StringBuffer();
            while ( (line = br.readLine()) != null ) {
                //if(!line.trim().equals(""))
                {
                    line="<br>"+line;
                    strbuf.append(line);
                }
            }
            System.out.println(strbuf.toString());
            System.out.println(s.length());


            System.out.println(new File("Dir_test/ex1").toPath().toFile().getName());
            System.out.println(new File("Dir_test/ex1").getParentFile().getName());
            System.out.println(new File("Dir_test/ex").getParentFile().getCanonicalPath());
            System.out.println(new File("Dir_test/ex").getParentFile().getCanonicalPath());
            //System.out.println(FileUtils.isEmptyDirectory(new File("Dir_test")));
            FileUtils.newFilePrintWriter(new File("Dir_test/File/File1"), StandardCharsets.UTF_8).append("Franxx\r").flush();
            //Channels.newOutputStream()
            //System.out.println(new File("").getCanonicalPath());
            //FileUtils.openAsOutputStream(new File("Dir_test/File/File1").toPath(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(FileUtils.deleteFile(new File("Dir_test/File5")));



    }


}
