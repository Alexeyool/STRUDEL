package com.strudel.alexeyool.strudel;

import android.graphics.Bitmap;

public  class Cover{
    private static final String URL = "http://shtrudel.pro-depot.co.il/strudel_jpg/";
    private static final String[] mannthArray = {
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
    };

    String data;
    String fileName;
    String url;
    Bitmap image;
    int month;
    int year;

    public Cover(int _month, int _year) {
        month = _month;
        year = _year;
        addData(month, year);
        addFileName(month, year);
        addUrl(month, year);
    }

    public void addData(int manth, int year ){
        data = (mannthArray[manth-1]) + " " + year;
    }

    public void addFileName(int manth, int year){
        fileName = manth + "_" + year + ".jpg";
    }

    public void addUrl(int manth, int year){
        url = URL + manth + "_" + year + ".jpg";
    }

    public void addImage(Bitmap bitmap){
        image = bitmap;
    }

    public String getData(){

        return data;
    }

    public Bitmap getImage(){
        return image;
    }

    public String getUrl(){
        return url;
    }

    public String getFileName(){
        return fileName;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

}
