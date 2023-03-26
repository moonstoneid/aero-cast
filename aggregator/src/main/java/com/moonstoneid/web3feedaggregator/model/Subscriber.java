package com.moonstoneid.web3feedaggregator.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "subscriber")
public class Subscriber implements Serializable {

    @Id
    @Column(name = "account_address", length = 42, nullable = false)
    private String accountAddress;

    @Column(name = "contract_address", length = 42, nullable = false)
    private String contactAddress;

    // TODO: This is a workaround to prevent LazyInitializationException, should be fixed for prod
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sub_contract_address", referencedColumnName = "contract_address")
    private List<Subscription> subscriptions;

}
