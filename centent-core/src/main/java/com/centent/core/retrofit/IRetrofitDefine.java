package com.centent.core.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public interface IRetrofitDefine {

    String getBaseUrl();

    default Retrofit buildRetrofit() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 反序列化时忽略对象中不存在的json字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                // 自定义日志打印
                .addInterceptor(new CommonInterceptor());

        return new Retrofit.Builder()
                .baseUrl(this.getBaseUrl())
                .client(client.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }
}
