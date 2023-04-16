package com.moonstoneid.web3feedaggregator.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

@Slf4j
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request req = chain.request().newBuilder()
                .build();
        log.debug("URL: " + req.url());
        log.debug("Method: " + req.method());
        log.debug("Body: " + bodyToString(req));
        return chain.proceed(req);
    }

    private static String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

}