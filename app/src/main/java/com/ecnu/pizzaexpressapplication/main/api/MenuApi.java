package com.ecnu.pizzaexpressapplication.main.api;

import com.ecnu.pizzaexpressapplication.bean.Dishes;
import com.lemon.support.request.SimpleCall;
import retrofit2.http.GET;

import java.util.List;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
public interface MenuApi {
    @GET("menu")
    SimpleCall<List<Dishes>> queryMeny();
}
