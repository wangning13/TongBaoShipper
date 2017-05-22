package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.constant.Prefs;
import cn.edu.nju.software.tongbaoshipper.controller.fragment.FragmentHome;
import cn.edu.nju.software.tongbaoshipper.controller.fragment.FragmentMy;
import cn.edu.nju.software.tongbaoshipper.controller.fragment.FragmentOrder;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.service.UserService;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class FrameActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * is start app(load user message)
     */
    private static boolean IS_START_APP = true;
    private ImageView ivHome, ivOrder, ivMy;
    private TextView tvHome, tvOrder, tvMy;
    private FragmentManager fm;
    private FragmentTransaction ft;
    // fragment my contact PopupWindow
    private PopupWindow popupWindow;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init jpush
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
        Set<String> tags = new HashSet<>();
        tags.add(Common.JPUSH_TAGS);
        JPushInterface.setTags(FrameActivity.this, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i == 0) {
                    Log.d(FrameActivity.class.getName(), String.format("init tags: %s", set.toString()));
                }
            }
        });

        SDKInitializer.initialize(this.getApplicationContext());
        ShipperService.getAllTruckType(FrameActivity.this);
        setContentView(R.layout.activity_frame);
        initView();

        // valid token
        requestQueue = Volley.newRequestQueue(FrameActivity.this);
        tokenValid();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        LinearLayout btnHome = (LinearLayout) findViewById(R.id.frame_btn_home);
//        LinearLayout btnNearby = (LinearLayout) findViewById(R.id.frame_btn_nearby);
        LinearLayout btnOrder = (LinearLayout) findViewById(R.id.frame_btn_order);
        LinearLayout btnMy = (LinearLayout) findViewById(R.id.frame_btn_my);
        ivHome = (ImageView) findViewById(R.id.frame_iv_home);
//        ivNearby = (ImageView) findViewById(R.id.frame_iv_nearby);
        ivOrder = (ImageView) findViewById(R.id.frame_iv_order);
        ivMy = (ImageView) findViewById(R.id.frame_iv_my);
        tvHome = (TextView) findViewById(R.id.frame_tv_home);
//        tvNearby = (TextView) findViewById(R.id.frame_tv_nearby);
        tvOrder = (TextView) findViewById(R.id.frame_tv_order);
        tvMy = (TextView) findViewById(R.id.frame_tv_my);

        // 进入应用到首页
        fm = this.getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frame_fragment, new FragmentHome(this));
        ft.commit();

        // 添加底部按钮监听
        btnHome.setOnClickListener(this);
