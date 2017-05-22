package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.model.Message;
import cn.edu.nju.software.tongbaoshipper.model.PostRequest;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class MessageAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener {

    private Context context;
    private ArrayList<Message> arrMessage;
    private Dialog dialog;
    private RequestQueue requestQueue;

    public MessageAdapter(Context context, ArrayList<Message> arrMessage, ListView lvMessage) {
        super();
        this.context = context;
        this.arrMessage = arrMessage;
        lvMessage.setOnItemLongClickListener(this);
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public int getCount() {
        return arrMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return arrMessage.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        ImageView ivType = (ImageView) convertView.findViewById(R.id.item_message_iv);
        ImageView ivDot = (ImageView) convertView.findViewById(R.id.item_message_iv_dot);
        TextView tvType = (TextView) convertView.findViewById(R.id.item_message_tv_type);
        TextView tvTime = (TextView) convertView.findViewById(R.id.item_message_tv_time);
        TextView tvContent = (TextView) convertView.findViewById(R.id.item_message_tv_content);
        Message message = arrMessage.get(position);
        String[] types = context.getResources().getStringArray(R.array.message_type);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm", Locale.CHINA);

        // TODO add message type image drawable
        switch (message.getType()) {
            case Message.TYPE_ORDER_GRAB:
                tvType.setText(types[message.getType()]);
                ivType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home_message));
                break;
            case Message.TYPE_ORDER_FINISH:
                tvType.setText(types[message.getType()]);
                ivType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home_message));
                break;
            case Message.TYPE_ORDER_CANCEL:
                tvType.setText(types[message.getType()]);
                ivType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home_message));
                break;
            case Message.TYPE_OTHER_MESSAGE:
                tvType.setText(types[message.getType()]);
                ivType.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home_message));
                break;
            default:
                Log.e(MessageAdapter.class.getName(), "Unknown type");
        }
        if (message.isHasRead()) {
            ivDot.setVisibility(View.INVISIBLE);
        }
        tvTime.setText(sdf.format(message.getTime()));
        tvContent.setText(message.getContent());

        return convertView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        View vDialog = LayoutInflater.from(context).inflate(R.layout.dialog_item_message, parent, false);
        dialog = new AlertDialog.Builder(context)
                .setView(vDialog)
                .create();
        Button btnDelete = (Button) vDialog.findViewById(R.id.dialog_item_message_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MessageAdapter.class.getName(), "message delete");
                dialog.dismiss();

                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("id", String.valueOf(arrMessage.get(position).getId()));
                Request<JSONObject> request = new PostRequest(Net.URL_USER_DELETE_MESSAGE,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(MessageAdapter.class.getName(), jsonObject.toString());
                                try {
                                    if (UserService.deleteMessage(jsonObject)) {
                                        Toast.makeText(context, context.getResources().getString(R.string.item_message_delete_success),
                                                Toast.LENGTH_SHORT).show();
                                        arrMessage.remove(position);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, UserService.getErrorMsg(jsonObject),
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
                                Log.e(MessageAdapter.class.getName(), volleyError.getMessage(), volleyError);
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
}
