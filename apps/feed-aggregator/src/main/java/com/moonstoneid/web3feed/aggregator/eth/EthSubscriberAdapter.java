package com.moonstoneid.web3feed.aggregator.eth;

import java.util.List;

import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import com.moonstoneid.web3feed.common.eth.BaseEthProxy;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.contracts.FeedRegistry;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber.CreateSubscriptionEventResponse;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber.RemoveSubscriptionEventResponse;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;

@Component
@Slf4j
public class EthSubscriberAdapter extends BaseEthProxy {

    public interface EventCallback {
        void onCreateSubscription(String subContractAddr, String blockNumber, String pubContractAddr);
        void onRemoveSubscription(String subContractAddr, String blockNumber, String pubContractAddr);
    }

    private final Credentials credentials;
    private final String regContractAddr;

    private final MultiValueMap<String, Disposable> eventSubscribers = new LinkedMultiValueMap<>();

    public EthSubscriberAdapter(Web3j web3j, EthRegistryProperties ethRegistryProperties) {
        super(web3j);
        this.credentials = createDummyCredentials();
        this.regContractAddr = ethRegistryProperties.getContractAddress();
    }

    public String getSubscriberContractAddress(String subAccountAddr) {
        FeedRegistry contract = getRegistryContract();
        try {
            return contract.getSubscriberContractByAddress(subAccountAddr).sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSubscriptionEventListener(String subContractAddr, String blockNumber,
            EventCallback callback) {
        log.debug("Adding event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subContractAddr));

        FeedSubscriber contract = getSubscriberContract(subContractAddr);

        EthFilter subEventFilter = createEventFilter(subContractAddr, blockNumber,
                FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        Disposable subEventSubscriber = contract.createSubscriptionEventFlowable(subEventFilter)
                .subscribe(r -> handleSubscribeEvent(subContractAddr, r, callback));
        eventSubscribers.add(subContractAddr, subEventSubscriber);

        EthFilter unsubEventFilter = createEventFilter(subContractAddr, blockNumber,
                FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        Disposable unsubEventSubscriber = contract.removeSubscriptionEventFlowable(unsubEventFilter)
                .subscribe(r -> handleUnsubscribeEvent(subContractAddr, r, callback));
        eventSubscribers.add(subContractAddr, unsubEventSubscriber);
    }

    private void handleSubscribeEvent(String subContractAddr,
            CreateSubscriptionEventResponse response, EventCallback callback) {
        String blockNumber = getBlockNumberFromEventResponse(response);
        String pubContractAddr = response.pubAddress;
        callback.onCreateSubscription(subContractAddr, blockNumber, pubContractAddr);
    }

    private void handleUnsubscribeEvent(String subContractAddr,
            RemoveSubscriptionEventResponse response, EventCallback callback) {
        String blockNumber = getBlockNumberFromEventResponse(response);
        String pubContractAddr = response.pubAddress;
        callback.onRemoveSubscription(subContractAddr, blockNumber, pubContractAddr);
    }

    public void unregisterSubscriptionEventListener(String subContractAddr) {
        log.debug("Removing event listener on subscriber contract '{}'.",
                EthUtil.shortenAddress(subContractAddr));

        for (Disposable eventSubscriber : eventSubscribers.get(subContractAddr)) {
            eventSubscriber.dispose();
        }
    }

    public List<FeedSubscriber.Subscription> getSubscriberSubscriptions(String subContractAddr) {
        FeedSubscriber contract = getSubscriberContract(subContractAddr);
        try {
            return contract.getSubscriptions().sendAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FeedRegistry getRegistryContract() {
        return FeedRegistry.load(regContractAddr, web3j, credentials, contractGasProvider);
    }

    private FeedSubscriber getSubscriberContract(String contractAddr) {
        return FeedSubscriber.load(contractAddr, web3j, credentials, contractGasProvider);
    }

}
