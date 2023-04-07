package com.moonstoneid.web3feedaggregator.service;

import java.util.List;

import com.moonstoneid.web3feedaggregator.eth.EthPublisherEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.eth.EthUtil;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublisherService {

    private final PublisherRepo publisherRepo;

    private final EntryService entryservice;

    private final EthService ethService;
    private final EthPublisherEventListener ethEventListener;

    public PublisherService(PublisherRepo publisherRepo, EntryService entryservice,
            EthService ethService) {
        this.publisherRepo = publisherRepo;
        this.entryservice = entryservice;
        this.ethService = ethService;
        this.ethEventListener = new EthPublisherEventListener(this, entryservice, ethService,
                ethService.getWeb3j());
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    public void initEventListener() {
        ethEventListener.registerPublisherEventListeners();
    }

    public List<Publisher> getPublishers() {
        return publisherRepo.findAll();
    }

    public Publisher createPublisher(String address) {
        Publisher publisher = publisherRepo.getById(address);
        if (publisher != null) {
            return publisher;
        }

        log.info("Trying to create publisher '{}' ...", address);

        publisher = new Publisher();
        publisher.setContractAddress(address.toLowerCase());
        publisher.setFeedUrl(ethService.getPublisherFeedUrl(address));
        publisher.setBlockNumber(ethService.getCurrentBlockNumber());
        publisherRepo.save(publisher);

        log.info("Publisher '{}' was created.", EthUtil.shortenAddress(address));

        // Fetch entries for new publisher
        entryservice.fetchEntries(address);

        ethEventListener.registerPublisherEventListener(address);

        return publisher;
    }

    public void removePublisher(String address) {
        log.info("Trying to remove publisher '{}' ...", EthUtil.shortenAddress(address));

        Publisher publisher = publisherRepo.getById(address);
        if (publisher == null) {
            log.info("Publisher '{}' was not found.", EthUtil.shortenAddress(address));
            return;
        }
        ethEventListener.unregisterPublisherEventListener(address);
        entryservice.removeEntriesByPublisher(address);
        publisherRepo.deleteById(address);

        log.info("Publisher '{}' has been removed.", EthUtil.shortenAddress(address));
    }

}
