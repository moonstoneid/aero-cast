package com.moonstoneid.web3feed.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "h2.server")
@Data
public class H2ServerProperties {
    private Integer port;
}
