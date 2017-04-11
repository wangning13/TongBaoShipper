package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.common.Driver;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;

public class DriverAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener {

    private Enum operationType;
    private Context context;
    private ArrayList<Driver> arrDriver;
    private Dialog dialog;
    private RequestQueue requestQueue;
    public enum OperationType {
        add,
        dial
    }

    public DriverAdapter(Context context, ArrayList<Driver> arrDriver, ListView lvDriver, Enum operationType) {
        super();
        this.operationType = operationType;
        this.context = context;
        this.arrDriver = arrDriver;
        // user frequent driver can delete
        if (this.operationType == OperationType.dial) {
            lvDriver.setOnItemLongClickListener(this);
        }
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public int getCount() {
        return arrDriver.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return arrDriver.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_driver, parent, false);
        Driver driver = arrDriver.get(position);
        ImageView ivAvatar = (ImageView) convertView.findViewById(R.id.item_driver_iv_avatar);
        ImageView ivOperation = (ImageView) convertView.findViewById(R.id.item_driver_iv_operation);
        TextView tvName = (TextView) convertView.findViewById(R.id.item_driver_tv_name);
        TextView tvTime = (TextView) convertView.findViewById(R.id.item_driver_tv_time);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.item_driver_tv_phone);

        setDriverIcon(ivAvatar, driver);
        setOperationImage(ivOperation, driver);
        tvName.setText(driver.getNickName());
        tvPhone.setText(driver.getPhoneNum());
        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(driver.getRegisterTime()));

        return convertView;
    }

    /**
     * set driver icon
     *
     * @param iv     ImageView
     * @param driver Driver
     */
    private void setDriverIcon(final ImageView iv, Driver driver) {
        if (driver.getIconUrl() == null) {
            return;
        }
        ImageRequest request = new ImageRequest(driver.getIconUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d(DriverAdapter.class.getName(), "get driver icon");
                        iv.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(DriverAdapter.class.getName(), volleyError.getMessage(), volleyError);
                        Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(request);
    }

    /**
     * set operationImage and add onClickListener
     *
     * @param iv     ImageView
     * @param driver Driver
     */
    private void setOperationImage(ImageView iv, final Driver driver) {
        if (operationType.equals(OperationType.add)) {
            iv.setImageResource(R.drawable.driver_add);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(DriverAdapter.class.getName(), "add driver");
                    Map<String, String> params = new HashMap<>();
                    params.put("token", User.getInstance().getToken());
                    params.put("id", String.valueOf(driver.getId()));
                    Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_ADD_FREQUENT_DRIVER,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    Log.d(DriverAdapter.class.getName(), jsonObject.toString());
                                    try {
                                        if (ShipperService.addFrequentDriver(jsonObject)) {
                                            Toast.makeText(context, context.getResources().getString(R.string.item_driver_add_success),
                                                    Toast.LENGTH_SHORT).show();
                                            ((Activity) context).finish();
                                        } else {
                                            Toast.makeText(context, ShipperService.getErrorMsg(jsonObject),
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
                                    Log.e(DriverAdapter.class.getName(), volleyError.getMessage(), volleyError);
                                    //http authentication 401
//                                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                            startActivity(intent);
//                                            return;
//                                        }
                                    Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, params);
                    requestQueue.add(request);
                }
            });

        } else if (operationType.equals(OperationType.dial)) {
            iv.setImageResource(R.drawable.dial);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(DriverAdapter.class.getName(), "dial driver");
                    Uri telUri = Uri.parse("tel:" + driver.getPhoneNum());
                    Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                    context.startActivity(intent);
                }
            });

        } else {
            Log.e(DriverAdapter.class.getName(), "Unknown operation type");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        View vDialog = LayoutInflater.from(context).inflate(R.layout.dialog_item_driver, parent, false);
        dialog = new AlertDialog.Builder(context)
                .setView(vDialog)
                .create();
        Button btnDelete = (Button) vDialog.findViewById(R.id.dialog_item_driver_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DriverAdapter.class.getName(), "driver delete");
                dialog.dismiss();

                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("id", String.valueOf(arrDriver.get(position).getId()));
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_DELETE_FREQUENT_DRIVER,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(DriverAdapter.class.getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.deleteFrequentDriver(jsonObject)) {
                                        Toast.makeText(context, context.getResources().getString(R.string.item_driver_delete_success),
                                                Toast.LENGTH_SHORT).show();
                                        // 修改相应adapter arr
                                        arrDriver.remove(position);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, ShipperService.getErrorMsg(jsonObject),
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
                                Log.e(DriverAdapter.class.getName(), volleyError.getMessage(), volleyError);
                                //http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
            }
        });
        dialog.show();
        return true;
    }
}
