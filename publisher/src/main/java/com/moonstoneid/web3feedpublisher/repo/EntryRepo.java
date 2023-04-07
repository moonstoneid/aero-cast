package com.moonstoneid.web3feedpublisher.repo;

import com.moonstoneid.web3feedpublisher.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepo extends JpaRepository<Entry, String> {

}
