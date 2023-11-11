package com.moonstoneid.aerocast.common.config;

import com.moonstoneid.aerocast.common.util.LogInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@EnableConfigurationProperties({EthApiProperties.class})
@Slf4j
public class EthConfig {

    @Bean
    public Web3j web3j(EthApiProperties ethApiProperties) {
        String ethApiUrl = ethApiProperties.getUrl();
        boolean enableRequestLogging = ethApiProperties.isEnableRequestLogging();
        log.debug("Using Ethereum API URL {}.", ethApiUrl);
        return buildWeb3j(ethApiUrl, enableRequestLogging);
    }

    public static Web3j buildWeb3j(String ethApiUrl, boolean enableRequestLogging) {
        if (ethApiUrl == null || ethApiUrl.isEmpty()) {
            throw new Error("Ethereum API URL cannot be empty!");
        }

        OkHttpClient httpClient = buildHttpClient(enableRequestLogging);
        HttpService httpService = new HttpService(ethApiUrl, httpClient);

        return Web3j.build(httpService);
    }

    private static OkHttpClient buildHttpClient(boolean enableRequestLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (enableRequestLogging) {
            builder.addNetworkInterceptor(new LogInterceptor());
        }
        return builder.build();
    }

}
