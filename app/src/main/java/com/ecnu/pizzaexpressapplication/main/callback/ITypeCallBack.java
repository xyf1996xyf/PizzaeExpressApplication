package com.ecnu.pizzaexpressapplication.main.callback;

/**
 * Created by yerunjie on 2019-03-28
 *
 * @author yerunjie
 */
public interface ITypeCallBack {
    int getSelectedGroupCountByTypeId(int typeId);

    void onTypeClicked(int typeId);
}
