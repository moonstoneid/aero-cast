package com.moonstoneid.web3feedaggregator.eth;

import java.util.List;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

@Service
public class EthService {

    private final Web3j web3j;

    public EthService(Web3j web3j) {
        this.web3j = web3j;
    }

    public String getSubscriberContractAddress(String accountAddress) {
        // TODO!!!
        return null;
    }

    public List<FeedSubscriber.Subscription> getSubscriberSubscriptions(String contractAddress) {
        // TODO!!!
        return null;
    }

    public String getPublisherFeedUrl(String contractAddress) {
        // TODO!!!
        return null;
    }

    public List<FeedPublisher.PubItem> getPublisherItems(String contractAddress) {
        // TODO!!!
        return null;
    }

}
