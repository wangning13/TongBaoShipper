package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import cn.edu.nju.software.tongbaoshipper.R;

public class AutoAddressAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<PoiInfo> arraddress;
    private AutoCompleteTextView actvaddress;
    private Filter myFilter;

    public AutoAddressAdapter(Context context, List<PoiInfo> arraddress, AutoCompleteTextView actvaddress) {
        super();
        this.context = context;
        this.arraddress = arraddress;
        this.actvaddress =actvaddress;
    }

    @Override
    public int getCount() {
        return arraddress.size();
    }

    @Override
    public Object getItem(int position) {
        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_auto_complete_address, parent, false);

        TextView addname=(TextView) convertView.findViewById(R.id.item_tv_address_name);
        TextView adddetail=(TextView) convertView.findViewById(R.id.item_tv_address_detail);
        PoiInfo addinfo=arraddress.get(position);
        String name=addinfo.name;
        String detail=addinfo.address;
        addname.setText(name);
        adddetail.setText(detail);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results=new FilterResults();
                   // ArrayList<String> value=new ArrayList<>(arraddress);
                   // System.out.println(value.size()+"KKK");
                    results.values=arraddress;
                    results.count=arraddress.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
        return myFilter;
    }

}
