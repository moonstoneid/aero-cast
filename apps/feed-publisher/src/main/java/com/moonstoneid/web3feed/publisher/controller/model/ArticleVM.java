package com.moonstoneid.web3feed.publisher.controller.model;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleVM {

    private Integer id;
    private String title;
    private String summary;
    private String content;
    private OffsetDateTime date;

}
