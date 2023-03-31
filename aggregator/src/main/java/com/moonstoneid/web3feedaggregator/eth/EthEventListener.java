package com.moonstoneid.web3feedaggregator.eth;

import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.service.PublisherService;
import com.moonstoneid.web3feedaggregator.service.SubscriberService;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

@Service
public class EthEventListener {

    private final SubscriberService subscriberService;
    private final PublisherService publisherService;
    private final Web3j web3j;

    public EthEventListener(SubscriberService subscriberService, PublisherService publisherService,
            Web3j web3j) {
        this.web3j = web3j;
        this.subscriberService = subscriberService;
        this.publisherService = publisherService;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    public void registerListeners() {
        registerSubscriberEventListeners();
        registerPublisherEventListeners();
    }

    private void registerSubscriberEventListeners() {
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

    private void registerPublisherEventListeners() {
        publisherService.getPublishers().forEach(this::registerPublisherEventListener);
    }

    public void registerPublisherEventListener(Publisher publisher) {
        String contractAddress = publisher.getContractAddress();

        EthFilter subFilter = createFilter(contractAddress, FeedPublisher.NEWPUBITEM_EVENT);
        web3j.ethLogFlowable(subFilter).subscribe(l -> onCreateSubscriptionEvent(contractAddress, l));
    }

    private void onNewPubItemEvent(String pubAddress, Log log) {
        // TODO: Fetch publisher item
    }

    public void unregisterPublisherEventListener(Publisher publisher) {
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
