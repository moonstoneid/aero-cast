package com.moonstoneid.web3feedaggregator.service;

import java.util.List;

import com.moonstoneid.web3feedaggregator.eth.EthPublisherEventListener;
import com.moonstoneid.web3feedaggregator.eth.EthService;
import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    private final PublisherRepo publisherRepo;

    private final EthService ethService;
    private final EthPublisherEventListener ethEventListener;

    public PublisherService(PublisherRepo publisherRepo, EthService ethService) {
        this.publisherRepo = publisherRepo;

        this.ethService = ethService;
        this.ethEventListener = new EthPublisherEventListener(this, ethService.getWeb3j());
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

        publisher = new Publisher();
        publisher.setContractAddress(address);
        publisher.setFeedUrl(ethService.getPublisherFeedUrl(address));
        publisherRepo.save(publisher);

        // TODO: Fetch publisher items

        ethEventListener.registerPublisherEventListener(publisher);

        return publisher;
    }

    public void removePublisher(String address) {
        // TODO: Cleanup publisher items
    }

}
