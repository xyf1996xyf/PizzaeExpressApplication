package com.ecnu.pizzaexpressapplication.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseActivity;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseCallBack;
import com.ecnu.pizzaexpressapplication.bean.DishesWithCount;
import com.ecnu.pizzaexpressapplication.bean.OrderWithDishes;
import com.ecnu.pizzaexpressapplication.main.api.OrderApi;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderActivity extends PizzaExpressBaseActivity implements View.OnClickListener {

    private ListView dishesView;
    private TextView totalQuantityTextView, totalPriceTextView;
    private NumberFormat numberFormat;
    private OrderWithDishes order;
    private EditText et_remark;
    private int totalQuantity = 0;
    private double totalPrice = 0;
    private Button btn_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitleString("订单详情");
        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setMaximumFractionDigits(2);
        dishesView = (ListView) findViewById(R.id.dishesView);
        totalQuantityTextView = (TextView) findViewById(R.id.totalQuantity);
        totalPriceTextView = (TextView) findViewById(R.id.totalPrice);
        et_remark = findViewById(R.id.et_remark);
        btn_order = findViewById(R.id.btn_order);
        btn_order.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        order = (OrderWithDishes) bundle.getSerializable("order");

        ArrayList<HashMap<String, Object>> arrayList = getData(order.getDishes());

        SimpleAdapter adapter = new SimpleAdapter(this, arrayList, R.layout.dishes_view_content,
                new String[]{"name", "quantity", "price"},
                new int[]{R.id.dishName, R.id.dishQuantity, R.id.dishPrice});
        dishesView.setAdapter(adapter);

        totalQuantityTextView.setText(String.valueOf(totalQuantity));
        totalPriceTextView.setText(numberFormat.format(totalPrice));
    }

    @Override
    public void onClick(View view) {
        order.setRemark(et_remark.getText().toString());
        addRequest(getService(OrderApi.class).createOrder(order), new PizzaExpressBaseCallBack<OrderWithDishes>() {
            @Override
            public void onSuccess200(OrderWithDishes order) {
                Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("orderId", order.getId());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
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
