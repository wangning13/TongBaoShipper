package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.controller.fragment.FragmentWithdrawAlipay;
import cn.edu.nju.software.tongbaoshipper.controller.fragment.FragmentWithdrawBank;

public class WithdrawActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvAlipay, tvBank;
    private LinearLayout btnAlipay, btnBank;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initView();

    }

    /**
     * 初始化视图
     */
    private void initView() {
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.withdraw_btn_back);
        btnAlipay = (LinearLayout) findViewById(R.id.withdraw_btn_alipay);
        btnBank = (LinearLayout) findViewById(R.id.withdraw_btn_bank);
        tvAlipay = (TextView) findViewById(R.id.withdraw_tv_alipay);
        tvBank = (TextView) findViewById(R.id.withdraw_tv_bank);

        // 进入提现到支付宝
        fm = this.getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.withdraw_fragment, new FragmentWithdrawAlipay(this));
        ft.commit();

        // 添加监听事件
        btnBack.setOnClickListener(this);
        btnAlipay.setOnClickListener(this);
        btnBank.setOnClickListener(this);
    }

    /**
     * 初始化按钮样式
     */
    private void initTypeBtnStyle() {
        // 按钮背景初始化
        btnAlipay.setBackgroundResource(R.drawable.rect_withdraw_left);
        btnBank.setBackgroundResource(R.drawable.rect_withdraw_right);

        // 初始化按钮文字颜色
        tvAlipay.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
        tvBank.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
    }

    @Override
    public void onClick(View v) {
        initTypeBtnStyle();
        ft = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.withdraw_btn_back:
                Log.d(this.getClass().getName(), "back");
                finish();
                break;
            case R.id.withdraw_btn_alipay:
                Log.d(this.getClass().getName(), "alipay");
                btnAlipay.setBackgroundResource(R.drawable.rect_withdraw_left_pressed);
                tvAlipay.setTextColor(ContextCompat.getColor(this, R.color.font_white));
                ft.replace(R.id.withdraw_fragment, new FragmentWithdrawAlipay(this));
                break;
            case R.id.withdraw_btn_bank:
                Log.d(this.getClass().getName(), "bank");
                btnBank.setBackgroundResource(R.drawable.rect_withdraw_right_pressed);
                tvBank.setTextColor(ContextCompat.getColor(this, R.color.font_white));
                ft.replace(R.id.withdraw_fragment, new FragmentWithdrawBank(this));
                break;
            default:
                Log.e(this.getClass().getName(), "unknown button id");
                break;
        }
        ft.commit();
    }
}
