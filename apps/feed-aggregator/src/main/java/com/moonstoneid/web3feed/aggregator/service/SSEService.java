package com.moonstoneid.web3feed.aggregator.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moonstoneid.web3feed.aggregator.model.EntryDTO;
import com.moonstoneid.web3feed.aggregator.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class SSEService implements EntryService.EventListener {

    private final SubscriberService subscriberService;
    private final EntryService entryService;

    private final Map<String, Set<SseEmitter>> emitters = new HashMap<>();

    public SSEService (SubscriberService subscriberService, EntryService entryService) {
        this.subscriberService = subscriberService;
        this.entryService = entryService;
    }

    // Register listeners after Spring Boot has started
    @EventListener(ApplicationReadyEvent.class)
    protected void initEventListener() {
        entryService.registerEventListener(this);
    }

    public void registerEmitter(String subContractAddr, SseEmitter emitter) {
        Set<SseEmitter> em = emitters.get(subContractAddr);
        if (em == null) {
            em = new HashSet<>();
            emitters.put(subContractAddr, em);
        }
        em.add(emitter);
    }

    @Override
    public void onNewEntry(EntryDTO entry) {
        sendUpdate(entry);
    }

    private void sendUpdate(EntryDTO entry) {
        List<Subscription> subscriptions = subscriberService.getPublisherSubscriptions(
                entry.getPubContractAddress());
        for (Subscription subscription : subscriptions) {
            String subContractAddr = subscription.getSubContractAddress();
            sendSubscriberUpdate(subContractAddr, entry);
        }
    }

    private void sendSubscriberUpdate(String subContractAddr, EntryDTO entry) {
        Set<SseEmitter> sseEmitters = emitters.get(subContractAddr);
        if (sseEmitters == null) {
            return;
        }
        Iterator<SseEmitter> iterator = sseEmitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                emitter.send(entry);
            } catch (IOException e) {
                emitter.complete();
                iterator.remove();
            }
        }
    }

}
