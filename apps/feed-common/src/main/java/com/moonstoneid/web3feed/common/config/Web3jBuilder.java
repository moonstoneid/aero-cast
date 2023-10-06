package com.moonstoneid.web3feed.common.config;

import com.moonstoneid.web3feed.common.util.LogInterceptor;
import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public final class Web3jBuilder {

    private Web3jBuilder() {}

    public static Web3j build(String ethApiUrl, boolean enableRequestLogging) {
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
