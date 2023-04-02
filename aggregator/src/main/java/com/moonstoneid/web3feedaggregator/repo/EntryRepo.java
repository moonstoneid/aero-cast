package com.moonstoneid.web3feedaggregator.repo;

import java.util.List;
import java.util.Optional;

import com.moonstoneid.web3feedaggregator.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Entry.EntryId> {

    @Query("SELECT e " +
           "FROM Entry e " +
           "WHERE e.pubContractAddress IN (" +
           "SELECT s.pubContractAddress " +
           "FROM Subscription s " +
           "WHERE s.subContractAddress = :address)")
    List<Entry> findAllBySubscriberContractAddress(@Param("address") String address);

    @Query("SELECT e " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :address " +
            "AND e.url = :url")
    List<Entry> findAllByPubContractAddressAndEntryURL(@Param("address") String address, @Param("url") String url);

    @Query("SELECT MAX(e.number) " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :address")
    Optional<Integer> findMaxNumberByPublisherContractAddress(@Param("address") String address);

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM Entry e " +
            "WHERE e.pubContractAddress = :pubAddress")
    void deleteAllByPubContractAddress(String pubAddress);
}
