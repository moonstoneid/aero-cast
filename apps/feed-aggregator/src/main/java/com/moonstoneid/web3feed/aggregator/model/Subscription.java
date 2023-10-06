package com.moonstoneid.web3feed.aggregator.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "subscription")
@IdClass(Subscription.SubscriptionId.class)
public class Subscription implements Serializable {

    @Id
    @Column(name = "sub_contract_address", length = 42, nullable = false)
    private String subContractAddress;

    @Id
    @Column(name = "pub_contract_address", length = 42, nullable = false)
    private String pubContractAddress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionId implements Serializable {

        private String subContractAddress;
        private String pubContractAddress;

    }

}
