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
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etBankCard, etAmount;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        etBankCard = (EditText) findViewById(R.id.recharge_et_bank_card);
        etAmount = (EditText) findViewById(R.id.recharge_et_amount);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.recharge_btn_back);
        LinearLayout btnConfirm = (LinearLayout) findViewById(R.id.recharge_btn_confirm);

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge_btn_back:
                Log.d(RechargeActivity.class.getName(), "back");
                finish();
                break;
            case R.id.recharge_btn_confirm:
                Log.d(RechargeActivity.class.getName(), "confirm");
                if (etBankCard.getText().toString().equals("")) {
                    Toast.makeText(RechargeActivity.this, RechargeActivity.this.getResources().getString(R.string.input_bank_card),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if(etAmount.getText().toString().equals("")) {
                    Toast.makeText(RechargeActivity.this, RechargeActivity.this.getResources().getString(R.string.input_amount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO account useless
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("money", etAmount.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_USER_RECHARGE,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if (UserService.recharge(jsonObject, Double.valueOf(etAmount.getText().toString()))) {
                                        Toast.makeText(RechargeActivity.this, RechargeActivity.this.getResources().getString(R.string.recharge_success),
                                                Toast.LENGTH_SHORT).show();
                                        etAmount.setText("");
                                        etBankCard.setText("");
                                        // 充值成功自动关闭
                                        finish();
                                    } else {
                                        Toast.makeText(RechargeActivity.this, UserService.getErrorMsg(jsonObject),
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
                                Log.e(RechargeActivity.class.getName(), volleyError.getMessage(), volleyError);
                                // http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(RechargeActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(RechargeActivity.this, RechargeActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(RechargeActivity.class.getName(), "Unknown id");
                break;
        }
    }
}
