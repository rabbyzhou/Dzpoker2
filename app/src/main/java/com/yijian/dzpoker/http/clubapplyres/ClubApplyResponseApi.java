package com.yijian.dzpoker.http.clubapplyres;

import com.yijian.dzpoker.http.getclubapply.GetClubApplyBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qipu.qp on 2017/12/29.
 */

public interface ClubApplyResponseApi {

    @GET("func.ashx")
    Call<ResponseBody> getResponse(@Query("func") String funcName, @Query("param") String params);
}
