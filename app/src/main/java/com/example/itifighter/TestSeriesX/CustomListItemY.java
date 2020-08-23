package com.example.itifighter.TestSeriesX;

public class CustomListItemY {
    private String duration,topicHeader,quetion,imagex,score;
    public CustomListItemY(String topicHeader, String quetion,String score, String imagex, String duration){
        this.topicHeader = topicHeader;
        this.quetion = quetion;
        this.imagex = imagex;
        this.duration = duration;
        this.score = score;
    }
    public String getTopicHeader() {return topicHeader; }
    public String getQuetion() {return quetion; }
    public String getImagex(){return  imagex;}
    public String getDuration(){return duration;}
    public String getScore(){return score;}
}
