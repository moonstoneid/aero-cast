package com.moonstoneid.web3feed.aggregator.service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feed.aggregator.model.EntryDTO;
import com.moonstoneid.web3feed.common.eth.EthUtil;
import com.moonstoneid.web3feed.aggregator.model.Entry;
import com.moonstoneid.web3feed.aggregator.repo.EntryRepo;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EntryService {

    public interface EventListener {
        void onNewEntry(EntryDTO entry);
    }

    private final EntryRepo entryRepo;

    private final List<EventListener> eventListeners = new ArrayList<>();

    public EntryService(EntryRepo entryRepo) {
        this.entryRepo = entryRepo;
    }

    public void registerEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    public void unregisterEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    public void fetchEntries(String pubContractAddr, SyndFeed feed, List<String> guids) {
        guids.forEach(guid -> fetchEntry(pubContractAddr, feed, guid));
    }

    public void fetchEntry(String pubContractAddr, SyndFeed feed, String guid) {
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

        notifyNewEntry(pubContractAddr, entryNumber);
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

    public void removeEntries(String pubContractAddr) {
        entryRepo.deleteAllByPublisherContractAddress(pubContractAddr);
    }

    public List<EntryDTO> getSubscriberEntries(String subContractAddr) {
        return entryRepo.findAllSubscriberEntries(subContractAddr);
    }

    private void notifyNewEntry(String pubContractAddr, Integer number) {
        Optional<EntryDTO> entry = findPublisherEntry(pubContractAddr, number);
        entry.ifPresent(entryDTO -> eventListeners.forEach(l -> l.onNewEntry(entryDTO)));
    }

    private Optional<EntryDTO> findPublisherEntry(String pubContractAddr, Integer number) {
        return entryRepo.findPublisherEntry(pubContractAddr, number);
    }

}
