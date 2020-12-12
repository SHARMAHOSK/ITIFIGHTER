package com.example.itifighter;

class CustomListItem {
    private String topicHeader;
    private String months;
    private String description;
    private double price, discount;
    private int quesCount;
    private int duration;
    private int MPQ;
    private int type;
    private String imagex;

    public CustomListItem(String topicHeader, double price, double discount){
        this.topicHeader = topicHeader;
        this.price = price;
        this.discount = discount;
        type = 2;
    }

    public CustomListItem(String topicHeader, String description, double price, double discount, int quesCount, int duration, int MPQ,String imagex){
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

    public CustomListItem(String topicHeader, String description, String imagex){
        this.topicHeader = topicHeader;
        this.description = description;
        type=0;
        this.imagex = imagex;
    }

    public CustomListItem(String topicHeader, String description, String months, double price, double discount, String imagex) {
        this.topicHeader = topicHeader;
        this.months = months;
        this.description = description;
        this.price = price;
        type = 3;
        this.discount = discount;
        this.imagex = imagex;
    }

    public int getType() {
        return type;
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

    public String getTopicHeader() {return topicHeader; }
    public String getMonths() {return months; }
    public String getDescription() {return description; }
    public String getImagex(){return  imagex;}

    public int getDuration() {
        return this.duration;
    }

    public int getMPQ() {
        return this.MPQ;
    }
}
