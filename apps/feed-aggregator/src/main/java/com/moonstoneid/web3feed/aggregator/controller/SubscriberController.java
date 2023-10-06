package com.moonstoneid.web3feed.aggregator.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.moonstoneid.web3feed.aggregator.controller.model.EntryVM;
import com.moonstoneid.web3feed.aggregator.controller.model.SubscriberVM;
import com.moonstoneid.web3feed.aggregator.model.Entry;
import com.moonstoneid.web3feed.aggregator.model.Subscriber;
import com.moonstoneid.web3feed.aggregator.service.SubscriberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/subscriber")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping(value = "/{address}", produces = { "application/json" })
    public @ResponseBody SubscriberVM getSubscriber(@PathVariable("address") String address) {
        Subscriber subscriber = subscriberService.findSubscriberByAccountAddress(address);
        return toViewModel(subscriber);
    }

    @PostMapping(value = "/{address}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void registerSubscriber(@PathVariable("address") String address) {
        subscriberService.createSubscriberIfNotExists(address);
    }

    @DeleteMapping(value = "/{address}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unregisterSubscriber(@PathVariable("address") String address) {
        subscriberService.removeSubscriber(address);
    }

    @GetMapping(value = "/{address}/entries", produces = { "application/json" })
    public @ResponseBody List<EntryVM> getEntries(@PathVariable("address") String address) {
        List<Entry> entries = subscriberService.getEntriesBySubscriberAccountAddress(address);
        return toViewModel(entries);
    }

    private static SubscriberVM toViewModel(Subscriber subscriber) {
        SubscriberVM subscriberVM = new SubscriberVM();
        subscriberVM.contractAddress = subscriber.getContractAddress();
        return subscriberVM;
    }

    private static List<EntryVM> toViewModel(List<Entry> entries) {
        return entries.stream()
                .map(SubscriberController::toViewModel)
                .collect(Collectors.toList());
    }

    private static EntryVM toViewModel(Entry entry) {
        EntryVM entryVM = new EntryVM();
        entryVM.title = entry.getTitle();
        entryVM.description = entry.getDescription();
        entryVM.date = entry.getDate();
        entryVM.url = entry.getUrl();
        return entryVM;
    }

}
