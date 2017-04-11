package cn.edu.nju.software.tongbaoshipper.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import cn.edu.nju.software.tongbaoshipper.common.Order;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.controller.activity.HistoryOrderActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.RunningOrderActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.WaitingOrderActivity;
import cn.jpush.android.api.JPushInterface;

public class ReceiverService extends BroadcastReceiver {

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.d(this.getClass().getName(), String.format("onReceive: %s, extras: %s", intent.getAction(), bundle.toString()));

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(this.getClass().getName(), "message received");
            receiveMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(this.getClass().getName(), "notification received");
            receiveNotification(bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(this.getClass().getName(), "notification opened");
            openNotification(context, bundle);
        } else {
            Log.d(this.getClass().getName(), "Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * received message
     *
     * @param bundle Bundle
     */
    private void receiveMessage(Context context, Bundle bundle) {
        Log.d(this.getClass().getName(), String.format("title: %s, message: %s",
                bundle.getString(JPushInterface.EXTRA_TITLE),
                bundle.getString(JPushInterface.EXTRA_MESSAGE)));
        Log.d(this.getClass().getName(), String.format("content: %s",
                bundle.getString(JPushInterface.EXTRA_EXTRA)));
        Toast.makeText(context, String.format("message: %s", bundle.getString(JPushInterface.EXTRA_MESSAGE)),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * receive notification
     *
     * @param bundle Bundle
     */
    private void receiveNotification(Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String message = bundle.getString("message");
        Log.d(this.getClass().getName(), String.format("title: %s, alert: %s, extras: %s, message: %s",
                title, alert, extras, message));
    }

    /**
     * open notification
     *
     * @param bundle Bundle
     */
    private void openNotification(final Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (extras != null && !extras.isEmpty()) {
            try {
                JSONObject extrasJson = new JSONObject(extras);
                final int orderId = extrasJson.getInt("id");
                // get order detail info
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("id", String.valueOf(orderId));
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_GET_ORDER_DETAIL,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(this.getClass().getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.getResult(jsonObject)) {
                                        Order order = ShipperService.getDetailOrder(jsonObject);
                                        Intent intent;
                                        switch (order.getState()) {
                                            case 0:
                                                intent = new Intent(context, WaitingOrderActivity.class);
                                                break;
                                            case 1:
                                                intent = new Intent(context, RunningOrderActivity.class);
                                                break;
                                            case 2:
                                                intent = new Intent(context, HistoryOrderActivity.class);
                                                break;
                                            case 3:
                                                intent = new Intent(context, HistoryOrderActivity.class);
                                                break;
                                            case 4:
                                                intent = new Intent(context, RunningOrderActivity.class);
                                                break;
                                            default:
                                                Log.e(this.getClass().getName(), "Unknown order state");
                                                return;
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("id", String.valueOf(orderId));
                                        context.startActivity(intent);
                                    } else {
                                        Log.e(this.getClass().getName(), "Do not get shipper order!");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(this.getClass().getName(), volleyError.getMessage(), volleyError);
                                Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                requestQueue.start();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(this.getClass().getName(), "Extras has no order id!");
            }
        } else {
            Log.d(this.getClass().getName(), "Extras error!");
        }
        System.out.println(extras);
    }

}
