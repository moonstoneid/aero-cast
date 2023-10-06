package com.moonstoneid.web3feed.common.eth;

import java.math.BigInteger;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

public final class EthUtil {

    private EthUtil() {}

    public static String shortenAddress(String addr) {
        return addr.substring(0, 6) + "..." + addr.substring(addr.length() - 2);
    }

}
