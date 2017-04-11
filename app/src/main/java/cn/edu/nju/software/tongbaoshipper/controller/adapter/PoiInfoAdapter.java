package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.controller.activity.AddAddressActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.ChangeAddressActivity;

public class PoiInfoAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

    private Context context;
    private List<PoiInfo> poiInfos;
    private Enum type;
    public enum OperationType {
        add,
        change
    }

    public PoiInfoAdapter(Context context, List<PoiInfo> poiInfos, ListView lvPoiInfo, Enum type) {
        super();
        this.context = context;
        this.poiInfos = poiInfos;
        this.type = type;
        lvPoiInfo.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return poiInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return poiInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_poi_info, parent, false);
        PoiInfo poiInfo = poiInfos.get(position);
        TextView tvPoiInfo = (TextView) convertView.findViewById(R.id.item_poi_info_tv);

        tvPoiInfo.setText(String.format("%s(%s)", poiInfo.name, poiInfo.address));
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(AddAddressActivity.class.getName() + PoiInfoAdapter.class.getName(), "item click:" + position);
        PoiInfo poiInfo = poiInfos.get(position);
        String addressName = String.format("%s(%s)", poiInfo.name, poiInfo.address);
        LatLng location = poiInfo.location;
        if (this.type == OperationType.add) {
            ((AddAddressActivity) context).setLocation(addressName, location);
        } else if (this.type == OperationType.change) {
            ((ChangeAddressActivity) context).setLocation(addressName, location);
        }
    }
}
