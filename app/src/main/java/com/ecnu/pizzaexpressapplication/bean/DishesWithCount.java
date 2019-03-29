package com.ecnu.pizzaexpressapplication.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class DishesWithCount extends Dishes implements Serializable {

    private int count;

}