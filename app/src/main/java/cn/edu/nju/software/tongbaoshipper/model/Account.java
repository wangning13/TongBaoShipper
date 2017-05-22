package cn.edu.nju.software.tongbaoshipper.model;

import java.util.Date;

public class Account {

    /**
     * 账单id
     */
    private int id;
    /**
     * 账单类型
     */
    private int type;
    /**
     * 账单所涉及的金额
     */
    private double money;
    /**
     * 账单生成时间
     */
    private Date buildTime;
    /**
     * 账单对应的订单（可选）
     */
    private int orderId = 0;
    /**
     * 充值
     */
    public static final int TYPE_RECHARGE = 0;
    /**
     * 提现
     */
    public static final int TYPE_WITHDRAW = 1;
    /**
     * 支付
     */
    public static final int TYPE_PAY = 2;
    /**
     * 退款
     */
    public static final int TYPE_REFUND = 3;
    /**
     * 到账
     */
    public static final int TYPE_ACCOUNT = 4;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
