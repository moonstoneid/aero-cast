package com.moonstoneid.aerocast.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eth.registry")
@Data
public class EthRegistryProperties {
    public String contractAddress;
}
