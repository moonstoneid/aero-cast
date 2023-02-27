package com.moonstoneid.web3publisher;

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
        private String privatekey;
        private String contractaddress;
    }

    @Getter
    @Setter
    public static class EthApi {
        private String url;
    }

    private Eth eth;

}