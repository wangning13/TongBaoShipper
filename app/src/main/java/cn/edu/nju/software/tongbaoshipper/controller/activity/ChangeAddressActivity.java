package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.Address;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.PoiInfoAdapter;

public class ChangeAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvPoiInfo;
    private Address address;
    private EditText etLocation, etPhone, etContact;
    private AddressTextWatch addressTextWatch;
    private PoiSearch poiSearch;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_add_address);

        Intent intent = this.getIntent();
        address = (Address) intent.getSerializableExtra(Address.class.getName());
        addressTextWatch = new AddressTextWatch();
        initView();
        requestQueue = Volley.newRequestQueue(this);
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new AddressPoiSearchResultListener());
    }

    /**
     * init view
     */
    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.add_address_tv_title);
        etLocation = (EditText) findViewById(R.id.add_address_et_location);
        etPhone = (EditText) findViewById(R.id.add_address_et_phone);
        etContact = (EditText) findViewById(R.id.add_address_et_contact);
        lvPoiInfo = (ListView) findViewById(R.id.add_address_lv);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.add_address_btn_back);
        LinearLayout btnConfirm = (LinearLayout) findViewById(R.id.add_address_btn);

        tvTitle.setText(R.string.title_change_address);
        etLocation.setText(address.getAddressName());
        etPhone.setText(address.getPhoneNum());
        etContact.setText(address.getContactName());

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        etLocation.addTextChangedListener(addressTextWatch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_address_btn_back:
                Log.d(ChangeAddressActivity.class.getName(), "back");
                finish();
                break;
            case R.id.add_address_btn:
                Log.d(ChangeAddressActivity.class.getName(), "confirm");
                if (etLocation.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") ||
                        address == null) {
                    Toast.makeText(ChangeAddressActivity.this, ChangeAddressActivity.this.getResources().getString(R.string.add_address_info_less),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("id", String.valueOf(address.getId()));
                params.put("address", etLocation.getText().toString());
                params.put("lat", String.valueOf(address.getLat()));
                params.put("lng", String.valueOf(address.getLng()));
                params.put("contactName", etContact.getText().toString());
                params.put("contactPhone", etPhone.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_EDIT_FREQUENT_ADDRESS,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if (ShipperService.editFrequentAddress(jsonObject)) {
                                        Toast.makeText(ChangeAddressActivity.this, ChangeAddressActivity.this.getResources().getString(R.string.change_address_success),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ChangeAddressActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                                Log.e(ChangeAddressActivity.class.getName(), volleyError.getMessage(), volleyError);
//                                http authentication 401
//                                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                                    startActivity(intent);
//                                                    return;
//                                                }
                                Toast.makeText(ChangeAddressActivity.this, ChangeAddressActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(ChangeAddressActivity.class.getName(), "Unknown id");
                break;
        }
    }


    /**
     * set address info
     *
     * @param addressName String
     * @param location    Location
     */
    public void setLocation(String addressName, LatLng location) {
        // remove text changed listener
        etLocation.removeTextChangedListener(addressTextWatch);
        etLocation.setText(addressName);
        this.address.setLat(location.latitude);
        this.address.setLng(location.longitude);
        lvPoiInfo.setVisibility(View.INVISIBLE);
        // add text changed listener
        etLocation.addTextChangedListener(addressTextWatch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poiSearch.destroy();
    }

    class AddressPoiSearchResultListener implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Log.d(AddAddressActivity.class.getName(), "Not found result!");
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                Log.d(AddAddressActivity.class.getName(),
                        String.format("Found total poi number: %d, page number: %d",
                                poiResult.getTotalPoiNum(), poiResult.getTotalPageNum())
                );
                List<PoiInfo> poiInfos = poiResult.getAllPoi();
                Log.d(AddAddressActivity.class.getName(), String.valueOf(poiInfos.size()));
                PoiInfoAdapter poiInfoAdapter = new PoiInfoAdapter(ChangeAddressActivity.this,
                        poiInfos, lvPoiInfo, PoiInfoAdapter.OperationType.change);
                lvPoiInfo.setAdapter(poiInfoAdapter);
                lvPoiInfo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult == null || poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.d(AddAddressActivity.class.getName(), "Not found result!");
            } else {
                Log.d(AddAddressActivity.class.getName(), poiDetailResult.getName() + poiDetailResult.getAddress());
            }
        }
    }

    private class AddressTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String location = etLocation.getText().toString();
            if (!location.equals("")) {
                poiSearch.searchInCity(new PoiCitySearchOption()
                                .city("南京")
                                .keyword(location)
                                .pageCapacity(Common.POI_SEARCH_PAGE_CAPACITY)
                );
            }
        }
    }
}
