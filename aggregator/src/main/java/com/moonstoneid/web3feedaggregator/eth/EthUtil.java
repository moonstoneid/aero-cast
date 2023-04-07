package com.moonstoneid.web3feedaggregator.eth;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.utils.Numeric;

public class EthUtil {

    public static EthFilter createFilter(String addr, String blockNumber, Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameter.valueOf(Numeric.toBigInt(blockNumber)),
                DefaultBlockParameterName.LATEST, addr);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

}
