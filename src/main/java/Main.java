import java.io.File;
import java.nio.file.Paths;

import static antSword.Utils.*;

public class Main {
    public static String[] classNames =
            {
                    "base.Info",
                    "base.Probedb",
                    "command.Exec",
                    "command.Listcmd",
                    "database.mysql.Query",
                    "database.mysql.Show_columns",
                    "database.mysql.Show_databases",
                    "database.mysql.Show_tables",
                    "database.oracle.Query",
                    "database.oracle.Show_columns",
                    "database.oracle.Show_databases",
                    "database.oracle.Show_tables",
                    "database.sqlserver.Query",
                    "database.sqlserver.Show_columns",
                    "database.sqlserver.Show_databases",
                    "database.sqlserver.Show_tables",
                    "filemanager.Chmod",
                    "filemanager.Copy",
                    "filemanager.Create_file",
                    "filemanager.Delete",
                    "filemanager.Dir",
                    "filemanager.Download_file",
                    "filemanager.Filehash",
                    "filemanager.Mkdir",
                    "filemanager.Read_file",
                    "filemanager.Rename",
                    "filemanager.Retime",
                    "filemanager.Upload_file",
                    "filemanager.Wget",
                    "other.PortScan",
                    "other.RedisConn"
            };

    public static void main(String[] args) throws Exception {

        //根目录
        String baseDir = new File(Main.class.getClassLoader().getResource("").getFile()).getAbsolutePath();
        //模板目录
        String tplDir = baseDir + File.separator + "template";
        //输出模板目录
        String outputDir = new File("").getAbsolutePath() + File.separator + "dist";

        copyDirectory(Paths.get(tplDir), Paths.get(outputDir));

        for (String className : classNames) {
            System.out.println("Start to process class: " + className);
            Class<?> clz = Class.forName(className);
            String payload = genPayload(className, false);
            replaceJsTemplate(clz.getPackage().getName(), clz.getSimpleName(), payload, outputDir);
            System.out.println("[+] Generate template Success! :" + className);
            System.out.println("*********************************************************************************************");
        }
    }


}
