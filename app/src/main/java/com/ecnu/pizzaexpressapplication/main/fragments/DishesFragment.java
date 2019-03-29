package com.ecnu.pizzaexpressapplication.main.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseCallBack;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseFragment;
import com.ecnu.pizzaexpressapplication.bean.Dishes;
import com.ecnu.pizzaexpressapplication.bean.DishesWithCount;
import com.ecnu.pizzaexpressapplication.bean.OrderWithDishes;
import com.ecnu.pizzaexpressapplication.main.adapter.*;
import com.ecnu.pizzaexpressapplication.main.api.MenuApi;
import com.ecnu.pizzaexpressapplication.main.callback.IShoppingCart;
import com.ecnu.pizzaexpressapplication.main.callback.ITypeCallBack;
import com.ecnu.pizzaexpressapplication.order.OrderActivity;
import com.ecnu.pizzaexpressapplication.utils.AsyncBitmapLoader;
import com.flipboard.bottomsheet.BottomSheetLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DishesFragment extends PizzaExpressBaseFragment implements IShoppingCart, ITypeCallBack, View.OnClickListener {

    private ViewGroup anim_mask_layout;
    private LinearLayout bottom;
    private RelativeLayout main;
    private RecyclerView rvType, rvSelected;
    private SparseArray<GoodsItem> selectedList;
    private SparseIntArray groupSelect;
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private SelectAdapter selectAdapter;
    private GoodsAdapter myAdapter;
    private TypeAdapter typeAdapter;
    private NumberFormat numberFormat;
    private TextView tvCount, tvCost, tvSubmit, tvTips;
    private ImageView imgCart;
    private StickyListHeadersListView listView;
    //private DishesIntroduction dishesIntroduction;
    private View dishesIntroductionView;
    private ProgressBar shoppingProgressBar;

    private double startPrice = 20.00;

    private ArrayList<GoodsItem> dataList, typeList, orderList;

    private Handler mHanlder;
    private String message;


    private ProgressDialog dialog;
    private static Handler handler = new Handler();

    private boolean isDishesIntroduction = false;

    private boolean isAddCart;
    private List<GoodsItem> dishesList;
    private AsyncBitmapLoader asyncBitmapLoader = AsyncBitmapLoader.asyncBitmapLoader;

    protected void initView() {
        setTitleString("点单");
        rvType = (RecyclerView) getActivity().findViewById(R.id.typeRecyclerView);

        anim_mask_layout = (RelativeLayout) getActivity().findViewById(R.id.containerLayout);

        imgCart = (ImageView) getActivity().findViewById(R.id.imgCart);
        bottomSheetLayout = (BottomSheetLayout) getActivity().findViewById(R.id.bottomSheetLayout);
        listView = (StickyListHeadersListView) getActivity().findViewById(R.id.itemListView);

        tvCount = (TextView) getActivity().findViewById(R.id.tvCount);
        tvCost = (TextView) getActivity().findViewById(R.id.tvCost);
        tvTips = (TextView) getActivity().findViewById(R.id.tvTips);
        tvSubmit = (TextView) getActivity().findViewById(R.id.tvSubmit);
        tvSubmit.setOnClickListener(this);
        bottom = getActivity().findViewById(R.id.bottom);
        bottom.setOnClickListener(this);
        rvType.setLayoutManager(new LinearLayoutManager(getContext()));
        typeAdapter = new TypeAdapter(this, typeList);
        rvType.setAdapter(typeAdapter);
        rvType.addItemDecoration(new DividerDecoration(getContext()));

        myAdapter = new GoodsAdapter(dataList, this);
        listView.setAdapter(myAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                GoodsItem item = dataList.get(firstVisibleItem);
                if (typeAdapter.selectTypeId != item.typeId) {
                    typeAdapter.selectTypeId = item.typeId;
                    typeAdapter.notifyDataSetChanged();
                    rvType.smoothScrollToPosition(getSelectedGroupPosition(item.typeId));
                }
            }
        });

    }

    public void playAnimation(int[] start_location) {
        ImageView img = new ImageView(getContext());
        img.setImageResource(R.drawable.button_add);
        setAnim(img, start_location);
    }

    private Animation createAnim(int startX, int startY) {
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);

        AnimationSet set = new AnimationSet(false);

        Animation translationX = new TranslateAnimation(0, des[0] - startX, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1] - startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1, 0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);

        return set;
    }

    private void addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y - loc[1]);
        vg.addView(view);
    }

    private void setAnim(final View v, int[] start_location) {

        addViewToAnimLayout(anim_mask_layout, v, start_location);
        Animation set = createAnim(start_location[0], start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim_mask_layout.removeView(v);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(set);
    }

    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id) {
        GoodsItem temp = selectedList.get(id);
        if (temp == null) {
            return 0;
        }
        return temp.count;
    }

    //根据类别Id获取属于当前类别的数量
    public int getSelectedGroupCountByTypeId(int typeId) {
        return groupSelect.get(typeId);
    }

    //根据类别id获取分类的Position 用于滚动左侧的类别列表
    public int getSelectedGroupPosition(int typeId) {
        for (int i = 0; i < typeList.size(); i++) {
            if (typeId == typeList.get(i).typeId) {
                return i;
            }
        }
        return 0;
    }

    /*public void onGoodClicked(GoodsItem item) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        dishesIntroductionView = layoutInflater.inflate(R.layout.activity_dishes_introduction, null);
        dishesIntroduction = new DishesIntroduction(dishesIntroductionView);
        dishesIntroduction.bindData(item);
        main.addView(dishesIntroductionView);
        isDishesIntroduction = true;
    }*/

    /*@Override
    public void onBackPressed() {
        if (isDishesIntroduction) {
            main.removeView(dishesIntroductionView);
            isDishesIntroduction = false;
        } else {
            super.onBackPressed();
        }
    }*/


    /*class DishesIntroduction implements View.OnClickListener {
        private TextView name, price, tvAdd, tvMinus, tvCount, dishesIntroduce;
        private GoodsItem item;
        private RatingBar ratingBar;
        private ImageView dishesImage;
        private android.support.v7.widget.Toolbar successToolBar;

        public DishesIntroduction(final View dishesIntroductionView) {
            name = (TextView) dishesIntroductionView.findViewById(R.id.dishesName);
            price = (TextView) dishesIntroductionView.findViewById(R.id.dishesPrice);
            dishesIntroduce = (TextView) dishesIntroductionView.findViewById(R.id.dishesIntroduce);
            tvCount = (TextView) dishesIntroductionView.findViewById(R.id.count);
            tvMinus = (TextView) dishesIntroductionView.findViewById(R.id.tvMinus);
            tvAdd = (TextView) dishesIntroductionView.findViewById(R.id.tvAdd);
            ratingBar = (RatingBar) dishesIntroductionView.findViewById(R.id.dishesRating);
            dishesImage = (ImageView) dishesIntroductionView.findViewById(R.id.dishesImg);
            //successToolBar = (Toolbar) dishesIntroductionView.findViewById(R.id.successToolBar);
            setSupportActionBar(successToolBar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            successToolBar.setNavigationIcon(R.drawable.return_img);
            successToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //main.removeAllViews();
                    main.removeView(dishesIntroductionView);
                }
            });
            *//*successToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String msg = "";
                    switch (menuItem.getItemId()) {
                        case R.id.action_collect:
                            if (menuItem.getIcon().equals(R.drawable.collect_off)) {
                                menuItem.setIcon(R.drawable.collect_on);
                                msg += "已收藏";
                            } else if (menuItem.getIcon().equals(R.drawable.collect_on)) {
                                menuItem.setIcon(R.drawable.collect_off);
                                msg += "取消收藏";
                            }
                            break;
                        case R.id.action_share:
                            msg += "已分享";
                            break;
                        case R.id.action_settings:
                            msg += "Click setting";
                            break;
                    }

                    if (!msg.equals("")) {
                        Toast.makeText(MenuActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });*//*

            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
            dishesIntroductionView.setOnClickListener(this);
        }


        public void clear() {
            tvCount.setVisibility(View.GONE);
            tvMinus.setVisibility(View.GONE);
        }

        public void bindData(final GoodsItem item) {
            this.item = item;
            name.setText(item.name);
            ratingBar.setRating(item.rating);
            item.count = getSelectedItemCountById(item.id);
            tvCount.setText(String.valueOf(item.count));
            price.setText(numberFormat.format(item.price));
            dishesIntroduce.setText(item.introduce);
            successToolBar.setTitle(item.name);
            if (item.count < 1) {
                tvCount.setVisibility(View.GONE);
                tvMinus.setVisibility(View.GONE);
            } else {
                tvCount.setVisibility(View.VISIBLE);
                tvMinus.setVisibility(View.VISIBLE);
            }
            ViewGroup.LayoutParams layoutParams = dishesImage.getLayoutParams();
            //System.out.println(layoutParams.width + "     " + layoutParams.height);
            Bitmap bitmap = asyncBitmapLoader.loadBitmap(dishesImage, item.pictureAddress, dishesImage.getLayoutParams().width, dishesImage.getLayoutParams().height, new AsyncBitmapLoader.ImageCallBack() {
                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                    item.picture = bitmap;
                }
            });
            if (bitmap != null) {
                dishesImage.setImageBitmap(bitmap);
            }
            *//*if (item.picture == null) {
                new Thread(new LoadDishesPictureThread(item.pictureAddress, layoutParams.width, layoutParams.height)).start();
            }
            dishesImage.setImageBitmap(item.picture);*//*
        }

        *//*class LoadDishesPictureThread implements Runnable {
            private String address;
            private int reqWidth;
            private int reqHeight;
            private Bitmap bitmap;
            private DishesDAO dishesDAO = new DishesDAOImpl();

            public LoadDishesPictureThread(String address, int reqWidth, int reqHeight) {
                this.address = address;
                this.reqWidth = reqWidth;
                this.reqHeight = reqHeight;
            }

            @Override
            public void run() {
                try {
                    bitmap = dishesDAO.getBitmapByAddress(address, reqWidth, reqHeight);
                } catch (Exception e) {
                    bitmap = null;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        item.picture = bitmap;
                        dishesImage.setImageBitmap(item.picture);
                    }
                });
            }
        }*//*

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvAdd: {
                    int count = getSelectedItemCountById(item.id);
                    if (count < 1) {
                        //tvMinus.setAnimation(getShowAnimation());
                        tvMinus.setVisibility(View.VISIBLE);
                        tvCount.setVisibility(View.VISIBLE);
                    }
                    add(item, false);
                    count++;
                    tvCount.setText(String.valueOf(count));
                    int[] loc = new int[2];
                    v.getLocationInWindow(loc);
                    playAnimation(loc);
                }
                break;
                case R.id.tvMinus: {
                    int count = getSelectedItemCountById(item.id);
                    if (count < 2) {
                        //tvMinus.setAnimation(getHiddenAnimation());
                        tvMinus.setVisibility(View.GONE);
                        tvCount.setVisibility(View.GONE);
                    }
                    count--;
                    remove(item, false);//activity.getSelectedItemCountById(item.id)
                    tvCount.setText(String.valueOf(count));

                }
                break;
                default:
                    break;
            }

        }
    }*/

    public void onTypeClicked(int typeId) {
        listView.setSelection(getSelectedPosition(typeId));
    }

    private int getSelectedPosition(int typeId) {
        int position = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).typeId == typeId) {
                position = i;
                break;
            }
        }
        return position;
    }

    protected View createBottomSheetView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet, (ViewGroup) getActivity().getWindow().getDecorView(), false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener((View.OnClickListener) this);
        selectAdapter = new SelectAdapter(this, selectedList);
        rvSelected.setAdapter(selectAdapter);
        return view;
    }

    private void showBottomSheet() {
        if (bottomSheet == null) {
            bottomSheet = createBottomSheetView();
        }
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {
            if (selectedList.size() != 0) {
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }

    //添加商品
    public void add(GoodsItem item, boolean refreshGoodList) {
        int groupCount = groupSelect.get(item.typeId);
        if (groupCount == 0) {
            groupSelect.append(item.typeId, 1);
        } else {
            groupSelect.append(item.typeId, ++groupCount);
        }

        GoodsItem temp = selectedList.get(item.id);
        if (temp == null) {
            item.count = 1;
            selectedList.append(item.id, item);
        } else {
            temp.count++;
        }
        update(refreshGoodList);
    }

    /*public void addAll() {
        for (GoodsItem dishes : dishesList) {
            GoodsItem temp = GoodsItem.getWholeGoodsItem(dishes);
            if (temp == null) {
                Toast.makeText(getContext(), dishes.name + "暂时不能点", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < dishes.count; i++) {
                    add(temp, true);
                }
            }
        }
    }*/

    //移除商品
    public void remove(GoodsItem item, boolean refreshGoodList) {
        int groupCount = groupSelect.get(item.typeId);
        if (groupCount == 1) {
            groupSelect.delete(item.typeId);
        } else if (groupCount > 1) {
            groupSelect.append(item.typeId, --groupCount);
        }

        GoodsItem temp = selectedList.get(item.id);
        if (temp != null) {
            if (temp.count < 2) {
                selectedList.remove(item.id);
            } else {
                item.count--;
            }
        }
        update(refreshGoodList);
    }

    private void update(boolean refreshGoodList) {
        int size = selectedList.size();
        int count = 0;
        double cost = 0;
        for (int i = 0; i < size; i++) {
            GoodsItem item = selectedList.valueAt(i);
            count += item.count;
            cost += item.count * item.price;
        }

        if (count < 1) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
        }

        tvCount.setText(String.valueOf(count));

        if (cost > startPrice) {
            tvTips.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
        } else {
            tvSubmit.setVisibility(View.GONE);
            tvTips.setVisibility(View.VISIBLE);
        }

        tvCost.setText(numberFormat.format(cost));

        if (myAdapter != null && refreshGoodList) {
            myAdapter.notifyDataSetChanged();
        }
        if (selectAdapter != null) {
            selectAdapter.notifyDataSetChanged();
        }
        if (typeAdapter != null) {
            typeAdapter.notifyDataSetChanged();
        }
        if (bottomSheetLayout.isSheetShowing() && selectedList.size() < 1) {
            bottomSheetLayout.dismissSheet();
        }
    }

    //清空购物车
    public void clearCart() {
        selectedList.clear();
        groupSelect.clear();
        /*if (dishesIntroduction != null) {
            dishesIntroduction.clear();
        }*/
        update(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom:
                showBottomSheet();
                break;
            case R.id.clear:
                clearCart();
                break;
            case R.id.tvSubmit:
                ArrayList<GoodsItem> list = new ArrayList<GoodsItem>();
                for (int i = 0; i < selectedList.size(); i++) {
                    list.add(selectedList.valueAt(i));
                }
                List<DishesWithCount> dishesList = new ArrayList<>();

                for (GoodsItem goodsItem : list) {
                    DishesWithCount dishes = new DishesWithCount();
                    dishes.setId(goodsItem.id);
                    dishes.setName(goodsItem.name);
                    dishes.setSalePrice(new BigDecimal(goodsItem.price));
                    dishes.setCount(goodsItem.count);
                    dishesList.add(dishes);
                }

                OrderWithDishes orderWithDishes = new OrderWithDishes();
                orderWithDishes.setDishes(dishesList);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", orderWithDishes);
                intent.putExtras(bundle);
                intent.setClass(getContext(), OrderActivity.class);
                startActivity(intent);
                clearCart();
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setMaximumFractionDigits(2);


        isAddCart = false;

        mHanlder = new Handler(getActivity().getMainLooper());

        main = (RelativeLayout) getActivity().findViewById(R.id.main);
        shoppingProgressBar = (ProgressBar) getActivity().findViewById(R.id.shoppingCartProgressBar);
        shoppingProgressBar.setIndeterminate(false);
        shoppingProgressBar.setVisibility(View.VISIBLE);
        /*dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在获取，请稍后...");
        dialog.setCancelable(false);
        dialog.show();*/

        //new Thread(new MyThread()).start();
        addRequest(getService(MenuApi.class).queryMeny(), new PizzaExpressBaseCallBack<List<Dishes>>() {
            @Override
            public void onSuccess200(List<Dishes> list) {
                try {
                    Collections.sort(list, new Comparator<Dishes>() {
                        @Override
                        public int compare(Dishes dishes, Dishes t1) {
                            if (dishes.getTypeId() == t1.getTypeId()) {
                                return dishes.getId() - t1.getId();
                            }
                            return dishes.getTypeId() - t1.getTypeId();
                        }
                    });
                    int typeId = 0;
                    typeList = new ArrayList<>();
                    dataList = new ArrayList<>();
                    for (Dishes dishes : list) {
                        GoodsItem goodsItem = new GoodsItem(dishes);
                        if (goodsItem.typeId != typeId) {
                            typeList.add(goodsItem);
                            typeId = goodsItem.typeId;
                        }
                        dataList.add(goodsItem);
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "失败", Toast.LENGTH_SHORT).show();
                    //dialog.setMessage("失败");
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        shoppingProgressBar.setVisibility(View.GONE);
                        initView();
                    }
                });
            }
        });
        selectedList = new SparseArray<GoodsItem>();
        groupSelect = new SparseIntArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dishes, container, false);
    }

    public static DishesFragment newInstance() {
        return new DishesFragment();
    }
}
