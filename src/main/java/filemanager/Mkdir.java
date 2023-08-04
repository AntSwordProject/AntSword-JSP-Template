package filemanager;

import antSword.Template;

import java.io.File;

public class Mkdir extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        return CreateDirCode(z1);

    }

    String CreateDirCode(String dirPath) throws Exception {
        File f = new File(dirPath);
        f.mkdir();
        return "1";
    }
}
