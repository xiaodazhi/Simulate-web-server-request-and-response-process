package server;

import java.io.*;

public class HttpServletResponse {

    //自己创建的一个对象
    //目的是为了存储响应信息  就是一个字符串

    private StringBuilder responseContent = new StringBuilder();
    public void write(String str){
        this.responseContent.append(str);
    }
    //第二种方式  让response读取一个文件  文件中的内容是响应信息
    public void sendRedirect(String path){
        File file = new File("Server//src//file//"+path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String value = reader.readLine();
            while(value!=null){
                this.responseContent.append(value);
                value = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponseContent(){
        return this.responseContent.toString();
    }
}
