package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Common;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.PoiInfoAdapter;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private LatLng location;
    private ListView lvPoiInfo;
    private EditText etLocation, etContact, etPhone;
    private AddressTextWatch addressTextWatch;
    private PoiSearch poiSearch;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_add_address);

        addressTextWatch = new AddressTextWatch();
        initView();
        requestQueue = Volley.newRequestQueue(this);

        // init poi search
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new AddressPoiSearchResultListener());
    }

    /**
     * init view
     */
    private void initView() {
        lvPoiInfo = (ListView) findViewById(R.id.add_address_lv);
        etLocation = (EditText) findViewById(R.id.add_address_et_location);
        etContact = (EditText) findViewById(R.id.add_address_et_contact);
        etPhone = (EditText) findViewById(R.id.add_address_et_phone);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.add_address_btn_back);
        LinearLayout btnConfirm = (LinearLayout) findViewById(R.id.add_address_btn);

        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        etLocation.addTextChangedListener(addressTextWatch);
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.add_address_btn_back:
                Log.d(AddAddressActivity.class.getName(), "back");
                finish();
                break;
            case R.id.add_address_btn:
                Log.d(AddAddressActivity.class.getName(), "confirm");
                if (etLocation.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") ||
                        location == null) {
                    Toast.makeText(AddAddressActivity.this, AddAddressActivity.this.getResources().getString(R.string.add_address_info_less),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("address", etLocation.getText().toString());
                params.put("lat", String.valueOf(location.latitude));
                params.put("lng", String.valueOf(location.longitude));
                params.put("contactName", etContact.getText().toString());
                params.put("contactPhone", etPhone.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_ADD_FREQUENT_ADDRESS,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(AddAddressActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.addFrequentAddress(jsonObject)) {
                                        Toast.makeText(AddAddressActivity.this, AddAddressActivity.this.getResources().getString(R.string.add_address_success),
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AddAddressActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                                Log.e(AddAddressActivity.class.getName(), volleyError.getMessage(), volleyError);
                                //http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(AddAddressActivity.this, AddAddressActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(AddAddressActivity.class.getName(), "Unknown id");
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
        this.location = location;
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
                PoiInfoAdapter poiInfoAdapter = new PoiInfoAdapter(AddAddressActivity.this,
                        poiInfos, lvPoiInfo, PoiInfoAdapter.OperationType.add);
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

    class AddressTextWatch implements TextWatcher {

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
