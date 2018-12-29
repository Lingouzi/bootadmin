1. 技术架构:
springboot2 + freemarker + mongodb4, redis, websocket[stomp], activemq
开发工具:IDEA
部署环境:tomcat8 + nginx
拓展:activemq消息队列, redis缓存, shiro权限控制, websocket实现站内消息通知


2. 目录说明
所有代码在com.ly.bootadmin包下, BootadminApplication作为顶层,
关于springboot的错误页面定义,参考: http://tengj.top/2018/05/16/springboot13/
-------------------------------------------
java
    com.ly.bootadmin
        -activemq      消息队列的相关配置和使用demo
        -common        通用方法, 文件上传等
        -config        基础配置,基础设置类目录
        -error         全局异常控制
        -exception     异常控制
        -index         控制首页访问
        -login         控制登录模块
        -pay           支付相关, [未实现,]
        -redis         redis缓存相关
        -security      安全控制目录, shiro
        -sys           用户,角色,权限,业务目录
        -utils         工具类,id生成器, 消息返回结构等
        -web           filter,listener,interceptor目录
        -websocket
resources              配置文件, 页面, 静态资源存放目录
    public             公共目录,存放直接可以被外部访问的文件
        error          公共静态错误页面在这里,可以直接访问.
    static             静态资源, css, js,图片等
        css
        img
        js
            buttonLoading.js  按钮编程loading样式的小插件
            custom.js  通用js文件
        plugins
            bootstrap-fileinput 多文件上传的插件, 可以预览,支持顺序上传. layui也有上传文件插件, 多文件和单文件,但是不支持顺序,所以可以依据情况选择.
            errorpage-js  404,500,401页面的部分特效使用擦肩
            flatpicker 日期选择控件
            layui      后台样式插件,
            lyModal    本系统自写的一个弹窗提示插件.具体使用查看js 和 登录界面的调用.
    templates          页面
        error          错误页面(如果新建404,500等错误页面,那么就会默认打开这个页面)
        ftl            模板目录
        sys            后台管理页面.
        index.html
        login.html


3. springboot2.0 集成 reids
参看: https://blog.csdn.net/qq_21019419/article/details/84337838
第一种, 单应用版:https://blog.csdn.net/qq_21019419/article/details/84337838
第二种, 微服务或者分布式:https://www.cnblogs.com/taiyonghai/p/9454764.html

4. springboot2.0 集成 shiro
参看:

5. springboot2 集成 activemq
查看: https://blog.csdn.net/qq_21019419/article/details/84668793

6.


模板工程:
https://blog.csdn.net/hl_java/article/details/81088577
---本机创建一个项目目录, cd 进入
命令:
mvn archetype:generate -DarchetypeCatalog=local
依据提示进行创建即可.

N.注意事项

1. json包的选择
本项目使用的json为springboot自带的jackson,所以在使用@responsebody的时候可以直接返回正确的对象格式,
如果你使用的是fastjson, 那么在使用@responsebody返回的时候,会经过filter包装成jackson,这样会导致某些设定失效(比如忽略了某个字段@JsonIgnore注解不起作用, 某些Long字段不能转为string返回)
如果要使用fastjson,那么要做一个修改,
参考:https://blog.csdn.net/mickjoust/article/details/51671060
关于jackson的学习, 参考:https://blog.csdn.net/u011054333/article/details/80504154