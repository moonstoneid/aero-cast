package com.moonstoneid.aerocast.common.util;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LogInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        logRequest(request);
        return chain.proceed(request);
    }

    private void logRequest(Request request) {
        Request r = request.newBuilder().build();
        log.debug("URL: " + r.url());
        log.debug("Method: " + r.method());
        log.debug("Body: " + bodyToString(r));
    }

    private static String bodyToString(Request request) {
        RequestBody body = request.body();
        if (body == null) {
            return "";
        }
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            throw new RuntimeException("Could not read request body!", e);
        }
    }

}
