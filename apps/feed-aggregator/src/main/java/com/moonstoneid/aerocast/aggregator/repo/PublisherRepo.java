package com.moonstoneid.aerocast.aggregator.repo;

import com.moonstoneid.aerocast.aggregator.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PublisherRepo extends JpaRepository<Publisher, String> {

    @Modifying
    @Transactional
    @Query("UPDATE Publisher p SET p.blockNumber = :blockNumber WHERE p.contractAddress = :contractAddr")
    void updatePublisherBlockNumber(String contractAddr, String blockNumber);

}
