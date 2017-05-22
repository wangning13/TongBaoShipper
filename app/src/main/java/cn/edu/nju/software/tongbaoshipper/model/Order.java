package cn.edu.nju.software.tongbaoshipper.model;

import org.json.JSONArray;

public class Order {
    private int id;
    private int state;
    private String addressFrom;
    private double addressFromLat,addressFromLng;
    private String addressTo;
    private double addressToLat,addressToLng;
    private String fromContactName,fromContactPhone;
    private String toContactName,toContactPhone;
    private String loadTime;
    private String placeTime;
    private String goodsType;
    private int goodsWeight,goodsSize;
    private JSONArray truckTypes;
    private String remark;
    private int payType;
    private int price;

    public String getDriverPhoneNum() {
        return driverPhoneNum;
    }

    public void setDriverPhoneNum(String driverPhoneNum) {
        this.driverPhoneNum = driverPhoneNum;
    }

    public int getEvaluatePoint() {
        return evaluatePoint;
    }

    public void setEvaluatePoint(int evaluatePoint) {
        this.evaluatePoint = evaluatePoint;
    }

    public String getEvaluateContent() {
        return evaluateContent;
    }

    public void setEvaluateContent(String evaluateContent) {
        this.evaluateContent = evaluateContent;
    }

    private String driverPhoneNum;
    private int evaluatePoint;
    private String evaluateContent;



    public Order()
    {

    }
    public Order(String placeTime,String loadTime,String addressFrom,String fromContactName,String fromContactPhone,String addressTo,String toContactName,String toContactPhone){
        this.placeTime=placeTime;
        this.addressFrom=addressFrom;
        this.addressTo=addressTo;
        this.loadTime=loadTime;
        this.fromContactName=fromContactName;
        this.fromContactPhone=fromContactPhone;
        this.toContactName=toContactName;
        this.toContactPhone=toContactPhone;
    }

    public void setLatLng(double addressFromLat,double addressFromLng,double addressToLat,double addressToLng){
        setAddressFromLat(addressFromLat);
        setAddressFromLng(addressFromLng);
        setAddressToLat(addressToLat);
        setAddressToLng(addressToLng);
    }



    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public double getAddressFromLat() {
        return addressFromLat;
    }

    public void setAddressFromLat(double addressFromLat) {
        this.addressFromLat = addressFromLat;
    }

    public double getAddressFromLng() {
        return addressFromLng;
    }

    public void setAddressFromLng(double addressFromLng) {
        this.addressFromLng = addressFromLng;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public double getAddressToLat() {
        return addressToLat;
    }

    public void setAddressToLat(double addressToLat) {
        this.addressToLat = addressToLat;
    }

    public double getAddressToLng() {
        return addressToLng;
    }

    public void setAddressToLng(double addressToLng) {
        this.addressToLng = addressToLng;
    }

    public String getFromContactName() {
        return fromContactName;
    }

    public void setFromContactName(String fromContactName) {
        this.fromContactName = fromContactName;
    }

    public String getFromContactPhone() {
        return fromContactPhone;
    }

    public void setFromContactPhone(String fromContactPhone) {
        this.fromContactPhone = fromContactPhone;
    }

    public String getToContactName() {
        return toContactName;
    }

    public void setToContactName(String toContactName) {
        this.toContactName = toContactName;
    }

    public String getToContactPhone() {
        return toContactPhone;
    }

    public void setToContactPhone(String toContactPhone) {
        this.toContactPhone = toContactPhone;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public int getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(int goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public int getGoodsSize() {
        return goodsSize;
    }

    public void setGoodsSize(int goodsSize) {
        this.goodsSize = goodsSize;
    }

    public JSONArray getTruckTypes() {
        return truckTypes;
    }

    public void setTruckTypes(JSONArray truckTypes) {
        this.truckTypes = truckTypes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public String getPlaceTime() {
        return placeTime;
    }

    public void setPlaceTime(String placeTime) {
        this.placeTime = placeTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
