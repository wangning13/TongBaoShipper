package cn.edu.nju.software.tongbaoshipper.controller.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.controller.activity.AboutActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.FeedbackActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.FrameActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.LoginActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.MessageActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.SettingActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.UserActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.WalletActivity;

public class FragmentMy extends Fragment implements View.OnClickListener {

    private Context context;
    private ImageView ivAvatar;
    private TextView tvName, tvPhone;
    private RelativeLayout btnUser, btnWallet, btnMessage,
            btnHelp, btnContact, btnFeedback, btnAbout;
    private RequestQueue requestQueue;

    public FragmentMy() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentMy(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 设置用户姓名、手机号、用户头像
        if (User.isLogin()) {
            tvName.setText(User.getInstance().getNiceName());
            tvPhone.setText(User.getInstance().getPhoneNum());
            if (User.getInstance().getIcon() == null) {
                ImageRequest request = new ImageRequest(User.getInstance().getIconUrl(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                Log.d(FragmentMy.class.getName(), "get icon");
                                // 临时存储用户icon
                                User.getInstance().setIcon(bitmap);
                                ivAvatar.setImageBitmap(bitmap);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(FragmentMy.class.getName(), volleyError.getMessage(), volleyError);
                                Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                requestQueue.add(request);
            } else {
                ivAvatar.setImageBitmap(User.getInstance().getIcon());
            }
        } else {
            // 用户若没有登录，跳转到登陆界面
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.fragment_my, null);

        initView(view);
        return view;
    }

    /**
     * 初始化视图
     * @param view view
     */
    private void initView(View view) {
        Log.d(this.getClass().getName(), "init view");
        ivAvatar = (ImageView) view.findViewById(R.id.my_iv_avatar);
        LinearLayout btnSetting = (LinearLayout) view.findViewById(R.id.my_btn_setting);
        btnUser = (RelativeLayout) view.findViewById(R.id.my_btn_user);
        btnWallet = (RelativeLayout) view.findViewById(R.id.my_btn_wallet);
        btnMessage = (RelativeLayout) view.findViewById(R.id.my_btn_message);
        btnHelp = (RelativeLayout) view.findViewById(R.id.my_btn_help);
        btnContact = (RelativeLayout) view.findViewById(R.id.my_btn_contact);
        btnFeedback = (RelativeLayout) view.findViewById(R.id.my_btn_feedback);
        btnAbout = (RelativeLayout) view.findViewById(R.id.my_btn_about);
        tvName = (TextView) view.findViewById(R.id.my_tv_name);
        tvPhone = (TextView) view.findViewById(R.id.my_tv_phone);

        // 添加按钮监听事件
        btnSetting.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnWallet.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnFeedback.setOnClickListener(this);
        btnAbout.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(view.getContext());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_btn_setting:
                Log.d(this.getClass().getName(), "setting");
                Intent intentSetting = new Intent(context, SettingActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.my_btn_user:
                Log.d(this.getClass().getName(), "avatar");
                Intent intentUser = new Intent(context, UserActivity.class);
                startActivity(intentUser);
                break;
            case R.id.my_btn_wallet:
                Log.d(this.getClass().getName(), "wallet");
                Intent intentWallet = new Intent(context, WalletActivity.class);
                startActivity(intentWallet);
                break;
            case R.id.my_btn_message:
                Log.d(this.getClass().getName(), "message");
                Intent intentMessage = new Intent(context, MessageActivity.class);
                startActivity(intentMessage);
                break;
            case R.id.my_btn_contact:
                Log.d(this.getClass().getName(), "contact");
                ((FrameActivity) getActivity()).showPopupWindow();
                break;
            case R.id.my_btn_feedback:
                Log.d(this.getClass().getName(), "feedback");
                Intent intentFeedback = new Intent(context, FeedbackActivity.class);
                startActivity(intentFeedback);
                break;
            case R.id.my_btn_about:
                Log.d(this.getClass().getName(), "about");
                Intent intentAbout = new Intent(context, AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                Log.e(this.getClass().getName(), "unknown button id");
                break;
        }
    }

}
