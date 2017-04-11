package cn.edu.nju.software.tongbaoshipper.common;

import java.util.Date;

public class Driver {

    /**
     * 司机id
     */
    private int id;
    /**
     * 司机昵称
     */
    private String nickName;
    /**
     * 司机手机号
     */
    private String phoneNum;
    /**
     * 司机icon url
     */
    private String iconUrl;
    /**
     * 司机注册时间
     */
    private Date registerTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
