package cn.edu.nju.software.tongbaoshipper.controller.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.Banner;
import cn.edu.nju.software.tongbaoshipper.controller.activity.AddressActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.DriverActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.FrameActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.HelpActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.MessageActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.PlaceOrderActivity;
import cn.edu.nju.software.tongbaoshipper.controller.activity.WalletActivity;
import cn.edu.nju.software.tongbaoshipper.controller.adapter.BannerPagerAdapter;

import static cn.edu.nju.software.tongbaoshipper.R.id.home_btn_help;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private Context context;
    private List<ImageView> ivList;
    private ArrayList<Banner> arrBanner;
    private BannerPagerAdapter bannerPagerAdapter;
    private RequestQueue requestQueue;

    /**
     * banner image index
     */
    private int currentItem = 0;
//    private UIHandler handler = new UIHandler(FragmentHome.this);

    public FragmentHome() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentHome(Context context) {
        super();
        this.context = context;
        arrBanner = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.fragment_home, null);

        // init view
        initView(view);

        requestQueue = Volley.newRequestQueue(context);

        // init banner resource
//        initBannerResource();
        // set banner adapter
        bannerPagerAdapter = new BannerPagerAdapter(context, arrBanner, ivList);
        // init banner action
//        initBannerAction();
        // scroll banner image
//        startAdvertise();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requestQueue.stop();
    }

    /**
     * init view
     *
     * @param view View
     */
    private void initView(View view) {
        Log.i(this.getClass().getName(), "init view");
        TextView btnMessage = (TextView) view.findViewById(R.id.home_btn_message);
        TextView btnAddress = (TextView) view.findViewById(R.id.home_btn_address);
        TextView btnDriver = (TextView) view.findViewById(R.id.home_btn_driver);
        TextView btnWallet = (TextView) view.findViewById(R.id.home_btn_wallet);
        TextView btnOrder = (TextView) view.findViewById(R.id.home_btn_order);
        TextView btnHelp = (TextView) view.findViewById(R.id.home_btn_help);
        RelativeLayout btnOrderTruck = (RelativeLayout) view.findViewById(R.id.home_btn_order_truck);

        // 添加事件监听器
        btnMessage.setOnClickListener(this);
        btnAddress.setOnClickListener(this);
        btnDriver.setOnClickListener(this);
        btnWallet.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnOrderTruck.setOnClickListener(this);
    }

    /**
     * init banner resource
     */
//    private void initBannerResource() {
//        ivList = new ArrayList<>();
//        arrBanner = new ArrayList<>();
//        // get banner data
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                Net.URL_USER_GET_BANNER_INFO,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        Log.d(FragmentHome.class.getName(), jsonObject.toString());
//                        try {
//                            if (UserService.getResult(jsonObject)) {
//                                ArrayList<Banner> arr = UserService.getBannerInfo(jsonObject);
//                                // init banner
//                                for (int i = 0; i < arr.size(); i++) {
//                                    arrBanner.add(arr.get(i));
//                                    bannerPagerAdapter.notifyDataSetChanged();
//                                }
//
//                                // init banner dot
//                                for (int j = 0; j < arrBanner.size(); j++) {
//                                    View vDot = new View(context);
//                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
//                                    params.leftMargin = 4;
//                                    vDot.setLayoutParams(params);
//                                    if (j == 0) {
//                                        vDot.setEnabled(true);
//                                    } else {
//                                        vDot.setEnabled(false);
//                                    }
//                                    vDot.setBackgroundResource(R.drawable.dot_background);
//                                }
//                                // init banner image
//                                for (int i = 0; i < arrBanner.size(); i++) {
//                                    ImageRequest imageRequest = new ImageRequest(arrBanner.get(i).getImgUrl(),
//                                            new Response.Listener<Bitmap>() {
//                                                @Override
//                                                public void onResponse(Bitmap bitmap) {
//                                                    Log.d(FragmentHome.class.getName(), "get banner icon");
//                                                    ImageView iv = new ImageView(context);
//                                                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                                    iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                                            LinearLayout.LayoutParams.MATCH_PARENT));
//                                                    iv.setImageBitmap(bitmap);
//                                                    ivList.add(iv);
//                                                    bannerPagerAdapter.notifyDataSetChanged();
//                                                }
//                                            }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
//                                            new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError volleyError) {
//                                                    Log.e(FragmentHome.class.getName(), volleyError.getMessage(), volleyError);
//                                                    Toast.makeText(context, context.getResources().getString(R.string.network_error),
//                                                            Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                    );
//                                    requestQueue.add(imageRequest);
//                                    requestQueue.start();
//                                    bannerPagerAdapter.notifyDataSetChanged();
//                                }
//                            } else {
//                                Toast.makeText(context, UserService.getErrorMsg(jsonObject),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e(context.getClass().getName(), volleyError.getMessage(), volleyError);
//                    }
//                }
//        );
//        requestQueue.add(request);
//        requestQueue.start();
//    }

    /**
     * init banner action
     */
//    private void initBannerAction() {
//        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            private int index = 0;
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                layoutDot.getChildAt(index).setEnabled(false);
//                layoutDot.getChildAt(position).setEnabled(true);
//                index = position;
//                currentItem = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }

//    private void startAdvertise() {
//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        // 4 seconds scroll banner
//        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 4,
//                TimeUnit.SECONDS);
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.home_btn_message:
                Log.d(this.getClass().getName(), "message");
                Intent intentMessage = new Intent(context, MessageActivity.class);
                startActivity(intentMessage);
                break;
            case R.id.home_btn_address:
                Log.d(this.getClass().getName(), "address");
                Intent intentAddress = new Intent(context, AddressActivity.class);
                startActivity(intentAddress);
                break;
            case R.id.home_btn_driver:
                Log.d(this.getClass().getName(), "driver");
                Intent intentDriver = new Intent(context, DriverActivity.class);
                startActivity(intentDriver);
                break;
            case R.id.home_btn_wallet:
                Log.d(this.getClass().getName(), "wallet");
                Intent intentUser = new Intent(context, WalletActivity.class);
                startActivity(intentUser);
                break;
            case R.id.home_btn_order:
                Log.d(this.getClass().getName(), "order");
                ((FrameActivity) getActivity()).getOrder();
                break;
            case home_btn_help:
                Log.d(this.getClass().getName(), "help");
                Intent intentHelp = new Intent(context,HelpActivity.class);
                startActivity(intentHelp);
                break;
            case R.id.home_btn_order_truck:
                Log.d(this.getClass().getName(), "order truck");
                Intent intentPlaceOrder = new Intent(context, PlaceOrderActivity.class);
                intentPlaceOrder.putExtra("source", 0);
                startActivity(intentPlaceOrder);
                break;
            default
                    :
                Log.d(this.getClass().getName(), "unknown button id");
                break;
        }
    }

    /**
     * scroll task
     */
//    private class ScrollTask implements Runnable {
//
//        @Override
//        public void run() {
//            synchronized (vpBanner) {
//                currentItem = (currentItem + 1) % ivList.size();
//                handler.obtainMessage().sendToTarget();
//            }
//        }
//    }

    /**
     * ui update handler
     */
//    private static class UIHandler extends Handler {
//        private WeakReference<FragmentHome> fragment;
//
//        UIHandler(FragmentHome fragment) {
//            this.fragment = new WeakReference<>(fragment);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            FragmentHome fragmentHome = fragment.get();
//            fragmentHome.vpBanner.setCurrentItem(fragmentHome.currentItem);
//        }
//    }

}
