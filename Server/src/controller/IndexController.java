package controller;

import server.HttpServlet;
import server.HttpServletRequest;
import server.HttpServletResponse;

public class IndexController extends HttpServlet {
        /**控制层做的三件事
        //1.接收请求发送过来携带的参数
        //2.找到某一个业务层的方法  让业务层做事
        //3.将最终业务层执行完毕的结果交还给服务器  让服务器写回给浏览器
        //第三步需要一个最终结果  我们想到了返回值
        //那么能不能不用返回值就能得到结果
        //我们可以将结果装到一个数组或集合中  这样就不用设计返回值了*/
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        //1.将响应信息放在文件中
        //2.我们现在不用自己写响应信息  需要告知response对象  我的信息在一个文件里  让它去读
        response.sendRedirect("index.view");
    }
}
