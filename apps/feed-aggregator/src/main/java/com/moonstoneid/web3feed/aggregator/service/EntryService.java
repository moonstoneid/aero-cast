package com.moonstoneid.web3feed.aggregator.service;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.List;

import com.moonstoneid.web3feed.common.eth.EthUtil;
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

    public EntryService(EntryRepo entryRepo) {
        this.entryRepo = entryRepo;
    }

    public void fetchEntries(Publisher pub, List<String> guids) {
        SyndFeed feed = getFeed(pub.getContractAddress(), pub.getFeedUrl());
        guids.forEach(guid -> fetchEntry(pub.getContractAddress(), feed, guid));
    }

    public void fetchEntry(Publisher pub, String guid) {
        SyndFeed feed = getFeed(pub.getContractAddress(), pub.getFeedUrl());
        fetchEntry(pub.getContractAddress(), feed, guid);
    }

    private SyndFeed getFeed(String pubContractAddr, String feedUrl) {
        SyndFeed feed;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
        } catch (FeedException | IOException e) {
            log.info("Could not read RSS feed from URL '{}' for publisher '{}' ...", feedUrl,
                    EthUtil.shortenAddress(pubContractAddr));
            throw new RuntimeException("Could not read RSS feed from URL: " + feedUrl);
        }
        return feed;
    }

    private void fetchEntry(String pubContractAddr, SyndFeed feed, String guid) {
        log.info("Fetching entry '{}/{}' ...", EthUtil.shortenAddress(pubContractAddr), guid);

        if (!existsFeedEntry(feed, guid)) {
            log.info("RSS entry for entry '{}/{}' is not available.",
                    EthUtil.shortenAddress(pubContractAddr), guid);
            return;
        }

        if (existsEntry(pubContractAddr, guid)) {
            log.info("Entry '{}/{}' already exists.", EthUtil.shortenAddress(pubContractAddr),
                    guid);
            return;
        }

        SyndEntry feedEntry = getFeedEntry(feed, guid);

        int entryNumber = getNextEntryNumber(pubContractAddr);
        saveEntry(pubContractAddr, entryNumber, feedEntry);
    }

    private boolean existsFeedEntry(SyndFeed feed, String guid) {
        boolean exists = false;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                exists = true;
            }
        }
        return exists;
    }

    private SyndEntry getFeedEntry(SyndFeed feed, String guid) {
        SyndEntry entry = null;
        for (SyndEntry feedEntry : feed.getEntries()) {
            if (feedEntry.getUri().equals(guid)) {
                entry = feedEntry;
            }
        }
        return entry;
    }

    private boolean existsEntry(String pubContractAddr, String guid) {
        return entryRepo.existsByPublisherContractAddressAndUrl(pubContractAddr, guid);
    }

    private int getNextEntryNumber(String pubContractAddr) {
        return entryRepo.getMaxNumberByPublisherContractAddress(pubContractAddr) + 1;
    }

    private void saveEntry(String pubContractAddr, int number, SyndEntry feedEntry) {
        Entry entry = new Entry();
        entry.setPubContractAddress(pubContractAddr);
        entry.setNumber(number);
        entry.setTitle(feedEntry.getTitle());
        entry.setDescription(feedEntry.getDescription().getValue());
        entry.setDate(feedEntry.getPublishedDate().toInstant().atOffset(ZoneOffset.UTC));
        entry.setUrl(feedEntry.getUri());
        entryRepo.save(entry);
    }

    public List<Entry> getEntriesBySubscriberContractAddress(String subContractAddr) {
        return entryRepo.findAllBySubscriberContractAddress(subContractAddr);
    }

    public void removeEntries(String pubContractAddr) {
        entryRepo.deleteAllByPublisherContractAddress(pubContractAddr);
    }

}
