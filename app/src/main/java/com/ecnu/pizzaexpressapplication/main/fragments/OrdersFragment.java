package com.ecnu.pizzaexpressapplication.main.fragments;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseCallBack;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseFragment;
import com.ecnu.pizzaexpressapplication.bean.Order;
import com.ecnu.pizzaexpressapplication.main.adapter.OrdersAdapter;
import com.ecnu.pizzaexpressapplication.main.api.OrderApi;
import com.ecnu.pizzaexpressapplication.order.OrderDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends PizzaExpressBaseFragment {
    private static final int offset = 30;
    private static final int pageSize = 10;
    private Unbinder unbinder;

    @BindView(R.id.rv_orders)
    RecyclerView rv_orders;
    @BindView(R.id.srl_orders)
    SwipeRefreshLayout srl_orders;

    private OrdersAdapter adapter;
    private List<Order> orders = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int pageNumber = 0;
    private int lastVisibleItem = 5;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        addRequest(getService(OrderApi.class).queryOrders(pageSize, pageNumber), new PizzaExpressBaseCallBack<List<Order>>() {
            @Override
            public void onSuccess200(List<Order> list) {
                orders.addAll(list);
                adapter.notifyDataSetChanged();
                srl_orders.setRefreshing(false);
                pageNumber++;
            }
        });
    }

    private void initView() {
        setTitleString("订单");
        srl_orders.setColorSchemeResources(R.color.colorPrimary);
        srl_orders.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 0;
                orders.clear();
                initData();
            }
        });
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        rv_orders.setLayoutManager(linearLayoutManager);
        rv_orders.setItemAnimator(new DefaultItemAnimator());
        adapter = new OrdersAdapter(orders);
        rv_orders.setAdapter(adapter);
        rv_orders.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = offset;
                outRect.right = offset;
                outRect.bottom = offset;
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = offset;
                }
            }
        });
        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Order order) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("orderId", order.getId());
                startActivity(intent);
            }
        });
        rv_orders.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    initData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

}
