package com.bong.smartcart;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by 배봉현 on 2017-05-13.
 */
public class ListItem implements Serializable{

    private Drawable iconDrawable ;
    private String idStr ;
    private String nameStr ;
    private String priceStr ;
    private String countStr ;
    private String placeStr ;
    private String categoryStr ;

    public ListItem() {
        super();
    }
    /*public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }*/
    public ListItem(String idStr, String nameStr, String priceStr, String countStr, String placeStr, String categoryStr) {
        super();
        this.idStr = idStr;
        this.nameStr = nameStr;
        this.priceStr = priceStr;
        this.countStr = countStr;
        this.placeStr = placeStr;
        this.categoryStr = categoryStr;
    }
    public void setId(String id) {
        idStr = id ;
    }
    public void setName(String name) {
        nameStr = name ;
    }
    public void setPrice(String price) {
        priceStr = price ;
    }
    public void setCount(String count) {
        countStr = count ;
    }
    public void setPlace(String place) {
        placeStr = place ;
    }
    public void setCategory(String category) {
        categoryStr = category ;
    }

    /*public Drawable getIcon() {
        return this.iconDrawable ;
    }*/
    public String getId() {
        return this.idStr ;
    }
    public String getName() {
        return this.nameStr ;
    }
    public String getPrice() {
        return this.priceStr ;
    }
    public String getCount() {
        return this.countStr ;
    }
    public String getPlace() {
        return this.placeStr ;
    }
    public String getCategory() {
        return this.categoryStr ;
    }

}
