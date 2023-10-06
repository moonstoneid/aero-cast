package com.moonstoneid.web3feed.publisher.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

}
