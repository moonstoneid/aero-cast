package com.moonstoneid.web3feedaggregator.service;

import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import org.springframework.stereotype.Service;

@Service
public class EntryService {

    private final EntryRepo entryRepo;
    private final PublisherRepo publisherRepo;

    private final EthService ethService;

    public EntryService(EntryRepo entryRepo, PublisherRepo publisherRepo, EthService ethService) {
        this.entryRepo = entryRepo;
        this.publisherRepo = publisherRepo;

        this.ethService = ethService;
    }

}
