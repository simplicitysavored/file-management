package xyz.yuanjin.project;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetCardTest {
    public static void main(String[] args) {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            // 拿到所有网卡
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            // 遍历每个网卡，拿到ip
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        System.out.println(ni.getName() + " " + ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
