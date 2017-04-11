package cn.edu.nju.software.tongbaoshipper.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import cn.edu.nju.software.tongbaoshipper.R;

public class MapActivity extends AppCompatActivity {

    private MapView mvMap;

    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_map);

        mvMap = (MapView) findViewById(R.id.map_mv);
        mvMap.showZoomControls(false);
        mvMap.showScaleControl(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mvMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvMap.onDestroy();
    }
}
