package com.moonstoneid.aerocast.aggregator.config;

import com.moonstoneid.aerocast.common.config.BaseAppConfig;
import com.moonstoneid.aerocast.common.config.EthPublisherProperties;
import com.moonstoneid.aerocast.common.config.EthRegistryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EthPublisherProperties.class, EthRegistryProperties.class})
public class AppConfig extends BaseAppConfig {

}
