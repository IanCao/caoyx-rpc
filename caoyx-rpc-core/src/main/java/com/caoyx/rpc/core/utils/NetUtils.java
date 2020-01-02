package com.caoyx.rpc.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static com.caoyx.rpc.core.constant.Constants.ANY_HOST;
import static com.caoyx.rpc.core.constant.Constants.LOCAL_HOST;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 15:27
 */
public class NetUtils {
    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static String getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS.getHostAddress();
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress.getHostAddress();
    }

    private static InetAddress getLocalAddress0() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (Throwable e) {

        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (null == interfaces) {
                return address;
            }
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    if (network.isLoopback() || network.isVirtual() || !network.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = addresses.nextElement();
                        if (!isValidV4Address(inetAddress)) {
                            continue;
                        }
                        try {
                            try {
                                if (inetAddress.isReachable(100)) {
                                    return inetAddress;
                                }
                            } catch (IOException e) {
                                // ignore
                            }

                        } catch (Throwable e) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return address;
    }

    private static boolean isValidV4Address(InetAddress address) {
        if (address == null
                || address.isLoopbackAddress()
                || !(address instanceof Inet4Address)) {
            return false;
        }
        String name = address.getHostName();
        return (name != null
                && !LOCAL_HOST.equals(name)
                && !ANY_HOST.equals(name)
                && !IP_PATTERN.matcher(name).matches());
    }
}