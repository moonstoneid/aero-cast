package com.moonstoneid.web3feed.aggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.error.NotFoundException;
import com.moonstoneid.web3feed.aggregator.eth.EthSubscriberEventListener;
import com.moonstoneid.web3feed.aggregator.eth.EthSubscriberService;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feed.aggregator.model.Entry;
import com.moonstoneid.web3feed.aggregator.model.Publisher;
import com.moonstoneid.web3feed.aggregator.model.Subscriber;
import com.moonstoneid.web3feed.aggregator.model.Subscription;
import com.moonstoneid.web3feed.aggregator.repo.EntryRepo;
import com.moonstoneid.web3feed.aggregator.repo.SubscriberRepo;
import com.moonstoneid.web3feed.aggregator.repo.SubscriptionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

@Service
@Slf4j
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final PublisherService publisherService;
    private final EntryRepo entryRepo;

    private final EthSubscriberService ethSubService;
    private final EthSubscriberEventListener ethSubEventListener;

    public SubscriberService(SubscriberRepo subscriberRepo, SubscriptionRepo subscriptionRepo,
            PublisherService publisherService, EntryRepo entryRepo,
            EthSubscriberService ethSubService, EthSubscriberEventListener ethSubEventListener) {
        this.subscriberRepo = subscriberRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.publisherService = publisherService;
        this.entryRepo = entryRepo;

        this.ethSubService = ethSubService;
        this.ethSubEventListener = ethSubEventListener;
    }

    public List<Subscriber> getSubscribers() {
        return subscriberRepo.findAll();
    }

    public Subscriber findSubscriberByAccountAddress(String subAccountAddr) {
        Optional<Subscriber> subscriber = subscriberRepo.findById(subAccountAddr);
        return subscriber
                .orElseThrow(() -> new NotFoundException("Subscriber was not found!"));
    }

    public void createSubscriberIfNotExists(String subAccountAddr) {
        log.info("Trying to register subscriber '{}' ...", EthUtil.shortenAddress(subAccountAddr));

        if (subscriberRepo.existsById(subAccountAddr)) {
            log.info("Subscriber '{}' is already registered.", EthUtil.shortenAddress(subAccountAddr));
            return;
        }

        String subContractAddr = ethSubService.getSubscriberContractAddress(subAccountAddr);
        if (subContractAddr == null) {
            log.error("A contract for subscriber '{}' was not found!", EthUtil.shortenAddress(
                    subAccountAddr));
            throw new NotFoundException("Subscriber was not found!");
        }

        // Save to db
        Subscriber sub = saveSubscriber(subAccountAddr, subContractAddr);
        // Register event listener
        ethSubEventListener.registerSubEventListener(sub);

        log.info("Subscriber '{}' with contract '{}' has been registered.",
                EthUtil.shortenAddress(subAccountAddr), EthUtil.shortenAddress(subContractAddr));
    }

    public void removeSubscriber(String subAccountAddr) {
        log.info("Trying to unregister subscriber '{}' ...", EthUtil.shortenAddress(subAccountAddr));

        Optional<Subscriber> sub = subscriberRepo.findById(subAccountAddr);
        if (sub.isEmpty()) {
            log.info("Subscriber '{}' was not found.", EthUtil.shortenAddress(subAccountAddr));
            return;
        }

        ethSubEventListener.unregisterSubEventListener(sub.get());

        subscriberRepo.deleteById(subAccountAddr);

        log.info("Subscriber '{}' has been unregistered.", EthUtil.shortenAddress(subAccountAddr));
    }

    public void addSubscription(String subAccountAddr, String pubContractAddr) {
        // Get subscriber
        Optional<Subscriber> sub = subscriberRepo.findById(subAccountAddr);
        if (sub.isEmpty()) {
            return;
        }
        Subscriber subscriber = sub.get();

        log.info("Adding subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubContractAddr), EthUtil.shortenAddress(subAccountAddr));

        // Abort if subscriber already has subscription
        boolean existsSubscription = subscriber.getSubscriptions()
                .stream()
                .anyMatch(o -> o.getPubContractAddress().equalsIgnoreCase(pubContractAddr));
        if (existsSubscription) {
            return;
        }

        // Create publisher if publisher does not exist
        publisherService.createPublisherIfNotExists(pubContractAddr);

        // Create new subscription and update subscriber
        Subscription subscription = createSubscription(subscriber.getContractAddress(), pubContractAddr);
        updateSubscriber(subscriber, subscription);
    }

    public void removeSubscription(String subAccountAddr, String pubContractAddr) {
        // Get subscriber
        Optional<Subscriber> sub = subscriberRepo.findById(subAccountAddr);
        if (sub.isEmpty()) {
            return;
        }
        Subscriber subscriber = sub.get();

        log.info("Removing subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubContractAddr),
                EthUtil.shortenAddress(subAccountAddr));

        subscriber.getSubscriptions().removeIf(s ->
                s.getPubContractAddress().equalsIgnoreCase(pubContractAddr));
        if (subscriber.getSubscriptions().isEmpty()) {
            subscriber.setSubscriptions(null);
            subscriptionRepo.deleteById(subscriber.getContractAddress(), pubContractAddr);
        }
        subscriberRepo.save(subscriber);

        // Check if no more subscriptions exist for publisher
        if (!subscriptionsExist(pubContractAddr)) {
            publisherService.removePublisher(pubContractAddr);
        }
    }

    public List<Entry> getEntriesBySubscriberAccountAddress(String subAccountAddr) {
        Optional<Subscriber> sub = subscriberRepo.findById(subAccountAddr);
        if (sub.isEmpty()) {
            throw new NotFoundException("Subscriber was not found!");
        }
        return entryRepo.findAllBySubscriberContractAddress(sub.get().getContractAddress());
    }

    private Subscriber updateSubscriber(Subscriber subscriber, Subscription subscription) {
        subscriber.getSubscriptions().add(subscription);

        // Update block number
        subscriber.setBlockNumber(Numeric.toHexStringWithPrefix(ethSubService.getCurrentBlockNumber()));
        return subscriberRepo.save(subscriber);
    }

    private Subscriber saveSubscriber(String subAccountAddr, String subContractAddr) {
        Subscriber subscriber = new Subscriber();
        subscriber.setAccountAddress(subAccountAddr.toLowerCase());
        subscriber.setContractAddress(subContractAddr);
        subscriber.setSubscriptions(getSubscriptions(subContractAddr));
        subscriber.setBlockNumber(Numeric.toHexStringWithPrefix((ethSubService.getCurrentBlockNumber())));
        return subscriberRepo.save(subscriber);
    }

    private List<Subscription> getSubscriptions(String subContractAddr) {
        List<FeedSubscriber.Subscription> feedSubscriptions = ethSubService.getSubscriberSubscriptions(
                subContractAddr);
        List<Subscription> subscriptions = new ArrayList<>();
        for (FeedSubscriber.Subscription feedSubscription : feedSubscriptions) {
            // Create publisher if publisher does not exist
            Publisher pub = publisherService.createPublisherIfNotExists(feedSubscription.pubAddress);
            subscriptions.add(createSubscription(subContractAddr, pub.getContractAddress()));
        }
        return subscriptions;
    }

    private Subscription createSubscription(String subContractAddr, String pubContractAddr) {
        Subscription subscription = new Subscription();
        subscription.setSubContractAddress(subContractAddr);
        subscription.setPubContractAddress(pubContractAddr);
        return subscription;
    }

    private boolean subscriptionsExist(String pubContractAddr) {
        return subscriberRepo.findAll()
                .stream()
                .anyMatch(o -> o.getSubscriptions()
                        .stream()
                        .anyMatch(s -> s.getPubContractAddress().equalsIgnoreCase(pubContractAddr)));
    }

}
