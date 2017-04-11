package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhone, etPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initView() {
        etPhone = (EditText) findViewById(R.id.register_et_phone);
        etPassword = (EditText) findViewById(R.id.register_et_password);
        LinearLayout btnRegister = (LinearLayout) findViewById(R.id.register_btn);
        TextView tvLogin = (TextView) findViewById(R.id.register_tv_login);

        // 添加事件监听
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                Log.d(RegisterActivity.class.getName(), "register");
                if (TextUtils.isEmpty(etPhone.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.register_message_not_completed),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 密码长度大于等于8
                if (etPassword.getText().toString().length() >= Common.PASSWORD_MIN_LENGTH) {
                    // 判断手机号是否已注册
                    Map<String, String> paramsCheck = new HashMap<>();
                    paramsCheck.put("phoneNumber", etPhone.getText().toString());
                    Request<JSONObject> requestCheck = new PostRequest(Net.URL_USER_HAS_REGISTER,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Log.d(RegisterActivity.class.getName(), jsonObject.toString());
                                    try {
                                        if (UserService.hasRegister(jsonObject)) {
                                            Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.register_has),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            // 注册手机号
                                            Map<String, String> paramsRegister = new HashMap<>();
                                            paramsRegister.put("phoneNumber", etPhone.getText().toString());
                                            paramsRegister.put("password", etPassword.getText().toString());
                                            paramsRegister.put("type", String.valueOf(Common.USER_TYPE_SHIPPER));
                                            Request<JSONObject> requestRegister = new PostRequest(Net.URL_USER_REGISTER,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject jsonObject) {
                                                            Log.d(RegisterActivity.class.getName(), jsonObject.toString());
                                                            try {
                                                                if (UserService.register(jsonObject)) {
                                                                    // 注册成功，自动跳转到登陆界面
                                                                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.register_success),
                                                                            Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegisterActivity.this, UserService.getErrorMsg(jsonObject),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError volleyError) {
                                                            Log.e(RegisterActivity.class.getName(), volleyError.getMessage(), volleyError);
                                                            Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.network_error),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, paramsRegister);
                                            requestQueue.add(requestRegister);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Log.e(RegisterActivity.class.getName(), volleyError.getMessage(), volleyError);
                                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.network_error),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, paramsCheck);
                    requestQueue.add(requestCheck);
                } else {
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.password_length_error),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register_tv_login:
                Log.d(RegisterActivity.class.getName(), "login");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Log.e(RegisterActivity.class.getName(), "Unknown id");
        }

    }
}
