package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.edu.nju.software.tongbaoshipper.common.Account;
import cn.edu.nju.software.tongbaoshipper.R;

public class AccountAdapter extends BaseAdapter {

    private ArrayList<Account> arrAccount;
    private Context context;

    public AccountAdapter(Context context, ArrayList<Account> arrAccount) {
        this.context = context;
        this.arrAccount = arrAccount;
    }

    @Override
    public int getCount() {
        return arrAccount.size();
    }

    @Override
    public Object getItem(int position) {
        return arrAccount.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrAccount.get(position).getId();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
        Account account = arrAccount.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        TextView tvType = (TextView) convertView.findViewById(R.id.item_account_type);
        TextView tvTime = (TextView) convertView.findViewById(R.id.item_account_time);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.item_account_money);

        tvType.setText(getTypeName(account.getType()));
        tvTime.setText(sdf.format(account.getBuildTime()));
        setMoneyTextStyle(tvMoney, account.getType(), account.getMoney());
        return convertView;
    }

    /**
     * 获取类型名称
     * @param type  account type
     * @return String
     */
    private String getTypeName(int type) {
        String[] types = context.getResources().getStringArray(R.array.account_type);
        if (type <= Account.TYPE_ACCOUNT) {
            return types[type];
        } else {
            Log.e(AccountAdapter.class.getName(), "Unknown type id");
            return null;
        }
    }

    /**
     * 设置etMoney样式
     * @param tv        text view
     * @param type      account type
     * @param money     amount
     */
    private void setMoneyTextStyle(TextView tv, int type, double money) {
        switch (type) {
            case Account.TYPE_RECHARGE:
            case Account.TYPE_REFUND:
            case Account.TYPE_ACCOUNT:
                tv.setText(String.format("+%.2f", money));
                tv.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case Account.TYPE_WITHDRAW:
            case Account.TYPE_PAY:
                tv.setText(String.format("-%.2f", money));
                tv.setTextColor(ContextCompat.getColor(context, R.color.orange));
                break;
            default:
                Log.e(AccountAdapter.class.getName(), "Unknown type id");
        }
    }
}
