package com.ecnu.pizzaexpressapplication.main.adapter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.main.fragments.DishesFragment;
import com.ecnu.pizzaexpressapplication.utils.AsyncBitmapLoader;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;


public class GoodsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private AsyncBitmapLoader asyncBitmapLoader = AsyncBitmapLoader.asyncBitmapLoader;
    private ArrayList<GoodsItem> dataList;
    private DishesFragment mContext;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public GoodsAdapter(ArrayList<GoodsItem> dataList, DishesFragment mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(mContext.getContext());
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_header_view, parent, false);
        }
        ((TextView) (convertView)).setText(dataList.get(position).typeName);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return dataList.get(position).typeId;
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_goods, parent, false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        GoodsItem item = dataList.get(position);
        holder.bindData(item);
        return convertView;
    }

    class ItemViewHolder implements View.OnClickListener {
        private TextView name, price, tvAdd, tvMinus, tvCount, tvIntroduce;
        private GoodsItem item;
        private RatingBar ratingBar;
        private ImageView dishesImage;
        private Handler handler = new Handler();

        public ItemViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.tvName);
            price = (TextView) itemView.findViewById(R.id.tvPrice);
            tvIntroduce = (TextView) itemView.findViewById(R.id.tvIntroduce);
            tvCount = (TextView) itemView.findViewById(R.id.count);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            dishesImage = (ImageView) itemView.findViewById(R.id.img);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindData(GoodsItem item) {
            this.item = item;
            name.setText(item.name);
            ratingBar.setRating(item.rating);
            item.count = mContext.getSelectedItemCountById(item.id);
            tvCount.setText(String.valueOf(item.count));
            price.setText(nf.format(item.price));
            tvIntroduce.setText(item.introduce);
            if (item.count < 1) {
                tvCount.setVisibility(View.GONE);
                tvMinus.setVisibility(View.GONE);
            } else {
                tvCount.setVisibility(View.VISIBLE);
                tvMinus.setVisibility(View.VISIBLE);
            }
            Bitmap bitmap = asyncBitmapLoader.loadBitmap(dishesImage, item.pictureAddress, dishesImage.getLayoutParams().width, dishesImage.getLayoutParams().height, new AsyncBitmapLoader.ImageCallBack() {
                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                }
            });
            if (bitmap != null) {
                dishesImage.setImageBitmap(bitmap);
            }
        }


        @Override
        public void onClick(View v) {
            DishesFragment activity = mContext;
            switch (v.getId()) {
                case R.id.tvAdd: {
                    int count = activity.getSelectedItemCountById(item.id);
                    if (count < 1) {
                        tvMinus.setAnimation(getShowAnimation());
                        tvMinus.setVisibility(View.VISIBLE);
                        tvCount.setVisibility(View.VISIBLE);
                    }
                    activity.add(item, false);
                    count++;
                    tvCount.setText(String.valueOf(count));
                    int[] loc = new int[2];
                    v.getLocationInWindow(loc);
                    activity.playAnimation(loc);
                }
                break;
                case R.id.tvMinus: {
                    int count = activity.getSelectedItemCountById(item.id);
                    if (count < 2) {
                        tvMinus.setAnimation(getHiddenAnimation());
                        tvMinus.setVisibility(View.GONE);
                        tvCount.setVisibility(View.GONE);
                    }
                    count--;
                    activity.remove(item, false);//activity.getSelectedItemCountById(item.id)
                    tvCount.setText(String.valueOf(count));

                }
                break;
                default:
                    //activity.onGoodClicked(item);
                    break;
            }

        }
    }

    private Animation getShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    private Animation getHiddenAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }
}
