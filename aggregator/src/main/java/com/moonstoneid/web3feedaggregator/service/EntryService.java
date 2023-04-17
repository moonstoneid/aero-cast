package com.moonstoneid.web3feedaggregator.service;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.EthUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EntryService {

    private final EntryRepo entryRepo;
    // TODO: Would be better to have a PublisherService but this leads to a cyclic dependency
    private final PublisherRepo publisherRepo;

    private final EthService ethService;

    public EntryService(EntryRepo entryRepo, PublisherRepo publisherRepo, EthService ethService) {
        this.entryRepo = entryRepo;
        this.publisherRepo = publisherRepo;
        this.ethService = ethService;
    }

    public void fetchEntries(String pubContrAddr) {
        log.info("Fetching entries for publisher '{}' ...", EthUtil.shortenAddress(pubContrAddr));

        List<FeedPublisher.PubItem> pubItems = ethService.getPublisherItems(pubContrAddr);
        pubItems.forEach(pubItem -> {
            String guid = pubItem.data;
            createEntry(pubContrAddr, guid);
        });
    }

    public void createEntry(String pubContrAddr, String guid) {
        Optional<Publisher> pub = publisherRepo.findById(pubContrAddr);
        if (pub.isEmpty()) {
            return;
        }
        SyndFeed feed = getFeed(pub.get());

        createEntry(pubContrAddr, guid, feed);
    }

    private void createEntry(String pubContrAddr, String guid, SyndFeed feed) {
        if (!pubItemExists(feed, guid)) {
            return;
        }

        log.info("Fetching entry '{}/{}' ...", EthUtil.shortenAddress(pubContrAddr), guid);

        SyndEntry feedEntry = getEntry(feed, guid);
        if (!entryRepo.existsByPubAddrAndEntryURL(pubContrAddr, guid)) {
            saveEntry(pubContrAddr, feedEntry);
        }
    }

    private void saveEntry(String pubContrAddr, SyndEntry feedEntry) {
        Entry entry = new Entry();
        entry.setPubContractAddress(pubContrAddr.toLowerCase());
        entry.setNumber(getNextPubItemNumber(pubContrAddr));
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

    private int getNextPubItemNumber(String pubContrAddr) {
        Optional<Integer> max = entryRepo.findMaxNumberByPublisherContractAddress(pubContrAddr);
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

    public void removeEntriesByPublisher(String pubContrAddr) {
        entryRepo.deleteAllByPubContractAddress(pubContrAddr);
    }

}
