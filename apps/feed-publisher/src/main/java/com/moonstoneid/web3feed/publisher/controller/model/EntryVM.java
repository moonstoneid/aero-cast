package com.moonstoneid.web3feed.publisher.controller.model;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryVM {

    private Integer id;
    private String title;
    private String description;
    private OffsetDateTime date;

}
