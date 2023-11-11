package com.moonstoneid.aerocast.common.eth;

import java.io.IOException;

import com.moonstoneid.aerocast.common.eth.contracts.FeedRegistry;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

class FeedRegistryWrapper extends FeedRegistry {

    FeedRegistryWrapper(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(contractAddress, web3j, credentials, contractGasProvider);
    }

    protected <T extends Type, R> R executeCallSingleValueReturn(
            Function function, Class<R> returnType) throws IOException {
        T result = executeCallSingleValueReturn(function);
        return Web3jFix.convertResult(result, returnType);
    }

}
