package server;

import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class ServerFileReader {

    //这个类的目的是为了在服务器启动的时候
    //读取server.properties的配置文件---->port端口号

    private static HashMap<String,String> map = new HashMap<String,String>();

    //静态块  类加载的时候先加载  读取配置文件server.properties
    //将文件中的port端口号存入集合  一个缓存机制
    static {
        try {
            Properties pro = new Properties();
            pro.load(new FileReader("Server\\src\\server.properties"));
            Enumeration en = pro.propertyNames();
            while(en.hasMoreElements()){
                String key = (String)en.nextElement();//port
                String value = pro.getProperty(key);
                map.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getValue(String key){
        return map.get(key);
    }
}
