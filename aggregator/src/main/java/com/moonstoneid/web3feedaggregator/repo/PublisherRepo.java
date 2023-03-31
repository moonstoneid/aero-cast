package com.moonstoneid.web3feedaggregator.repo;

import com.moonstoneid.web3feedaggregator.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepo extends JpaRepository<Publisher, String> {

    @Query("SELECT p FROM Publisher p WHERE p.contractAddress = :id")
    Publisher getById(String id);

}
