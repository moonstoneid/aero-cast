package com.moonstoneid.aerocast.aggregator.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.aerocast.aggregator.eth.EthPublisherAdapter;
import com.moonstoneid.aerocast.aggregator.repo.SubscriptionRepo;
import com.moonstoneid.aerocast.aggregator.error.NotFoundException;
import com.moonstoneid.aerocast.aggregator.model.Publisher;
import com.moonstoneid.aerocast.aggregator.repo.PublisherRepo;
import com.moonstoneid.aerocast.common.eth.EthUtil;
import com.moonstoneid.aerocast.common.eth.contracts.FeedPublisher;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublisherService implements EthPublisherAdapter.EventListener {

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
    public void initEventListener() {
        getPublishers().forEach(p -> ethPublisherAdapter.registerEventListener(
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
        String pubFeedUrl = pub.get().getFeedUrl();

        // Get publisher feed
        URL feedUrl = parsePublisherFeedUrl(contractAddr, pubFeedUrl);
        SyndFeed feed = getPublisherFeed(contractAddr, feedUrl);

        // Update publisher event block number
        updatePublisherEventBlockNumber(contractAddr, blockNumber);

        // Fetch entry
        entryService.fetchEntry(contractAddr, feed, pubItem.data);
    }

    public List<Publisher> getPublishers() {
        return publisherRepo.findAll();
    }

    private Optional<Publisher> findPublisher(String pubContractAddr) {
        String contractAddr = pubContractAddr.toLowerCase();
        return publisherRepo.findById(contractAddr);
    }

    public Publisher getPublisher(String pubContractAddr) {
        Optional<Publisher> publisher = findPublisher(pubContractAddr);
        return publisher
                .orElseThrow(() -> new NotFoundException("Publisher was not found!"));
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

        // Get publisher feed
        String pubFeedUrl = ethPublisherAdapter.getPublisherFeedUrl(pubContractAddr);
        URL feedUrl = parsePublisherFeedUrl(contractAddr, pubFeedUrl);
        SyndFeed feed = getPublisherFeed(contractAddr, feedUrl);

        // Get publisher favicon
        byte[] favicon = fetchPublisherFavicon(pubContractAddr, feedUrl);

        // Create publisher
        Publisher pub = new Publisher();
        pub.setContractAddress(contractAddr);
        pub.setFeedUrl(feedUrl.toString());
        pub.setName(feed.getTitle());
        pub.setFavicon(favicon);
        pub.setBlockNumber(currentBlockNum);
        publisherRepo.save(pub);

        // Fetch publisher entries
        List<FeedPublisher.PubItem> pubItems = ethPublisherAdapter.getPublisherItems(contractAddr);
        List<String> pubItemGuids = pubItems.stream().map(i -> i.data).toList();
        entryService.fetchEntries(pubContractAddr, feed, pubItemGuids);

        // Register publisher event listener
        ethPublisherAdapter.registerEventListener(contractAddr, currentBlockNum, this);

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
        ethPublisherAdapter.unregisterEventListener(contractAddr);

        // Remove publisher entries
        entryService.removeEntries(contractAddr);

        // Remove publisher
        publisherRepo.deleteById(contractAddr);

        log.info("Publisher '{}' has been removed.", EthUtil.shortenAddress(contractAddr));
    }

    private URL parsePublisherFeedUrl(String pubContractAddr, String feedUrl) {
        try {
            return new URL(feedUrl);
        } catch (Exception e) {
            log.error("Could not parse feed URL for publisher {}!", EthUtil.shortenAddress(
                    pubContractAddr), e);
            throw new RuntimeException("Could parse feed URL: " + feedUrl);
        }
    }

    private SyndFeed getPublisherFeed(String pubContractAddr, URL feedUrl) {
        try {
            return new SyndFeedInput().build(new XmlReader(feedUrl));
        } catch (FeedException | IOException e) {
            log.info("Could not read feed from URL '{}' for publisher '{}' ...", feedUrl,
                    EthUtil.shortenAddress(pubContractAddr));
            throw new RuntimeException("Could not read RSS feed from URL: " + feedUrl);
        }
    }

    private byte[] fetchPublisherFavicon(String pubContractAddr, URL feedUrl) {
        // Build favicon URL
        URL url;
        try {
            url = new URL(feedUrl.getProtocol(), feedUrl.getHost(), feedUrl.getPort(), "/favicon.ico");
        } catch (Exception e) {
            log.error("Could not build favicon URL for Publisher {}!", EthUtil.shortenAddress(
                    pubContractAddr), e);
            return null;
        }

        // Fetch favicon
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            return in.readAllBytes();
        } catch (IOException e) {
            log.error("Could not fetch favicon for Publisher {}!", EthUtil.shortenAddress(
                    pubContractAddr), e);
            return null;
        }
    }

    private void updatePublisherEventBlockNumber(String pubContractAddr, String blockNumber) {
        publisherRepo.updatePublisherBlockNumber(pubContractAddr, blockNumber);
    }

}
