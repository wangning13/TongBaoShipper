package cn.edu.nju.software.tongbaoshipper.controller.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sevenheaven.segmentcontrol.OnRefreshListener;
import com.sevenheaven.segmentcontrol.RefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.model.Order;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.activity.RunningOrderActivity;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.OrderListAdapter;


/**
 * Created by zhang on 2016/4/13.
 */


public class RunningOrderTab extends Fragment implements OnRefreshListener {
    private ArrayList<Order> orderList;
    private OrderListAdapter adapter;
    private RefreshListView rListView;
    private RequestQueue requestQueue;
    private LinearLayout emptyView;




    @Override
    public void onDownPullRefresh() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1000);
                //添加数据
                refreshOrderList();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter.notifyDataSetChanged();
                rListView.hideHeaderView();
            }
        }.execute(new Void[] {});
    }

    @Override
    public void onLoadingMore() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1000);

                //添加数据
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter.notifyDataSetChanged();

                // 控制脚布局隐藏
                rListView.hideFooterView();
            }
        }.execute(new Void[]{});
    }

    private void refreshOrderList()
    {
        System.out.println("继续刷新订单");
        Map<String, String> params = new HashMap<>();
        params.put("token", User.getInstance().getToken());
        params.put("type","1");
        Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_SHOW_MY_ORDER_LIST,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(getContext().getClass().getName(), jsonObject.toString());
                        try {
                            if (ShipperService.getResult(jsonObject)) {
                                System.out.println(jsonObject);
                                System.out.println("Refresh Waiting");
                                orderList=ShipperService.getOrderList(jsonObject);
                                System.out.println("一共" + orderList.size() + "条记录");
                                if (orderList!=null)
                                {
                                    if (orderList.size()<1) emptyView.setVisibility(View.VISIBLE);
                                    else  emptyView.setVisibility(View.INVISIBLE);
                                    adapter = new OrderListAdapter(getContext(),orderList,rListView);
                                    rListView.setAdapter(adapter);
                                }
                                else  emptyView.setVisibility(View.INVISIBLE);

                            } else {
                                Toast.makeText(getContext(), ShipperService.getErrorMsg(jsonObject),
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
                        Log.e(getContext().getClass().getName(), volleyError.getMessage(), volleyError);
                        // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }, params);
        requestQueue.add(request);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        refreshOrderList();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume


        } else {
            //相当于Fragment的onPause
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_orderrunning, container, false);
        initView(view);
        requestQueue= Volley.newRequestQueue(getContext());
        return  view;
    }



    private void initView(View view)
    {
        Log.i(this.getClass().getName(), "init view");

        rListView = (RefreshListView) view.findViewById(R.id.running_order_lv);
        emptyView = (LinearLayout) view.findViewById(R.id.ruuning_order_empty);

        rListView.setOnRefreshListener(this);
        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), RunningOrderActivity.class);
                intent.putExtra("id",id+"");
                startActivity(intent);

            }
        });
    }


}

