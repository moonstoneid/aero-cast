package com.moonstoneid.web3feedaggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.error.NotFoundException;
import com.moonstoneid.web3feedaggregator.model.Entry;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.repo.EntryRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;
    private final EntryRepo entryRepo;

    public SubscriberService(SubscriberRepo subscriberRepo, EntryRepo entryRepo) {
        this.subscriberRepo = subscriberRepo;
        this.entryRepo = entryRepo;
    }

    public Subscriber findSubscriberByAccountAddress(String address) {
        Optional<Subscriber> subscriber = subscriberRepo.findByAccountAddress(address);
        return subscriber
                .orElseThrow(() -> new NotFoundException("Subscriber was not found!"));
    }

    public void registerSubscriberByAccountAddress(String address) {
        //throw new NotFoundException("Subscriber was not found!");
        // TODO!!!
    }

    public void unregisterSubscriberByAccountAddress(String address) {
        //throw new NotFoundException("Subscriber was not found!");
        // TODO!!!
    }

    public List<Entry> getEntriesBySubscriberAccountAddress(String address) {
        //throw new NotFoundException("Subscriber was not found!");
        // TODO!!!
        return new ArrayList<>();
    }

}
