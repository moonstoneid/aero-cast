package com.moonstoneid.web3feed.aggregator.controller;

import com.moonstoneid.web3feed.aggregator.controller.model.SubscriberVM;
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
        Subscriber subscriber = subscriberService.getSubscriber(address);
        return toViewModel(subscriber);
    }

    @PostMapping(value = "/{address}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void registerSubscriber(@PathVariable("address") String address) {
        subscriberService.createSubscriber(address);
    }

    @DeleteMapping(value = "/{address}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unregisterSubscriber(@PathVariable("address") String address) {
        subscriberService.removeSubscriber(address);
    }

    private static SubscriberVM toViewModel(Subscriber subscriber) {
        SubscriberVM subscriberVM = new SubscriberVM();
        subscriberVM.contractAddress = subscriber.getContractAddress();
        return subscriberVM;
    }

}
