package com.moonstoneid.web3feedaggregator.service;

import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.contracts.FeedPublisher;
import com.moonstoneid.web3feedaggregator.model.Entry;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class EntryService {

    private final EntryRepo entryRepo;

    // TODO: Would be better to have a PublisherService but this leads to a cyclic dependency
    private final PublisherRepo publisherRepo;
    private final EthService ethService;

    public EntryService(EntryRepo entryRepo, EthService ethService, PublisherRepo publisherRepo) {
        this.entryRepo = entryRepo;
        this.ethService = ethService;
        this.publisherRepo = publisherRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchEntries() {
        publisherRepo.findAll().forEach(pub -> {
            List<FeedPublisher.PubItem> pubItems = ethService.getPublisherItems(pub.getContractAddress());
            pubItems.forEach(pubItem -> {
                String guid = pubItem.data;
                createEntry(guid, pub.getContractAddress());
            });
        });
    }

    public void createEntry(String guid, String pubAddress) {
        Publisher pub =  publisherRepo.getById(pubAddress);
        SyndFeed feed = getFeed(pub.getFeedUrl());

        if (pubItemExists(guid, feed)) {
            SyndEntry feedEntry = getEntry(guid, feed);

            if (!entryRepo.existsByPubAddrAndEntryURL(pub.getContractAddress(), guid)) {
                saveEntry(pub.getContractAddress(), feedEntry);
            }
        }
    }

    private void saveEntry(String pubAddress, SyndEntry feedEntry) {
        Entry entry = new Entry();
        entry.setPubContractAddress(pubAddress.toLowerCase());
        entry.setUrl(feedEntry.getUri());
        entry.setTitle(feedEntry.getTitle());
        entry.setDescription(feedEntry.getDescription().getValue());
        entry.setDate(feedEntry.getPublishedDate().toInstant().atOffset(ZoneOffset.UTC));
        entry.setNumber(getNumber(pubAddress));
        entryRepo.save(entry);
    }

    private SyndFeed getFeed(String url) {
        SyndFeed feed;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        } catch (FeedException | IOException e) {
            throw new RuntimeException("Cannot read RSS feed from URL: " + url);
        }
        return feed;
    }

    private int getNumber(String pubAddress) {
        Optional<Integer> max = entryRepo.findMaxNumberByPublisherContractAddress(pubAddress);
        if (max.isEmpty()) {
            return 1;
        } else {
            return max.get() + 1;
        }
    }

    private boolean pubItemExists(String guid, SyndFeed feed) {
        boolean exists = false;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                exists = true;
            }
        }
        return exists;
    }

    private SyndEntry getEntry(String guid, SyndFeed feed) {
        SyndEntry entry = null;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                entry = feedEntry;
            }
        }
        return entry;
    }

    public void removeEntriesByPublisher(String pubAddress) {
        entryRepo.deleteAllByPubContractAddress(pubAddress);
    }

}