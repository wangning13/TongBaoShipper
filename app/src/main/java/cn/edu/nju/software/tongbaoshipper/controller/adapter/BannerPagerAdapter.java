package cn.edu.nju.software.tongbaoshipper.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.software.tongbaoshipper.model.Banner;

public class BannerPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Banner> arrBanner = new ArrayList<>();
    private List<ImageView> ivList = new ArrayList<>();

    public BannerPagerAdapter(Context context, ArrayList<Banner> arrBanner, List<ImageView> ivList) {
        super();
        this.context = context;
        this.arrBanner = arrBanner;
        this.ivList = ivList;
    }

    @Override
    public int getCount() {
        return ivList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Log.d(this.getClass().getName(), "instantiate item position: " + position);
        ImageView view = ivList.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(context.getClass().getName(), "banner click: " + position);
                Uri uri = Uri.parse(arrBanner.get(position).getTargetUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(this.getClass().getName(), "destroy item position: " + position);
        container.removeView(ivList.get(position));
    }
}
