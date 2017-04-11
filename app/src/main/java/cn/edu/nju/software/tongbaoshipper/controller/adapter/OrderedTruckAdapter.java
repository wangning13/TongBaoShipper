package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.nju.software.tongbaoshipper.common.Truck;
import cn.edu.nju.software.tongbaoshipper.R;

public class OrderedTruckAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener {

    private Context context;
    private ArrayList<Truck> arrTruck;
    private Dialog dialog;
    private ListView lvTruck;

    public OrderedTruckAdapter(Context context, ArrayList<Truck> arrTruck, ListView lvTruck) {
        super();
        this.context = context;
        this.arrTruck = arrTruck;
        this.lvTruck =lvTruck;
        lvTruck.setOnItemLongClickListener(this);
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_ordered_truck, parent, false);
        Truck truck = arrTruck.get(position);

        TextView tv_truckType = (TextView) convertView.findViewById(R.id.tv_ordered_truck_type);
        TextView tv_truckStartingprice = (TextView) convertView.findViewById(R.id.tv_ordered_truck_startingprice);
        TextView tv_truckPrice = (TextView) convertView.findViewById(R.id.tv_ordered_truck_price);
        TextView tv_truckLwh = (TextView) convertView.findViewById(R.id.tv_ordered_truck_lwh);
        TextView tv_truckWeight = (TextView) convertView.findViewById(R.id.tv_ordered_truck_weight);

        tv_truckType.setText(truck.getTruckType());
        tv_truckStartingprice.setText(truck.getStartingpriceString());
        tv_truckPrice.setText(truck.getPriceString());
        tv_truckLwh.setText(truck.getLwh());
        tv_truckWeight.setText(truck.getWeight());


        return convertView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
        View vDialog = LayoutInflater.from(context).inflate(R.layout.dialog_item_truck, parent, false);
        dialog = new AlertDialog.Builder(context)
                .setView(vDialog)
                .create();
        Button btnDelete = (Button) vDialog.findViewById(R.id.dialog_item_truck_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(OrderedTruckAdapter.class.getName(), "truck delete");
                dialog.dismiss();
                arrTruck.remove(position);
                lvTruck.setAdapter(lvTruck.getAdapter());


            }
        });
        dialog.show();
        return true;
    }
}
