package com.centent.core.retrofit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


@Slf4j
public class CommonInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long startTime = System.currentTimeMillis();
        log.debug("Retrofit request --> [{}]{}", request.method(), request.url());

        RequestBody requestBody = request.body();
        if (Objects.nonNull(requestBody)) {
            Buffer requestBuffer = new Buffer();
            requestBody.writeTo(requestBuffer);
            log.debug("Request Content-Type: {}", requestBody.contentType());
            String bodyString = requestBuffer.readString(StandardCharsets.UTF_8);
            if (bodyString.length() > 3000) {
                bodyString = bodyString.substring(0, 3000);
            }
            log.debug("Request body: {}...", bodyString);
        }

        // 执行请求
        Response response = chain.proceed(request);

        long duration = System.currentTimeMillis() - startTime;

        if (!response.isSuccessful()) {
            log.error("Retrofit response <-- code: {}, duration: {}ms, message: {}", response.code(), duration, response.message());
            // 此时应该抛出异常
            return response;
        }

        ResponseBody responseBody = response.body();
        if (Objects.isNull(responseBody)) {
            log.error("Retrofit response <-- duration: {}ms, content: null", duration);
            // 此时应该抛出异常
            return response;
        }
        String bodyString = responseBody.string();
        // 如果请求时间超过2分钟，打印告警日志
        if (duration > 2 * 60 * 1000) {
            log.warn("Retrofit --- request takes too long, url: {}, duration: {}ms", request.url(), duration);
        }
        log.debug("Retrofit response <-- duration: {}ms, content: {}", duration, bodyString);

        return response.newBuilder()
                .body(ResponseBody.create(bodyString, responseBody.contentType()))
                .build();
    }
}
