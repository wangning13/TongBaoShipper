package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.Driver;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.DriverAdapter;

public class AddDriverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int HANDLER_UI = 0;
    private EditText etSearch;
    private LinearLayout vEmpty;
    private ListView lvAddDriver;
    private ArrayList<Driver> arrDriver;
    private DriverAdapter driverAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        initView();

        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        etSearch = (EditText) findViewById(R.id.add_driver_et_search);
        ImageView ivSearch = (ImageView) findViewById(R.id.add_driver_iv_search);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.add_driver_btn_back);
        vEmpty = (LinearLayout) findViewById(R.id.add_driver_empty);
        lvAddDriver = (ListView) findViewById(R.id.add_driver_lv);

        ivSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_driver_btn_back:
                Log.d(AddDriverActivity.class.getName(), "back");
                finish();
                break;
            case R.id.add_driver_iv_search:
                Log.d(AddDriverActivity.class.getName(), "search");
                if (etSearch.getText().toString().equals("")) {
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("phoneNum", etSearch.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_SEARCH_DRIVER,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(AddDriverActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.getResult(jsonObject)) {
                                        arrDriver = ShipperService.searchDriver(jsonObject);
                                    } else {
                                        Toast.makeText(AddDriverActivity.this, ShipperService.getErrorMsg(jsonObject),
                                                Toast.LENGTH_SHORT).show();
                                        arrDriver = new ArrayList<>();
                                    }
                                    final UIHandler handler = new UIHandler(AddDriverActivity.this);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(AddDriverActivity.class.getName(), "thread ui");
                                            handler.sendEmptyMessage(HANDLER_UI);
                                        }
                                    });
                                    thread.start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(AddDriverActivity.class.getName(), volleyError.getMessage(), volleyError);
                                // http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(AddDriverActivity.this, AddDriverActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(AddDriverActivity.class.getName(), "Unknown id");
                break;
        }
    }

    static class UIHandler extends Handler {
        WeakReference<AddDriverActivity> activity;

        UIHandler(AddDriverActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AddDriverActivity addDriverActivity = activity.get();
            switch (msg.what) {
                case HANDLER_UI:
                    Log.d(AddDriverActivity.class.getName() + UIHandler.class.getName(), "handler ui");
                    addDriverActivity.driverAdapter = new DriverAdapter(addDriverActivity,
                            addDriverActivity.arrDriver,
                            addDriverActivity.lvAddDriver,
                            DriverAdapter.OperationType.add);
                    addDriverActivity.lvAddDriver.setAdapter(addDriverActivity.driverAdapter);
                    addDriverActivity.lvAddDriver.setEmptyView(addDriverActivity.vEmpty);
                    break;
                default:
                    Log.e(AddDriverActivity.class.getName() + UIHandler.class.getName(), "Unknown handler id");
                    break;
            }
        }
    }
}
