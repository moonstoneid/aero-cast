package com.moonstoneid.web3feed.aggregator.service;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.eth.EthPublisherAdapter;
import com.moonstoneid.web3feed.aggregator.model.Publisher;
import com.moonstoneid.web3feed.aggregator.repo.PublisherRepo;
import com.moonstoneid.web3feed.aggregator.repo.SubscriptionRepo;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.contracts.FeedPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublisherService implements EthPublisherAdapter.EventCallback {

    private final PublisherRepo publisherRepo;
    private final SubscriptionRepo subscriptionRepo;

    private final EntryService entryService;

    private final EthPublisherAdapter ethPublisherAdapter;

    public PublisherService(PublisherRepo publisherRepo, SubscriptionRepo subscriptionRepo,
            EntryService entryService, EthPublisherAdapter ethPublisherAdapter) {
        this.publisherRepo = publisherRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.entryService = entryService;
        this.ethPublisherAdapter = ethPublisherAdapter;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    protected void initEventListener() {
        getPublishers().forEach(p -> ethPublisherAdapter.registerPubItemEventListener(
                p.getContractAddress(), p.getBlockNumber(), this));
    }

    @Override
    public void onNewPubItem(String pubContractAddr, String blockNumber, FeedPublisher.PubItem pubItem) {
        String contractAddr = pubContractAddr.toLowerCase();

        // Get publisher
        Optional<Publisher> pub = findPublisher(contractAddr);
        if (pub.isEmpty()) {
            return;
        }

        // Update publisher event block number
        updatePublisherEventBlockNumber(contractAddr, blockNumber);

        // Fetch entry
        entryService.fetchEntry(pub.get(), pubItem.data);
    }

    public List<Publisher> getPublishers() {
        return publisherRepo.findAll();
    }

    private Optional<Publisher> findPublisher(String pubContractAddr) {
        String contractAddr = pubContractAddr.toLowerCase();
        return publisherRepo.findById(contractAddr);
    }

    public void createPublisherIfNotExists(String pubContractAddr) {
        Optional<Publisher> pub = findPublisher(pubContractAddr);
        if (pub.isEmpty()) {
            createPublisher(pubContractAddr);
        }
    }

    public void cleanupPublishers() {
        getPublishers().forEach(p -> removePublisherIfNotUsed(p.getContractAddress()));
    }

    public void removePublisherIfNotUsed(String pubContractAddr) {
        if (!subscriptionRepo.existsByPublisherContractAddress(pubContractAddr)) {
            removePublisher(pubContractAddr);
        }
    }

    private void createPublisher(String pubContractAddr) {
        String contractAddr = pubContractAddr.toLowerCase();

        log.info("Trying to create publisher '{}' ...", EthUtil.shortenAddress(contractAddr));

        String currentBlockNum = ethPublisherAdapter.getCurrentBlockNumber();

        // Create publisher
        Publisher pub = new Publisher();
        pub.setContractAddress(contractAddr);
        pub.setFeedUrl(ethPublisherAdapter.getPublisherFeedUrl(contractAddr));
        pub.setBlockNumber(currentBlockNum);
        pub = publisherRepo.save(pub);

        // Fetch publisher entries
        List<FeedPublisher.PubItem> pubItems = ethPublisherAdapter.getPublisherItems(contractAddr);
        List<String> pubItemGuids = pubItems.stream().map(i -> i.data).toList();
        entryService.fetchEntries(pub, pubItemGuids);

        // Register publisher event listener
        ethPublisherAdapter.registerPubItemEventListener(contractAddr, currentBlockNum, this);

        log.info("Publisher '{}' was created.", EthUtil.shortenAddress(contractAddr));
    }

    private void removePublisher(String pubContractAddr) {
        String contractAddr = pubContractAddr.toLowerCase();

        log.info("Trying to remove publisher '{}' ...", EthUtil.shortenAddress(contractAddr));

        // Get publisher
        Optional<Publisher> pub = findPublisher(contractAddr);
        if (pub.isEmpty()) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(contractAddr));
            return;
        }

        // Unregister publisher event listener
        ethPublisherAdapter.unregisterPubItemEventListener(contractAddr);

        // Remove publisher entries
        entryService.removeEntries(contractAddr);

        // Remove publisher
        publisherRepo.deleteById(contractAddr);

        log.info("Publisher '{}' has been removed.", EthUtil.shortenAddress(contractAddr));
    }

    private void updatePublisherEventBlockNumber(String pubContractAddr, String blockNumber) {
        publisherRepo.updatePublisherBlockNumber(pubContractAddr, blockNumber);
    }

}
