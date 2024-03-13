package com.centent.core.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public interface IRetrofitDefine {

    String getBaseUrl();

    default Retrofit buildRetrofit() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                // 自定义日志打印
                .addInterceptor(new CommonInterceptor());

        return new Retrofit.Builder()
                .baseUrl(this.getBaseUrl())
                .client(client.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
