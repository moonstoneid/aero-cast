package com.moonstoneid.web3publisher.eth;

import java.math.BigInteger;

import com.moonstoneid.web3publisher.AppProperties;
import com.moonstoneid.web3publisher.eth.contracts.FeedPublisher;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Component
public class Publisher {

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    private final AppProperties appProperties;
    private final Web3j web3j;

    public Publisher(AppProperties appProperties, Web3j web3j) {
        this.appProperties = appProperties;
        this.web3j = web3j;
    }

    public void publish(String link) {
        String contractAddr = appProperties.getEth().getPubContractAddress();
        FeedPublisher contract = FeedPublisher.load(contractAddr, web3j, getCredentials(),
                contractGasProvider);
        try {
            contract.publish(link).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Credentials getCredentials() {
        return Credentials.create(appProperties.getEth().getPrivateKey());
    }

    private static final ContractGasProvider contractGasProvider = new ContractGasProvider() {
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

}
