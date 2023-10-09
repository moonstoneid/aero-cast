package com.moonstoneid.web3feed.common.eth;

import java.math.BigInteger;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

public abstract class BaseEthProxy {

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    protected final ContractGasProvider contractGasProvider = new ContractGasProvider() {
        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return GAS_PRICE;
        }

        @Override
        public BigInteger getGasPrice() {
            return GAS_PRICE;
        }

        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return GAS_LIMIT;
        }

        @Override
        public BigInteger getGasLimit() {
            return GAS_LIMIT;
        }
    };

    protected final Web3j web3j;

    protected BaseEthProxy(Web3j web3j) {
        this.web3j = web3j;
    }

    protected static Credentials createDummyCredentials() {
        try {
            return Credentials.create(Keys.createEcKeyPair());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentBlockNumber() {
        try {
            BigInteger blockNumber = web3j.ethBlockNumber().sendAsync().get().getBlockNumber();
            return Numeric.toHexStringWithPrefix(blockNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static EthFilter createEventFilter(String contractAddress, String blockNumber,
            Event event) {
        BigInteger blockNum;
        if (blockNumber != null) {
            blockNum = Numeric.toBigInt(blockNumber).add(BigInteger.ONE);
        } else {
            blockNum = BigInteger.ZERO;
        }

        DefaultBlockParameter from = DefaultBlockParameter.valueOf(blockNum);
        DefaultBlockParameter to = DefaultBlockParameterName.LATEST;

        EthFilter filter = new EthFilter(from, to, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

    protected static String getBlockNumberFromEventResponse(BaseEventResponse response) {
        return Numeric.toHexStringWithPrefix(response.log.getBlockNumber());
    }

}
