package cn.edu.nju.software.tongbaoshipper.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.Address;
import cn.edu.nju.software.tongbaoshipper.common.Driver;
import cn.edu.nju.software.tongbaoshipper.common.DriverPosition;
import cn.edu.nju.software.tongbaoshipper.common.Order;
import cn.edu.nju.software.tongbaoshipper.common.PostRequest;
import cn.edu.nju.software.tongbaoshipper.common.Truck;
import cn.edu.nju.software.tongbaoshipper.constant.Net;

public class ShipperService {


    private static ArrayList<Truck> truckType=null;
    private static ArrayList<Order> runningOrder=new ArrayList<Order>();
    private static ArrayList<Order> waitingOrder=new ArrayList<Order>();
    private static ArrayList<Order> historyOrder=new ArrayList<Order>();
    private static ArrayList<Order> allOrder=new ArrayList<Order>();


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    /**
     * 获取错误信息
     * @param jsonObject    response
     * @return String
     * @throws JSONException
     */
    public static String getErrorMsg(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("errorMsg");
    }

    /**
     * 获取请求处理结果
     * @param jsonObject    response
     * @return boolean
     * @throws JSONException
     */
    public static boolean getResult(JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt("result") == Net.NET_OPERATION_SUCCESS;
    }

    /**
     * 获取常用司机列表
     * @param jsonObject    response
     * @return  ArrayList<Driver></>
     * @throws JSONException
     */
    public static ArrayList<Driver> getFrequentDrivers(JSONObject jsonObject) throws JSONException {
        ArrayList<Driver> arrDriver = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                Driver driver = new Driver();
                JSONObject object = data.getJSONObject(i);
                driver.setId(object.getInt("id"));
                driver.setNickName(object.getString("nickName"));
                driver.setPhoneNum(object.getString("phoneNum"));
                driver.setIconUrl(object.getString("iconUrl"));
                try {
                    driver.setRegisterTime(sdf.parse(object.getString("registerTime")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                arrDriver.add(driver);
            }
        }
        return arrDriver;
    }

    /**
     * 获取常用地址列表
     * @param jsonObject    response
     * @return ArrayList<Address></>
     * @throws JSONException
     */
    public static ArrayList<Address> getFrequentAddresses(JSONObject jsonObject) throws JSONException {
        ArrayList<Address> arrAddress = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                Address address = new Address();
                JSONObject object = data.getJSONObject(i);
                address.setId(object.getInt("id"));
                address.setAddressName(object.getString("name"));
                address.setContactName(object.getString("contactName"));
                address.setPhoneNum(object.getString("contactPhone"));
                address.setLat(object.getDouble("lat"));
                address.setLng(object.getDouble("lng"));
                arrAddress.add(address);
            }
        }
        return arrAddress;
    }

    /**
     * 添加常用司机
     * @param jsonObject    response
     * @return  boolean
     */
    public static boolean addFrequentDriver(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 删除常用司机
     * @param jsonObject    response
     * @return  boolean
     * @throws JSONException
     */
    public static boolean deleteFrequentDriver(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 根据手机号搜索司机（可以是模糊搜索）
     * @param jsonObject    response
     * @return  boolean
     * @throws JSONException
     */
    public static ArrayList<Driver> searchDriver(JSONObject jsonObject) throws JSONException {
        ArrayList<Driver> arrDriver = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                Driver driver = new Driver();
                driver.setId(object.getInt("id"));
                driver.setNickName(object.getString("nickName"));
                driver.setPhoneNum(object.getString("phoneNum"));
                driver.setIconUrl(object.getString("iconUrl"));
                try {
                    driver.setRegisterTime(sdf.parse(object.getString("registerTime")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                arrDriver.add(driver);
            }
        }
        return arrDriver;
    }

    /**
     * 添加常用地址
     * @param jsonObject    response
     * @return boolean
     * @throws JSONException
     */
    public static boolean addFrequentAddress(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 修改常用地址
     * @param jsonObject    response
     * @return  boolean
     * @throws JSONException
     */
    public static boolean editFrequentAddress(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 删除常用地址
     * @param jsonObject    response
     * @return  boolean
     * @throws JSONException
     */
    public static boolean deleteFrequentAddress(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    public static int placeOrder(JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt("result");
    }
    public static boolean splitOrder(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }


    public static ArrayList<Truck> getAllTruckType(final Context context)
    {
        if (truckType==null)
        {
            RequestQueue requestQueue= Volley.newRequestQueue(context);

            Map<String, String> params = new HashMap<>();
                Request<JSONObject> request = new PostRequest(Net.URL_USER_GET_ALL_TRUCK_TYPES,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.d(context.getClass().getName(), jsonObject.toString());
                                try {
                                    if (ShipperService.getResult(jsonObject)) {

                                        System.out.println(context.getClass().getName() + "Init");


                                        ShipperService.setAllTruckType(jsonObject);
                                    } else {
                                        Toast.makeText(context, ShipperService.getErrorMsg(jsonObject),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e(context.getClass().getName(), volleyError.getMessage(), volleyError);
                                // http authentication 401
//                        if (volleyError.networkResponse.statusCode == Net.NET_ERROR_AUTHENTICATION) {
//                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            return;
//                        }
                                Toast.makeText(context, context.getResources().getString(R.string.network_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, params);
                requestQueue.add(request);

        }
        return truckType;
    }
    public static ArrayList<Truck> setAllTruckType(JSONObject jsonObject) throws JSONException {
        truckType = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                Truck truck = new Truck();
                //TODO complete
                JSONObject object = data.getJSONObject(i);
                truck.setId(i);
                truck.setTruckType(object.getString("name"));
                truck.setStartingprice(object.getDouble("basePrice"));
                truck.setPrice(object.getDouble("overPrice"));
                truck.setWeight(object.getDouble("capacity"));
                truck.setLength(object.getDouble("length"));
                truck.setWidth(object.getDouble("width"));
                truck.setHeight(object.getDouble("height"));
                truck.setBaseDistance(object.getInt("baseDistance"));

                truckType.add(truck);
            }

        }
        return truckType;
    }

    public static ArrayList<Truck> getTestTruckList()
    {
        String[] ls={"大货车","小客车","面包车","金杯车","依维柯"};
        ArrayList<Truck> truckArrayList=new ArrayList<Truck>();
        int i=0;
        for (String s:ls)
        {
            i++;
            Truck truck=new Truck(i,s,3.5,2.5,1.5,5.8,6.5,3.57);
            truckArrayList.add(truck);

        }
        return truckArrayList;
    }


    public static ArrayList<Order> getOrderList(JSONObject jsonObject) throws JSONException {

        ArrayList<Order> orderArrayList = new ArrayList<Order>();

        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                Order order = new Order();
                //TODO complete
                JSONObject object = data.getJSONObject(i);
                order.setId(object.getInt("id"));
                order.setPlaceTime(object.getString("time"));
                order.setAddressFrom(object.getString("addressFrom"));
                order.setAddressTo(object.getString("addressTo"));
                order.setTruckTypes(object.getJSONArray("truckTypes"));
                order.setPrice(object.getInt("money"));
                order.setFromContactName(object.getString("fromContactName"));
                order.setFromContactPhone(object.getString("fromContactPhone"));
                order.setToContactName(object.getString("toContactName"));
                order.setToContactPhone(object.getString("toContactPhone"));
                order.setLoadTime(object.getString("loadTime"));
                order.setState(object.getInt("state"));

                orderArrayList.add(order);
            }
        }
        return orderArrayList;
    }


    public static Order getDetailOrder(JSONObject jsonObject) throws JSONException {

        Order order=new Order();

        if (getResult(jsonObject)) {
            JSONObject object = jsonObject.getJSONObject("data");
            //TODO complete
            order.setId(object.getInt("id"));
            order.setPlaceTime(object.getString("time"));
            order.setAddressFrom(object.getString("addressFrom"));
            order.setAddressTo(object.getString("addressTo"));
            order.setTruckTypes((object.getJSONArray("truckTypes")));
            order.setPrice(object.getInt("money"));
            order.setFromContactName(object.getString("fromContactName"));
            order.setFromContactPhone(object.getString("fromContactPhone"));
            order.setToContactName(object.getString("toContactName"));
            order.setToContactPhone(object.getString("toContactPhone"));
            order.setLoadTime(object.getString("loadTime"));
            order.setState(object.getInt("state"));
            order.setDriverPhoneNum(object.getString("driverPhoneNum"));

            if (object.getString("evaluatePoint").equals("null"))
            order.setEvaluatePoint(-1);
            System.out.println(order.getEvaluatePoint()+"分");
            order.setEvaluateContent(object.getString("evaluateContent"));

        }
        return order;
    }


    public static ArrayList<DriverPosition> getDriverPositionList(JSONObject jsonObject) throws JSONException {

        ArrayList<DriverPosition> driverPositions = new ArrayList<DriverPosition>();

        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                DriverPosition position = new DriverPosition();
                Driver driver=new Driver();
                //TODO complete
                JSONObject object = data.getJSONObject(i);
                driver.setId(object.getInt("driverId"));
                driver.setNickName("测试名");
                driver.setPhoneNum("123-1232-2323");
                position.setDriver(driver);
                position.setPosition(new LatLng(object.getDouble("lat"),object.getDouble("lng")));
                driverPositions.add(position);
            }
        }
        return driverPositions;

    }

/**/
    public static Order getTestOrder()
    {
        Order order=new Order("2016年4月25日 00:10:57","2016年4月25日 00:11:00","南京大学鼓楼校区","张三","124-3434-3234","南京大学仙林校区","李四","123-3434-3434");
        order.setId(1261763711);
        order.setPrice(150);
        JSONArray truckArray=new JSONArray();
        truckArray.put(1);
        truckArray.put(2);
        order.setTruckTypes(truckArray);
        order.setState(1);
        return order;
    }

}
