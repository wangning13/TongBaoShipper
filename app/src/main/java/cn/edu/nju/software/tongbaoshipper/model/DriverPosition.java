package cn.edu.nju.software.tongbaoshipper.model;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by zhang on 2016/4/29.
 */
public class DriverPosition {
    private Driver driver;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    private LatLng position;

}
