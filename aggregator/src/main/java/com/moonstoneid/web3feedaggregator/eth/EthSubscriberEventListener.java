package com.moonstoneid.web3feedaggregator.eth;

import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.service.SubscriberService;
import org.jetbrains.annotations.Nullable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

public class EthSubscriberEventListener {

    private final SubscriberService subscriberService;
    private final Web3j web3j;

    public EthSubscriberEventListener(SubscriberService subscriberService, Web3j web3j) {
        this.web3j = web3j;
        this.subscriberService = subscriberService;
    }

    public void registerSubscriberEventListeners() {
        subscriberService.getSubscribers().forEach(this::registerSubscriberEventListener);
    }

    public void registerSubscriberEventListener(Subscriber subscriber) {
        String accountAddress = subscriber.getAccountAddress();
        String contractAddress = subscriber.getContractAddress();

        EthFilter subFilter = createFilter(contractAddress, FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        web3j.ethLogFlowable(subFilter).subscribe(l -> onCreateSubscriptionEvent(accountAddress, l));

        EthFilter unsubFilter = createFilter(contractAddress, FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        web3j.ethLogFlowable(unsubFilter).subscribe(l -> onRemoveSubscriptionEvent(accountAddress, l));
    }

    private void onCreateSubscriptionEvent(String subAddress, Log log) {
        String pubAddress = getPublisherAddressFromLog(log);
        if (pubAddress == null) {
            return;
        }

        subscriberService.addSubscription(subAddress, pubAddress);
    }

    private void onRemoveSubscriptionEvent(String subAddress, Log log) {
        String pubAddress = getPublisherAddressFromLog(log);
        if (pubAddress == null) {
            return;
        }

        subscriberService.removeSubscription(subAddress, pubAddress);
    }

    @Nullable
    private static String getPublisherAddressFromLog(Log log) {
        Optional<String> item = log.getTopics()
                .stream()
                .filter(str -> str.startsWith("0x000000000000000000000000"))
                .findFirst();
        if (item.isEmpty()) {
            return null;
        }
        // Remove padding and add '0x' address prefix
        return "0x" + item.get().substring(26);
    }

    public void unregisterSubscriberEventListener(Subscriber subscriber) {
        // TODO!!!
    }

    // TODO: Impl. smarter algorithm that only a processes only events after a specific block timestamp
    private static EthFilter createFilter(String contractAddress, Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

}