//        btnNearby.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnMy.setOnClickListener(this);
    }

    /**
     * 初始化底部按钮样式
     */
    private void initBottomBtnStyle() {
        // frame图片资源重新配置
        ivHome.setImageResource(R.drawable.frame_home);
//        ivNearby.setImageResource(R.drawable.frame_nearby);
        ivOrder.setImageResource(R.drawable.frame_order);
        ivMy.setImageResource(R.drawable.frame_my);
        // 初始化底部文字颜色
        tvHome.setTextColor(ContextCompat.getColor(this, R.color.font_gray));
//        tvNearby.setTextColor(ContextCompat.getColor(this, R.color.font_gray));
        tvOrder.setTextColor(ContextCompat.getColor(this, R.color.font_gray));
        tvMy.setTextColor(ContextCompat.getColor(this, R.color.font_gray));

    }


    @Override
    public void onClick(View v) {
        initBottomBtnStyle();
        ft = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.frame_btn_home:
                Log.d(this.getClass().getName(), "home");
                ivHome.setImageResource(R.drawable.frame_home_pressed);
                tvHome.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
                ft.replace(R.id.frame_fragment, new FragmentHome(this));
                break;
//            case R.id.frame_btn_nearby:
//                Log.d(this.getClass().getName(), "nearby");
//                ivNearby.setImageResource(R.drawable.frame_nearby_pressed);
//                tvNearby.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
//                ft.replace(R.id.frame_fragment, new FragmentNearBy(this));
//                break;
            case R.id.frame_btn_order:
                Log.d(this.getClass().getName(), "order");
                ivOrder.setImageResource(R.drawable.frame_order_pressed);
                tvOrder.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
                ft.replace(R.id.frame_fragment, new FragmentOrder(this));
                break;
            case R.id.frame_btn_my:
                Log.d(this.getClass().getName(), "my");
                ivMy.setImageResource(R.drawable.frame_my_pressed);
                tvMy.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
                ft.replace(R.id.frame_fragment, new FragmentMy(this));
                break;
            default:
                Log.e(this.getClass().getName(), "unknown frame button");
                break;
        }
        ft.commit();
    }

    public void getOrder() {
        initBottomBtnStyle();
        ft = fm.beginTransaction();
        Log.d(this.getClass().getName(), "order");
        ivOrder.setImageResource(R.drawable.frame_order_pressed);
        tvOrder.setTextColor(ContextCompat.getColor(this, R.color.font_blue));
        ft.replace(R.id.frame_fragment, new FragmentOrder(this));
        ft.commit();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (popupWindow != null && popupWindow.isShowing()) {
//                popupWindow.dismiss();
//                popupWindow = null;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    private int mBackKeyPressedTimes = 0;

    @Override
    public void onBackPressed() {
        if (mBackKeyPressedTimes == 0) {
            Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
            mBackKeyPressedTimes = 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mBackKeyPressedTimes = 0;
                    }
                }
            }.start();
            return;
        }  else{
            FrameActivity.this.finish();
        }
        super.onBackPressed();
    }

    /**
     * 设置popupWindow点击外部dismiss
     *
     * @param event motion event
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 显示底部联系客服菜单（FragmentMy）
     */
    public void showPopupWindow() {
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
        popupWindow.showAtLocation(findViewById(R.id.activity_frame), Gravity.BOTTOM, 0, 0);

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
                Log.d(FrameActivity.class.getName() + FragmentMy.class.getName(), "pop dial");
                Uri telUri = Uri.parse("tel:" + FrameActivity.this.getResources().getString(R.string.contact_telephone_number));
                Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(FrameActivity.class.getName() + FragmentMy.class.getName(), "pop cancel");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    /**
     * on start app: valid token whether timeout or not
     */
    private void tokenValid() {
        if (IS_START_APP) {
            IS_START_APP = false;
            final User user = (User) UserService.getObject(FrameActivity.this, Prefs.PREF_KEY_USER);
            // check user token whether timeout
            if (user != null) {
                Log.d(FrameActivity.class.getName(), "valid token whether timeout or not");
                Map<String, String> params = new HashMap<>();
                params.put("token", user.getToken());
                Request<JSONObject> request = new PostRequest(Net.URL_USER_TOKEN_VALID,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(FrameActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (UserService.tokenValid(jsonObject)) {
                                        // user login
                                        Map<String, String> params = new HashMap<>();
                                        params.put("phoneNumber", user.getPhoneNum());
                                        params.put("password", user.getPassword());
                                        params.put("type", String.valueOf(Common.USER_TYPE_SHIPPER));
                                        Request<JSONObject> request = new PostRequest(Net.URL_USER_LOGIN,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject jsonObject) {
                                                        Log.d(FrameActivity.class.getName(), "auto login");
                                                        Log.d(FrameActivity.class.getName(), jsonObject.toString());
                                                        try {
                                                            if (UserService.login(FrameActivity.this, jsonObject, user.getPhoneNum(),
                                                                    user.getPassword(), Common.USER_TYPE_SHIPPER)) {
                                                                // local record user message
                                                                UserService.saveObject(FrameActivity.this, Prefs.PREF_KEY_USER, User.getInstance());
                                                            } else {
                                                                Toast.makeText(FrameActivity.this, UserService.getErrorMsg(jsonObject),
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
                                                        Log.e(FrameActivity.class.getName(), volleyError.getMessage(), volleyError);
                                                        Toast.makeText(FrameActivity.this, FrameActivity.this.getResources().getString(R.string.network_error),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }, params);

                                        requestQueue.add(request);
                                    } else {
                                        UserService.tokenInvalid(FrameActivity.this, true);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(FrameActivity.class.getName(), volleyError.getMessage(), volleyError);
                                Toast.makeText(FrameActivity.this, FrameActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
            } else {
                UserService.tokenInvalid(FrameActivity.this, false);
            }
        }
    }
}
