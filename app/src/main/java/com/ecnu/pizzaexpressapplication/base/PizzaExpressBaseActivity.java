package com.ecnu.pizzaexpressapplication.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.widget.LoadingDialog;
import com.ecnu.pizzaexpressapplication.widget.MessageDialog;
import com.lemon.support.Base.BaseActivity;

/**
 * Created by yerunjie on 2018/2/4
 *
 * @author yerunjie
 */
public class PizzaExpressBaseActivity extends BaseActivity {
    public String TAG = getClass().getSimpleName();

    @Override
    protected View getErrorView() {
        return LayoutInflater.from(this).inflate(R.layout.widget_view_error_page, null);
    }

    @Override
    protected boolean needAutoRetry(Object body) {
        return false;
    }

    @Override
    protected Dialog getLoadingDialog() {
        return new LoadingDialog(this);
    }

    @Override
    protected Dialog getRetryDialog(final DialogInterface.OnClickListener onButtonListener, DialogInterface.OnCancelListener onCancelListener) {
        final MessageDialog dialog = new MessageDialog(this);
        dialog.setMessage(R.string.app_dialog_retry_msg);
        dialog.setPositiveButton(R.string.app_dialog_retry_positive_btn);
        dialog.setOnButtonClickListener(new MessageDialog.OnButtonClickListener() {
            @Override
            public void onClick(int which) {
                onButtonListener.onClick(dialog, which);
            }
        });
        dialog.setOnCancelListener(onCancelListener);
        return dialog;
    }

    @Override
    protected Dialog getDialog(String message, String positiveText, final DialogInterface.OnClickListener onButtonListener, DialogInterface.OnCancelListener onCancelListener) {
        final MessageDialog dialog = new MessageDialog(this);
        dialog.setMessage(message);
        dialog.setPositiveButton(positiveText);
        dialog.setOnButtonClickListener(new MessageDialog.OnButtonClickListener() {
            @Override
            public void onClick(int which) {
                onButtonListener.onClick(dialog, which);
            }
        });
        dialog.setOnCancelListener(onCancelListener);
        return dialog;
    }
}
