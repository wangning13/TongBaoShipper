package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout btnBack, btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        btnBack = (LinearLayout) findViewById(R.id.setting_btn_back);
        btnChangePassword = (LinearLayout) findViewById(R.id.setting_btn_change_password);
        btnLogout = (LinearLayout) findViewById(R.id.setting_btn_logout);

        // 添加事件监听
        btnBack.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn_back:
                Log.d(SettingActivity.class.getName(), "back");
                finish();
                break;
            case R.id.setting_btn_change_password:
                Log.d(SettingActivity.class.getName(), "change password");
                Intent intentChangePassword = new Intent(this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.setting_btn_logout:
                Log.d(SettingActivity.class.getName(), "logout");
                View vDialog = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_setting_modify_password, null);
                final Dialog dialog = new AlertDialog.Builder(SettingActivity.this).setView(vDialog).create();
                Button btnCancel = (Button) vDialog.findViewById(R.id.dialog_btn_cancel);
                Button btnComfirm = (Button) vDialog.findViewById(R.id.dialog_btn_confirm);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(SettingActivity.class.getName(), "cancel logout");
                        dialog.dismiss();
                    }
                });
                btnComfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // user logout
                        Log.d(SettingActivity.class.getName(), "confirm logout");
                        // just use token invalids
                        UserService.tokenInvalid(SettingActivity.this, false);
                        // login activity
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.show();
                break;
            default:
                Log.e(SettingActivity.class.getName(), "Unknown id");
        }
    }
}
