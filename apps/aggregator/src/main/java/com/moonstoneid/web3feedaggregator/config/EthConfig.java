package com.moonstoneid.web3feedaggregator.config;

import com.moonstoneid.web3feedaggregator.AppProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@Slf4j
public class EthConfig {

    private final AppProperties appProperties;

    public EthConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public Web3j web3j() {
        String ethApiUrl = appProperties.getEth().getApi().getUrl();
        if (ethApiUrl == null || ethApiUrl.isEmpty()) {
            log.info("No Ethereum API URL was configured.");
            return null;
        }
        HttpService httpService = new HttpService(ethApiUrl, initHttpClient());
        return Web3j.build(httpService);
    }

    protected OkHttpClient initHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (log.isDebugEnabled()) {
            builder.addNetworkInterceptor(new LogInterceptor());
        }
        return builder.build();
    }

}
