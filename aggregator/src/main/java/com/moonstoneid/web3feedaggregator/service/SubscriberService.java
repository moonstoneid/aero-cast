package com.moonstoneid.web3feedaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.error.NotFoundException;
import com.moonstoneid.web3feedaggregator.eth.EthSubscriberEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Entry;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.model.Subscription;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriptionRepo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final PublisherService publisherService;
    private final EntryRepo entryRepo;

    private final EthService ethService;
    private final EthSubscriberEventListener ethEventListener;

    public SubscriberService(SubscriberRepo subscriberRepo, SubscriptionRepo subscriptionRepo,
            PublisherService publisherService, EntryRepo entryRepo, EthService ethService) {
        this.subscriberRepo = subscriberRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.publisherService = publisherService;
        this.entryRepo = entryRepo;

        this.ethService = ethService;
        this.ethEventListener = new EthSubscriberEventListener(this, ethService.getWeb3j());
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    public void initEventListener() {
        ethEventListener.registerSubscriberEventListeners();
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
            subscriptions.add(subscription);
        }

        Subscriber subscriber = new Subscriber();
        subscriber.setAccountAddress(address.toLowerCase());
        subscriber.setContractAddress(contractAddress);
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

        subscriber.getSubscriptions().removeIf(s -> s.getPubContractAddress().equalsIgnoreCase(pubAddress));
        if (subscriber.getSubscriptions().isEmpty()) {
            subscriber.setSubscriptions(null);
            subscriptionRepo.deleteById(subscriber.getContractAddress(), pubAddress);
        }
        subscriberRepo.save(subscriber);

        // Check if no more subscriptions exist for publisher
        if (!subscriptionsExist(pubAddress)) {
            publisherService.removePublisher(pubAddress);
        }
    }

    public List<Entry> getEntriesBySubscriberAccountAddress(String address) {
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            throw new NotFoundException("Subscriber was not found!");
        }
        return entryRepo.findAllBySubscriberContractAddress(subscriber.getContractAddress());
    }

    private boolean subscriptionsExist(String pubAddress) {
        return subscriberRepo.findAll()
                .stream()
                .anyMatch(o -> o.getSubscriptions()
                        .stream()
                        .anyMatch(s -> s.getPubContractAddress().equalsIgnoreCase(pubAddress)));
    }

}
