package com.ecnu.pizzaexpressapplication.main.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import com.ecnu.pizzaexpressapplication.bean.Dishes;

import java.io.Serializable;
import java.util.Random;

public class GoodsItem implements Serializable {
    public int id;
    public int typeId;
    public int rating;
    public String name;
    public String typeName;
    public double price;
    public int count;
    public String introduce;
    public String pictureAddress;
    public Bitmap picture = null;

    public GoodsItem(int id, double price, String name, int typeId, String typeName) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.rating = new Random().nextInt(5) + 1;
    }

    public GoodsItem(Dishes dishes) {
        this.id = dishes.getId();
        this.introduce = dishes.getDescription();
        this.price = dishes.getSalePrice().doubleValue();
        this.name = dishes.getName();
        this.typeId = dishes.getTypeId();
        this.typeName = dishes.getTypeName();
        this.pictureAddress = dishes.getUrl();
        this.rating = new Random().nextInt(5) + 1;
    }


   /* public static void getData() {
        goodsList = new ArrayList<>();
        typeList = new ArrayList<>();
        GoodsItem item = null;
        for (int i = 1; i < 15; i++) {
            for (int j = 1; j < 10; j++) {
                item = new GoodsItem(100 * i + j, Math.random() * 100, "商品" + (100 * i + j), i, "种类" + i);
                goodsList.add(item);
            }
            typeList.add(item);
        }
    }*/


    /*public static GoodsItem getWholeGoodsItem(GoodsItem dishes) {
        if (typeList == null || goodsList == null) {
            return null;
        } else {
            for (GoodsItem goodsItem : goodsList) {
                if (goodsItem.id == dishes.id) {
                    return goodsItem;
                }
            }
            return null;
        }
    }*/

}
