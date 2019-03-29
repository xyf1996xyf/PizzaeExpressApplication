package com.ecnu.pizzaexpressapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ecnu.pizzaexpressapplication.R;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseActivity;
import com.ecnu.pizzaexpressapplication.base.PizzaExpressBaseCallBack;
import com.ecnu.pizzaexpressapplication.login.api.LoginApi;
import com.ecnu.pizzaexpressapplication.login.request.LoginRequest;
import com.ecnu.pizzaexpressapplication.login.response.LoginResponse;
import com.ecnu.pizzaexpressapplication.main.MainActivity;
import com.lemon.support.util.DigestUtils;

public class LoginActivity extends PizzaExpressBaseActivity {
    @BindView(R.id.et_user_account)
    EditText et_user_account;
    @BindView(R.id.et_user_password)
    EditText et_user_password;
    @BindView(R.id.btn_login)
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleString("登录");
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumberString = et_user_account.getText().toString();
                String userPasswordString = et_user_password.getText().toString();
                LoginRequest request = new LoginRequest();
                request.setAccount(accountNumberString);
                request.setPassword(DigestUtils.md5(userPasswordString));
                addRequest(getService(LoginApi.class).doLogin(request), new PizzaExpressBaseCallBack<LoginResponse>() {
                    @Override
                    public void onSuccess200(LoginResponse o) {
                        makeToast("登陆成功");
                        Log.d(TAG, "onSuccess200: " + o.getToken());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
