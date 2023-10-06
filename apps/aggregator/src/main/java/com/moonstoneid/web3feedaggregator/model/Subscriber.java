package com.moonstoneid.web3feedaggregator.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

    // TODO: This is a workaround to prevent LazyInitializationException, should be fixed for prod
    // Added updatable = false to prevent Hibernate from updating the foreign key with null
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sub_contract_address", referencedColumnName = "contract_address", updatable = false,
            insertable = false)
    private List<Subscription> subscriptions;

}
