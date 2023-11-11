package com.moonstoneid.aerocast.aggregator.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

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
@Table(name = "entry")
@IdClass(Entry.EntryId.class)
public class Entry implements Serializable {

    @Id
    @Column(name = "pub_contract_address", length = 42, nullable = false)
    private String pubContractAddress;

    @Id
    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    @Column(name = "url", length = 2000, nullable = false)
    private String url;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntryId implements Serializable {

        private String pubContractAddress;
        private Integer number;

    }

}