package com.example.itifighter.TestSeriesX;

public class CustomListItemX {
    private String topicHeader,test,imagex,Uid,month1,month2,month3,price1,price2,price3,discount1,discount2,discount3;
    public CustomListItemX(String topicHeader, String test,String imagex, String Uid,String month1,String month2, String month3,String price1, String price2, String price3, String discount1, String discount2, String discount3){
        this.topicHeader = topicHeader;
        this.test = test;
        this.imagex = imagex;
        this.Uid = Uid;
        this.month1 = month1;
        this.month2 = month2;
        this.month3 = month3;
        this.price1 = price1;
        this.price2 = price2;
        this.price3 = price3;
        this.discount1 = discount1;
        this.discount2= discount2;
        this.discount3 = discount3;
    }

    public String getTopicHeader() {return topicHeader; }
    public String getTest() {return test; }
    public String getImagex(){return  imagex;}
    public String getId(){return Uid;}
    public String getPrice1() {return price1;}
    public String getPrice2() {return price2;}
    public String getPrice3() {return price3;}
    public String getMonth1() {return month1;}
    public String getMonth2() {return month2;}
    public String getMonth3() {return month3; }
    public String getDiscount1(){return  discount1;}
    public String getDiscount2(){return  discount2;}
    public String getDiscount3(){return  discount3;}

}
