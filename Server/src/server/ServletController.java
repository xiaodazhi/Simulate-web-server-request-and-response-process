package server;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class ServletController {
    //添加一个缓存  用来存储web.properties配置文件中的信息(一个请求的名字=真实Controller类名)
    private static HashMap<String,String> controllerNameMap = new HashMap<>();
    //添加一个集合  这个集合的目的是为了存储被管理的所有Controller类对象
    private static HashMap<String,HttpServlet> controllerObjectMap = new HashMap<>();
    //延迟加载对象的方式
    //创建一个静态块  在当前类加载的时候将配置文件中的所有信息读取出来存入缓存集合中
    static {
        try {
            Properties pro = new Properties();
            //加载一个输入流
            pro.load(new FileReader("Server\\src\\web.properties"));
            //获取到key
            Enumeration en = pro.propertyNames();
            while(en.hasMoreElements()){//看看有没有下一个元素
                //获取到下一个key
                String content = (String)en.nextElement();
                //获取到全部的value
                String realControllerName = pro.getProperty(content);
                //将key和value装入集合中
                controllerNameMap.put(content,realControllerName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**类的特点  高内聚低耦合
     *这个类的目的是为了管理  findController这个方法的  有几个问题
     * 1.由于这个方法掺杂了反射  而ServerHandler这个类中都是I/O及解析字符串相关的操作
     * 所以这个方法放在里面显得格格不入  我们单独用一个类来装这个方法
     * 2.每一次找寻Controller类的时候都需要参考web.properties  我们知道  每次使用流去读取文件性能很慢
     * 所以我们想到增加一个缓存机制  先将文件中的内容一次性读出来  装到一个容器中  以后就不用读文件了  而是直接在这个容器中找
     * 3.每一个Controller类都是由findController方法来找寻的
     * 找到Controller类的目的是为了执行其中的方法
     * 我们想到让类中的方法都有一个统一的规则  便于查找和使用
     * 4.我们发现Controller类与之前的Service和Dao相似  只有方法执行  没有属性
     * 想让Controller类的对象变成单例模式
     */
    //找人做事(找到资源)---控制层   controller  action  servlet
    //localhost:9999/index?name=zzt
    //content=index map---{{name,zzt},{},{}}
    public static void findController(HttpServletRequest request,HttpServletResponse response){
        /**
         * 此时我们需要找到资源名对应的控制层类  让对应的控制层类做事
         * 如何找到对应的控制层类  利用反射  但利用反射需要类全名
         * 例  资源名index  控制层类名为IndexController
         * 我们只知道资源名  但不知道控制层类全名
         * 这时候就需要配置文件了 web.properties  这个配置文件中写的都是  资源名与对应控制层类名的对应关系
         * key  资源名   value  controller.对应控制层类名
         * 我们通过读配置文件就可以得到资源名对应的控制层类名  从而访问控制层类  让控制层类中的方法执行
         */
        //获取request对象中的请求名字
        String content = request.getContent();
        try {
            //先去controllerObjectMap中找寻需要的对象
            HttpServlet controllerObject = controllerObjectMap.get(content);
            //如果集合中不存在这个对象  证明这个对象之前没有使用过  所以我们通过反射去创建这个对象  再将这个对象装入集合中  以便以后的人使用
            if(controllerObject==null) {
                //参考配置文件(缓存)  找到真实类名
                String realControllerName = controllerNameMap.get(content);
                //看请求对应的真实类名是否存在  如果存在  说明这个类是第一次使用
                if (realControllerName != null) {
                    //反射获取资源名对应的控制层类
                    Class clazz = Class.forName(realControllerName);
                    controllerObject = (HttpServlet) clazz.newInstance();
                    //将新创建的对象放在集合内
                    controllerObjectMap.put(content, controllerObject);
                }
            }
            //反射找寻类中的方法  让方法执行
            Method serviceMethod = controllerObject.getClass().getMethod("service",HttpServletRequest.class,HttpServletResponse.class);
            serviceMethod.invoke(controllerObject,request,response);
        } catch (ClassNotFoundException e) {
            response.write("请求的"+content+"Controller不存在");
        } catch(NoSuchMethodException e){
            response.write("405  没有可以执行的方法");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
