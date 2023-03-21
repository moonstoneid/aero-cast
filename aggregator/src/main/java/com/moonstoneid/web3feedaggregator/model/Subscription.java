package com.moonstoneid.web3feedaggregator.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "subscription")
@IdClass(Subscription.SubscriptionId.class)
public class Subscription {

    @Id
    @Column(name = "sub_contract_address", length = 42, nullable = false)
    private String subContactAddress;

    @Id
    @Column(name = "pub_contract_address", length = 42, nullable = false)
    private String pubContactAddress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionId implements Serializable {

        private String subContactAddress;
        private String pubContactAddress;

    }

}
