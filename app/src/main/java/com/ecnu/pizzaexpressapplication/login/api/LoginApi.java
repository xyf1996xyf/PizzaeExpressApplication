package com.ecnu.pizzaexpressapplication.login.api;

import com.ecnu.pizzaexpressapplication.login.request.LoginRequest;
import com.ecnu.pizzaexpressapplication.login.response.LoginResponse;
import com.lemon.support.request.SimpleCall;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by yerunjie on 17/11/22.
 */

public interface LoginApi {
    //登录
    @POST("user/login")
    SimpleCall<LoginResponse> doLogin(@Body LoginRequest request);

}
