package com.moonstoneid.web3feedaggregator.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.moonstoneid.web3feedaggregator.model.Publisher;
import com.moonstoneid.web3feedaggregator.model.Subscriber;
import com.moonstoneid.web3feedaggregator.model.Subscription;
import com.moonstoneid.web3feedaggregator.repo.PublisherRepo;
import com.moonstoneid.web3feedaggregator.repo.SubscriberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private SubscriberRepo subRepo;
    @Autowired
    private PublisherRepo pubRepo;

    public void test() {
        Publisher p1 = new Publisher();
        p1.setContractAddress("pc1");
        p1.setFeedUrl("purl1");
        pubRepo.save(p1);
        Publisher p2 = new Publisher();
        p2.setContractAddress("pc2");
        p2.setFeedUrl("purl2");
        pubRepo.save(p2);

        Subscription sp1 = new Subscription();
        sp1.setSubContractAddress("sc1");
        sp1.setPubContractAddress("pc1");

        Subscription sp2 = new Subscription();
        sp2.setSubContractAddress("sc1");
        sp2.setPubContractAddress("pc2");

        Subscriber s1 = new Subscriber();
        s1.setAccountAddress("sa1");
        s1.setContractAddress("sc1");
        s1.setSubscriptions(Stream.of(sp1, sp2).collect(Collectors.toList()));
        subRepo.save(s1);
    }

}
