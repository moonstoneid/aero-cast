package com.moonstoneid.web3feed.publisher.service;

import java.util.List;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import com.moonstoneid.web3feed.publisher.eth.EthPublisherAdapter;
import com.moonstoneid.web3feed.publisher.repo.EntryRepo;
import com.moonstoneid.web3feed.publisher.model.Entry;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EntryService {

    private final String baseUrl;

    private final EntryRepo entryRepo;
    private final EthPublisherAdapter ethPublisherAdapter;

    public EntryService(AppProperties appProperties, EntryRepo entryRepo,
            EthPublisherAdapter ethPublisherAdapter) {
        this.baseUrl = appProperties.getBaseUrl();
        this.entryRepo = entryRepo;
        this.ethPublisherAdapter = ethPublisherAdapter;
    }

    public List<Entry> getAllEntries() {
        return entryRepo.findAll();
    }

    public Entry getEntry(String id) {
        return entryRepo.findById(id).get();
    }

    public void saveEntry(Entry entry) {
        Assert.notNull(entry, "entry cannot be null");

        Entry savedEntry = entryRepo.save(entry);

        ethPublisherAdapter.publish(baseUrl + "/entry/" + savedEntry.getId());
    }

}
