package com.moonstoneid.web3feed.aggregator.eth;

import java.util.List;

import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import com.moonstoneid.web3feed.common.eth.BaseEthService;
import com.moonstoneid.web3feed.common.eth.contracts.FeedRegistry;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Service
public class EthSubscriberService extends BaseEthService {

    private final Credentials credentials;
    private final String regContractAddr;

    public EthSubscriberService(Web3j web3j, EthRegistryProperties ethRegistryProperties) {
        super(web3j);
        this.credentials = createDummyCredentials();
        this.regContractAddr = ethRegistryProperties.getContractAddress();
    }

    public String getSubscriberContractAddress(String subAccountAddr) {
        // Use FeedRegistry contract to get subscriber contract address
        FeedRegistry contract = FeedRegistry.load(regContractAddr, web3j, credentials,
                contractGasProvider);
        try {
            return contract.getSubscriberContractByAddress(subAccountAddr).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FeedSubscriber.Subscription> getSubscriberSubscriptions(String subContractAddr) {
        // Use FeedSubscriber contract to get subscriptions
        FeedSubscriber contract = FeedSubscriber.load(subContractAddr, web3j, credentials,
                contractGasProvider);
        try {
            return contract.getSubscriptions().sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
