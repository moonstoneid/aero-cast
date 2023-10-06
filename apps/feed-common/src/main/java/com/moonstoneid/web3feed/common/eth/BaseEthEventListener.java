package com.moonstoneid.web3feed.common.eth;

import java.math.BigInteger;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

public abstract class BaseEthEventListener {

    protected final Web3j web3j;

    protected BaseEthEventListener(Web3j web3j) {
        this.web3j = web3j;
    }

    protected static EthFilter createFilter(String contractAddress, BigInteger blockNumber,
            Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                DefaultBlockParameterName.LATEST, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

}
