package com.example.itifighter.ui.groups;

public class ListItem {
    private String name,desc,url,date;
    public ListItem(){}
    public ListItem(String name, String desc, String url, String date){
        this.name = name;
        this.desc = desc;
        this.url = url;
        this.date = date;
    }
    public String getName(){return name;}
    public String getDesc(){return desc;}
    public void setName(String name){this.name=name;}
    public void setDesc(String desc) {this.desc = desc;}
    public void setUrl(String url){this.url=url;}
    public String getUrl(){return url;}
    public String getDate(){return date;}
    public void setDate(String date) {this.date = date;}
}
