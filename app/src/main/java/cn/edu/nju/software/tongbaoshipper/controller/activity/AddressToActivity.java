package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.AutoAddressAdapter;

/**
 * 演示poi搜索功能
 */
public class AddressToActivity extends AppCompatActivity implements
        OnGetPoiSearchResultListener,View.OnClickListener{

    private PoiSearch mPoiSearch = null;
    private List<PoiInfo> addressList;
    private double longitude,latitude;
    final int RESULT_CODE_ARRIVE=103;

    private LinearLayout ok_btn,back_btn;
    private EditText nameinput,phoneinput;
    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView = null;
    private AutoAddressAdapter addressAdapter = null;
    private int loadIndex = 0;
    private CompleteTextWatcher textwatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_to_search);
        initView();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);




    }
    private class CompleteTextWatcher implements TextWatcher{
        @Override
        public void afterTextChanged(Editable arg0) {


        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                  int arg3) {
            if (cs.length() <= 0) {
                return;
            }
            String city = ((TextView) findViewById(R.id.address_to_tv_city)).getText()
                    .toString();
            EditText editSearchKey = (EditText) findViewById(R.id.address_to_searchkey);
            mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
                    .keyword(editSearchKey.getText().toString())
                    .pageNum(loadIndex));

        }
    }
    private void initView()
    {
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.address_to_searchkey);
        nameinput =(EditText) findViewById(R.id.address_to_name);
        phoneinput =(EditText) findViewById(R.id.address_to_phone);
        back_btn =(LinearLayout) findViewById(R.id.address_to_btn_back);
        ok_btn  =(LinearLayout) findViewById(R.id.address_to_btn_ok);

        back_btn.setOnClickListener(this);
        ok_btn.setOnClickListener(this);



        addressList=new ArrayList<PoiInfo>();
        addressAdapter = new AutoAddressAdapter(AddressToActivity.this,addressList,keyWorldsView);
        keyWorldsView.setAdapter(addressAdapter);
        keyWorldsView.setThreshold(1);


        keyWorldsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("选中地址");
                keyWorldsView.removeTextChangedListener(textwatcher);
                keyWorldsView.setText(addressList.get(position).name + " " + addressList.get(position).address);
                System.out.println(addressList.get(position).name + " " + addressList.get(position).address);

                longitude=addressList.get(position).location.longitude;
                latitude=addressList.get(position).location.latitude;

                keyWorldsView.addTextChangedListener(textwatcher);
                //Toast.makeText(AddressFromActivity.this, latitude+" "+longitude, Toast.LENGTH_LONG).show();
                System.out.println(latitude+" "+longitude);

            }
        });
        /**
         * 当输入关键字变化时，动态更新建议列表
         */

        textwatcher=new CompleteTextWatcher();
        keyWorldsView.addTextChangedListener(textwatcher);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(AddressToActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            addressList = new ArrayList<PoiInfo>();
            for (PoiInfo info : result.getAllPoi()) {
                if (info.location!=null)  addressList.add(info);
                System.out.println(info.name + "***" + info.address);
            }



            addressAdapter = new AutoAddressAdapter(AddressToActivity.this,addressList,keyWorldsView);
            keyWorldsView.setAdapter(addressAdapter);
            addressAdapter.notifyDataSetChanged();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(AddressToActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(AddressToActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            System.out.println(result.getName() + ": " + result.getAddress() + "纬度" + result.getLocation().latitude + "经度" + result.getLocation().longitude);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_to_btn_back:
                Log.d(AddressToActivity.class.getName(), "back");
                finish();
                break;
            case R.id.address_to_btn_ok:
                Log.d(AddressToActivity.class.getName(), "ok");
                System.out.println(phoneinput.length());
                if (keyWorldsView.length()*nameinput.length()*phoneinput.length()!=0) {
                    Intent intent = new Intent();
                    intent.putExtra("to", keyWorldsView.getText().toString());
                    intent.putExtra("name", nameinput.getText().toString());
                    intent.putExtra("phone", phoneinput.getText().toString());
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                    setResult(RESULT_CODE_ARRIVE, intent);
                    finish();
                }
                else {
                    Toast.makeText(AddressToActivity.this, "输入不可为空", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                Log.e(AddressToActivity.class.getName(), "Unknown id");
        }
    }



}
