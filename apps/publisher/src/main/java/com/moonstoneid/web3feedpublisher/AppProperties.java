package com.moonstoneid.web3feedpublisher;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "publisher")
@Getter
@Setter
public class AppProperties {

    @Getter
    @Setter
    public static class Eth {
        private EthApi api;
        private String privateKey;
        private String regContractAddress;
        private String pubContractAddress;
    }

    @Getter
    @Setter
    public static class EthApi {
        private String url;
    }

    private Eth eth;

}