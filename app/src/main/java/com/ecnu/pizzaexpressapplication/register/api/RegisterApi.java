package com.ecnu.pizzaexpressapplication.register.api;

import com.ecnu.pizzaexpressapplication.register.request.RegisterRequest;
import com.ecnu.pizzaexpressapplication.register.response.RegisterResponse;
import com.lemon.support.request.SimpleCall;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by yerunjie on 17/11/22.
 */

public interface RegisterApi {
    //注册
    @POST("user/register")
    SimpleCall<RegisterResponse> doRegister(@Body RegisterRequest request);

}
