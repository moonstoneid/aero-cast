package com.moonstoneid.aerocast.aggregator.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.moonstoneid.aerocast.aggregator.model.EntryDTO;
import com.moonstoneid.aerocast.aggregator.service.EntryService;
import com.moonstoneid.aerocast.aggregator.service.SubscriberService;
import com.moonstoneid.aerocast.aggregator.controller.model.EntryVM;
import com.moonstoneid.aerocast.aggregator.model.Subscriber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/subscriber")
public class EntryController {

    private final SubscriberService subscriberService;
    private final EntryService entryService;

    public EntryController(SubscriberService subscriberService, EntryService entryService) {
        this.subscriberService = subscriberService;
        this.entryService = entryService;
    }

    @GetMapping(value = "/{address}/entries", produces = { "application/json" })
    public @ResponseBody List<EntryVM> getEntries(@PathVariable("address") String address) {
        Subscriber subscriber = subscriberService.getSubscriber(address);
        List<EntryDTO> entries = entryService.getSubscriberEntries(
                subscriber.getContractAddress());
        return toViewModel(entries);
    }

    private static List<EntryVM> toViewModel(List<EntryDTO> entries) {
        return entries.stream()
                .map(EntryController::toViewModel)
                .collect(Collectors.toList());
    }

    private static EntryVM toViewModel(EntryDTO entry) {
        EntryVM entryVM = new EntryVM();
        entryVM.pubContractAddress = entry.getPubContractAddress();
        entryVM.pubName = entry.getPubName();
        entryVM.title = entry.getTitle();
        entryVM.description = entry.getDescription();
        entryVM.date = entry.getDate();
        entryVM.url = entry.getUrl();
        return entryVM;
    }

}
