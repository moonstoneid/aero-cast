package com.moonstoneid.web3feed.publisher.config;

import com.moonstoneid.web3feed.common.config.BaseAppConfig;
import com.moonstoneid.web3feed.common.config.EthPublisherProperties;
import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EthPublisherProperties.class, EthRegistryProperties.class})
public class AppConfig extends BaseAppConfig {

}
