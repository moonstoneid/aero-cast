package com.moonstoneid.web3feedaggregator.service;

import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.model.Subscription;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import java.util.List;
import java.util.Optional;

@Service
public class EthService {

    private final Web3j web3j;
    private final SubscriberRepo subscriberRepo;
    private final EntryRepo entryRepo;

    public EthService(Web3j web3j, SubscriberRepo subscriberRepo, EntryRepo entryRepo) {
        this.web3j = web3j;
        this.subscriberRepo = subscriberRepo;
        this.entryRepo = entryRepo;
    }

    // Register listeners after Spring Boot started
    @EventListener(ApplicationReadyEvent.class)
    private void registerListeners() {
        registerSubscriptionListeners();
        registerEntryListeners();
    }

    private void registerEntryListeners() {
        // TODO
    }

    // TODO: Impl. smarter algorithm that only a processes only events after a specific block timestamp
    private void registerSubscriptionListeners() {
        List<Subscriber> subscribers = subscriberRepo.findAll();

        for (Subscriber sub : subscribers) {
            EthFilter subFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
                    sub.getContactAddress());
            subFilter.addSingleTopic(EventEncoder.encode(FeedSubscriber.CREATESUBSCRIPTION_EVENT));

            EthFilter unsubFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
                    sub.getContactAddress());
            unsubFilter.addSingleTopic(EventEncoder.encode(FeedSubscriber.REMOVESUBSCRIPTION_EVENT));

            // Process CREATESUBSCRIPTION_EVENT
            web3j.ethLogFlowable(subFilter).subscribe(log -> onCreateSubscriptionEvent(log, sub));

            // Process REMOVESUBSCRIPTION_EVENT
            web3j.ethLogFlowable(unsubFilter).subscribe(log -> onRemoveSubscriptionEvent(log, sub));
        }
    }

    private void onCreateSubscriptionEvent(Log log, Subscriber sub) {
        String pubAddress = getPubAddr(log);
        if(pubAddress == null){
            return;
        }
        // Check if subscriptions of this subscriber already contain this publisher address
        if(sub.getSubscriptions().stream().noneMatch(o -> o.getPubContactAddress().equalsIgnoreCase(pubAddress))) {
            // Add and save
            Subscription subscription = new Subscription();
            subscription.setPubContactAddress(pubAddress);
            subscription.setSubContactAddress(log.getAddress());

            sub.getSubscriptions().add(subscription);
            subscriberRepo.save(sub);
        }
    }


    private void onRemoveSubscriptionEvent(Log log, Subscriber sub) {
        String pubAddress = getPubAddr(log);
        if(pubAddress == null){
            return;
        }
        sub.getSubscriptions().removeIf(o -> o.getPubContactAddress().equalsIgnoreCase(pubAddress));
        // TODO: Implement cascading save subscriberRepo.save(sub);
    }

    @Nullable
    private String getPubAddr(Log log) {
        Optional<String> item = log.getTopics().stream().filter(str -> str.startsWith("0x000000000000000000000000")).
                findFirst();
        if(item.isEmpty()){
            return null;
        }
        // Remove padding and add '0x' address prefix
        return "0x" + item.get().substring(26);
    }

}
