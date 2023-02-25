package com.moonstoneid.web3publisher.repo;

import com.moonstoneid.web3publisher.repo.model.DbItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepo extends JpaRepository<DbItem, String> {

}
