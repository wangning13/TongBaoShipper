package cn.edu.nju.software.tongbaoshipper.model;

/**
 * Created by zhang on 2016/4/19.
 */
public class Truck {

    private int id;
    private String truckType;
    private double length;
    private double width;
    private double height;
    private double weight;
    private double price;

    public double getStartingprice() {
        return startingprice;
    }

    public double getPrice() {
        return price;
    }

    private double startingprice;
    private int baseDistance;

    public Truck()
    {}

    public Truck(int id, String trucktype, double length, double width, double height, double weight, double price, double startingprice)
    {
        this.id=id;
        this.truckType=trucktype;
        this.length=length;
        this.width=width;
        this.height=height;
        this.weight=weight;
        this.price=price;
        this.startingprice=startingprice;
    }



    public String getLwh()
    {
        return (String.format("%.1f", length)+"m*"+String.format("%.1f", width)+"m*"+String.format("%.1f", height)+"m");
    }
    public String getPriceString() {
        return (String.format("%.1f", price)+"元/公里");
    }



    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartingpriceString() {
        return (String.format("%.1f", startingprice)+"元起/"+baseDistance+"公里");
    }

    public void setStartingprice(double startingprice) {
        this.startingprice = startingprice;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getWeight() {
        return (String.format("%.1f", weight)+" 吨");
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaseDistance() {
        return baseDistance;
    }

    public void setBaseDistance(int baseDistance) {
        this.baseDistance = baseDistance;
    }
}
