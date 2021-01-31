package com.example.itifighter.ui.notification;

public class ListItem {
    private String name,desc,url;
    public ListItem(){}
    public ListItem(String name, String desc, String url){
        this.name = name;
        this.desc = desc;
        this.url = url;
    }
    public String getName(){return name;}
    public String getDesc(){return desc;}
    public void setName(String name){this.name=name;}
    public void setDesc(String desc){this.desc=desc;}
    public void setUrl(String url){this.url=url;}
    public String getUrl(){return url;}
}
