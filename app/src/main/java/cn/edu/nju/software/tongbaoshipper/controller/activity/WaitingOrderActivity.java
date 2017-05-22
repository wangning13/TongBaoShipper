package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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

import cn.edu.nju.software.tongbaoshipper.model.Order;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;

/**
 * Created by zhang on 2016/4/16.
 */
public class WaitingOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView order_state;
    private TextView order_id;
    private TextView from_address;
    private TextView to_address;
    private TextView from_contact;
    private TextView to_contact;
    private TextView load_time;
    private TextView place_time;
    private TextView truck_type;
    private TextView order_price;
    private TextView cancel_tv;
    private TextView ok_tv;
    private ImageView state_iv;
    private RequestQueue requestQueue;
    private LinearLayout evaluation_view;

    private RatingBar evaluation_rating;
    private TextView evaluation_text;
    private TextView driver_tv;
    private Order order;
    private LinearLayout btn_back, btn_cancel, btn_ok;
    String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        requestQueue = Volley.newRequestQueue(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        // 初始化用户常用地址信息


        Intent intent = getIntent();
        orderid = intent.getStringExtra("id");
        System.out.println(orderid + "号订单获取信息");
        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("id", orderid);

        Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_GET_ORDER_DETAIL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(WaitingOrderActivity.class.getName(), jsonObject.toString());
                        try {
                            if (ShipperService.getResult(jsonObject)) {
                                System.out.println("开始解析订单");
                                System.out.println(jsonObject);

                                order = ShipperService.getDetailOrder(jsonObject);
                                order_state.setText("订单尚未被抢，等待司机抢单");
                                state_iv.setImageDrawable(getResources().getDrawable(R.drawable.order_waiting));
                                order_id.setText(order.getId() + "");
                                from_address.setText(order.getAddressFrom());
                                to_address.setText(order.getAddressTo());
                                from_contact.setText(order.getFromContactName() + " " + order.getFromContactPhone());
                                to_contact.setText(order.getToContactName() + " " + order.getToContactPhone());
                                load_time.setText(order.getLoadTime());
                                place_time.setText(order.getPlaceTime());
                                order_price.setText(order.getPrice() + "元");

                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < order.getTruckTypes().length(); i++)
                                    sb.append(order.getTruckTypes().getString(i) + " ");
                                truck_type.setText(sb.toString());

                            } else {
                                Toast.makeText(WaitingOrderActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                        Log.e(AllTruckActivity.class.getName(), volleyError.getMessage(), volleyError);
                        // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                        Toast.makeText(WaitingOrderActivity.this, WaitingOrderActivity.this.getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }, params);
        requestQueue.add(request);

    }


    private void initView() {
        order = ShipperService.getTestOrder();

        order_state = (TextView) findViewById(R.id.order_state_tv);
        order_id = (TextView) findViewById(R.id.detail_order_id_tv);
        from_address = (TextView) findViewById(R.id.detail_order_from_tv);
        to_address = (TextView) findViewById(R.id.detail_order_to_tv);
        from_contact = (TextView) findViewById(R.id.detail_order_from_contact_tv);
        to_contact = (TextView) findViewById(R.id.detail_order_to_contact_tv);
        load_time = (TextView) findViewById(R.id.detail_order_loadtime_tv);
        place_time = (TextView) findViewById(R.id.detail_order_placetime_tv);
        truck_type = (TextView) findViewById(R.id.detail_order_truckType_tv);
        order_price = (TextView) findViewById(R.id.detail_order_price_tv);
        cancel_tv = (TextView) findViewById(R.id.order_detail_cancel_tv);
        ok_tv = (TextView) findViewById(R.id.order_detail_ok_tv);
        state_iv = (ImageView) findViewById(R.id.order_iv_state);
        evaluation_view = (LinearLayout) findViewById(R.id.order_evaluation_info);
        driver_tv = (TextView) findViewById(R.id.detail_order_driver_tv);
        evaluation_rating = (RatingBar) findViewById(R.id.order_detail_remarkPoint);
        evaluation_text = (TextView) findViewById(R.id.detail_order_remarkText);

        btn_cancel = (LinearLayout) findViewById(R.id.order_detail_btn_cancel);
        btn_ok = (LinearLayout) findViewById(R.id.order_detail_btn_ok);
        btn_back = (LinearLayout) findViewById(R.id.order_detail_btn_back);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        order_state.setText("订单已付款，等待司机接单");
        order_id.setText(order.getId() + "");
        from_address.setText(order.getAddressFrom());
        to_address.setText(order.getAddressTo());
        from_contact.setText(order.getFromContactName() + " " + order.getFromContactPhone());
        to_contact.setText(order.getToContactName() + " " + order.getToContactPhone());
        load_time.setText(order.getLoadTime());
        place_time.setText(order.getPlaceTime());
        order_price.setText(order.getPrice() + "元");


        cancel_tv.setText("取消订单");
        ok_tv.setText("再来一单");


    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        AlertDialog dialog;
        View dialogView;
        TextView dialogText;
        switch (v.getId()) {
            case R.id.order_detail_btn_back:
                Log.d(WaitingOrderActivity.class.getName(), "back");
                finish();
                break;
            case R.id.order_detail_btn_cancel:
                Log.d(WaitingOrderActivity.class.getName(), "cancel order");

                builder = new AlertDialog.Builder(this);
                dialogView = (getLayoutInflater().inflate(R.layout.dialog_item_confirm, null));
                dialogText = (TextView) dialogView.findViewById(R.id.dialog_confirm_text);
                dialogText.setText("确认取消订单 ？");
                builder.setView(dialogView);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 执行点击确定按钮的业务逻辑
                        Map<String, String> params = new HashMap<>();
                        params.put("token", User.getInstance().getToken());
                        params.put("id", orderid);

                        Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_CANCEL_ORDER,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        Log.d(WaitingOrderActivity.class.getName(), jsonObject.toString());
                                        try {
                                            if (ShipperService.getResult(jsonObject)) {

                                                Toast.makeText(WaitingOrderActivity.this, "订单已提交取消请求",
                                                        Toast.LENGTH_SHORT).show();


                                            } else {
                                                Toast.makeText(WaitingOrderActivity.this, ShipperService.getErrorMsg(jsonObject),
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
                                        Log.e(AllTruckActivity.class.getName(), volleyError.getMessage(), volleyError);
                                        // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                                        Toast.makeText(WaitingOrderActivity.this, WaitingOrderActivity.this.getResources().getString(R.string.network_error),
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

                break;
            case R.id.order_detail_btn_ok:
                Log.d(WaitingOrderActivity.class.getName(), "another order");

                break;
            default:
                Log.e(WaitingOrderActivity.class.getName(), "Unknown id");
        }
    }

}
