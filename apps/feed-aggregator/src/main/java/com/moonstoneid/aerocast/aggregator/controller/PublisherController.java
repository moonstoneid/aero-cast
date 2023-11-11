package com.moonstoneid.aerocast.aggregator.controller;

import com.moonstoneid.aerocast.aggregator.service.PublisherService;
import com.moonstoneid.aerocast.aggregator.model.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/publisher")
@Slf4j
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping(value = "/{address}/favicon.ico", produces = { "image/x-icon" })
    public @ResponseBody byte[] getFavIcon(@PathVariable("address") String address) {
        Publisher publisher = publisherService.getPublisher(address);
        if (publisher.getFavicon() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return publisher.getFavicon();
    }

}
