package server;

public abstract class HttpServlet {
    /**
     * 这个类的目的是为了定义一个规则
     * 定义Controller类中方法的名字必须统一为service   方法参数必须统一  request  response
     * 服务器找寻的时候就方便了
     */
    public abstract void service(HttpServletRequest request,HttpServletResponse response);
}
