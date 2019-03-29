package com.ecnu.pizzaexpressapplication.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Dishes implements Serializable {

    private int id;

    private String name;

    private String description;

    private BigDecimal salePrice;

    private int typeId;

    private String typeName;

    private String url;

}