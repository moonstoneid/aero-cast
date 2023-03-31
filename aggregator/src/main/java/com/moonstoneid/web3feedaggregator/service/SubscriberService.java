package com.moonstoneid.web3feedaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.error.NotFoundException;
import com.moonstoneid.web3feedaggregator.eth.EthEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Entry;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.model.Subscription;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;
    private final EntryRepo entryRepo;

    private final PublisherService publisherService;

    private final EthService ethService;
    private final EthEventListener ethEventListener;

    public SubscriberService(SubscriberRepo subscriberRepo, EntryRepo entryRepo,
            PublisherService publisherService, EthService ethService,
            EthEventListener ethEventListener) {
        this.subscriberRepo = subscriberRepo;
        this.entryRepo = entryRepo;

        this.publisherService = publisherService;

        this.ethService = ethService;
        this.ethEventListener = ethEventListener;
    }

    public List<Subscriber> getSubscribers() {
        return subscriberRepo.findAll();
    }

    public Subscriber findSubscriberByAccountAddress(String address) {
        Optional<Subscriber> subscriber = subscriberRepo.findById(address);
        return subscriber
                .orElseThrow(() -> new NotFoundException("Subscriber was not found!"));
    }

    public void registerSubscriberByAccountAddress(String address) {
        if (subscriberRepo.existsById(address)) {
            return;
        }

        String contractAddress = ethService.getSubscriberContractAddress(address);
        if (contractAddress == null) {
            throw new NotFoundException("Subscriber was not found!");
        }

        List<FeedSubscriber.Subscription> feedSubscriptions = ethService.getSubscriberSubscriptions(
                contractAddress);

        List<Subscription> subscriptions = new ArrayList<>();
        for (FeedSubscriber.Subscription feedSubscription : feedSubscriptions) {
            Publisher publisher = publisherService.createPublisher(feedSubscription.pubAddress);
            Subscription subscription = new Subscription();
            subscription.setSubContractAddress(contractAddress);
            subscription.setPubContractAddress(publisher.getContractAddress());
        }

        Subscriber subscriber = new Subscriber();
        subscriber.setAccountAddress(address);
        subscriber.setSubscriptions(subscriptions);
        subscriberRepo.save(subscriber);

        ethEventListener.registerSubscriberEventListener(subscriber);
    }

    public void unregisterSubscriberByAccountAddress(String address) {
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            return;
        }

        ethEventListener.unregisterSubscriberEventListener(subscriber);

        subscriberRepo.deleteById(address);
    }

    public void addSubscription(String address, String pubAddress) {
        // Get subscriber
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            return;
        }

        // Get publisher
        publisherService.createPublisher(pubAddress);

        // Abort if subscriber already has subscription
        boolean existsSubscription = subscriber.getSubscriptions()
                .stream()
                .anyMatch(o -> o.getPubContractAddress().equalsIgnoreCase(pubAddress));
        if (existsSubscription) {
            return;
        }

        // Update subscriber
        Subscription subscriptions = new Subscription();
        subscriptions.setSubContractAddress(subscriber.getContractAddress());
        subscriptions.setPubContractAddress(pubAddress);
        subscriber.getSubscriptions().add(subscriptions);
        subscriberRepo.save(subscriber);
    }

    public void removeSubscription(String address, String pubAddress) {
        // Get subscriber
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            return;
        }

        // Update subscriber
        subscriber.getSubscriptions()
                .removeIf(s -> s.getPubContractAddress().equalsIgnoreCase(pubAddress));
        subscriberRepo.save(subscriber);

        // TODO: Cleanup publisher
    }

    public List<Entry> getEntriesBySubscriberAccountAddress(String address) {
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            throw new NotFoundException("Subscriber was not found!");
        }
        return entryRepo.findAllBySubscriberContractAddress(address);
    }

}
