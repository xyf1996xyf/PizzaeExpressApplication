package com.ecnu.pizzaexpressapplication.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.bean.Order;

import java.util.List;

/**
 * Created by yerunjie on 2018/6/6
 *
 * @author yerunjie
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> implements View.OnClickListener {

    private List<Order> orders;
    private OnItemClickListener mOnItemClickListener = null;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tv_detail.setText(order.getDetail());
        holder.tv_time.setText(order.getCreateTime());
        holder.tv_uuid.setText(order.getUuid());
        holder.tv_status.setText(order.getStatus());
        holder.tv_price.setText(order.getSalePrice().toString());
        if (order.getStatus().equals("新建")) {
            holder.btn_cancel.setVisibility(View.VISIBLE);
            holder.btn_pay.setVisibility(View.VISIBLE);
        } else {
            holder.btn_cancel.setVisibility(View.GONE);
            holder.btn_pay.setVisibility(View.GONE);
        }

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, orders.get((int) v.getTag()));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_uuid)
        TextView tv_uuid;
        @BindView(R.id.tv_detail)
        TextView tv_detail;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_status)
        TextView tv_status;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.btn_pay)
        Button btn_pay;
        @BindView(R.id.btn_cancel)
        Button btn_cancel;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
