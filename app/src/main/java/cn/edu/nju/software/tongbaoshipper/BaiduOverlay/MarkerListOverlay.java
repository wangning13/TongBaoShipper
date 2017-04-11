package cn.edu.nju.software.tongbaoshipper.BaiduOverlay;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示poi的overly
 */
public class MarkerListOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;

    private ArrayList<LatLng> positionList;

    /**
     * 构造函数
     *
     * @param baiduMap
     *            该 PoiOverlay 引用的 BaiduMap 对象
     */
    public MarkerListOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }


    public void setData(ArrayList<LatLng> list) {
        this.positionList = list;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {

        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < positionList.size(); i++) {

            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
                            + markerSize + ".png")).extraInfo(bundle)
                    .position(positionList.get(i)));

        }
        return markerList;
    }

    /**
     * 获取该 PoiOverlay 的 poi数据
     *
     * @return
     */
    public ArrayList<LatLng> getPositionList() {
        return positionList;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param i
     *            被点击的poi在
     *            {@link com.baidu.mapapi.search.poi.PoiResult#getAllPoi()} 中的索引
     * @return
     */
    public boolean onListMarkerClick(int i) {
//        if (mPoiResult.getAllPoi() != null
//                && mPoiResult.getAllPoi().get(i) != null) {
//            Toast.makeText(BMapManager.getInstance().getContext(),
//                    mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_LONG)
//                    .show();
//        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onListMarkerClick(marker.getExtraInfo().getInt("index"));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}
