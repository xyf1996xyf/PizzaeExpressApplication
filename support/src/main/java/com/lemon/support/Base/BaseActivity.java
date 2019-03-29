package com.lemon.support.Base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lemon.support.R;
import com.lemon.support.request.RequestManager;
import com.lemon.support.request.RequestStateListener;
import com.lemon.support.request.SimpleCall;
import com.lemon.support.request.SimpleCallBack;
import de.hdodenhof.circleimageview.CircleImageView;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Created by yerunjie on 18/1/2.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private View mRoot;
    private FrameLayout fl_container;
    protected Toolbar tb_title_bar;
    private TextView tv_title;
    private CircleImageView civ_title_bar_head_portrait;
    private float mIconSize, mTextSize;

    private int mCustomRootLayout = -1;
    protected Dialog mLoadingDialog, mRetryDialog;
    private RLBaseHandler mParentHandler;
    protected List<SimpleCall> mCancelableCallList = new ArrayList<>();
    protected List<SimpleCall> mUncancelableCallList = new ArrayList<>();

    protected Map<SimpleCall, RetryCallInfo> mRetryCallMap = new HashMap<>();
    protected Map<SimpleCall, RetryCallInfo> mAutoRetryCallMap = new HashMap<>();
    /**
     * 请求失败，不显示异常页面
     */
    public static final int NO_EMPTY_VIEW = -1;
    /**
     * 请求失败，显示一个位于titlebar下，撑满屏幕的异常页面
     */
    public static final int FULL_EMPTY_VIEW = 0;

    private static final long LOADING_DISMISS_DELAY = 200;
    private static final int WHAT_DISMISS_LOADINGDIALOG = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInternal();
    }

    @Override
    public void setContentView(int layoutResID) {
        mRoot = LayoutInflater.from(this)
                .inflate(mCustomRootLayout == -1 ? R.layout.base_activity_layout : mCustomRootLayout, null);
        super.setContentView(mRoot);
        initView();
        addViewToContent(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        mRoot = LayoutInflater.from(this)
                .inflate(mCustomRootLayout == -1 ? R.layout.base_activity_layout : mCustomRootLayout, null);
        super.setContentView(mRoot);
        initView();
        addViewToContent(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mRoot = LayoutInflater.from(this)
                .inflate(mCustomRootLayout == -1 ? R.layout.base_activity_layout : mCustomRootLayout, null);
        super.setContentView(mRoot);
        initView();
        addViewToContent(view, params);
    }

    private void addViewToContent(View view, ViewGroup.LayoutParams params) {
        fl_container.addView(view, params);
    }

    private void addViewToContent(int layoutResID) {
        addViewToContent(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    private void addViewToContent(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addViewToContent(view, params);
    }

    private void initView() {
        fl_container = mRoot.findViewWithTag("fl_container");
        tv_title = mRoot.findViewWithTag("tv_title");
        tb_title_bar = mRoot.findViewWithTag("tb_title_bar");
        setSupportActionBar(tb_title_bar);
        civ_title_bar_head_portrait = mRoot.findViewWithTag("civ_title_bar_head_portrait");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setTitleString(String title) {
        tv_title.setText(title);
    }

    public void setTitleId(int id) {
        setTitleString(getString(id));
    }

    public void showTitleBar() {
        tb_title_bar.setVisibility(View.VISIBLE);
    }

    public void hideTitleBar() {
        tb_title_bar.setVisibility(View.GONE);
    }

    public void showBackIcon() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb_title_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isIcon(String s) {
        return s.length() == 8 && s.startsWith("&#") && s.endsWith(";");
    }

    protected TextView setIconTypeface(TextView tv) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        tv.setTypeface(tf);
        return tv;
    }

    private Spanned getHtmlText(String which) {
        return Html.fromHtml(which);
    }

    private void refreshTextSize(TextView tv, String s) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, isIcon(s) ? mIconSize : mTextSize);
    }

    private static class RLBaseHandler extends Handler {

        private WeakReference<BaseActivity> mOuter;

        public RLBaseHandler(BaseActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity outer = mOuter.get();
            if (outer != null) {
                if (msg.what == WHAT_DISMISS_LOADINGDIALOG) {// 避免请求框高频闪动
                    try {
                        boolean needShow = outer.mCancelableCallList.size() > 0 || outer.mUncancelableCallList.size() > 0;
                        if (outer.mLoadingDialog != null && outer.mLoadingDialog.isShowing() && !needShow) {
                            outer.mLoadingDialog.dismiss();
                            if (!outer.mRetryDialog.isShowing() && outer.mRetryCallMap.size() > 0) {
                                outer.mRetryDialog.show();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private void initInternal() {
        mParentHandler = new RLBaseHandler(this);
        // 获取自定义容器布局，布局中的元素tag需要保持一致
        mCustomRootLayout = getCustomRootLayoutId();
        // 初始化加载框
        mLoadingDialog = getLoadingDialog();
        if (mLoadingDialog != null) {
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (mLoadingDialog.isShowing() && keyCode == KeyEvent.KEYCODE_BACK &&
                            keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        // 拦截在加载框显示过程中的物理返回键，用于下面做取消的动作
                        return true;
                    } else if (mLoadingDialog.isShowing() && keyCode == KeyEvent.KEYCODE_BACK &&
                            keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        cancelRequest();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        // 初始化重试框
        mRetryDialog = getRetryDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    showErrorView();
                } else {
                    retryCall(mRetryCallMap);
                }
            }
        }, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                showErrorView();
            }
        });
    }

    public <R> R getService(Class<R> c) {
        return RequestManager.create(this).getService(c);
    }

    /**
     * 指定endPoint对应的Retrofit来发起请求
     *
     * @param endPoint
     * @param c
     * @param <R>
     * @return
     */
    public <R> R getService(String endPoint, Class<R> c) {
        return RequestManager.create(this).getService(endPoint, c);
    }

    /**
     * 创建一个前台、不可取消、需要重试、无异常页面的请求
     *
     * @param call
     * @param callBack
     */
    public void addRequest(SimpleCall call, SimpleCallBack callBack) {
        addRequest(call, callBack, true);
    }

    /**
     * 创建一个不可取消、需要重试、无异常页面的请求
     *
     * @param call
     * @param callBack
     * @param isForeground 是否为前台请求
     */
    public void addRequest(SimpleCall call, SimpleCallBack callBack, boolean isForeground) {
        addRequest(call, callBack, isForeground, false);
    }

    /**
     * 创建一个需要重试，无异常页面的请求
     *
     * @param call
     * @param callBack
     * @param isForeground 是否为前台请求
     * @param cancelable   是否可以被取消
     */
    public void addRequest(SimpleCall call, SimpleCallBack callBack, boolean isForeground, boolean cancelable) {
        addRequest(call, callBack, isForeground, cancelable, true);
    }

    /**
     * 创建一个请求，若需要重试，则无异常页面
     *
     * @param call
     * @param callBack
     * @param isForeground 是否为前台请求
     * @param cancelable   是否可以被取消
     * @param needRetry    是否需要重试
     */
    public void addRequest(SimpleCall call, SimpleCallBack callBack, boolean isForeground,
                           boolean cancelable, boolean needRetry) {
        addRequest(call, callBack, isForeground, cancelable, needRetry, NO_EMPTY_VIEW);
    }

    /**
     * 创建一个后台请求。该请求可以被取消，不需要重试
     *
     * @param call
     * @param callBack
     */
    public void addBackgroundRequest(SimpleCall call, SimpleCallBack callBack) {
        addRequest(call, callBack, false, true, false, NO_EMPTY_VIEW);
    }

    /**
     * 创建一个请求
     *
     * @param call
     * @param callBack
     * @param isForeground       是否为前台请求
     * @param cancelable         是否可以被取消
     * @param needRetry          是否需要重试
     * @param errorViewContainer 将要装载重试页面的容器ID，此ID需要为当前页面的元素。若ID未找到或非ViewGroup，则默认FULL_EMPTY_VIEW
     */
    public void addRequest(final SimpleCall call, final SimpleCallBack callBack, final boolean isForeground,
                           final boolean cancelable, final boolean needRetry, final int errorViewContainer) {
        RequestManager.create(this).addRequest(call, callBack, new RequestStateListener() {
            @Override
            public void onStart() {
                if (isForeground) {
                    if (cancelable) {
                        mCancelableCallList.add(call);
                    } else {
                        mUncancelableCallList.add(call);
                    }
                    refreshDialogState();
                }
            }

            @Override
            public void onFinish() {
                if (isForeground) {
                    if (mCancelableCallList.contains(call)) {
                        mCancelableCallList.remove(call);
                    } else if (mUncancelableCallList.contains(call)) {
                        mUncancelableCallList.remove(call);
                    }
                    refreshDialogState();
                }
            }

            @Override
            public void onSuccess(Object body) {
                if (mRetryCallMap.containsKey(call)) {
                    mRetryCallMap.remove(call);
                }
                if (needAutoRetry(body) && !mRetryCallMap.containsKey(call)) {
                    RetryCallInfo info = new RetryCallInfo();
                    info.callBack = callBack;
                    info.cancelable = cancelable;
                    info.errorViewContainer = errorViewContainer;
                    mAutoRetryCallMap.put(call, info);
                }
            }

            @Override
            public void onFailure() {
                if (isForeground && needRetry && !mRetryCallMap.containsKey(call)) {
                    RetryCallInfo info = new RetryCallInfo();
                    info.callBack = callBack;
                    info.cancelable = cancelable;
                    info.errorViewContainer = errorViewContainer;
                    mRetryCallMap.put(call, info);
                }
            }
        });
    }

    /**
     * 取消前台、可以被取消的请求
     */
    public void cancelRequest() {
        for (SimpleCall call : mCancelableCallList) {
            call.cancel();
        }
    }

    /**
     * 刷新loading状态
     */
    private void refreshDialogState() {
        boolean needShow = mCancelableCallList.size() > 0 || mUncancelableCallList.size() > 0;
        mParentHandler.removeMessages(WHAT_DISMISS_LOADINGDIALOG);
        if (mLoadingDialog != null) {
            if (!mLoadingDialog.isShowing() && needShow) {
                mLoadingDialog.show();
            } else {
                mParentHandler.sendEmptyMessageDelayed(WHAT_DISMISS_LOADINGDIALOG, LOADING_DISMISS_DELAY);
            }
        }
    }

    private class RetryCallInfo {
        SimpleCallBack callBack;
        boolean cancelable;
        int errorViewContainer;
    }

    private void retryCall(Map<SimpleCall, RetryCallInfo> callMap) {
        Set<Map.Entry<SimpleCall, RetryCallInfo>> set = new HashSet<>();
        set.addAll(callMap.entrySet());
        for (Map.Entry<SimpleCall, RetryCallInfo> entry : set) {
            SimpleCall call = entry.getKey();
            RetryCallInfo info = entry.getValue();
            callMap.remove(call);
            addRequest(call.clone(), info.callBack, true, info.cancelable, true, info.errorViewContainer);
        }
    }

    private void showErrorView() {
        List<Integer> containerList = new ArrayList<>();
        for (RetryCallInfo info : mRetryCallMap.values()) {
            // 避免重复，不添加不需要显示异常页的请求
            if (!containerList.contains(info.errorViewContainer) && info.errorViewContainer != NO_EMPTY_VIEW) {
                containerList.add(info.errorViewContainer);
            }
        }
        for (int id : containerList) {
            View container = findViewById(id);
            if (id != FULL_EMPTY_VIEW && container != null && container instanceof ViewGroup) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(container.getWidth(), container.getHeight());
                ((ViewGroup) container).removeAllViews();
                ((ViewGroup) container).addView(getErrorView(), params);
            } else {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                fl_container.removeAllViews();
                fl_container.addView(getErrorView(), params);
            }
        }
        mRetryCallMap.clear();
    }

    protected int getCustomRootLayoutId() {
        return -1;
    }

    protected abstract View getErrorView();

    protected abstract boolean needAutoRetry(Object body);

    protected abstract Dialog getLoadingDialog();

    protected abstract Dialog getRetryDialog(DialogInterface.OnClickListener onButtonListener,
                                             DialogInterface.OnCancelListener onCancelListener);

    protected abstract Dialog getDialog(String message, String positiveText,
                                        DialogInterface.OnClickListener onButtonListener,
                                        DialogInterface.OnCancelListener onCancelListener);

    protected Dialog getDialog(int message, int positiveText,
                               DialogInterface.OnClickListener onButtonListener,
                               DialogInterface.OnCancelListener onCancelListener) {
        return getDialog(getString(message), getString(positiveText), onButtonListener, onCancelListener);
    }

    @Override
    public void finish() {

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        if (mRetryDialog.isShowing()) {
            mRetryDialog.dismiss();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCancelableCallList.clear();
        mUncancelableCallList.clear();
        mRetryCallMap.clear();
    }

    public void makeToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
