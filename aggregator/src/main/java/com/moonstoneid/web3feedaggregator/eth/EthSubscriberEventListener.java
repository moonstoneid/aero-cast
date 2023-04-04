package com.moonstoneid.web3feedaggregator.eth;

import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.service.SubscriberService;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;


public class EthSubscriberEventListener {

    private final SubscriberService subscriberService;
    private final Web3j web3j;
    
    private final MultiValueMap<String,Disposable> listeners = new LinkedMultiValueMap<>();

    public EthSubscriberEventListener(SubscriberService subscriberService, Web3j web3j) {
        this.web3j = web3j;
        this.subscriberService = subscriberService;
    }

    public void registerSubscriberEventListeners() {
        subscriberService.getSubscribers().forEach(this::registerSubscriberEventListener);
    }

    public void registerSubscriberEventListener(Subscriber subscriber) {
        String accountAddr = subscriber.getAccountAddress();
        String contractAddr = subscriber.getContractAddress();

        EthFilter subFilter = createFilter(contractAddr, FeedSubscriber.CREATESUBSCRIPTION_EVENT);
        Disposable sub = web3j.ethLogFlowable(subFilter).subscribe(l -> onCreateSubscriptionEvent(accountAddr, l));

        EthFilter unsubFilter = createFilter(contractAddr, FeedSubscriber.REMOVESUBSCRIPTION_EVENT);
        Disposable unsub = web3j.ethLogFlowable(unsubFilter).subscribe(l -> onRemoveSubscriptionEvent(accountAddr, l));

        // Store listeners so we can unregister them later
        listeners.add(contractAddr, sub);
        listeners.add(contractAddr, unsub);
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
        for (Disposable listener : listeners.get(subscriber.getContractAddress())) {
            listener.dispose();
        }
    }

    // TODO: Impl. smarter algorithm that only a processes only events after a specific block timestamp
    private static EthFilter createFilter(String contractAddress, Event event) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, contractAddress);
        filter.addSingleTopic(EventEncoder.encode(event));
        return filter;
    }

}
