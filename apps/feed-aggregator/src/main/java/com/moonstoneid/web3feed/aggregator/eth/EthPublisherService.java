package com.moonstoneid.web3feed.aggregator.eth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.web3feed.common.eth.BaseEthService;
import com.moonstoneid.web3feed.common.eth.contracts.FeedPublisher;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Service
public class EthPublisherService extends BaseEthService {

    private final Credentials credentials;

    public EthPublisherService(Web3j web3j) {
        super(web3j);
        this.credentials = createDummyCredentials();
    }

    public String getPublisherFeedUrl(String pubContractAddr) {
        // Use FeedPublisher contract to get feed URL
        FeedPublisher contract = FeedPublisher.load(pubContractAddr, web3j, credentials,
                contractGasProvider);
        try {
            return contract.getFeedUrl().sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FeedPublisher.PubItem> getPublisherItems(String pubContractAddr) {
        List<FeedPublisher.PubItem> items = new ArrayList<>();

        // Use FeedPublisher contract to get PubItems
        FeedPublisher contract = FeedPublisher.load(pubContractAddr, web3j, credentials,
                contractGasProvider);
        try {
            BigInteger count = contract.getTotalPubItemCount().sendAsync().get();
            if (count == null) {
                return items;
            }
            for (int i = 0; i < count.intValue(); i++) {
                FeedPublisher.PubItem pubItem = contract.getPubItem(BigInteger.valueOf(i))
                        .sendAsync().get();
                items.add(pubItem);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    public FeedPublisher.PubItem getPubItem(String pubContractAddr, BigInteger index) {
        // Use FeedPublisher contract to get PubItem
        FeedPublisher contract = FeedPublisher.load(pubContractAddr, web3j, credentials,
                contractGasProvider);
        try {
           return contract.getPubItem(index).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
