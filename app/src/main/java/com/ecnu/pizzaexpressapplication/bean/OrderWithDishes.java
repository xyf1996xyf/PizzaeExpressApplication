package com.ecnu.pizzaexpressapplication.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class OrderWithDishes extends Order implements Serializable {
    private List<DishesWithCount> dishes;

}
