package com.moonstoneid.aerocast.aggregator.controller;

import com.moonstoneid.aerocast.aggregator.model.Subscriber;
import com.moonstoneid.aerocast.aggregator.service.SubscriberService;
import com.moonstoneid.aerocast.aggregator.service.SSEService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Implements a controller for Server Sent Events (SSE). This allows a client-side EventSource to
 * establish a persistent connection through which updates can be sent.
 */

@RestController
@RequestMapping(path = "/sse")
public class SSEController {

    private final SSEService sseService;
    private final SubscriberService subscriberService;

    public SSEController(SSEService sseService, SubscriberService subscriberService) {
        this.sseService = sseService;
        this.subscriberService = subscriberService;
    }

    @GetMapping(path = "/{address}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("address") String address) {
        Subscriber subscriber = subscriberService.getSubscriber(address);

        SseEmitter emitter = new SseEmitter(-1L);
        sseService.registerEmitter(subscriber.getContractAddress(), emitter);
        return emitter;
    }

}
