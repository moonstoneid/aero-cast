package com.moonstoneid.web3feedaggregator.eth;

import java.math.BigInteger;
import java.util.List;

import com.moonstoneid.web3feedaggregator.AppProperties;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class EthService {

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final Web3j web3j;
    private final SubscriberRepo subscriberRepo;
    private final AppProperties appProperties;

    public EthService(Web3j web3j, SubscriberRepo subscriberRepo, AppProperties appProperties) {
        this.web3j = web3j;
        this.subscriberRepo = subscriberRepo;
        this.appProperties = appProperties;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public String getSubscriberContractAddress(String accountAddress) {
       return subscriberRepo.getById(accountAddress).getContractAddress();
    }

    public List<FeedSubscriber.Subscription> getSubscriberSubscriptions(String contractAddress) {
        // TODO!!!
        return null;
    }

    public String getPublisherFeedUrl(String contractAddress) {
        // Use FeedPublisher contract to get feed url
        FeedPublisher contract = FeedPublisher.load(contractAddress, web3j, getCredentials(),
                contractGasProvider);
        try {
            return contract.getFeedUrl().sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FeedPublisher.PubItem> getPublisherItems(String contractAddress) {
        // TODO!!!
        return null;
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
