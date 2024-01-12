package com.simsekberna.etech_app_v1.Models;

import java.io.Serializable;

public class Product implements Serializable {
    String Name;
    String Image;
    String Description;
    String Delivery;
    String Price;
    String Discount;
    String Brand;
    String Oldprice;
    String Category;
    public String getCategory() { return Category;  }
    public String getOldprice() { return Oldprice; }
    public String getName() {
        return Name;
    }
    public String getBrand() {
        return Brand;
    }

    public String getDiscount() {
        return Discount;
    }

    public String getImage() {
        return Image;
    }

    public String getDescription() {
        return Description;
    }

    public String getDelivery() {
        return Delivery;
    }

    public String getPrice() {
        return Price;
    }
}
