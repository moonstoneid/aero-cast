package com.moonstoneid.web3feed.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eth.api")
@Data
public class EthApiProperties {
    private String url;
    private boolean enableRequestLogging = false;
}
