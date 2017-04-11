package cn.edu.nju.software.tongbaoshipper.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import cn.edu.nju.software.tongbaoshipper.R;
import cn.edu.nju.software.tongbaoshipper.common.Account;
import cn.edu.nju.software.tongbaoshipper.common.Banner;
import cn.edu.nju.software.tongbaoshipper.common.Driver;
import cn.edu.nju.software.tongbaoshipper.common.Message;
import cn.edu.nju.software.tongbaoshipper.common.MonthlyAccount;
import cn.edu.nju.software.tongbaoshipper.common.Order;
import cn.edu.nju.software.tongbaoshipper.common.User;
import cn.edu.nju.software.tongbaoshipper.constant.Net;
import cn.edu.nju.software.tongbaoshipper.constant.Prefs;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class UserService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    /**
     * 获取错误信息
     *
     * @param jsonObject response
     * @return String
     * @throws JSONException
     */
    public static String getErrorMsg(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("errorMsg");
    }

    /**
     * 获取请求处理结果
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean getResult(JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt("result") == Net.NET_OPERATION_SUCCESS;
    }

    /**
     * show user info
     * for test
     */
    public static void showUserInfo() {
        // just for test
        System.out.println("id:" + User.getInstance().getId());
        System.out.println("nickName:" + User.getInstance().getNiceName());
        System.out.println("phoneNum:" + User.getInstance().getPhoneNum());
        System.out.println("password:" + User.getInstance().getPassword());
        System.out.println("iconUrl:" + User.getInstance().getIconUrl());
        System.out.println("money:" + User.getInstance().getMoney());
        System.out.println("point:" + User.getInstance().getPoint());
        System.out.println("type:" + User.getInstance().getType());
        System.out.println("token:" + User.getInstance().getToken());
        System.out.println("registerTime:" + User.getInstance().getRegisterTime());
    }

    /**
     * 用户登陆
     *
     * @param jsonObject response
     * @param phoneNum   phoneNum
     * @param password   password
     * @return boolean
     * @throws JSONException
     */
    public static boolean login(final Context context, JSONObject jsonObject, String phoneNum, String password, int type) throws JSONException {
        if (getResult(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            User user = new User();
            user.setId(data.getInt("id"));
            user.setNiceName(data.getString("nickName"));
            user.setIconUrl(data.getString("iconUrl"));
            user.setPoint(data.getInt("point"));
            user.setMoney(data.getDouble("money"));
            user.setToken(data.getString("token"));
            user.setPhoneNum(phoneNum);
            user.setPassword(password);
            user.setType(type);
            try {
                user.setRegisterTime(sdf.parse(data.getString("registerTime")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            User.login(user);

            // set jpush alias
            JPushInterface.setAlias(context, String.valueOf(User.getInstance().getId()), new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (i == 0) {
                        Log.d(context.getClass().getName(), String.format("login alias: %s", s));
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 判断手机号是否已注册
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean hasRegister(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 手机号注册
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean register(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 上传图片
     *
     * @param jsonObject response
     * @return iconUrl
     * @throws JSONException
     */
    public static String uploadPicture(JSONObject jsonObject) throws JSONException {
        String iconUrl = null;
        if (getResult(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            iconUrl = data.getString("url");
        }
        return iconUrl;
    }

    /**
     * 修改用户昵称
     *
     * @param jsonObject  response
     * @param newNickName nickName
     * @return boolean
     * @throws JSONException
     */
    public static boolean modifyNickName(JSONObject jsonObject, String newNickName) throws JSONException {
        if (getResult(jsonObject)) {
            User.getInstance().setNiceName(newNickName);
            return true;
        }
        return false;
    }

    /**
     * 修改密码
     *
     * @param jsonObject  response
     * @param newPassword password
     * @return boolean
     * @throws JSONException
     */
    public static boolean modifyPassword(JSONObject jsonObject, String newPassword) throws JSONException {
        if (getResult(jsonObject)) {
            User.getInstance().setPassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * 修改个人头像
     *
     * @param jsonObject response
     * @param iconUrl    icon
     * @return boolean
     * @throws JSONException
     */
    public static boolean modifyIcon(JSONObject jsonObject, String iconUrl) throws JSONException {
        if (getResult(jsonObject)) {
            User.getInstance().setIconUrl(iconUrl);
            return true;
        }
        return false;
    }

    public static boolean getContacts(JSONObject jsonObject) {
        return false;
    }

    public static boolean getContactDetail(JSONObject jsonObject) {
        return false;
    }


    public static Driver getDriverDetail(JSONObject jsonObject) throws JSONException {
        Driver driver = new Driver();

        if (getResult(jsonObject)) {
            JSONObject object = jsonObject.getJSONObject("data");
            //TODO complete
            driver.setId(object.getInt("id"));
            driver.setNickName(object.getString("nickName"));
            driver.setPhoneNum(object.getString("phoneNum"));
        }
        return driver;
    }


    /**
     * 获取我的消息列表
     *
     * @param jsonObject response
     * @return ArrayList<Message></>
     * @throws JSONException
     */
    public static ArrayList<Message> getMyMessages(JSONObject jsonObject) throws JSONException {
        ArrayList<Message> arrMessage = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                Message message = new Message();
                JSONObject object = data.getJSONObject(i);
                message.setId(object.getInt("id"));
                message.setType(object.getInt("type"));
                message.setContent(object.getString("content"));
                try {
                    message.setTime(sdf.parse(object.getString("time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(object.getInt("hasRead"));
                if (object.getInt("hasRead") != 0) {
                    message.setHasRead(true);
                } else {
                    message.setHasRead(false);
                }
                message.setOrderID(object.getInt("objectId"));
                arrMessage.add(message);
            }
            arrMessage.trimToSize();
        }
        return arrMessage;
    }

    /**
     * 读消息
     *
     * @param jsonObject response
     * @return bolean
     * @throws JSONException
     */
    public static boolean readMessage(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 删除消息
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean deleteMessage(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 发表反馈
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean addFeedback(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 获取首页轮播图片
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static ArrayList<Banner> getBannerInfo(JSONObject jsonObject) throws JSONException {
        ArrayList<Banner> arrBanner = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                Banner banner = new Banner();
                banner.setImgUrl(object.getString("imgUrl"));
                banner.setTargetUrl(object.getString("targetUrl"));
                arrBanner.add(banner);
            }
        }
        return arrBanner;
    }

    /**
     * 判断某个token是否有效
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean tokenValid(JSONObject jsonObject) throws JSONException {
        return getResult(jsonObject);
    }

    /**
     * 查看账单
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static ArrayList<Account> showAccount(JSONObject jsonObject) throws JSONException {
        ArrayList<Account> arrAccount = new ArrayList<>();
        if (getResult(jsonObject)) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = data.length() - 1; i >= 0; i--) {
                Account account = new Account();
                JSONObject object = data.getJSONObject(i);
                account.setId(object.getInt("id"));
                account.setType(object.getInt("type"));
                account.setMoney(object.getDouble("money"));
                try {
                    account.setBuildTime(sdf.parse(object.getString("time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                account.setOrderId(object.getJSONObject("order").getInt("id"));
                arrAccount.add(account);
            }
        }
        return arrAccount;
    }

    /**
     * 按月度查看账单
     *
     * @param jsonObject response
     * @return MonthlyAccount
     * @throws JSONException
     */
    public static MonthlyAccount getAccountByMonth(JSONObject jsonObject, int year, int month) throws JSONException {
        MonthlyAccount monthlyAccount = new MonthlyAccount();
        if (getResult(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            monthlyAccount.setYear(year);
            monthlyAccount.setMonth(month);
            monthlyAccount.setTotalIn(data.getDouble("totalIn"));
            monthlyAccount.setTotalOut(data.getDouble("totalOut"));
            JSONArray accountList = data.getJSONArray("accountList");
            ArrayList<Account> arrAccount = new ArrayList<>();
            for (int i = 0; i < accountList.length(); i++) {
                JSONObject object = accountList.getJSONObject(i);
                Account account = new Account();
                account.setId(object.getInt("id"));
                account.setType(object.getInt("type"));
                account.setMoney(object.getDouble("money"));
                try {
                    account.setBuildTime(sdf.parse(object.getString("time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                account.setOrderId(object.getInt("orderId"));
                arrAccount.add(account);
            }
            monthlyAccount.setAccountList(arrAccount);
            return monthlyAccount;
        }
        return monthlyAccount;
    }

    public static boolean getAllTruckTypes(JSONObject jsonObject) {
        return false;
    }

    /**
     * 充值
     *
     * @param jsonObject response
     * @param amount     money
     * @return boolean
     * @throws JSONException
     */
    public static boolean recharge(JSONObject jsonObject, double amount) throws JSONException {
        if (getResult(jsonObject)) {
            double money = User.getInstance().getMoney();
            User.getInstance().setMoney(money + amount);
            return true;
        }
        return false;
    }

    /**
     * 提现
     *
     * @param jsonObject response
     * @param amount     money
     * @return boolean
     * @throws JSONException
     */
    public static boolean withdraw(JSONObject jsonObject, double amount) throws JSONException {
        if (getResult(jsonObject)) {
            double money = User.getInstance().getMoney();
            if ((money - amount) > 0) {
                User.getInstance().setMoney(money - amount);
            } else {
                User.getInstance().setMoney(0);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取用户当前余额
     *
     * @param jsonObject response
     * @return boolean
     * @throws JSONException
     */
    public static boolean getMoney(JSONObject jsonObject) throws JSONException {
        if (getResult(jsonObject)) {
            JSONObject data = jsonObject.getJSONObject("data");
            User.getInstance().setMoney(data.getDouble("money"));
            return true;
        }
        return false;
    }

    /**
     * save object in SharedPreferences
     *
     * @param context Context
     * @param key     String
     * @param object  Object
     */
    public static void saveObject(Context context, String key, Object object) {
        Log.d(context.getClass().getName(), "save object: " + key);
        SharedPreferences.Editor editor = context.getSharedPreferences(Prefs.PREF_NAME, Context.MODE_PRIVATE).edit();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(bos.toByteArray(), Base64.DEFAULT));
            editor.putString(key, objectVal);
            editor.apply();
            Log.d(context.getClass().getName(), objectVal);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * get SharedPreferences object
     *
     * @param context Context
     * @param key     String
     * @return object
     */
    @SuppressWarnings("unchecked")
    public static Object getObject(Context context, String key) {
        Log.d(context.getClass().getName(), "get object: " + key);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Prefs.PREF_NAME, Context.MODE_PRIVATE);
        Object object = null;
        if (sharedPreferences.contains(key)) {
            String objectVal = sharedPreferences.getString(key, null);
            Log.d(context.getClass().getName(), objectVal);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
            ObjectInputStream input = null;
            try {
                input = new ObjectInputStream(bis);
                object = input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    /**
     * call when token invalid
     *
     * @param context Context
     * @param isShow  boolean-whether show toast message
     */
    public static void tokenInvalid(Context context, boolean isShow) {
        Log.d(context.getClass().getName(), "token invalid");
        User.logout(context);
        // init shared preferences user date
        SharedPreferences.Editor editor = context.getSharedPreferences(Prefs.PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Prefs.PREF_KEY_USER, "");
        editor.apply();
        // show login message invalid
        if (isShow) {
            Toast.makeText(context, context.getResources().getString(R.string.login_invalid),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
