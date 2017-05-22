package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etOldPassword, etNewPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.cp_btn_back);
        etOldPassword = (EditText) findViewById(R.id.cp_et_old_password);
        etNewPassword = (EditText) findViewById(R.id.cp_et_new_password);
        LinearLayout btnChangePassword = (LinearLayout) findViewById(R.id.cp_btn);

        btnBack.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cp_btn_back:
                Log.d(ChangePasswordActivity.class.getName(), "back");
                finish();
                break;
            case R.id.cp_btn:
                Log.d(ChangePasswordActivity.class.getName(), "change password");
                // 密码长度大于等于8
                if (etNewPassword.getText().toString().length() >= Common.PASSWORD_MIN_LENGTH) {
                    // 匹配原密码
                    if (etOldPassword.getText().toString().equals(User.getInstance().getPassword())) {
                        // 修改密码
                        Map<String, String> params = new HashMap<>();
                        params.put("token", User.getInstance().getToken());
                        params.put("oldPassword", etOldPassword.getText().toString());
                        params.put("newPassword", etNewPassword.getText().toString());
                        Request<JSONObject> request = new PostRequest(Net.URL_USER_MODIFY_PASSWORD,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        Log.d(ChangePasswordActivity.class.getName(), jsonObject.toString());
                                        try {
                                            if (UserService.modifyPassword(jsonObject, etNewPassword.getText().toString())) {
                                                Toast.makeText(ChangePasswordActivity.this, ChangePasswordActivity.this.getResources().getString(R.string.cp_change_password_success),
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, UserService.getErrorMsg(jsonObject),
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
                                        Log.e(ChangePasswordActivity.class.getName(), volleyError.getMessage(), volleyError);
                                        // http authentication 401
//                                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
//                                            startActivity(intent);
//                                            return;
//                                        }
                                        Toast.makeText(ChangePasswordActivity.this, ChangePasswordActivity.this.getResources().getString(R.string.network_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, params);
                        requestQueue.add(request);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, ChangePasswordActivity.this.getResources().getString(R.string.cp_old_password_error),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, ChangePasswordActivity.this.getResources().getString(R.string.password_length_error),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.e(ChangePasswordActivity.class.getName(), "Unknown id");
        }
    }
}
