package com.moonstoneid.web3feed.publisher.service;

import java.util.List;

import com.moonstoneid.web3feed.publisher.eth.EthPublisherService;
import com.moonstoneid.web3feed.publisher.repo.EntryRepo;
import com.moonstoneid.web3feed.publisher.model.Entry;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EntryService {

    private final EntryRepo entryRepo;
    private final EthPublisherService ethPublisherService;

    public EntryService(EntryRepo entryRepo, EthPublisherService ethPublisherService) {
        this.entryRepo = entryRepo;
        this.ethPublisherService = ethPublisherService;
    }

    public List<Entry> getAllEntries() {
        return entryRepo.findAll();
    }

    public Entry getEntry(String id) {
        return entryRepo.findById(id).get();
    }

    public void saveEntry(String url, Entry entry) {
        Assert.notNull(entry, "url cannot be null");
        Assert.notNull(entry, "entry cannot be null");

        Entry savedEntry = entryRepo.save(entry);

        // Publish to web3
        ethPublisherService.publish(url + "/feed/entry/" + savedEntry.getId());
    }

}
