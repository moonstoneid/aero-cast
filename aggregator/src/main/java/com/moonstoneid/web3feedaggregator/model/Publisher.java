package com.moonstoneid.web3feedaggregator.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "publisher")
public class Publisher implements Serializable {

    @Id
    @Column(name = "contract_address", length = 42, nullable = false)
    private String contractAddress;

    @Column(name = "feed_url", length = 500, nullable = false)
    private String feedUrl;

    @Column(name = "block_number", length = 42, nullable = false)
    private String blockNumber;
}
