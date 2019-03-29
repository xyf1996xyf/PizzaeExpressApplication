package com.ecnu.pizzaexpressapplication.base;

import android.app.Application;
import com.lemon.support.request.RequestManager;
import okhttp3.Cookie;

import java.util.List;

/**
 * Created by yerunjie
 */

public class PizzaExpressBaseApplication extends Application {
    public static PizzaExpressBaseApplication sAppContext;

    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    public static boolean isLogin() {
        List<Cookie> cookies = RequestManager.create(sAppContext).getCookieJar().getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.name().equals("token")) {
                return true;
            }
        }
        return false;
    }

}