package com.moonstoneid.web3feed.aggregator.repo;

import java.util.List;

import com.moonstoneid.web3feed.aggregator.model.Entry;
import com.moonstoneid.web3feed.aggregator.model.EntryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Entry.EntryId> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :pubContractAddr " +
            "AND e.url = :url")
    boolean existsByPublisherContractAddressAndUrl(String pubContractAddr, String url);

    @Query("SELECT COALESCE(MAX(e.number), 0) " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :pubContractAddr")
    int getMaxNumberByPublisherContractAddress(String pubContractAddr);

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :pubContractAddr")
    void deleteAllByPublisherContractAddress(String pubContractAddr);

    @Query("SELECT p.contractAddress AS pubContractAddress, p.name AS pubName, e.title AS title, " +
            "e.description AS description, e.date AS date, e.url AS url " +
            "FROM Entry e, Publisher p " +
            "WHERE e.pubContractAddress IN (" +
            "SELECT s.pubContractAddress " +
            "FROM Subscription s " +
            "WHERE s.subContractAddress = :subContractAddr)")
    List<EntryDTO> findAllBySubscriberContractAddress(String subContractAddr);

}
