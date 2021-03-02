package com.example.itifighter;

public class CustomListItem {
    private boolean paymentStatus;
    private String topicHeader;
    private String months, month2, month3;
    private String description;
    private double price;
    private double price2;
    private double price3;
    private double discount;
    private double discount2;
    private double discount3;
    private int quesCount;
    private int duration;
    private int MPQ, NOQ;
    private int type;

    private int chapterIndex;
    private String imagex;
    private String coupanCode, coupanActive, coupanDiscount;

    public CustomListItem() {
    }

    public CustomListItem(String topicHeader, double price, double discount) {
        this.topicHeader = topicHeader;
        this.price = price;
        this.discount = discount;
        type = 2;
    }

    public CustomListItem(String topicHeader, double price, double discount, boolean paymentStatus) {
        this.topicHeader = topicHeader;
        this.price = price;
        this.discount = discount;
        this.paymentStatus = paymentStatus;
        type = 2;
    }

    public CustomListItem(String topicHeader, String description, double price, double discount, int quesCount, int duration, int MPQ, String imagex) {
        this.topicHeader = topicHeader;
        this.description = description;
        this.price = price;
        type = 1;
        this.discount = discount;
        this.quesCount = quesCount;
        this.duration = duration;
        this.MPQ = MPQ;
        this.imagex = imagex;
    }

    public CustomListItem(int chapterIndex, String topicHeader, String description, double price, double discount, int quesCount, int duration, int MPQ, String imagex) {
        this.chapterIndex = chapterIndex;
        this.topicHeader = topicHeader;
        this.description = description;
        this.price = price;
        type = 1;
        this.discount = discount;
        this.quesCount = quesCount;
        this.duration = duration;
        this.MPQ = MPQ;
        this.imagex = imagex;
    }

    public CustomListItem(String topicHeader, String description, String imagex) {
        this.topicHeader = topicHeader;
        this.description = description;
        type = 0;
        this.imagex = imagex;
    }

    public CustomListItem(int chapterIndex,int quesCount, String topicHeader, String description, String months, double price, double discount, String imagex) {
        this.chapterIndex = chapterIndex;
        this.quesCount = quesCount;
        this.topicHeader = topicHeader;
        this.months = months;
        this.description = description;
        this.price = price;
        type = 3;
        this.discount = discount;
        this.imagex = imagex;
    }

    public CustomListItem(String name, String month1, String month2, String month3, String price1, String price2, String price3, String discount1, String discount2, String discount3, String couponCODE, String coupanDiscount, String couponACTIVE, String noq) {
        this.topicHeader = name;
        this.months = month1;
        this.month2 = month2;
        this.month3 = month3;
        this.price = Double.parseDouble(price1);
        this.price2 = Double.parseDouble(price2);
        this.price3 = Double.parseDouble(price3);
        this.discount = Double.parseDouble(discount1);
        this.discount2 = Double.parseDouble(discount2);
        this.discount3 = Double.parseDouble(discount3);
        this.coupanCode = couponCODE;
        this.coupanActive = couponACTIVE;
        this.coupanDiscount = coupanDiscount;
        this.NOQ = iNull(noq);

    }

    public int getType() {
        return type;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public int getQuesCount() {
        return quesCount;
    }

    public String getTopicHeader() {
        return topicHeader;
    }

    public String getMonths() {
        return months;
    }

    public String getDescription() {
        return description;
    }

    public String getImagex() {
        return imagex;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getMPQ() {
        return this.MPQ;
    }

    public String getMonth2() {
        return month2;
    }

    public String getMonth3() {
        return month3;
    }

    public double getPrice2() {
        return price2;
    }

    public double getPrice3() {
        return price3;
    }

    public double getDiscount2() {
        return discount2;
    }

    public double getDiscount3() {
        return discount3;
    }

    public int getNOQ() {
        return NOQ;
    }

    public String getCoupanCode() {
        return coupanCode;
    }

    public String getCoupanActive() {
        return coupanActive;
    }

    public boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCoupanDiscount() {
        return coupanDiscount;
    }

    public void setCoupanDiscount(String coupanDiscount) {
        this.coupanDiscount = coupanDiscount;
    }

    public int iNull(String value) {
        int num = 0;
        if (bNull(xNull(value))) {
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                num = 0;
            }
        }
        return iNull(num);
    }

    public int iNull(int value) {
        if (value < 1) return 0;
        else return value;
    }

    public String xNull(String str) {
        if (str != null) return str;
        else return "";
    }

    public boolean bNull(String str) {
        if (str == null) return false;
        else return !str.trim().isEmpty();
    }
}
