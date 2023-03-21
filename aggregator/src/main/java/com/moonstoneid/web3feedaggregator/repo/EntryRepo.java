package com.moonstoneid.web3feedaggregator.repo;

import com.moonstoneid.web3feedaggregator.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Entry.EntryId> {

}
