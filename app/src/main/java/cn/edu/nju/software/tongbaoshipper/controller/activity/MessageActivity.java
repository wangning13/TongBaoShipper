package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.model.Message;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.MessageAdapter;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvMessage;
    private LinearLayout vEmpty;
    private ArrayList<Message> arrMessage;
    private MessageAdapter messageAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onResume() {
        super.onResume();

        if (User.isLogin()) {
            Map<String, String> params = new HashMap<>();
            params.put("token", User.getInstance().getToken());
            Request<JSONObject> request = new PostRequest(Net.URL_USER_GET_MY_MESSAGES,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                Log.d(MessageActivity.class.getName(), jsonObject.toString());
                                if (UserService.getResult(jsonObject)) {
                                    arrMessage = UserService.getMyMessages(jsonObject);
                                    messageAdapter = new MessageAdapter(MessageActivity.this, arrMessage, lvMessage);
                                    lvMessage.setAdapter(messageAdapter);
                                    // set empty view
                                    lvMessage.setEmptyView(vEmpty);
                                    // set message hasRead
                                    for (Message message : arrMessage) {
                                        if (!message.isHasRead()) {
                                            Map<String, String> paramsHasRead = new HashMap<>();
                                            paramsHasRead.put("token", User.getInstance().getToken());
                                            paramsHasRead.put("id", String.valueOf(message.getId()));
                                            Request<JSONObject> requestHasRead = new PostRequest(Net.URL_USER_READ_MESSAGE,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject jsonObject) {
                                                            try {
                                                                if (!UserService.readMessage(jsonObject)) {
                                                                    Toast.makeText(MessageActivity.this, UserService.getErrorMsg(jsonObject),
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
                                                            Log.e(MessageActivity.class.getName(), volleyError.getMessage(), volleyError);
                                                            //http authentication 401
//                                                            if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                                                startActivity(intent);
//                                                                return;
//                                                            }
                                                            Toast.makeText(MessageActivity.this, MessageActivity.this.getResources().getString(R.string.network_error),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, paramsHasRead);
                                            requestQueue.add(requestHasRead);
                                        }
                                    }
                                } else {
                                    Toast.makeText(MessageActivity.this, UserService.getErrorMsg(jsonObject),
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
                            Log.e(MessageActivity.class.getName(), volleyError.getMessage(), volleyError);
                            Toast.makeText(MessageActivity.this, MessageActivity.this.getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, params);
            requestQueue.add(request);
        } else {
            Intent intent = new Intent(MessageActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();

        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */

    private void initView() {
        LinearLayout btnBack = (LinearLayout) this.findViewById(R.id.message_btn_back);
        lvMessage = (ListView) this.findViewById(R.id.message_lv);
        vEmpty = (LinearLayout) findViewById(R.id.message_empty);

        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_btn_back:
                Log.d(MessageActivity.class.getName(), "back");
                finish();
                break;
            default:
                Log.e(MessageActivity.class.getName(), "Unknown id");
                break;
        }
    }
}
