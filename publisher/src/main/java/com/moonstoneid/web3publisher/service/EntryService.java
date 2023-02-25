package com.moonstoneid.web3publisher.service;

import com.moonstoneid.web3publisher.repo.ItemRepo;
import com.moonstoneid.web3publisher.repo.model.DbItem;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class EntryService {
    private final ItemRepo entryRepo;

    public EntryService(ItemRepo repo) {
        this.entryRepo = repo;
    }

    public List<DbItem> getAll() {
        return entryRepo.findAll();
    }

    public void save(DbItem item) {
        Assert.notNull(item, "item cannot be null");
        entryRepo.save(item);
    }

}
