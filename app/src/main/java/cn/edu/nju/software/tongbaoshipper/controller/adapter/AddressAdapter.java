package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import cn.edu.nju.software.tongbaoshipper.controller.activity.ChangeAddressActivity;
import cn.edu.nju.software.tongbaoshipper.model.Address;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.ShipperService;
import cn.edu.nju.software.tongbaoshipper.controller.activity.PlaceOrderActivity;

public class AddressAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private Dialog dialog;
    private ArrayList<Address> arrAddress;
    private RequestQueue requestQueue;

    public AddressAdapter(Context context, ArrayList<Address> arrAddress, ListView lvAddress) {
        super();
        this.context = context;
        this.arrAddress = arrAddress;
        lvAddress.setOnItemLongClickListener(this);
        lvAddress.setOnItemClickListener(this);
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public int getCount() {
        return arrAddress.size();
    }

    @Override
    public Object getItem(int position) {
        return arrAddress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrAddress.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        TextView tvLocation = (TextView) convertView.findViewById(R.id.item_address_tv_location);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.item_address_tv_message);
        LinearLayout btnStart = (LinearLayout) convertView.findViewById(R.id.item_address_btn_start);
        LinearLayout btnFinal = (LinearLayout) convertView.findViewById(R.id.item_address_btn_final);
        final Address address = arrAddress.get(position);

        // TODO add message
        tvLocation.setText(address.getAddressName());
        tvMessage.setText(String.format("%s %s", address.getContactName(), address.getPhoneNum()));
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
                Intent startIntent= new Intent(context, PlaceOrderActivity.class);
                startIntent.putExtra("source",1);
                startIntent.putExtra("address",address.getAddressName());
                startIntent.putExtra("contact",address.getContactName());
                startIntent.putExtra("phone",address.getPhoneNum());
                startIntent.putExtra("lat",address.getLat());
                startIntent.putExtra("lng",address.getLng());
                context.startActivity(startIntent);




            }
        });
        btnFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "final", Toast.LENGTH_SHORT).show();
                Intent finalIntent= new Intent(context, PlaceOrderActivity.class);
                finalIntent.putExtra("source",2);
                finalIntent.putExtra("address",address.getAddressName());
                finalIntent.putExtra("contact",address.getContactName());
                finalIntent.putExtra("phone",address.getPhoneNum());
                finalIntent.putExtra("lat",address.getLat());
                finalIntent.putExtra("lng",address.getLng());
                context.startActivity(finalIntent);


            }
        });
        return convertView;
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        View vDialog = LayoutInflater.from(context).inflate(R.layout.dialog_item_address, parent, false);
        dialog = new AlertDialog.Builder(context)
                .setView(vDialog)
                .create();
        Button btnChange = (Button) vDialog.findViewById(R.id.dialog_item_address_btn_change);
        Button btnDelete = (Button) vDialog.findViewById(R.id.dialog_item_address_btn_delete);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AddressAdapter.class.getName(), "address change");
                dialog.dismiss();
                Address address = arrAddress.get(position);
                changeAddress(address);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AddressAdapter.class.getName(), "address delete");
                dialog.dismiss();

                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("id", String.valueOf(arrAddress.get(position).getId()));
                Request<JSONObject> request = new PostRequest(Net.URL_SHIPPER_DELETE_FREQUENT_ADDRESS,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(AddressAdapter.class.getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.deleteFrequentAddress(jsonObject)) {
                                        Toast.makeText(context, context.getResources().getString(R.string.item_address_delete_success),
                                                Toast.LENGTH_SHORT).show();
                                        // 修改相应adapter、arr
                                        arrAddress.remove(position);
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
                                Log.e(AddressAdapter.class.getName(), volleyError.getMessage(), volleyError);
//                                 http authentication 401
//                                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
//                                                    startActivity(intent);
//                                                    return;
//                                                }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = arrAddress.get(position);
        changeAddress(address);
    }

    private void changeAddress(Address address) {
        Intent intent = new Intent(context, ChangeAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Address.class.getName(), address);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
