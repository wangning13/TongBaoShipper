package cn.edu.nju.software.tongbaoshipper.common;

import java.io.Serializable;
import java.util.ArrayList;

public class MonthlyAccount implements Serializable {

    /**
     * 月度账单--年份
     */
    private int year;
    /**
     * 月度账单--月份
     */
    private int month;
    /**
     * 月度账单
     */
    private ArrayList<Account> accountList;
    /**
     * 月度总收入
     */
    private double totalIn;
    /**
     * 月度总支出
     */
    private double totalOut;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public ArrayList<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(ArrayList<Account> accountList) {
        this.accountList = accountList;
    }

    public double getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(double totalIn) {
        this.totalIn = totalIn;
    }

    public double getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(double totalOut) {
        this.totalOut = totalOut;
    }
}
