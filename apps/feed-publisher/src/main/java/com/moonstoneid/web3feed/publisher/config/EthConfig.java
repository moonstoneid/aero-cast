package com.moonstoneid.web3feed.publisher.config;

import com.moonstoneid.web3feed.common.config.EthApiProperties;
import com.moonstoneid.web3feed.common.config.EthPublisherProperties;
import com.moonstoneid.web3feed.common.config.EthRegistryProperties;
import com.moonstoneid.web3feed.common.config.Web3jBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;

@Configuration
@EnableConfigurationProperties({EthApiProperties.class, EthRegistryProperties.class,
        EthPublisherProperties.class})
@Slf4j
public class EthConfig {

    @Bean
    public Web3j web3j(EthApiProperties ethApiProperties) {
        String ethApiUrl = ethApiProperties.getUrl();
        return Web3jBuilder.build(ethApiUrl, log.isDebugEnabled());
    }

}
