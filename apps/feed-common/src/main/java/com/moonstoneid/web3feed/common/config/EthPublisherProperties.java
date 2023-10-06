package com.moonstoneid.web3feed.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eth.publisher")
@Data
public class EthPublisherProperties {
    public String privateKey;
    public String contractAddress;
}
