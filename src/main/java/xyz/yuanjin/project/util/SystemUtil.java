package xyz.yuanjin.project.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.pojo.config.ProtectConfig;
import xyz.yuanjin.project.pojo.config.SystemConfig;
import xyz.yuanjin.project.pojo.dto.YjFile;
import xyz.yuanjin.project.property.SystemProperty;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author yuanjin
 */
@Component
public class SystemUtil implements ApplicationContextAware {
    private static ApplicationContext APPLICATION_CONTEXT;

    public static SystemProperty systemProperty() {
        return APPLICATION_CONTEXT.getBean(SystemProperty.class);
    }

    /**
     * 判定是否为[受保护]文件
     *
     * @param file 文件
     * @return {Boolean}
     */
    public static boolean isProtectFile(File file) {
//        for (Pattern pattern : SystemUtil.systemProperty().getNasProtectFilePatterns()) {
//            if (pattern.matcher(file.getAbsolutePath()).matches()) {
//                return true;
//            }
//        }
        ProtectConfig protectConfig = SystemConfig.getInstance().getProtectConfig();
        for (String filePath : protectConfig.getFilePath()) {
            if (Objects.equals(filePath, file.getAbsolutePath())) {
                return true;
            }
        }
        for (Pattern pattern : protectConfig.getFilePathRegexList()) {
            if (pattern.matcher(file.getAbsolutePath()).matches()) {
                return true;
            }
        }

        return false;
    }
    /**
     * 判定是否为[受保护]文件
     *
     * @param file 文件
     * @return {Boolean}
     */
    public static boolean isProtectFile(YjFile file) {
//        for (Pattern pattern : SystemUtil.systemProperty().getNasProtectFilePatterns()) {
//            if (pattern.matcher(file.getAbsolutePath()).matches()) {
//                return true;
//            }
//        }
        ProtectConfig protectConfig = SystemConfig.getInstance().getProtectConfig();
        for (String filePath : protectConfig.getFilePath()) {
            if (Objects.equals(filePath, file.getAbsolutePath())) {
                return true;
            }
        }
        for (Pattern pattern : protectConfig.getFilePathRegexList()) {
            if (pattern.matcher(file.getAbsolutePath()).matches()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SystemUtil.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 获取所有网卡的ipv4
     *
     * @return Map
     */
    public static Map<String, String> getAllNetIpv4AddrMap() {
        Map<String, String> map = new HashMap<>();
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
                        map.put(ni.getName(), ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 打印项目地址
     */
    public static void printSystemLink() {
        Map<String, String> map = getAllNetIpv4AddrMap();
        map.forEach((key, value) -> {
            System.out.format("%s%s:%s%s", "http://", value, systemProperty().getServerPort(), systemProperty().getContextPath());
            System.out.println();
        });
    }
}
