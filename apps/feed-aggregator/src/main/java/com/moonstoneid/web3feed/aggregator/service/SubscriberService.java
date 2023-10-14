package com.moonstoneid.web3feed.aggregator.service;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.error.ConflictException;
import com.moonstoneid.web3feed.aggregator.error.NotFoundException;
import com.moonstoneid.web3feed.aggregator.eth.EthSubscriberAdapter;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.contracts.FeedSubscriber;
import com.moonstoneid.web3feed.aggregator.model.Subscriber;
import com.moonstoneid.web3feed.aggregator.model.Subscription;
import com.moonstoneid.web3feed.aggregator.repo.SubscriberRepo;
import com.moonstoneid.web3feed.aggregator.repo.SubscriptionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SubscriberService implements EthSubscriberAdapter.EventCallback {

    private final SubscriberRepo subscriberRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final PublisherService publisherService;

    private final EthSubscriberAdapter ethSubscriberAdapter;

    public SubscriberService(SubscriberRepo subscriberRepo, SubscriptionRepo subscriptionRepo,
            PublisherService publisherService, EthSubscriberAdapter ethSubscriberAdapter) {
        this.subscriberRepo = subscriberRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.publisherService = publisherService;
        this.ethSubscriberAdapter = ethSubscriberAdapter;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    protected void initEventListener() {
        getSubscribers().forEach(s -> ethSubscriberAdapter.registerSubscriptionEventListener(
                s.getContractAddress(), s.getBlockNumber(), this));
    }

    @Override
    public void onCreateSubscription(String subContractAddr, String blockNumber,
            String pubContractAddr) {
        String contractAddr = subContractAddr.toLowerCase();

        // Update subscriber event block number
        updateSubscriberEventBlockNumber(contractAddr, blockNumber);

        // Create subscription
        createSubscription(contractAddr, pubContractAddr);
    }

    @Override
    public void onRemoveSubscription(String subContractAddr, String blockNumber,
            String pubContractAddr) {
        String contractAddr = subContractAddr.toLowerCase();

        // Update subscriber event block number
        updateSubscriberEventBlockNumber(contractAddr, blockNumber);

        // Remove subscription
        removeSubscription(contractAddr, pubContractAddr);
    }

    public List<Subscriber> getSubscribers() {
        return subscriberRepo.findAll();
    }

    private Optional<Subscriber> findSubscriber(String subAccountAddr) {
        String accountAddr = subAccountAddr.toLowerCase();
        return subscriberRepo.findById(accountAddr);
    }

    public Subscriber getSubscriber(String subAccountAddr) {
        Optional<Subscriber> subscriber = findSubscriber(subAccountAddr);
        return subscriber
                .orElseThrow(() -> new NotFoundException("Subscriber was not found!"));
    }

    public void createSubscriber(String subAccountAddr) {
        String accountAddr = subAccountAddr.toLowerCase();

        log.info("Trying to create subscriber '{}' ...", EthUtil.shortenAddress(accountAddr));

        String currentBlockNum = ethSubscriberAdapter.getCurrentBlockNumber();

        // Check if subscriber exists
        if (subscriberRepo.existsById(accountAddr)) {
            log.error("Subscriber '{}' already exists!", EthUtil.shortenAddress(subAccountAddr));
            throw new ConflictException("Subscriber already exists!");
        }

        // Get subscriber contract address
        String contractAddr = ethSubscriberAdapter.getSubscriberContractAddress(accountAddr);
        if (contractAddr == null) {
            log.error("Contract for subscriber '{}' was not found!", EthUtil.shortenAddress(
                    accountAddr));
            throw new NotFoundException("Subscriber was not found!");
        }

        // Create subscriber
        Subscriber sub = new Subscriber();
        sub.setAccountAddress(accountAddr);
        sub.setContractAddress(contractAddr);
        sub.setBlockNumber(currentBlockNum);
        subscriberRepo.save(sub);

        // Create subscriptions
        createSubscriptions(contractAddr, ethSubscriberAdapter.getSubscriberSubscriptions(
                contractAddr));

        // Register subscriber event listener
        ethSubscriberAdapter.registerSubscriptionEventListener(contractAddr, currentBlockNum, this);

        log.info("Subscriber '{}' has been created.", EthUtil.shortenAddress(accountAddr));
    }

    public void removeSubscriber(String subAccountAddr) {
        String accountAddr = subAccountAddr.toLowerCase();

        log.info("Trying to remove subscriber '{}' ...", EthUtil.shortenAddress(accountAddr));

        // Check if subscriber exists
        Optional<Subscriber> sub = findSubscriber(accountAddr);
        if (sub.isEmpty()) {
            log.error("Subscriber '{}' was not found!", EthUtil.shortenAddress(accountAddr));
            return;
        }
        String subContractAddr = sub.get().getContractAddress();

        // Unregister subscriber event listener
        ethSubscriberAdapter.unregisterSubscriptionEventListener(subContractAddr);

        // Remove subscriber
        subscriberRepo.deleteById(accountAddr);

        // Cleanup publishers
        publisherService.cleanupPublishers();

        log.info("Subscriber '{}' has been removed.", EthUtil.shortenAddress(accountAddr));
    }

    private void createSubscriptions(String subContractAddr,
            List<FeedSubscriber.Subscription> feedSubs) {
        for (FeedSubscriber.Subscription feedSub : feedSubs) {
            createSubscription(subContractAddr, feedSub.pubAddress);
        }
    }

    private void createSubscription(String subContractAddr, String pubContractAddr) {
        log.info("Adding subscription '{}/{}' ...", EthUtil.shortenAddress(subContractAddr),
                EthUtil.shortenAddress(pubContractAddr));

        // Create publisher
        publisherService.createPublisherIfNotExists(pubContractAddr);

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setSubContractAddress(subContractAddr);
        subscription.setPubContractAddress(pubContractAddr);
        subscriptionRepo.save(subscription);
    }

    private void removeSubscription(String subContractAddr, String pubContractAddr) {
        log.info("Removing subscription '{}/{}' ...", EthUtil.shortenAddress(subContractAddr),
                EthUtil.shortenAddress(pubContractAddr));

        // Remove subscription
        subscriptionRepo.deleteById(subContractAddr, pubContractAddr);

        // Remove publisher
        publisherService.removePublisherIfNotUsed(pubContractAddr);
    }

    private void updateSubscriberEventBlockNumber(String subContractAddr, String blockNumber) {
        subscriberRepo.updateSubscriberBlockNumber(subContractAddr, blockNumber);
    }

}
