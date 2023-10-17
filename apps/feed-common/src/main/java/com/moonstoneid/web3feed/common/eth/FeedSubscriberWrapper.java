package com.moonstoneid.web3feed.common.eth;

import java.io.IOException;

import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

class FeedSubscriberWrapper extends FeedSubscriber {

    FeedSubscriberWrapper(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(contractAddress, web3j, credentials, contractGasProvider);
    }

    protected <T extends Type, R> R executeCallSingleValueReturn(
            Function function, Class<R> returnType) throws IOException {
        T result = executeCallSingleValueReturn(function);
        return Web3jFix.convertResult(result, returnType);
    }

}
