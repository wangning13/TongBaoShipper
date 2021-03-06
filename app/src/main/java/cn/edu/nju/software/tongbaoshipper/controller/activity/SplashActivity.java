package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.constant.Prefs;

/**
 * Created by motoon on 2017/3/28.
 */

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 1000; // 延迟
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(Prefs.USER_INFO, Context.MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean(Prefs.IS_LOGIN,false);
        if (isLogin){
            //默认是自动登录状态，直接跳转
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(SplashActivity.this, FrameActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }

            }, SPLASH_DISPLAY_LENGHT);
        }else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGHT);
        }
    }
}
