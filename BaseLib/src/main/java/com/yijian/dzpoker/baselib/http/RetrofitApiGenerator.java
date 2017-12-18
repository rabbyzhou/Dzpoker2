package com.yijian.dzpoker.baselib.http;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by QIPU on 2017/12/18.
 */
public class RetrofitApiGenerator {

    private static String API_BASE_URL = "http://106.14.221.253:85/";

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createRequestApi(Class<T> requestClass) {
        Retrofit retrofit = builder.client(HttpRequestManager.okHttpClient).build();
        return retrofit.create(requestClass);
    }

}
