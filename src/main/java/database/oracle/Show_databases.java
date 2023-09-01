package database.oracle;

import antSword.Template;

import java.sql.*;

public class Show_databases extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargencode");
        String z2 = getParam("antswordargconn");
        return showDatabases(z1, z2);

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

    String showDatabases(String encode, String conn) throws Exception {
        String sql = "SELECT USERNAME FROM ALL_USERS ORDER BY 1";
        String columnsep = "\t";
        String rowsep = "";
        return executeSQL(encode, conn, sql, columnsep, rowsep, false);
    }
}