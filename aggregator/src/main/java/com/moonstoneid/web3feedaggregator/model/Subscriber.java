package com.moonstoneid.web3feedaggregator.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "subscriber")
public class Subscriber {

    @Id
    @Column(name = "account_address", length = 42, nullable = false)
    private String accountAddress;

    @Column(name = "contract_address", length = 42, nullable = false)
    private String contactAddress;

    @OneToMany
    @JoinColumn(name = "contract_address")
    private List<Subscription> subscriptions;

}
