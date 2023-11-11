package com.moonstoneid.aerocast.aggregator.repo;

import com.moonstoneid.aerocast.aggregator.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription, String> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Subscription s " +
            "WHERE s.pubContractAddress IN (" +
            "SELECT p.contractAddress " +
            "FROM Publisher p " +
            "WHERE p.contractAddress = :pubContractAddr)")
    boolean existsByPublisherContractAddress(String pubContractAddr);

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM Subscription s " +
            "WHERE s.subContractAddress = :subContractAddr " +
            "AND s.pubContractAddress = :pubContractAddr")
    void deleteById(String subContractAddr, String pubContractAddr);


    @Query("SELECT s " +
            "FROM Subscription s " +
            "WHERE s.pubContractAddress = :pubContractAddr")
    List<Subscription> findAllByPublisherContractAddress(String pubContractAddr);

}
