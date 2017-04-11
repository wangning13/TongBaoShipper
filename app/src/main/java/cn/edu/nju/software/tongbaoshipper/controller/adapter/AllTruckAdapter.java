package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.nju.software.tongbaoshipper.common.Truck;
import cn.edu.nju.software.tongbaoshipper.R;

public class AllTruckAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Truck> arrTruck;
    private Dialog dialog;
    private ListView lvTruck;


    public AllTruckAdapter(Context context, ArrayList<Truck> arrTruck, ListView lvTruck) {
        super();
        this.context = context;
        this.arrTruck = arrTruck;
        this.lvTruck = lvTruck;
    }

    @Override
    public int getCount() {
        return arrTruck.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return arrTruck.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_all_truck, parent, false);
        Truck truck = arrTruck.get(position);

        TextView tv_truckType = (TextView) convertView.findViewById(R.id.tv_truck_type);
        TextView tv_truckStartingprice = (TextView) convertView.findViewById(R.id.tv_truck_startingprice);
        TextView tv_truckPrice = (TextView) convertView.findViewById(R.id.tv_truck_price);
        TextView tv_truckLwh = (TextView) convertView.findViewById(R.id.tv_truck_lwh);
        TextView tv_truckWeight = (TextView) convertView.findViewById(R.id.tv_truck_weight);

        tv_truckType.setText(truck.getTruckType());
        tv_truckStartingprice.setText(truck.getStartingpriceString());
        tv_truckPrice.setText(truck.getPriceString());
        tv_truckLwh.setText(truck.getLwh());
        tv_truckWeight.setText(truck.getWeight());


        return convertView;
    }

}
