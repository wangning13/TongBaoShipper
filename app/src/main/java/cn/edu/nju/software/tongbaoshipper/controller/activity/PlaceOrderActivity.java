package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.sevenheaven.segmentcontrol.CompleteListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.Truck;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.OrderedTruckAdapter;

/**
 * Created by zhang on 2016/4/16.
 */
public class PlaceOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private CompleteListView vehicleList;
    private ArrayList<Truck> arrTruck,allList;
    private EditText date_input,start_point_input,arrive_point_input;
    private OrderedTruckAdapter adapter;
    private LinearLayout btn_back,btn_add_truck;
    private RelativeLayout btn_place_order;
    private ScrollView sv;
    private RoutePlanSearch mDriveSearch;
    private TextView text_money,text_distance;
    private RequestQueue requestQueue;
    private String startaddress,startname,startphone,arriveaddress,arrivename,arrivephone;

    private int distance,price;
    private double startlng,startlat,arrivelng,arrivelat;

    final int RESULT_CODE_DATE=101;
    final int RESULT_CODE_START=102;
    final int RESULT_CODE_ARRIVE=103;
    final int RESULT_CODE_TRUCK=104;

    final int REQUEST_CODE_DATE=101;
    final int REQUEST_CODE_START=102;
    final int REQUEST_CODE_ARRIVE=103;
    final int REQUEST_CODE_TRUCK=104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        initView();
        requestQueue = Volley.newRequestQueue(this);


    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Intent intent=getIntent();
        int request=intent.getIntExtra("source",0);
        if (request==1){
            startaddress=intent.getStringExtra("address");
            startname=intent.getStringExtra("contact");
            startphone=intent.getStringExtra("phone");
            startlat =intent.getDoubleExtra("lat", -200);
            startlng =intent.getDoubleExtra("lng",-200);
            start_point_input.setText(startaddress+"\n"+startname+" "+startphone);
        }
        if (request==2){
            arriveaddress=intent.getStringExtra("address");
            arrivename=intent.getStringExtra("contact");
            arrivephone =intent.getStringExtra("phone");
            arrivelat=intent.getDoubleExtra("lat",-200);
            arrivelng=intent.getDoubleExtra("lng",-200);
            arrive_point_input.setText(arriveaddress+"\n"+arrivename+" "+arrivephone);
        }




    }

    @Override
    protected void onDestroy() {
        mDriveSearch.destroy();
        super.onDestroy();
    }

    private void initView()
    {
        System.out.println("Place Order Get TruckType");
        allList=ShipperService.getAllTruckType(PlaceOrderActivity.this);
        distance=price=-1;
        startlng=startlat=arrivelng=arrivelat=-200;
        mDriveSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener =new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {


                System.out.println(drivingRouteResult.getRouteLines().get(0).getDistance()+"M");
                distance=(drivingRouteResult.getRouteLines().get(0).getDistance()-1)/1000+1;
                price=0;
                for (Truck t:arrTruck)
                {
                    price+=t.getStartingprice();
                    if (distance>t.getBaseDistance())
                        price+=t.getPrice()*(distance-t.getBaseDistance());
                }
                text_money.setText("运输费用共 "+price+"元");

                text_distance.setText("行车距离共 "+distance+"公里");

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        mDriveSearch.setOnGetRoutePlanResultListener(listener);




        date_input=(EditText)findViewById(R.id.date_input);
        start_point_input=(EditText)findViewById(R.id.start_address_input);
        arrive_point_input=(EditText)findViewById(R.id.arrive_address_input);
        btn_add_truck=(LinearLayout)findViewById(R.id.add_truck_btn);
        btn_back=(LinearLayout)findViewById(R.id.place_order_btn_back);
        btn_place_order=(RelativeLayout)findViewById(R.id.place_order_truck_btn);
        vehicleList=(CompleteListView) findViewById(R.id.vehicle_list);
        sv=(ScrollView)findViewById(R.id.place_order_sv);
        text_money=(TextView)findViewById(R.id.transport_money_text);
        text_distance=(TextView)findViewById(R.id.transport_distance_text);

        arrTruck=new ArrayList<>();
        adapter = new OrderedTruckAdapter(vehicleList.getContext(),arrTruck,vehicleList);
        vehicleList.setAdapter(adapter);

        btn_back.setOnClickListener(this);
        btn_add_truck.setOnClickListener(this);
        btn_place_order.setOnClickListener(this);
        date_input.setOnTouchListener(new EditTextClickListener());

        start_point_input.setOnTouchListener(new EditTextClickListener());
        arrive_point_input.setOnTouchListener(new EditTextClickListener());
        start_point_input.addTextChangedListener(new AddressChangeListener());
        arrive_point_input.addTextChangedListener(new AddressChangeListener());

    }

    private class AddressChangeListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (startlng>-200 && arrivelng>-200)
            {
                System.out.println("开始计算距离");
                PlanNode st = PlanNode.withLocation(new LatLng(startlat, startlng));
                PlanNode ed = PlanNode.withLocation(new LatLng(arrivelat, arrivelng));
                mDriveSearch.drivingSearch(new DrivingRoutePlanOption().from(st).to(ed));

            }


        }
    }

    private class EditTextClickListener implements View.OnTouchListener
    {
        int touch_flag=0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            touch_flag++;
            if(touch_flag>=2){
                //自己业务
                switch(v.getId()) {
                    case R.id.date_input:
                        Log.d(PlaceOrderActivity.class.getName(), "get date");
                        Intent intent=new Intent(PlaceOrderActivity.this,DatePickerActivity.class);
                        startActivityForResult(intent,REQUEST_CODE_DATE);
                        break;
                    case R.id.start_address_input:
                        Log.d(PlaceOrderActivity.class.getName(), "get start");
                        Intent fromintent=new Intent(PlaceOrderActivity.this,AddressFromActivity.class);
                        startActivityForResult(fromintent, REQUEST_CODE_START);

                        break;
                    case R.id.arrive_address_input:
                        Log.d(PlaceOrderActivity.class.getName(), "get arrive");
                        Intent tointent=new Intent(PlaceOrderActivity.this,AddressToActivity.class);
                        startActivityForResult(tointent, REQUEST_CODE_ARRIVE);
                        break;
                    default:
                        Log.e(PlaceOrderActivity.class.getName(), "Unknown id");
                }
                touch_flag=0;

            }
            return false;
        }
    }

    private String getTruckArray()
    {
        String s="["+arrTruck.get(0).getId();
        for(int i=1;i<arrTruck.size();i++)
        {
            s+=","+arrTruck.get(i).getId();
        }
        s+="]";
        return s;
    }

    private boolean checkOrderInfo()
    {
        if (date_input.length()*start_point_input.length()*arrive_point_input.length()*arrTruck.size()==0)
            return false;
        else
            return true;
    }

    private void splitOrder(final Map<String, String> params)
    {
        AlertDialog.Builder builder;
        AlertDialog dialog;
        View dialogView;
        TextView dialogText;

        builder = new AlertDialog.Builder(this);
        dialogView=(getLayoutInflater().inflate(R.layout.dialog_item_confirm,null));
        dialogText=(TextView)dialogView.findViewById(R.id.dialog_confirm_text);
        dialogText.setText("下单失败，订单过大，是否需要拆单 ？");
        builder.setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_SPLIT_ORDER,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(PlaceOrderActivity.class.getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.splitOrder(jsonObject)) {
                                        System.out.println("下单成功");
                                        Toast.makeText(PlaceOrderActivity.this, "下单成功",
                                                Toast.LENGTH_SHORT).show();
                                        // 添加成功自动关闭
                                        finish();
                                    }  else {
                                        Toast.makeText(PlaceOrderActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                                Log.e(PlaceOrderActivity.class.getName(), volleyError.getMessage(), volleyError);
                                //http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(PlaceOrderActivity.this, PlaceOrderActivity.this.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);


            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击取消按钮的业务逻辑
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        AlertDialog dialog;
        View dialogView;
        TextView dialogText;
        switch(v.getId()) {
            case R.id.place_order_btn_back:
                Log.d(PlaceOrderActivity.class.getName(), "back");
                finish();
                break;
            case R.id.add_truck_btn:
                Log.d(PlaceOrderActivity.class.getName(), "add truck");
                Intent intent=new Intent(PlaceOrderActivity.this,AllTruckActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TRUCK);
                vehicleList.setAdapter(adapter);
                break;
            case R.id.place_order_truck_btn:
                Log.d(PlaceOrderActivity.class.getName(), "place order");

                if (checkOrderInfo()){


                    builder = new AlertDialog.Builder(this);
                    dialogView=(getLayoutInflater().inflate(R.layout.dialog_item_confirm,null));
                    dialogText=(TextView)dialogView.findViewById(R.id.dialog_confirm_text);
                    dialogText.setText("确认下单 ？");
                    builder.setView(dialogView);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 执行点击确定按钮的业务逻辑
                            final Map<String, String> params = new HashMap<>();
                            params.put("token", User.getInstance().getToken());
                            params.put("addressFrom",startaddress);
                            params.put("addressFromLat", startlat+"");
                            params.put("addressFromLng", startlng+"");
                            params.put("addressTo", arriveaddress);
                            params.put("addressToLat", arrivelat+"");
                            params.put("addressToLng", arrivelng+"");
                            params.put("fromContactName",startname);
                            params.put("fromContactPhone", startphone);
                            params.put("toContactName", arrivename);
                            params.put("toContactPhone", arrivephone);
                            params.put("loadTime", date_input.getText().toString());
                            params.put("goodsType", "1");
                            params.put("goodsWeight", "1");
                            params.put("goodsSize", "1");
                            params.put("truckTypes", getTruckArray());
                            params.put("remark", "remark");
                            params.put("payType", "0");
                            params.put("price", price+"");

                            System.out.println(params);



                            Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_PLACE_ORDER,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject jsonObject) {
                                            Log.d(PlaceOrderActivity.class.getName(), jsonObject.toString());
                                            try {
                                                int flag=ShipperService.placeOrder(jsonObject);
                                                if (flag==1) {
                                                    System.out.println("下单成功");
                                                    Toast.makeText(PlaceOrderActivity.this, "下单成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    // 添加成功自动关闭
                                                    finish();
                                                } else if (flag==2) {
                                                    splitOrder(params);
                                                } else {
                                                    Toast.makeText(PlaceOrderActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                                            Log.e(PlaceOrderActivity.class.getName(), volleyError.getMessage(), volleyError);
                                            //http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                            Toast.makeText(PlaceOrderActivity.this, PlaceOrderActivity.this.getResources().getString(R.string.network_error),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }, params);
                            requestQueue.add(request);


                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 执行点击取消按钮的业务逻辑
                        }
                    });
                    dialog = builder.create();
                    dialog.show();



                }
                else    {

                    Toast.makeText(PlaceOrderActivity.this, "请填写完整的订单信息",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                Log.e(PlaceOrderActivity.class.getName(), "Unknown id");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(PlaceOrderActivity.class.getName(), "onActivityResult"+"requestCode"+requestCode+"\n resultCode="+resultCode);
        if(requestCode==REQUEST_CODE_DATE) {
            if(resultCode==RESULT_CODE_DATE) {


                String result=data.getStringExtra("date");
                date_input.setText(result);
            }
        }

        if(requestCode==REQUEST_CODE_TRUCK) {
            if(resultCode==RESULT_CODE_TRUCK) {
                int result=data.getIntExtra("truck", 0);
                arrTruck.add(allList.get(result));
                if (distance>0){
                    price=0;

                    for (Truck t:arrTruck)
                    {
                        price+=t.getStartingprice();
                        if (distance>t.getBaseDistance())
                            price+=t.getPrice()*(distance-t.getBaseDistance());
                    }
                    text_money.setText("运输费用共 " + price + "元");
                }
                vehicleList.setAdapter(vehicleList.getAdapter());
            }
        }
        if(requestCode==REQUEST_CODE_START) {
            if(resultCode==RESULT_CODE_START) {
                startaddress=data.getStringExtra("from");
                startname=data.getStringExtra("name");
                startphone=data.getStringExtra("phone");
                startlng=data.getDoubleExtra("longitude",-200);
                startlat=data.getDoubleExtra("latitude",-200);
                start_point_input.setText(startaddress+"\n"+startname+" "+startphone);
            }
        }
        if(requestCode==REQUEST_CODE_ARRIVE) {
            if(resultCode==RESULT_CODE_ARRIVE) {
                arriveaddress=data.getStringExtra("to");
                arrivename=data.getStringExtra("name");
                arrivephone=data.getStringExtra("phone");
                arrivelng=data.getDoubleExtra("longitude",-200);
                arrivelat=data.getDoubleExtra("latitude",-200);
                arrive_point_input.setText(arriveaddress+"\n"+arrivename+" "+arrivephone);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
