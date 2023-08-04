package filemanager;

import antSword.Template;

import java.io.File;

public class Chmod extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        String z2 = getParam("antswordargmode");
        return ChmodCode(z1, z2);

    }

    String ChmodCode(String path, String permstr) {
        try {
            int permissions = Integer.parseInt(permstr, 8);
            File f = new File(path);
            if ((permissions & 256) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, true);
            }
            if ((permissions & 128) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, true);
            }

            if ((permissions & 64) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, true);
            }
            if ((permissions & 32) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, false);
            }
            if ((permissions & 16) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, false);
            }
            if ((permissions & 8) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, false);
            }
            if ((permissions & 4) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, false);
            }
            if ((permissions & 2) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, false);
            }
            if ((permissions & 1) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, false);
            }
        } catch (Exception e) {
            return "0";
        }
        return "1";
    }
}
