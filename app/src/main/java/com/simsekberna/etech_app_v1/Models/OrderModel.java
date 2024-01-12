package com.simsekberna.etech_app_v1.Models;

public class OrderModel {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderModel(String date, String time, String totalAmount) {
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
    }
    public OrderModel() {
    }

    String date;
    String time;
    String totalAmount;
}
