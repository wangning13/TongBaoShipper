package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.model.Account;
import cn.edu.nju.software.tongbaoshipper.model.MonthlyAccount;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.AccountAdapter;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int HANDLER_UI = 0;
    private int year, month;
    private TextView tvYear, tvMonth, tvIncome, tvCost;
    private ListView lvAccount;
    private LinearLayout vEmpty;
    private AccountAdapter accountAdapter;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initViw();
    }

    private void initViw() {
        tvYear = (TextView) findViewById(R.id.account_tv_year);
        tvMonth = (TextView) findViewById(R.id.account_tv_month);
        tvIncome = (TextView) findViewById(R.id.account_tv_income);
        tvCost = (TextView) findViewById(R.id.account_tv_cost);
        lvAccount = (ListView) findViewById(R.id.account_lv);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.account_btn_back);
        LinearLayout btnTime = (LinearLayout) findViewById(R.id.account_btn_time);
        vEmpty = (LinearLayout) findViewById(R.id.account_empty);

        btnBack.setOnClickListener(this);
        btnTime.setOnClickListener(this);

        if (!User.isLogin()) {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        // 设置默认账单时间
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        showMonthlyAccount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_btn_back:
                Log.d(AccountActivity.class.getName(), "back");
                finish();
                break;
            case R.id.account_btn_time:
                Log.d(AccountActivity.class.getName(), "time");
                showPopWindow();
                break;
            default:
                Log.e(AccountActivity.class.getName(), "Unknown id");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
            } else {
                finish();
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

    /**
     * 显示底部选择时间菜单
     */
    private void showPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_account_month, null);

        popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.pop_window_anim);
        popupWindow.showAtLocation(findViewById(R.id.activity_account), Gravity.BOTTOM, 0, 0);

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

        // TODO 调整datePicker样式
        TextView tvCancel = (TextView) view.findViewById(R.id.account_month_tv_cancel);
        TextView tvComplete = (TextView) view.findViewById(R.id.account_month_tv_complete);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.account_month_dp);
        if (datePicker != null) {
            // datePicker date dismiss
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            datePicker.setMinDate(User.getInstance().getRegisterTime().getTime());
            datePicker.init(year, month, 1, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.d(AccountActivity.class.getName(),
                            String.format("date changed: year-%d month-%d", year, monthOfYear));
                    AccountActivity.this.year = year;
                    AccountActivity.this.month = monthOfYear;
                }
            });
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AccountActivity.class.getName(), "pop cancel");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AccountActivity.class.getName(), "pop complete");
                showMonthlyAccount();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
    }

    /**
     * show monthly account
     * (for month + 1: the default DatePicker monthOfYear starts from 0)
     */
    private void showMonthlyAccount() {
        RequestQueue requestQueue = Volley.newRequestQueue(AccountActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("year", String.valueOf(year));
        params.put("month", String.valueOf(month + 1));
        Request<JSONObject> request = new PostRequest(Net.URL_USER_GET_ACCOUNT_BY_MONTH,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(AccountActivity.class.getName(), jsonObject.toString());
                        try {
                            if (UserService.getResult(jsonObject)) {
                                // thread update ui
                                final MonthlyAccount monthlyAccount = UserService.getAccountByMonth(jsonObject, year, month + 1);
                                final UIHandler handler = new UIHandler(AccountActivity.this);
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(AccountActivity.class.getName(), "thread ui");
                                        Message msg = new Message();
                                        msg.what = HANDLER_UI;
                                        msg.obj = monthlyAccount;
                                        handler.sendMessage(msg);
                                    }
                                });
                                thread.start();
                            } else {
                                Toast.makeText(AccountActivity.this, UserService.getErrorMsg(jsonObject),
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
                        Log.e(AccountActivity.class.getName(), volleyError.getMessage(), volleyError);
                        Toast.makeText(AccountActivity.this, AccountActivity.this.getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }, params);
        requestQueue.add(request);
    }

    static class UIHandler extends Handler {
        WeakReference<AccountActivity> activity;

        UIHandler(AccountActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AccountActivity accountActivity = activity.get();
            switch (msg.what) {
                case HANDLER_UI:
                    // update account ui
                    Log.d(AccountActivity.class.getName() + UIHandler.class.getName(), "handler ui");
                    MonthlyAccount monthlyAccount = (MonthlyAccount) msg.obj;
                    accountActivity.tvYear.setText(String.format("%d%s",
                            monthlyAccount.getYear(), accountActivity.getResources().getString(R.string.year)));
                    accountActivity.tvMonth.setText(String.format("%02d%s",
                            monthlyAccount.getMonth(), accountActivity.getResources().getString(R.string.month)));
                    accountActivity.tvIncome.setText(String.valueOf(monthlyAccount.getTotalIn()));
                    accountActivity.tvCost.setText(String.valueOf(monthlyAccount.getTotalOut()));
                    ArrayList<Account> arrAccount = monthlyAccount.getAccountList();
                    accountActivity.accountAdapter = new AccountAdapter(accountActivity, arrAccount);
                    accountActivity.lvAccount.setAdapter(accountActivity.accountAdapter);
                    accountActivity.lvAccount.setEmptyView(accountActivity.vEmpty);
                    break;
                default:
                    Log.e(AccountActivity.class.getName() + UIHandler.class.getName(), "Unknown handler what");
                    break;
            }
        }
    }

}
