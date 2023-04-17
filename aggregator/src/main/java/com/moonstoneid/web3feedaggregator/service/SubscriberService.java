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
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

@Service
@Slf4j
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final PublisherService publisherService;
    private final EntryRepo entryRepo;
    private final EthService ethService;
    private final EthSubscriberEventListener ethSubEventListener;

    public SubscriberService(SubscriberRepo subscriberRepo, SubscriptionRepo subscriptionRepo,
            PublisherService publisherService, EntryRepo entryRepo, EthService ethService) {
        this.subscriberRepo = subscriberRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.publisherService = publisherService;
        this.entryRepo = entryRepo;

        this.ethService = ethService;
        this.ethSubEventListener = new EthSubscriberEventListener(this, ethService);
    }

    public List<Subscriber> getSubscribers() {
        return subscriberRepo.findAll();
    }

    public Subscriber findSubscriberByAccountAddress(String subAccAddr) {
        Optional<Subscriber> subscriber = subscriberRepo.findById(subAccAddr);
        return subscriber
                .orElseThrow(() -> new NotFoundException("Subscriber was not found!"));
    }

    public void createSubscriberIfNotExists(String subAccAddr) {
        log.info("Trying to register subscriber '{}' ...", EthUtil.shortenAddress(subAccAddr));

        if (subscriberRepo.existsById(subAccAddr)) {
            log.info("Subscriber '{}' is already registered.", EthUtil.shortenAddress(subAccAddr));
            return;
        }

        String subContrAddr = ethService.getSubscriberContractAddress(subAccAddr);
        if (subContrAddr == null) {
            log.error("A contract for subscriber '{}' was not found!", EthUtil.shortenAddress(subAccAddr));
            throw new NotFoundException("Subscriber was not found!");
        }

        // Save to db
        Subscriber sub = saveSubscriber(subAccAddr, subContrAddr);
        // Register event listener
        ethSubEventListener.registerSubEventListener(sub);

        log.info("Subscriber '{}' with contract '{}' has been registered.",
                EthUtil.shortenAddress(subAccAddr), EthUtil.shortenAddress(subContrAddr));
    }

    public void removeSubscriber(String subAccAddr) {
        log.info("Trying to unregister subscriber '{}' ...", EthUtil.shortenAddress(subAccAddr));

        Optional<Subscriber> sub = subscriberRepo.findById(subAccAddr);
        if (sub.isEmpty()) {
            log.info("Subscriber '{}' was not found.", EthUtil.shortenAddress(subAccAddr));
            return;
        }

        ethSubEventListener.unregisterSubEventListener(sub.get());

        subscriberRepo.deleteById(subAccAddr);

        log.info("Subscriber '{}' has been unregistered.", EthUtil.shortenAddress(subAccAddr));
    }

    public void addSubscription(String subAccAddr, String pubContrAddr) {
        // Get subscriber
        Optional<Subscriber> sub = subscriberRepo.findById(subAccAddr);
        if (sub.isEmpty()) {
            return;
        }
        Subscriber subscriber = sub.get();

        log.info("Adding subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubContrAddr), EthUtil.shortenAddress(subAccAddr));

        // Abort if subscriber already has subscription
        boolean existsSubscription = subscriber.getSubscriptions()
                .stream()
                .anyMatch(o -> o.getPubContractAddress().equalsIgnoreCase(pubContrAddr));
        if (existsSubscription) {
            return;
        }

        // Create publisher if publisher does not exist
        publisherService.createPublisherIfNotExists(pubContrAddr);

        // Create new subscription and update subscriber
        Subscription subscription = createSubscription(subscriber.getContractAddress(), pubContrAddr);
        updateSubscriber(subscriber, subscription);
    }

    public void removeSubscription(String subAccAddr, String pubContrAddr) {
        // Get subscriber
        Optional<Subscriber> sub = subscriberRepo.findById(subAccAddr);
        if (sub.isEmpty()) {
            return;
        }
        Subscriber subscriber = sub.get();

        log.info("Removing subscription '{}/{}' of subscriber '{}' ...",
                EthUtil.shortenAddress(subscriber.getContractAddress()),
                EthUtil.shortenAddress(pubContrAddr), EthUtil.shortenAddress(subAccAddr));

        subscriber.getSubscriptions().removeIf(s ->
                s.getPubContractAddress().equalsIgnoreCase(pubContrAddr));
        if (subscriber.getSubscriptions().isEmpty()) {
            subscriber.setSubscriptions(null);
            subscriptionRepo.deleteById(subscriber.getContractAddress(), pubContrAddr);
        }
        subscriberRepo.save(subscriber);

        // Check if no more subscriptions exist for publisher
        if (!subscriptionsExist(pubContrAddr)) {
            publisherService.removePublisher(pubContrAddr);
        }
    }

    public List<Entry> getEntriesBySubscriberAccountAddress(String subAccAddr) {
        Optional<Subscriber> sub = subscriberRepo.findById(subAccAddr);
        if (sub.isEmpty()) {
            throw new NotFoundException("Subscriber was not found!");
        }
        return entryRepo.findAllBySubscriberContractAddress(sub.get().getContractAddress());
    }

    private Subscriber updateSubscriber(Subscriber subscriber, Subscription subscription) {
        subscriber.getSubscriptions().add(subscription);

        // Update block number
        subscriber.setBlockNumber(Numeric.toHexStringWithPrefix(ethService.getCurrentBlockNumber()));
        return subscriberRepo.save(subscriber);
    }

    private Subscriber saveSubscriber(String subAccAddr, String subContrAddr) {
        Subscriber subscriber = new Subscriber();
        subscriber.setAccountAddress(subAccAddr.toLowerCase());
        subscriber.setContractAddress(subContrAddr);
        subscriber.setSubscriptions(getSubscriptions(subContrAddr));
        subscriber.setBlockNumber(Numeric.toHexStringWithPrefix((ethService.getCurrentBlockNumber())));
        return subscriberRepo.save(subscriber);
    }

    private List<Subscription> getSubscriptions(String subContrAddr) {
        List<FeedSubscriber.Subscription> feedSubscriptions = ethService.getSubscriberSubscriptions(
                subContrAddr);
        List<Subscription> subscriptions = new ArrayList<>();
        for (FeedSubscriber.Subscription feedSubscription : feedSubscriptions) {
            // Create publisher if publisher does not exist
            Publisher pub = publisherService.createPublisherIfNotExists(feedSubscription.pubAddress);
            subscriptions.add(createSubscription(subContrAddr, pub.getContractAddress()));
        }
        return subscriptions;
    }

    private Subscription createSubscription(String subContrAddr, String pubContrAddr) {
        Subscription subscription = new Subscription();
        subscription.setSubContractAddress(subContrAddr);
        subscription.setPubContractAddress(pubContrAddr);
        return subscription;
    }

    private boolean subscriptionsExist(String pubContrAddr) {
        return subscriberRepo.findAll()
                .stream()
                .anyMatch(o -> o.getSubscriptions()
                        .stream()
                        .anyMatch(s -> s.getPubContractAddress().equalsIgnoreCase(pubContrAddr)));
    }

}
