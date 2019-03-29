package com.ecnu.pizzaexpressapplication.main.callback;

import com.ecnu.pizzaexpressapplication.main.adapter.GoodsItem;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
public interface IShoppingCart {

    void add(GoodsItem item, boolean refreshGoodList);

    void remove(GoodsItem item, boolean refreshGoodList);
}
