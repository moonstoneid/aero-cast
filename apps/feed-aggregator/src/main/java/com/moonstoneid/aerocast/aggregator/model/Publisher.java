package com.moonstoneid.aerocast.aggregator.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Lob
    @Column(name = "favicon")
    private byte[] favicon;

    @Column(name = "block_number", length = 42, nullable = false)
    private String blockNumber;

}
