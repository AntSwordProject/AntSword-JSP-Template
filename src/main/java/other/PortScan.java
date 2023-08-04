package other;

import antSword.Template;

import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScan extends Template {
    @Override
    public String run() throws Exception {
        String z1 = getParam("antswordargip");
        String z2 = getParam("antswordargports");
        return Scan(z1, z2);

    }

    String Scan(String ip, String ports) throws Exception {
        StringBuffer sb = new StringBuffer();
        String[] portlist = ports.split(",");
        Socket socket = null;
        for (int i = 0; i < portlist.length; i++) {
            try {
                socket = new Socket(ip, Integer.parseInt(portlist[i]));
                socket.setSoTimeout(1);
                sb.append(ip + "\t" + portlist[i] + "\tOpen\n");
            } catch (Exception e) {
                sb.append(ip + "\t" + portlist[i] + "\tClosed\n");
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
        return sb.toString();
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
