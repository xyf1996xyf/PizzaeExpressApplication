package com.ecnu.pizzaexpressapplication.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
@Data
@ToString
public class Order implements Serializable {

    private int id;

    private String uuid;

    private int userId;

    private String status;

    private BigDecimal costPrice;

    private BigDecimal salePrice;

    private int factoryId;

    private int deliverClerkId;

    private String remark;

    private String createTime;

    private String address;

    private String deliverTime;

    private String detail;

}
