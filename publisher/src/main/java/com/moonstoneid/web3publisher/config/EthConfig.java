package com.moonstoneid.web3publisher.config;

import com.moonstoneid.web3publisher.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class EthConfig {

    private final AppProperties appProperties;

    public EthConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public Web3j getProvider() {
        String ethApiUrl = appProperties.getEth().getApi().getUrl();
        if (ethApiUrl == null || ethApiUrl.isEmpty()) {
            return null;
        }
        return Web3j.build(new HttpService(ethApiUrl));
    }

}
