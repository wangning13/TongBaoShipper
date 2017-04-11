package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.constant.Prefs;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhone, etPassword;
    protected LinearLayout btnRegister;
    private RequestQueue requestQueue;
    private PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        etPhone = (EditText) findViewById(R.id.login_et_phone);
        etPassword = (EditText) findViewById(R.id.login_et_password);
        TextView tvLoginError = (TextView) findViewById(R.id.login_tv_error);
        LinearLayout btnLogin = (LinearLayout) findViewById(R.id.login_btn);
        btnRegister = (LinearLayout) findViewById(R.id.login_btn_register);

        // 添加监听事件
        tvLoginError.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                Log.d(LoginActivity.class.getName(), "login");
                if (TextUtils.isEmpty(etPassword.getText().toString()) || TextUtils.isEmpty(etPhone.getText().toString())) {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.login_message_not_completed),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("phoneNumber", etPhone.getText().toString());
                params.put("password", etPassword.getText().toString());
                params.put("type", String.valueOf(Common.USER_TYPE_SHIPPER));
                Request<JSONObject> request = new PostRequest(Net.URL_USER_LOGIN,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(LoginActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (UserService.login(LoginActivity.this, jsonObject, etPhone.getText().toString(),
                                            etPassword.getText().toString(), Common.USER_TYPE_SHIPPER)) {
                                        // local record user message
                                        UserService.saveObject(LoginActivity.this, Prefs.PREF_KEY_USER, User.getInstance());
                                        Intent intent = new Intent(LoginActivity.this,FrameActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, UserService.getErrorMsg(jsonObject),
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
                                Log.e(LoginActivity.class.getName(), volleyError.getMessage(), volleyError);
                                Toast.makeText(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);

                requestQueue.add(request);
                break;
            case R.id.login_tv_error:
                Log.d(LoginActivity.class.getName(), "error");
                showPopWindow();
                break;
            case R.id.login_btn_register:
                Log.d(LoginActivity.class.getName(), "register");
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Log.e(LoginActivity.class.getName(), "Unknown id");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
            } else {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        return super.onTouchEvent(event);
    }

    private void showPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_my_contact, null);

        popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.pop_window_anim);
        popupWindow.showAtLocation(findViewById(R.id.activity_login), Gravity.BOTTOM, 0, 0);

        // popupWindow调整屏幕亮度
        final Window window = this.getWindow();
        final WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.5f;
        window.setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                window.setAttributes(params);
            }
        });

        // popupWindow设置按键监听
        LinearLayout btnDial = (LinearLayout) view.findViewById(R.id.contact_btn_dial);
        LinearLayout btnCancel = (LinearLayout) view.findViewById(R.id.contact_btn_cancel);
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LoginActivity.class.getName(), "pop dial");
                Uri telUri = Uri.parse("tel:" + LoginActivity.this.getResources().getString(R.string.contact_telephone_number));
                Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LoginActivity.class.getName(), "pop cancel");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }
}
