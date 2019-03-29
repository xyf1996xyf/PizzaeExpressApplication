package com.ecnu.pizzaexpressapplication.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.lemon.support.Base.BaseActivity;
import com.lemon.support.request.RequestManager;
import com.lemon.support.request.SimpleCall;
import com.lemon.support.request.SimpleCallBack;

import static com.lemon.support.Base.BaseActivity.NO_EMPTY_VIEW;

public class PizzaExpressBaseFragment extends Fragment {
    public String TAG = getClass().getSimpleName();

    public PizzaExpressBaseFragment() {
        // Required empty public constructor
    }

    protected void setTitleString(String title) {
        BaseActivity baseActivity = getBaseActivity();
        baseActivity.setTitleString(title);
    }

    protected void setTitleId(int id) {
        setTitleString(getString(id));
    }


    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public <R> R getService(Class<R> c) {
        return RequestManager.create(getActivity()).getService(c);
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
        return RequestManager.create(getActivity()).getService(endPoint, c);
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
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).addRequest(call, callBack, isForeground, cancelable, needRetry, errorViewContainer);
        } else {
            RequestManager.create(activity).addRequest(call, callBack, null);
        }
    }

}
