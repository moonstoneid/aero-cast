package com.moonstoneid.web3feedaggregator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "publisher")
public class Publisher {

    @Id
    @Column(name = "contract_address", length = 42, nullable = false)
    private String contactAddress;

    @Column(name = "feed_url", length = 500, nullable = false)
    private String feedUrl;

}
