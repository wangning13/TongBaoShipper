package cn.edu.nju.software.tongbaoshipper.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.service.UserService;

public class FragmentWithdrawBank extends Fragment implements View.OnClickListener{

    private Context context;
    private EditText etBankCard, etAmount;
    private LinearLayout btnConfirm;
    private RequestQueue requestQueue;

    public FragmentWithdrawBank() {
        super();
    }

    public FragmentWithdrawBank(Context context) {
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.fragment_withdraw_bank, null);

        // init view
        initView(view);
        return view;
    }

    private void initView(View view) {
        etBankCard = (EditText) view.findViewById(R.id.withdraw_bank_et_account);
        etAmount = (EditText) view.findViewById(R.id.withdraw_bank_et_amount);
        btnConfirm = (LinearLayout) view.findViewById(R.id.withdraw_bank_btn_confirm);

        // 设置提现金额hint
        etAmount.setHint(String.format("%s%.2f%s",
                context.getResources().getString(R.string.withdraw_amount_hint),
                User.getInstance().getMoney(),
                context.getResources().getString(R.string.money_unit)));

        btnConfirm.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.withdraw_bank_btn_confirm:
                Log.d(FragmentWithdrawBank.class.getName(), "withdraw bank");
                if (TextUtils.isEmpty(etBankCard.getText().toString())) {
                    Toast.makeText(context, context.getResources().getString(R.string.input_bank_card),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if(TextUtils.isEmpty(etAmount.getText().toString())) {
                    Toast.makeText(context, context.getResources().getString(R.string.input_amount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO withdraw bank card useless
                Map<String, String> params = new HashMap<>();
                params.put("token", User.getInstance().getToken());
                params.put("money", etAmount.getText().toString());
                Request<JSONObject> request = new PostRequest(Net.URL_USER_WITHDRAW,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    Log.d(FragmentWithdrawBank.class.getName(), jsonObject.toString());
                                    if (UserService.withdraw(jsonObject, Double.valueOf(etAmount.getText().toString()))) {
                                        Toast.makeText(context, context.getResources().getString(R.string.withdraw_success),
                                                Toast.LENGTH_SHORT).show();
                                        // 设置提现金额hint
                                        etAmount.setHint(String.format("%s%.2f%s",
                                                context.getResources().getString(R.string.withdraw_amount_hint),
                                                User.getInstance().getMoney(),
                                                context.getResources().getString(R.string.money_unit)));
                                        etAmount.setText("");
                                        etBankCard.setText("");
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
                                Log.e(FragmentWithdrawBank.class.getName(), volleyError.getMessage(), volleyError);
                                // http authentication 401
//                                if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                                    Intent intent = new Intent(context, LoginActivity.class);
//                                    startActivity(intent);
//                                    return;
//                                }
                                Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);
                break;
            default:
                Log.e(FragmentWithdrawBank.class.getName(), "Unknown id");
                break;
        }
    }
}
