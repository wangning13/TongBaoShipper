package cn.edu.nju.software.tongbaoshipper.controller.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.sevenheaven.segmentcontrol.SegmentControl;
import com.sevenheaven.segmentcontrol.SegmentControl.OnSegmentControlClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.software.tongbaoshipper.model.Order;
import cn.edu.nju.software.tongbaoshipper.model.User;
import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.controller.activity.LoginActivity;

/**
 * Created by MoranHe on 2016/1/13.
 */
public class FragmentOrder extends Fragment {

    private Context context;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private SegmentControl selectbutton;
    private View view;
    private RequestQueue requestQueue;
    private ArrayList<Order> allList,waitingList,runningList,historyList;

    public FragmentOrder() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentOrder(Context context) {
        super();
        this.context = context;
    }

    @Override public void onResume(){
        super.onResume();
        if (User.isLogin()) {
            WaitingOrderTab tab01 = new WaitingOrderTab();
            RunningOrderTab tab02 = new RunningOrderTab();
            HistoryOrderTab tab03 = new HistoryOrderTab();
            mFragments.add(tab01);
            mFragments.add(tab02);
            mFragments.add(tab03);


            mAdapter = new FragmentPagerAdapter(getChildFragmentManager())
            {
                @Override
                public int getCount()
                {
                    return mFragments.size();
                }

                @Override
                public Fragment getItem(int arg0)
                {
                    return mFragments.get(arg0);
                }
            };

            mViewPager.setAdapter(mAdapter);
            mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {

                    selectbutton.setSelectedIndex(position);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            selectbutton.setOnSegmentControlClickListener(new OnSegmentControlClickListener() {
                @Override
                public void onSegmentControlClick(int index) {
                    mViewPager.setCurrentItem(index);
                }
            });
            selectbutton.setSelectedIndex(mViewPager.getCurrentItem());

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }




    private void initView(final View view)
    {
        Log.i(this.getClass().getName(), "init view");
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        selectbutton =(SegmentControl) view.findViewById(R.id.segment_control);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(context, R.layout.fragment_order, null);
        initView(view);
        return view;
    }


}
