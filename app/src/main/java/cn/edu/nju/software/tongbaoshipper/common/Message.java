package cn.edu.nju.software.tongbaoshipper.common;

import java.util.Date;

public class Message {
    /**
     * 消息id
     */
    private int id;
    /**
     * 消息类型（0：订单被抢到-货主
     *          1：订单完成-司机
     *          2：正在进行的订单被取消-司机
     *          3：其他消息）
     */
    private int type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否已读
     */
    private boolean hasRead;
    /**
     * 消息产生时间
     */
    private Date time;
    /**
     * 订单为0、1、2产生
     */
    private int orderID;

    /**
     * 订单被抢到-货主
     */
    public static final int TYPE_ORDER_GRAB = 0;
    /**
     * 订单完成-司机
     */
    public static final int TYPE_ORDER_FINISH = 1;
    /**
     * 正在进行的订单被取消-司机
     */
    public static final int TYPE_ORDER_CANCEL = 2;
    /**
     * 其他消息
     */
    public static final int TYPE_OTHER_MESSAGE = 3;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
}
