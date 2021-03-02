package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

//创建一个线程
public class ServerHandler extends Thread{
    private Socket socket;
    public ServerHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        this.receiveRequest();
    }
    private void receiveRequest(){
        //负责接收请求  读取消息
        try {
            InputStream is = socket.getInputStream();//socket获取到的最基本的字节流
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));//将字节流转化成字符流  并包装成高级流  可以读取一行
            //读取消息   content?key=value&key=value...
            String contentAndParams = reader.readLine();
            //调用一个方法解析读取过来的信息
            this.parseContentAndParams(contentAndParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseContentAndParams(String contentAndParams) {
        //创建两个变量  存储请求的资源名  及  携带的参数
        String content = null;
        HashMap<String,String> paramsMap = null;
        //负责解析客户端发过来的字符串content?key=value&key=value...
        //先找寻?所在的位置
        int questionMarkIndex = contentAndParams.indexOf("?");
        if(questionMarkIndex!=-1){//判断是否携带了参数  如果?存在  说明有参数  ?不存在  说明没有参数
            //携带了参数  开始解析  截取?前面的信息-->请求资源名  ?后面的信息拆分存入集合里
            content = contentAndParams.substring(0,questionMarkIndex);
            paramsMap = new HashMap<String,String>();
            //处理?后面的参数  拆分存入map集合key=value&key=value...
            String params = contentAndParams.substring(questionMarkIndex+1);
            String[] keyAndValues = params.split("&");
            for(String keyAndValue:keyAndValues){
                String[] KV = keyAndValue.split("=");
                paramsMap.put(KV[0],KV[1]);
            }
        }else{//没有携带参数  请求发过来的信息就是完整的资源名
            content = contentAndParams;
        }
        //至此  将请求发送过来的字符串解析完毕   content  paramsMap
        //自己创建两个对象  一个是为了包含所有请求携带的信息
        //另一个是为了接收响应回来的结果  创建时是空对象
        //控制层Controller执行完毕  将对象填满当作参数
        HttpServletRequest request = new HttpServletRequest(content,paramsMap);
        HttpServletResponse response = new HttpServletResponse();//空的
        ServletController.findController(request,response);
        //上面这个方法执行完毕  真实的Controller里面的那个service方法执行完毕了
        //response对象中应该就有响应的信息了
        this.responseToBrowser(response);
    }

    private void responseToBrowser(HttpServletResponse response) {
        //将最终的响应信息写回浏览器
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            //我们写回浏览器时是写回一行  如果响应信息为多行呢
            //如何展示成多行  在服务器端我们无法做处理  因为总是调用println方法  每次写回一行
            //那就只能在浏览器展示时做处理了  我们在响应信息中规定一种特殊的符号作为换行符  例如<br>
            //在浏览器端进行解析  将<br>换成换行符进行展示
            out.println(response.getResponseContent());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
