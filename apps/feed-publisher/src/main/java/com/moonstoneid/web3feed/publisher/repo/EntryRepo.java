package com.moonstoneid.web3feed.publisher.repo;

import com.moonstoneid.web3feed.publisher.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepo extends JpaRepository<Entry, String> {

}
