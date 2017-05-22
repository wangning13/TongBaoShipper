package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.constant.RequestCode;
import cn.edu.nju.software.tongbaoshipper.service.UserService;
import cz.msebera.android.httpclient.Header;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 用户头像存储名称（拍照）
     */
    private static final String USER_PHOTO_NAME = "/avatar.png";
    /**
     * 更新头像
     */
    private static final int HANDLE_UI_IV_AVATAR  = 0;

    private ImageView ivAvatar;
    private TextView tvName, tvPhone;
    private PopupWindow popupWindow;
    private RequestQueue requestQueue;
    private AsyncHttpClient client;

    @Override
    protected void onResume() {
        super.onResume();

        // 设置用户昵称、手机号、头像信息
        if (User.isLogin()) {
            tvName.setText(User.getInstance().getNiceName());
            tvPhone.setText(User.getInstance().getPhoneNum());
            String internetUrl = "http://on5vygg48.bkt.clouddn.com/vae.jpg";
            if (User.getInstance().getIcon() != null) {
                ivAvatar.setImageBitmap(User.getInstance().getIcon());
            }
        } else {
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ivAvatar = (ImageView) findViewById(R.id.user_iv_avatar);
        tvName = (TextView) findViewById(R.id.user_tv_name);
        tvPhone = (TextView) findViewById(R.id.user_tv_phone);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.user_btn_back);
        RelativeLayout btnAvatar = (RelativeLayout) findViewById(R.id.user_btn_avatar);
        RelativeLayout btnName = (RelativeLayout) findViewById(R.id.user_btn_name);

        // 添加监听事件
        btnBack.setOnClickListener(this);
        btnAvatar.setOnClickListener(this);
        btnName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_btn_back:
                Log.d(this.getClass().getName(), "back");
                finish();
                break;
            case R.id.user_btn_avatar:
                Log.d(this.getClass().getName(), "avatar");
                showPopWindow();
                break;
            case R.id.user_btn_name:
                Log.d(this.getClass().getName(), "name");
                View vDialog = LayoutInflater.from(UserActivity.this).inflate(R.layout.dialog_user_modify_nick_name, null);
                final Dialog dialog = new AlertDialog.Builder(UserActivity.this).setView(vDialog).create();
                final EditText etNickName = (EditText) vDialog.findViewById(R.id.dialog_et);
                Button btnCancel = (Button) vDialog.findViewById(R.id.dialog_btn_cancel);
                Button btnComfirm = (Button) vDialog.findViewById(R.id.dialog_btn_confirm);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(UserActivity.class.getName(), "cancel modify nick name");
                        dialog.dismiss();
                    }
                });
                btnComfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // modify user nick name
                        Log.d(User.class.getName(), "confirm modify nick name");
                        Map<String, String> params = new HashMap<>();
                        params.put("token", User.getInstance().getToken());
                        params.put("nickName", etNickName.getText().toString());
                        Request<JSONObject> request = new PostRequest(Net.URL_USER_MODIFY_NICK_NAME,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        Log.d(UserActivity.class.getName(), jsonObject.toString());
                                        try {
                                            if (UserService.modifyNickName(jsonObject, etNickName.getText().toString())) {
                                                Toast.makeText(UserActivity.this, UserActivity.this.getResources().getString(R.string.user_modify_nick_name_success),
                                                        Toast.LENGTH_SHORT).show();
                                                // change ui user nick name
                                                tvName.setText(etNickName.getText().toString());

                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(UserActivity.this, UserService.getErrorMsg(jsonObject),
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
                                        Log.e(UserActivity.class.getName(), volleyError.getMessage(), volleyError);
                                        // http authentication 401
//                                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                                    startActivity(intent);
//                                                    return;
//                                                }
                                        Toast.makeText(UserActivity.this, UserActivity.this.getResources().getString(R.string.network_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, params);
                        requestQueue.add(request);
                    }
                });
                dialog.show();
                break;
            default:
                Log.e(this.getClass().getName(), "unknown button id");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 设置当popupWindow出现时，点击back退出
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

    /**
     * 设置popupWindow点击外部dismiss
     *
     * @param event     motion event
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
     * 显示底部更换头像菜单
     */
    private void showPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_user_avatar, null);

        popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.pop_window_anim);
        popupWindow.showAtLocation(findViewById(R.id.activity_user), Gravity.BOTTOM, 0, 0);

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
        LinearLayout btnCamera = (LinearLayout) view.findViewById(R.id.avatar_setting_btn_camera);
        LinearLayout btnPhoto = (LinearLayout) view.findViewById(R.id.avatar_setting_btn_photo);
        LinearLayout btnCancel = (LinearLayout) view.findViewById(R.id.avatar_setting_btn_cancel);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(UserActivity.class.getName(), "pop camera");
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), USER_PHOTO_NAME)));
                    startActivityForResult(intent, RequestCode.REQUEST_USER_ACTIVITY_CAMERA);
                } catch (Exception e) {
                    Toast.makeText(UserActivity.this, "相机无法启动，请先开启相机权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(UserActivity.class.getName(), "pop photo");
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RequestCode.REQUEST_USER_ACTIVITY_PHOTO);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(UserActivity.class.getName(), "pop cancel");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
    }

    /**
     * 调用系统裁剪photo
     * @param uri   uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", this.getResources().getDimension(R.dimen.avatar_width));
        intent.putExtra("outputY", this.getResources().getDimension(R.dimen.avatar_width));
//        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), USER_PHOTO_NAME)));
        startActivityForResult(intent, RequestCode.REQUEST_USER_ACTIVITY_CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCode.REQUEST_USER_ACTIVITY_CAMERA:
                Log.d(UserActivity.class.getName(), String.format("camera: %d", resultCode));
                if (resultCode == RESULT_OK) {
                    File avatar = new File(Environment.getExternalStorageDirectory(), USER_PHOTO_NAME);
//                    System.out.println(avatar.getAbsolutePath());
                    // 裁剪图片
                    cropPhoto(Uri.fromFile(avatar));
                }
                break;
            case RequestCode.REQUEST_USER_ACTIVITY_PHOTO:
                Log.d(UserActivity.class.getName(), String.format("photo: %d", resultCode));
                if (resultCode == RESULT_OK) {
                    // 裁剪图片
                    cropPhoto(data.getData());
                }
                break;
            case RequestCode.REQUEST_USER_ACTIVITY_CROP_PHOTO:
                Log.d(UserActivity.class.getName(), String.format("crop photo: %d", resultCode));
                if (resultCode == RESULT_OK) {
                    File icon = new File(Environment.getExternalStorageDirectory(), USER_PHOTO_NAME);
                    final Bitmap bitmap = BitmapFactory.decodeFile(icon.getAbsolutePath());
                    client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    try {
                        params.put("file", icon, "application/octet-stream");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    client.post(Net.URL_USER_UPLOAD_PICTURE, params,
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d(UserActivity.class.getName(), response.toString());
                                    try {
                                        if (UserService.getResult(response)) {
                                            final String iconUrl = UserService.uploadPicture(response);
                                            // 修改用户icon
                                            RequestParams params = new RequestParams();
                                            params.put("token", User.getInstance().getToken());
                                            params.put("iconUrl", iconUrl);
                                            client.post(Net.URL_USER_MODIFY_ICON, params,
                                                    new JsonHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                            Log.d(UserActivity.class.getName(), response.toString());
                                                            try {
                                                                if (UserService.modifyIcon(response, iconUrl)) {
                                                                    Toast.makeText(UserActivity.this, UserActivity.this.getResources().getString(R.string.user_modify_icon_success),
                                                                            Toast.LENGTH_SHORT).show();
                                                                    User.getInstance().setIcon(bitmap);
                                                                    // 线程更新avatar iv
                                                                    final UIHandler handle = new UIHandler(UserActivity.this);
                                                                    Thread thread = new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Log.d(UserActivity.class.getName(), "thread avatar");
                                                                            handle.sendEmptyMessage(HANDLE_UI_IV_AVATAR);
                                                                        }
                                                                    });
                                                                    thread.start();
                                                                    // popupWindow dismiss
                                                                    if (popupWindow != null && popupWindow.isShowing()) {
                                                                        Log.d(UserActivity.class.getName(), "popupWindow.dismiss()");
                                                                        popupWindow.dismiss();
                                                                        popupWindow = null;
                                                                    }
                                                                } else {
                                                                    Toast.makeText(UserActivity.this, UserService.getErrorMsg(response),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                            Log.e(UserActivity.class.getName(), errorResponse.toString());
                                                            if (statusCode == Net.NET_ERROR_AUTHENTICATION) {
                                                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(UserActivity.this, UserActivity.this.getResources().getString(R.string.network_error),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(UserActivity.this, UserService.getErrorMsg(response),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.e(UserActivity.class.getName(), errorResponse.toString());
                                    if (statusCode == Net.NET_ERROR_AUTHENTICATION) {
                                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(UserActivity.this, UserActivity.this.getResources().getString(R.string.network_error),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            default:
                Log.e(UserActivity.class.getName(), "Unknown result code");
                break;
        }
    }

    /**
     * 弱引用UIHandler(内部类)
     */
    static class UIHandler extends Handler {
        WeakReference<UserActivity> activity;

        UIHandler(UserActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserActivity userActivity = activity.get();
            switch (msg.what) {
                case HANDLE_UI_IV_AVATAR:
                    Log.d(UserActivity.class.getName() + UIHandler.class.getName(), "handle avatar");
                    userActivity.ivAvatar.setImageBitmap(User.getInstance().getIcon());
                    break;
                default:
                    Log.e(UserActivity.class.getName() + UIHandler.class.getName(), "Unknown handle what");
                    break;
            }
        }
    }
}
