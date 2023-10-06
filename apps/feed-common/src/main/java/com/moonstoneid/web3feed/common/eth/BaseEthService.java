package com.moonstoneid.web3feed.common.eth;

import java.math.BigInteger;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

public abstract class BaseEthService {

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

    protected BaseEthService(Web3j web3j) {
        this.web3j = web3j;
    }

    public BigInteger getCurrentBlockNumber() {
        try {
            return web3j.ethBlockNumber().sendAsync().get().getBlockNumber();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Credentials createDummyCredentials() {
        try {
            return Credentials.create(Keys.createEcKeyPair());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
