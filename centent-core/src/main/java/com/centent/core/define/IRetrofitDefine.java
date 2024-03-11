package com.centent.core.define;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public interface IRetrofitDefine {

    String getBaseUrl();

    default Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(this.getBaseUrl()) // 设置网络请求的Url地址
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
