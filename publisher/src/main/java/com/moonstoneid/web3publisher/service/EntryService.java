package com.moonstoneid.web3publisher.service;

import com.moonstoneid.web3publisher.eth.Publisher;
import com.moonstoneid.web3publisher.repo.ItemRepo;
import com.moonstoneid.web3publisher.repo.model.DbItem;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class EntryService {
    private final ItemRepo entryRepo;
    private final Publisher pub;

    public EntryService(ItemRepo repo,  Publisher pub) {
        this.entryRepo = repo;
        this.pub = pub;
    }

    public List<DbItem> getAll() {
        return entryRepo.findAll();
    }

    public DbItem getItem(String id) {
        return entryRepo.findById(id).get();
    }

    public void save(DbItem item, String url) {
        Assert.notNull(item, "item cannot be null");
        DbItem savedItem = entryRepo.save(item);

        // Publish to web3
        pub.publish(url + "/" + savedItem.getId());
    }

}
