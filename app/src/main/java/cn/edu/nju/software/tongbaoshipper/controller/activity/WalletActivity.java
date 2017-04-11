package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.controller.adapter.AccountAdapter;
import cn.edu.nju.software.tongbaoshipper.common.Account;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvMoney;
    private ListView lvAccount;
    private LinearLayout vEmpty;
    private AccountAdapter accountAdapter;
    private RequestQueue requestQueue;


    @Override
    protected void onResume() {
        super.onResume();

        // 设置用户账户余额、账单信息
        if (User.isLogin()) {
            tvMoney.setText(String.format("￥%.2f", User.getInstance().getMoney()));
            Map<String, String> params = new HashMap<>();
            params.put("token", User.getInstance().getToken());
            Request<JSONObject> request = new PostRequest(Net.URL_USER_SHOW_ACCOUNT,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(WalletActivity.class.getName(), jsonObject.toString());
                            try {
                                if (UserService.getResult(jsonObject)) {
                                    // 初始化账单数据
                                    ArrayList<Account> arrAccount = UserService.showAccount(jsonObject);
                                    accountAdapter = new AccountAdapter(WalletActivity.this, arrAccount);
                                    lvAccount.setAdapter(accountAdapter);
                                    lvAccount.setEmptyView(vEmpty);
                                } else {
                                    Toast.makeText(WalletActivity.this, UserService.getErrorMsg(jsonObject),
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
                            Log.e(WalletActivity.class.getName(), volleyError.getMessage(), volleyError);
                            // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                            Toast.makeText(WalletActivity.this, WalletActivity.this.getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, params);
            requestQueue.add(request);
        } else {
            Intent intent = new Intent(WalletActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initView();

        requestQueue = Volley.newRequestQueue(this);

        // 校验用户余额
        if (User.isLogin()) {
            Map<String, String> params = new HashMap<>();
            params.put("token", User.getInstance().getToken());
            Request<JSONObject> request = new PostRequest(Net.URL_USER_GET_MONEY,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(WalletActivity.class.getName(), jsonObject.toString());
                            try {
                                if (!UserService.getMoney(jsonObject)) {
                                    Toast.makeText(WalletActivity.this, UserService.getErrorMsg(jsonObject),
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
                            Log.e(WalletActivity.class.getName(), volleyError.getMessage(), volleyError);
                            // http authentication 401
//                            if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                Intent intent = new Intent(WalletActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                return;
//                            }
                            Toast.makeText(WalletActivity.this, WalletActivity.this.getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, params);
            requestQueue.add(request);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        tvMoney = (TextView) findViewById(R.id.wallet_tv_money);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.wallet_btn_back);
        RelativeLayout btnBill = (RelativeLayout) findViewById(R.id.wallet_btn_bill);
        RelativeLayout btnWithdraw = (RelativeLayout) findViewById(R.id.wallet_btn_withdraw);
        RelativeLayout btnRecharge = (RelativeLayout) findViewById(R.id.wallet_btn_recharge);
        lvAccount = (ListView) findViewById(R.id.account_lv);
        vEmpty = (LinearLayout) findViewById(R.id.account_empty);

        // 设置监听事件
        btnBack.setOnClickListener(this);
        btnBill.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
        btnRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.wallet_btn_back:
                Log.d(this.getClass().getName(), "back");
                finish();
                break;
            case R.id.wallet_btn_bill:
                Log.d(this.getClass().getName(), "bill");
                Intent intentBill = new Intent(WalletActivity.this, AccountActivity.class);
                startActivity(intentBill);
                break;
            case R.id.wallet_btn_withdraw:
                Log.d(this.getClass().getName(), "withdraw");
                Intent intentWithdraw = new Intent(WalletActivity.this, WithdrawActivity.class);
                startActivity(intentWithdraw);
                break;
            case R.id.wallet_btn_recharge:
                Log.d(this.getClass().getName(), "recharge");
                Intent intentRecharge = new Intent(WalletActivity.this, RechargeActivity.class);
                startActivity(intentRecharge);
                break;
            default:
                Log.e(this.getClass().getName(), "unknown button id");
                break;
        }

    }
}
