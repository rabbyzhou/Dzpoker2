package com.yijian.dzpoker.http.getclubgames;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qipu.qp on 2018/1/6.
 */

public interface GetClubGamesApi {
    @GET("func.ashx")
    Call<ResponseBody> getResponse(@Query("func") String funcName, @Query("param") String params);
}
