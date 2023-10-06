package com.moonstoneid.web3feed.aggregator.repo;

import com.moonstoneid.web3feed.aggregator.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, String> {

}
