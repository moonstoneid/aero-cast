package com.moonstoneid.web3feedaggregator.repo;

import java.util.List;

import com.moonstoneid.web3feedaggregator.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Entry.EntryId> {

    @Query("SELECT e " +
           "FROM Entry e " +
           "WHERE e.pubContractAddress IN (" +
           "SELECT s.pubContractAddress " +
           "FROM Subscription s " +
           "WHERE s.subContractAddress = :address)")
    List<Entry> findAllBySubscriberContractAddress(@Param("address") String address);

}
