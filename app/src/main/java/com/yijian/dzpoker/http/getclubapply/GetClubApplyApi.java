package com.yijian.dzpoker.http.getclubapply;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by QIPU on 2017/12/18.
 */

public interface GetClubApplyApi {
    @GET("func.ashx")
    Call<ResponseBody> getResponse(@Query("func") String funcName, @Query("param") String params);
}
