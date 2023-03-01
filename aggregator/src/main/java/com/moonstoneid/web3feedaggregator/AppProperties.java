package com.moonstoneid.web3feedaggregator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "web3feedaggregator")
@Getter
@Setter
public class AppProperties {

    @Getter
    @Setter
    public static class Eth {
        private EthApi api;
    }

    @Getter
    @Setter
    public static class EthApi {
        private String url;
    }

    private Eth eth;

}