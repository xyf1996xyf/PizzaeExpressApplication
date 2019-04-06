package com.ecnu.pizzaexpressapplication.register;

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
import com.ecnu.pizzaexpressapplication.register.api.RegisterApi;
import com.ecnu.pizzaexpressapplication.register.request.RegisterRequest;
import com.ecnu.pizzaexpressapplication.register.response.RegisterResponse;
import com.ecnu.pizzaexpressapplication.login.LoginActivity;
import com.lemon.support.util.DigestUtils;

public class RegisterActivity extends PizzaExpressBaseActivity {
    @BindView(R.id.et_user_account)
    EditText et_user_account;
    @BindView(R.id.et_user_password)
    EditText et_user_password;
    @BindView(R.id.et_user_phone)
    EditText et_user_phone;
    @BindView(R.id.et_user_address)
    EditText et_user_address;
    @BindView(R.id.btn_register)
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleString("注册");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumberString = et_user_account.getText().toString();
                String userPasswordString = et_user_password.getText().toString();
                String phoneNumberString = et_user_phone.getText().toString();
                String addressString = et_user_address.getText().toString();
                RegisterRequest request = new RegisterRequest();
                request.setAccount(accountNumberString);
                request.setPassword(DigestUtils.md5(userPasswordString));
                request.setPhone(phoneNumberString);
                request.setAddress(addressString);
                addRequest(getService(RegisterApi.class).doRegister(request), new PizzaExpressBaseCallBack<RegisterResponse>() {
                    @Override
                    public void onSuccess200(RegisterResponse o) {
                        makeToast("注册成功");
                        //Log.d(TAG, "onSuccess200: " + o.getToken());
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
