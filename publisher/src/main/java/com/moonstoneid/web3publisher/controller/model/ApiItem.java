package com.moonstoneid.web3publisher.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ApiItem {
    private Integer id;
    private String title;
    private String description;
    private OffsetDateTime pubDate;

}
