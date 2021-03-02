此项目是封装一个简单的tomcat服务器以及模拟浏览器与服务器之间请求与响应的过程
使用控制台来模拟浏览器与服务器之间请求与响应的过程

使用到了Java中的集合  IO  反射  异常  Socket等知识
实现此项目的目的是为了更加清晰的理解请求与响应的过程



首先

浏览器发送请求到服务器

URL大致如下

```java
localhost:9999/index
```



服务器端定义一个抽象类HttpServlet  其中定义抽象方法service()

这个类的作用是制定规则  所有的Controller类都必须继承继承这个类并重写方法service()

并定义两个类  一个是HttpServletRequest  一个是HttpServletResponse

HttpServletRequest对象的作用是存储浏览器发送的所有请求信息  作为Controller的参数  Controller可以获取到其中的请求信息

HttpServletResponse对象的作用是存储服务器响应回浏览器的所有响应信息(就是一个String)



并定义一个线程类ServerHandler  负责接收请求并解析请求信息  还负责将最终的响应信息写回浏览器



定义一个类ServletController

这个类的作用是读取web.properties文件(读取增加了缓存机制 即一次性读取  装入集合中)

web.properties文件中主要记录了请求名与对应Controller类名的关系

例如

```properties
index=controller.IndexController
login=controller.LoginController
```

通过解析出的请求信息及web.properties文件信息  通过反射找到对应的Controller类  并让其中的方法执行  执行的结果装入Response对象中响应回浏览器  这是一种响应回去的方式(直接响应回浏览器)

还提供了一种响应方式

读取文件(这里我将文件名定义为index.view)并将文件信息响应回去

```properties
*****************<br>
****银行系统******<br>
*****************<br>
<form action="login" method="">
请输入账号<input name="name" value=""><br>
请输入密码<input name="password" value=""><br>
```

这里我解析了<input>标签做成了控制台输入

浏览器接收到响应信息并进行解析  展示



