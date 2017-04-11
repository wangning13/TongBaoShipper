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
import cn.edu.nju.software.tongbaoshipper.common.Address;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.service.UserService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.AddressAdapter;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvAddress;
    private LinearLayout vEmpty;
    private ArrayList<Address> arrAddress;
    private AddressAdapter addressAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onResume() {
        super.onResume();

        // 初始化用户常用地址信息
        if (User.isLogin()) {
            Map<String, String> params = new HashMap<>();
            params.put("token", User.getInstance().getToken());
            Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_GET_FREQUENT_ADDRESSES,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d(AddressActivity.class.getName(), jsonObject.toString());
                            try {
                                if (ShipperService.getResult(jsonObject)) {
                                    arrAddress = ShipperService.getFrequentAddresses(jsonObject);
                                    addressAdapter = new AddressAdapter(AddressActivity.this, arrAddress, lvAddress);
                                    lvAddress.setAdapter(addressAdapter);
                                    lvAddress.setEmptyView(vEmpty);
                                } else {
                                    Toast.makeText(AddressActivity.this, UserService.getErrorMsg(jsonObject),
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
                            Log.e(AddressActivity.class.getName(), volleyError.getMessage(), volleyError);
                            // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                            Toast.makeText(AddressActivity.this, AddressActivity.this.getResources().getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, params);
            requestQueue.add(request);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initView();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * init view
     */
    private void initView() {
        TextView tvAdd = (TextView) findViewById(R.id.address_title_tv_add);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.address_btn_back);
        lvAddress = (ListView) findViewById(R.id.address_lv);
        vEmpty = (LinearLayout) findViewById(R.id.address_empty);

        tvAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_btn_back:
                Log.d(AddressActivity.class.getName(), "back");
                finish();
                break;
            case R.id.address_title_tv_add:
                Log.d(AddressActivity.class.getName(), "add");
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(AddressActivity.class.getName(), "Unknown id");
                break;
        }
    }
}
