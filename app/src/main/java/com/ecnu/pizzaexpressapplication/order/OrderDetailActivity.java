package com.ecnu.pizzaexpressapplication.order;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseActivity;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseCallBack;
import com.ecnu.pizzaexpressapplication.bean.DishesWithCount;
import com.ecnu.pizzaexpressapplication.bean.OrderWithDishes;
import com.ecnu.pizzaexpressapplication.bean.ResultVo;
import com.ecnu.pizzaexpressapplication.main.api.OrderApi;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetailActivity extends PizzaExpressBaseActivity implements View.OnClickListener {
    private ListView dishesView;
    private TextView totalQuantityTextView, totalPriceTextView;
    private NumberFormat numberFormat;
    private OrderWithDishes order;
    private TextView tv_remark;
    private TextView tv_status;
    private TextView tv_create_time;
    private int totalQuantity = 0;
    private double totalPrice = 0;
    private Button btn_pay, btn_cancel;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setTitleString("订单详情");
        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setMaximumFractionDigits(2);
        dishesView = (ListView) findViewById(R.id.dishesView);
        totalQuantityTextView = (TextView) findViewById(R.id.totalQuantity);
        totalPriceTextView = (TextView) findViewById(R.id.totalPrice);
        tv_remark = findViewById(R.id.tv_remark);
        tv_status = findViewById(R.id.tv_status);
        tv_create_time = findViewById(R.id.tv_create_time);
        btn_pay = findViewById(R.id.btn_pay);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_pay.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        btn_pay.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);

        orderId = getIntent().getExtras().getInt("orderId");
        initData();
    }

    private void initData() {
        addRequest(getService(OrderApi.class).queryOrder(orderId), new PizzaExpressBaseCallBack<OrderWithDishes>() {
            @Override
            public void onSuccess200(final OrderWithDishes order) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_remark.setText(order.getRemark());
                        tv_status.setText(order.getStatus());
                        tv_create_time.setText(order.getCreateTime());
                        ArrayList<HashMap<String, Object>> arrayList = getData(order.getDishes());

                        SimpleAdapter adapter = new SimpleAdapter(OrderDetailActivity.this, arrayList, R.layout.dishes_view_content,
                                new String[]{"name", "quantity", "price"},
                                new int[]{R.id.dishName, R.id.dishQuantity, R.id.dishPrice});
                        dishesView.setAdapter(adapter);
                        totalQuantityTextView.setText(String.valueOf(totalQuantity));
                        totalPriceTextView.setText(numberFormat.format(totalPrice));
                        if (order.getStatus().equals("新建")) {
                            btn_pay.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                        } else {
                            btn_pay.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        int to = 0;
        switch (view.getId()) {
            case R.id.btn_pay:
                to = 30;
                break;
            case R.id.btn_cancel:
                to = 90;
                break;
            default:
                break;
        }
        if (to != 0) {
            addRequest(getService(OrderApi.class).updateOrderStatus(orderId, to), new PizzaExpressBaseCallBack<ResultVo>() {
                @Override
                public void onSuccess200(ResultVo resultVo) {
                    if (resultVo.isSuccess()) {
                        Toast.makeText(OrderDetailActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderDetailActivity.this, resultVo.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    initData();
                }
            });
        }
    }

    private ArrayList<HashMap<String, Object>> getData(List<DishesWithCount> list) {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

        for (DishesWithCount temp : list) {
            arrayList.add(getElement(temp));
        }

        return arrayList;
    }

    private HashMap<String, Object> getElement(DishesWithCount item) {
        HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
        tempHashMap.put("name", item.getName());
        tempHashMap.put("quantity", item.getCount());
        totalQuantity += item.getCount();
        tempHashMap.put("price", numberFormat.format(item.getSalePrice().doubleValue()));
        totalPrice += item.getSalePrice().doubleValue() * item.getCount();
        return tempHashMap;
    }
}
