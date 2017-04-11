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
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFeedback;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        etFeedback = (EditText) findViewById(R.id.feedback_tv);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.feedback_btn_back);
        LinearLayout btnSubmit = (LinearLayout) findViewById(R.id.feedback_btn_submit);

        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_btn_back:
                Log.d(FeedbackActivity.class.getName(), "back");
                finish();
                break;
            case R.id.feedback_btn_submit:
                Log.d(FeedbackActivity.class.getName(), "submit");
                if (etFeedback.getText().toString().equals("")) {
                    Toast.makeText(FeedbackActivity.this, FeedbackActivity.this.getResources().getString(R.string.feedback_input_error),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Map<String, String> params = new HashMap<>();
                params.put("content", etFeedback.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_USER_ADD_FEEDBACK,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(FeedbackActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (UserService.addFeedback(jsonObject)) {
                                        Toast.makeText(FeedbackActivity.this, FeedbackActivity.this.getResources().getString(R.string.feedback_success),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(FeedbackActivity.this, UserService.getErrorMsg(jsonObject),
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
                                Log.e(FeedbackActivity.class.getName(), volleyError.getMessage(), volleyError);
//                                http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(FeedbackActivity.this, FeedbackActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(FeedbackActivity.class.getName(), "Unknown id");
                break;
        }
    }
}
