package com.moonstoneid.web3publisher.eth;

import com.moonstoneid.web3publisher.AppProperties;
import com.moonstoneid.web3publisher.eth.wrapper.FeedPublisher;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@Component
public class Publisher {

    private final Web3j web3j;
    private final AppProperties appProperties;
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    public Publisher(Web3j web3j, AppProperties appProperties) {
        this.web3j = web3j;
        this.appProperties = appProperties;
    }

    public void publish(String link) {
        String contractAddr = appProperties.getEth().getContractaddress();
        FeedPublisher contract = FeedPublisher.load(contractAddr, web3j, getCredentials(), contractGasProvider);
        try {
            contract.publish(link).sendAsync().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Credentials getCredentials() {
        return Credentials.create(appProperties.getEth().getPrivatekey());
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
