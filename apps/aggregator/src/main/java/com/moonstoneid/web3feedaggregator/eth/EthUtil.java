package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

public class EthUtil {

    public static EthFilter createFilter(String contrAddr, BigInteger blockNumber, Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                DefaultBlockParameterName.LATEST, contrAddr);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

    // Create a method to shorten ethereum addresses to the first 6 and last 2 characters
    public static String shortenAddress(String addr) {
        return addr.substring(0, 6) + "..." + addr.substring(addr.length() - 2);
    }

}
