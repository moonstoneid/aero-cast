package com.moonstoneid.web3feed.common.eth;

public final class EthUtil {

    private EthUtil() {}

    public static String shortenAddress(String addr) {
        return addr.substring(0, 6) + "..." + addr.substring(addr.length() - 2);
    }

}
