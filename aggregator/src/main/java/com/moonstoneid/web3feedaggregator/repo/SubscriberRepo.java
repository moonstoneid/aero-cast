package com.moonstoneid.web3feedaggregator.repo;

import com.moonstoneid.web3feedaggregator.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, String> {

    @Query("SELECT s FROM Subscriber s WHERE s.accountAddress = :id")
    Subscriber getById(String id);

}