package com.moonstoneid.aerocast.aggregator.repo;

import com.moonstoneid.aerocast.aggregator.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, String> {

    @Modifying
    @Transactional
    @Query("UPDATE Subscriber s " +
            "SET s.blockNumber = :blockNumber " +
            "WHERE s.contractAddress = :contractAddr")
    void updateSubscriberBlockNumber(String contractAddr, String blockNumber);

}
