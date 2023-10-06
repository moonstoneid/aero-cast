package com.moonstoneid.web3feed.aggregator.service;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.eth.EthPublisherService;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.common.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feed.aggregator.model.Entry;
import com.moonstoneid.web3feed.aggregator.model.Publisher;
import com.moonstoneid.web3feed.aggregator.repo.EntryRepo;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EntryService {

    private final EntryRepo entryRepo;
    private final PublisherService publisherService;
    private final EthPublisherService ethPublisherService;

    public EntryService(EntryRepo entryRepo, PublisherService publisherService,
            EthPublisherService ethPublisherService) {
        this.entryRepo = entryRepo;
        this.publisherService = publisherService;
        this.ethPublisherService = ethPublisherService;
    }

    public void fetchEntries(String pubContractAddr) {
        log.info("Fetching entries for publisher '{}' ...", EthUtil.shortenAddress(pubContractAddr));

        List<FeedPublisher.PubItem> pubItems = ethPublisherService.getPublisherItems(pubContractAddr);
        pubItems.forEach(pubItem -> {
            String guid = pubItem.data;
            createEntry(pubContractAddr, guid);
        });
    }

    public void createEntry(String pubContractAddr, String guid) {
        Optional<Publisher> pub = publisherService.findPublisher(pubContractAddr);
        if (pub.isEmpty()) {
            return;
        }
        SyndFeed feed = getFeed(pub.get());

        createEntry(pubContractAddr, guid, feed);
    }

    private void createEntry(String pubContractAddr, String guid, SyndFeed feed) {
        if (!pubItemExists(feed, guid)) {
            return;
        }

        log.info("Fetching entry '{}/{}' ...", EthUtil.shortenAddress(pubContractAddr), guid);

        SyndEntry feedEntry = getEntry(feed, guid);
        if (!entryRepo.existsByPubAddrAndEntryURL(pubContractAddr, guid)) {
            saveEntry(pubContractAddr, feedEntry);
        }
    }

    private void saveEntry(String pubContractAddr, SyndEntry feedEntry) {
        Entry entry = new Entry();
        entry.setPubContractAddress(pubContractAddr.toLowerCase());
        entry.setNumber(getNextPubItemNumber(pubContractAddr));
        entry.setTitle(feedEntry.getTitle());
        entry.setDescription(feedEntry.getDescription().getValue());
        entry.setDate(feedEntry.getPublishedDate().toInstant().atOffset(ZoneOffset.UTC));
        entry.setUrl(feedEntry.getUri());
        entryRepo.save(entry);
    }

    private SyndFeed getFeed(Publisher publisher) {
        SyndFeed feed;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(publisher.getFeedUrl())));
        } catch (FeedException | IOException e) {
            log.info("Could not read RSS feed from URL '{}' for for publisher '{}' ...",
                    publisher.getFeedUrl(), EthUtil.shortenAddress(publisher.getContractAddress()));
            throw new RuntimeException("Could not read RSS feed from URL: " + publisher.getFeedUrl());
        }
        return feed;
    }

    private boolean pubItemExists(SyndFeed feed, String guid) {
        boolean exists = false;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                exists = true;
            }
        }
        return exists;
    }

    private int getNextPubItemNumber(String pubContractAddr) {
        Optional<Integer> max = entryRepo.findMaxNumberByPublisherContractAddress(pubContractAddr);
        if (max.isEmpty()) {
            return 1;
        } else {
            return max.get() + 1;
        }
    }

    private SyndEntry getEntry(SyndFeed feed, String guid) {
        SyndEntry entry = null;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                entry = feedEntry;
            }
        }
        return entry;
    }

    public void removeEntriesByPublisher(String pubContractAddr) {
        entryRepo.deleteAllByPubContractAddress(pubContractAddr);
    }

}
