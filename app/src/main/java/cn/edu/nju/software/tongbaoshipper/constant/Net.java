package cn.edu.nju.software.tongbaoshipper.constant;

public class Net {

    /**
     * 网络处理失败
     */
    public static final int NET_OPERATION_FAILURE = 0;
    /**
     * 网络处理成功
     */
    public static final int NET_OPERATION_SUCCESS = 1;
    /**
     * 未经授权http error 401
     */
    public static final int NET_ERROR_AUTHENTICATION = 401;
    /**
     * url前缀
     */
    public static final String URL_PREFIX = "http://182.92.239.143:8080/tongbao";
    /**
     * 注册
     */
    public static final String URL_USER_REGISTER = URL_PREFIX + "/user/register";
    /**
     * 登陆
     */
    public static final String URL_USER_LOGIN = URL_PREFIX + "/user/login";
    /**
     * 判断手机号有没有被注册过
     */
    public static final String URL_USER_HAS_REGISTER = URL_PREFIX + "/user/hasRegister";
    /**
     * 上传图片
     */
    public static final String URL_USER_UPLOAD_PICTURE = URL_PREFIX + "/user/uploadPicture";
    /**
     * 修改个人昵称
     */
    public static final String URL_USER_MODIFY_NICK_NAME = URL_PREFIX + "/user/auth/modifyNickName";
    /**
     * 修改密码
     */
    public static final String URL_USER_MODIFY_PASSWORD = URL_PREFIX + "/user/auth/modifyPassword";
    /**
     * 修改个人头像
     */
    public static final String URL_USER_MODIFY_ICON = URL_PREFIX + "/user/auth/modifyIcon";
    /**
     * 获取联系人列表
     */
    public static final String URL_USER_GET_CONTACTS = URL_PREFIX + "/user/auth/getContacts";
    /**
     * 获取联系人
     */
    public static final String URL_USER_GET_CONTACT_DETAIL = URL_PREFIX + "/user/auth/getContactDetail";
    /**
     * 获取我的消息列表
     */
    public static final String URL_USER_GET_MY_MESSAGES = URL_PREFIX + "/user/auth/getMyMessages";
    /**
     * 读消息
     */
    public static final String URL_USER_READ_MESSAGE = URL_PREFIX + "/user/auth/readMessage";
    /**
     * 删除消息
     */
    public static final String URL_USER_DELETE_MESSAGE = URL_PREFIX + "/user/auth/deleteMessage";
    /**
     * 发表反馈
     */
    public static final String URL_USER_ADD_FEEDBACK = URL_PREFIX + "/user/addFeedback";
    /**
     * 获取首页轮播图片
     */
    public static final String URL_USER_GET_BANNER_INFO = URL_PREFIX + "/user/getBannerInfo";
    /**
     * 判断某个token是否有效
     */
    public static final String URL_USER_TOKEN_VALID = URL_PREFIX + "/user/tokenValid";
    /**
     * 查看账单
     */
    public static final String URL_USER_SHOW_ACCOUNT = URL_PREFIX + "/user/auth/showAccount";
    /**
     * 按月度查看账单
     */
    public static final String URL_USER_GET_ACCOUNT_BY_MONTH = URL_PREFIX + "/user/auth/getAccountByMonth";
    /**
     * 获取所有车辆类型
     */
    public static final String URL_USER_GET_ALL_TRUCK_TYPES = URL_PREFIX + "/user/getAllTruckTypes";
    /**
     * 充值
     */
    public static final String URL_USER_RECHARGE = URL_PREFIX + "/user/auth/recharge";
    /**
     * 提现
     */
    public static final String URL_USER_WITHDRAW = URL_PREFIX + "/user/auth/withdraw";
    /**
     * 获取当前用户余额
     */
    public static final String URL_USER_GET_MONEY = URL_PREFIX + "/user/auth/getMoney";
    /**
     * 获取常用司机列表
     */
    public static final String URL_SHIPPER_GET_FREQUENT_DRIVERS = URL_PREFIX + "/shipper/auth/getFrequentDrivers";
    /**
     * 获取常用地址列表
     */
    public static final String URL_SHIPPER_GET_FREQUENT_ADDRESSES = URL_PREFIX + "/shipper/auth/getFrequentAddresses";
    /**
     * 添加常用司机
     */
    public static final String URL_SHIPPER_ADD_FREQUENT_DRIVER = URL_PREFIX + "/shipper/auth/addFrequentDriver";
    /**
     * 删除常用司机
     */
    public static final String URL_SHIPPER_DELETE_FREQUENT_DRIVER = URL_PREFIX + "/shipper/auth/deleteFrequentDriver";
    /**
     * 根据手机号搜索司机（可以是模糊搜索）
     */
    public static final String URL_SHIPPER_SEARCH_DRIVER = URL_PREFIX + "/shipper/auth/searchDriver";
    /**
     * 添加常用地址
     */
    public static final String URL_SHIPPER_ADD_FREQUENT_ADDRESS = URL_PREFIX + "/shipper/auth/addFrequentAddress";
    /**
     * 修改常用地址
     */
    public static final String URL_SHIPPER_EDIT_FREQUENT_ADDRESS = URL_PREFIX + "/shipper/auth/editFrequentAddress";
    /**
     * 删除常用地址
     */
    public static final String URL_SHIPPER_DELETE_FREQUENT_ADDRESS = URL_PREFIX + "/shipper/auth/deleteFrequentAddress";
    /**
     * 获取所有司机目前最新的位置
     */
    public static final String URL_SHIPPER_GET_DRIVERS_POSITION = URL_PREFIX + "/shipper/auth/getDriversPosition";
    /**
     * 下单
     */
    public static final String URL_SHIPPER_PLACE_ORDER = URL_PREFIX + "/shipper/auth/placeOrder";
    /**
     * 当下单失败时需要提示用户是否自动拆单，用户点击是调用的方法 需要再次传入订单信息
     */
    public static final String URL_SHIPPER_SPLIT_ORDER = URL_PREFIX + "/shipper/auth/splitOrder";
    /**
     * 评论订单
     */
    public static final String URL_SHIPPER_EVALUATE_ORDER = URL_PREFIX + "/shipper/auth/evaluateOrder";
    /**
     * 查看订单列表
     */
    public static final String URL_SHIPPER_SHOW_MY_ORDER_LIST = URL_PREFIX + "/shipper/auth/showMyOrderList";
    /**
     * 显示订单详情，只能看到自己的订单
     */
    public static final String URL_SHIPPER_GET_ORDER_DETAIL = URL_PREFIX + "/shipper/auth/getOrderDetail";
    /**
     * 删除订单，注意这里并不是真正删除，只是设置对其不可见
     */

    public static final String URL_SHIPPER_DELETE_ORDER = URL_PREFIX + "/shipper/auth/deleteOrder";
    /**
     * 取消订单
     */
    public static final String URL_SHIPPER_CANCEL_ORDER = URL_PREFIX + "/shipper/auth/cancelOrder";
    /**
     * 结束订单
     */
    public static final String URL_SHIPPER_FINISH_ORDER = URL_PREFIX + "/shipper/auth/finishOrder";


}
