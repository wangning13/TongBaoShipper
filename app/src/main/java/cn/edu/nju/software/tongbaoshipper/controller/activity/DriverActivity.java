package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.model.Driver;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.DriverAdapter;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout vEmpty;
    private ArrayList<Driver> arrDriver;
    private DriverAdapter driverAdapter;
    private ListView lvDriver;
    private RequestQueue requestQueue;

    @Override
    protected void onResume() {
        super.onResume();

        if (User.isLogin()) {
            Map<String, String> params = new HashMap<>();
            params.put("token", User.getInstance().getToken());
            Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_GET_FREQUENT_DRIVERS,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(DriverActivity.class.getName(), jsonObject.toString());
                            try {
                                if (ShipperService.getResult(jsonObject)) {
                                    arrDriver = ShipperService.getFrequentDrivers(jsonObject);
                                    driverAdapter = new DriverAdapter(DriverActivity.this,
                                            arrDriver, lvDriver, DriverAdapter.OperationType.dial);
                                    lvDriver.setAdapter(driverAdapter);
                                    lvDriver.setEmptyView(vEmpty);
                                } else {
                                    Toast.makeText(DriverActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                            Log.e(DriverActivity.class.getName(), volleyError.getMessage(), volleyError);
                            // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                            Toast.makeText(DriverActivity.this, DriverActivity.this.getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, params);
            requestQueue.add(request);
        } else {
            Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        TextView tvAdd = (TextView) findViewById(R.id.driver_title_tv_add);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.driver_btn_back);
        vEmpty = (LinearLayout) findViewById(R.id.driver_empty);
        lvDriver = (ListView) findViewById(R.id.driver_lv);

        btnBack.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver_btn_back:
                Log.d(DriverActivity.class.getName(), "back");
                finish();
                break;
            case R.id.driver_title_tv_add:
                Log.d(DriverActivity.class.getName(), "add");
                Intent intent = new Intent(DriverActivity.this, AddDriverActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(DriverActivity.class.getName(), "Unknown id");
                break;
        }
    }
}
