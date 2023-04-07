package com.moonstoneid.web3feedaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.error.NotFoundException;
import com.moonstoneid.web3feedaggregator.eth.EthSubscriberEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.EthUtil;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feedaggregator.model.Entry;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.model.Subscription;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriptionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        this.ethEventListener = new EthSubscriberEventListener(this, ethService);
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

    public void createSubscriber(String address) {
        log.info("Trying to register subscriber '{}' ...", EthUtil.shortenAddress(address));

        if (subscriberRepo.existsById(address)) {
            log.info("Subscriber '{}' is already registered.", EthUtil.shortenAddress(address));
            return;
        }

        String contractAddress = ethService.getSubscriberContractAddress(address);
        if (contractAddress == null) {
            log.error("A contract for subscriber '{}' was not found!", EthUtil.shortenAddress(address));
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
        subscriber.setBlockNumber(ethService.getCurrentBlockNumber());

        subscriberRepo.save(subscriber);

        log.info("Subscriber '{}' with contract '{}' has been registered.",
                EthUtil.shortenAddress(address), EthUtil.shortenAddress(contractAddress));

        ethEventListener.registerSubscriberEventListener(subscriber);
    }

    public void removeSubscriber(String address) {
        log.info("Trying to unregister subscriber '{}' ...", EthUtil.shortenAddress(address));

        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            log.info("Subscriber '{}' was not found.", EthUtil.shortenAddress(address));
            return;
        }

        ethEventListener.unregisterSubscriberEventListener(subscriber);

        subscriberRepo.deleteById(address);

        log.info("Subscriber '{}' has been unregistered.", EthUtil.shortenAddress(address));
    }

    public void addSubscription(String address, String pubAddress) {
        // Get subscriber
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            return;
        }

        log.info("Adding subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubAddress), EthUtil.shortenAddress(address));

        // Create publisher if publisher does not exists
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

        // Update block number
        subscriber.setBlockNumber(ethService.getCurrentBlockNumber());
        subscriberRepo.save(subscriber);
    }

    public void removeSubscription(String address, String pubAddress) {
        // Get subscriber
        Subscriber subscriber = subscriberRepo.getById(address);
        if (subscriber == null) {
            return;
        }

        log.info("Removing subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubAddress), EthUtil.shortenAddress(address));

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
