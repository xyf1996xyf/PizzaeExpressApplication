package com.ecnu.pizzaexpressapplication.main.api;

import com.ecnu.pizzaexpressapplication.bean.Order;
import com.ecnu.pizzaexpressapplication.bean.OrderWithDishes;
import com.ecnu.pizzaexpressapplication.bean.ResultVo;
import com.lemon.support.request.SimpleCall;
import retrofit2.http.*;

import java.util.List;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
public interface OrderApi {
    @GET("order")
    SimpleCall<List<Order>> queryOrders(@Query("pageSize") int pageSize,
                                        @Query("pageNumber") int pageNumber);

    @POST("order")
    SimpleCall<OrderWithDishes> createOrder(@Body OrderWithDishes order);

    @PUT("order/{id}/status/{to}")
    SimpleCall<ResultVo> updateOrderStatus(@Path("id") int id,
                                           @Path("to") int toStatus);

    @GET("order/{id}")
    SimpleCall<OrderWithDishes> queryOrder(@Path("id") int id);
}
