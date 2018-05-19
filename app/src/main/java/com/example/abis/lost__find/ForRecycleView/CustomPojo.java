package com.example.abis.lost__find.ForRecycleView;

import android.net.Uri;

import java.util.ArrayList;

public class CustomPojo {
    //POJO class consists of get method and set method
    private String name;
    private String time,content;

    private String imgpath, message, weekday;

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    private ArrayList<CustomPojo> customPojo =new ArrayList<>();

    public CustomPojo() {

    }
    //getting content value
    public String getContent(){return content;}
    //setting content value
    public void setContent(String content){this.content=content;}

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
