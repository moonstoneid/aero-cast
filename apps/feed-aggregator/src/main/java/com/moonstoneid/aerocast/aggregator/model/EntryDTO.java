package com.moonstoneid.aerocast.aggregator.model;

import java.time.OffsetDateTime;

public interface EntryDTO {

    String getPubContractAddress();
    String getPubName();
    String getTitle();
    String getDescription();
    OffsetDateTime getDate();
    String getUrl();

}
