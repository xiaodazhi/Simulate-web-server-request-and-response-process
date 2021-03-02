package browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Browser {
    private Scanner input = new Scanner(System.in);
    private Socket socket;
    private String ip;
    private int port;

    //模拟打开浏览器窗口
    public void openBrowser(){
        //输入一个URL:统一资源定位符  即就是一个网址
        //URL是具有一定格式的
        //协议名IP地址:端口/文件夹名/资源名?key=value&key=value
        //例: localhost:9999/index?name=zzt
        System.out.println("URL:");
        String url = input.nextLine();
        this.parseURL(url);
    }

    //设计一个方法  负责解析url字符串
    private void parseURL(String url){
        //找寻:和第一个/所在的位置
        int colonIndex = url.indexOf(":");
        int slashIndex = url.indexOf("/");
        //获取IP  port  contentAndParams
        ip = url.substring(0,colonIndex);
        port = Integer.parseInt(url.substring(colonIndex+1,slashIndex));
        String contentAndParams = url.substring(slashIndex+1);
        this.createSocketAndSendRequest(ip,port,contentAndParams);
    }

    //设计一个方法  创建一个Socket  将contentAndParams发送给服务器
    private void createSocketAndSendRequest(String ip,int port,String contentAndParams){
        try {
            //通过IP和port创建一个socket
            socket = new Socket(ip,port);
            //将contentAndParams发送出去  给服务器
            //创建一个输出流PrintWriter  可以发送一行
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(contentAndParams);
            out.flush();
            //浏览器等待响应信息
            this.receiveResponseContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设计一个方法  负责接收服务器回写的响应信息
    private void receiveResponseContent(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseContent = reader.readLine();
            //解析响应信息并展示
            this.parseResponseContentAndShow(responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设计一个方法  负责解析服务器回写的响应信息并展示
    private void parseResponseContentAndShow(String responseContent){
        //创建两个新的变量  用于存储新一次的请求和参数
        String content = null;
        HashMap<String,String> paramsMap = null;
        while(true){
            //解析一个<br>标签
            responseContent = responseContent.replace("<br>","\r\n");
            //解析其他的标签  我想在响应信息中加一些标签  例如<input>  浏览器看到这个标签  就会将这个标签变成一个输入  输入用户名或密码等
            int lessThanIndex = responseContent.indexOf("<");
            int greatThanIndex = responseContent.indexOf(">");
            //如果<>成对出现  证明存在一个有意义的标签
            if(lessThanIndex!=-1 && greatThanIndex!=-1 && lessThanIndex<greatThanIndex){
                //<前面的正常显示  <>以内的全部换成一个动作  >以后的正常显示
                System.out.println(responseContent.substring(0,lessThanIndex));
                //分析标签是什么类型  做相应的处理
                String tag = responseContent.substring(lessThanIndex,greatThanIndex+1);
                if(tag.contains("input")){
                    String value = input.nextLine();
                    if(paramsMap==null){
                        paramsMap = new HashMap<String,String>();
                    }//<input name="" value="">
                    //按照空格进行拆分 <input    name=""    value="">
                    String[] keyAndValues = tag.split(" ");
                    for(String keyAndValue:keyAndValues){//进行循环判断
                        if(keyAndValue.contains("=")){//如果当前的一组中包含有等号  证明是正常的参数
                            //按照=进行拆分  即得到  name   ""   value   ""  四个值
                            String[] KV = keyAndValue.split("=");
                            if("name".equals(KV[0])){
                                //将name后的""内的属性值截出来  作为paramsMap的键存进去  输入的value作为值存进去
                                paramsMap.put(KV[1].substring(1,KV[1].length()-1),value);
                            }
                        }
                    }

                }else if(tag.contains("form")){//<form action="login" method="">  如果是一个<form>表单标签  就说明要发送请求了  我们要的是action的属性值
                    String[] keyAndValues = tag.split(" ");
                    for(String keyAndValue:keyAndValues){//进行循环判断
                        if(keyAndValue.contains("=")){//如果当前的一组中包含有等号  证明是正常的参数
                            //按照=进行拆分  即得到  name   ""   value   ""  四个值
                            String[] KV = keyAndValue.split("=");
                            if("action".equals(KV[0])){
                                //产生一个新的请求
                                content = KV[1].substring(1,KV[1].length()-1);
                            }
                        }
                    }
                }
                responseContent = responseContent.substring(greatThanIndex+1);
            }else{//如果符号不成对  证明不存在其他标签
                //则直接输出全部的内容
                System.out.println(responseContent);
                break;
            }
        }
        //至此将所有的响应信息解析完毕
        //如果标签中遇到了<form>表示我还有一次新的请求
        this.sendNewRequest(content,paramsMap);
    }

    //向服务器发送一个新的请求
    private void sendNewRequest(String content,HashMap<String,String> paramsMap){
        if(content!=null){//证明遇到了一个<form>标签 还需要发送下一次请求 因为上述判断  只有是<form>标签  content中才能被存入值
            StringBuilder url = new StringBuilder(ip);
            //将新的请求拼串
            url.append(":");
            url.append(port);
            url.append("/");
            url.append(content);
            if(paramsMap!=null){//证明新的请求还有参数
                url.append("?");
                //进行迭代遍历
                Iterator<String> it = paramsMap.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    String value = paramsMap.get(key);
                    url.append(key);
                    url.append("=");
                    url.append(value);
                    url.append("&");
                }
                //循环执行完毕后  最终多了一个&符号  将其删除
                url.delete(url.length()-1,url.length());
            }
            //此时我们已经将新的请求拼串完毕  我们开始给服务器发送请求
            this.parseURL(url.toString());
        }
    }
}
