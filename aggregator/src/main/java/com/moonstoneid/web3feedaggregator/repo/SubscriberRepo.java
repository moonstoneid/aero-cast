package com.moonstoneid.web3feedaggregator.repo;

import java.util.Optional;

import com.moonstoneid.web3feedaggregator.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, String> {

    Optional<Subscriber> findByAccountAddress(String accountAddress);

}
