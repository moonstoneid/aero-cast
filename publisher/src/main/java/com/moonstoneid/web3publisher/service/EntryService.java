package com.moonstoneid.web3publisher.service;

import java.util.List;

import com.moonstoneid.web3publisher.eth.Publisher;
import com.moonstoneid.web3publisher.repo.EntryRepo;
import com.moonstoneid.web3publisher.model.Entry;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EntryService {

    private final EntryRepo entryRepo;
    private final Publisher publisher;

    public EntryService(EntryRepo entryRepo, Publisher publisher) {
        this.entryRepo = entryRepo;
        this.publisher = publisher;
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
        publisher.publish(url + "/" + savedEntry.getId());
    }

}
