package com.moonstoneid.web3feed.aggregator.repo;

import com.moonstoneid.web3feed.aggregator.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription, String> {

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM Subscription e " +
            "WHERE e.subContractAddress = :subAddress AND e.pubContractAddress = :pubAddress")
    void deleteById (@Param("subAddress") String subContractAddress, @Param("pubAddress")  String pubContractAddress);
}
