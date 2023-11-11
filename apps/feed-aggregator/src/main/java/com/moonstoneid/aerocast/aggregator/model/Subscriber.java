package com.moonstoneid.aerocast.aggregator.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "subscriber")
public class Subscriber implements Serializable {

    @Id
    @Column(name = "account_address", length = 42, nullable = false)
    private String accountAddress;

    @Column(name = "contract_address", length = 42, nullable = false)
    private String contractAddress;

    @Column(name = "block_number", length = 42, nullable = false)
    private String blockNumber;

}
