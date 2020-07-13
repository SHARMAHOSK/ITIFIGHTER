package com.example.itifighter.ui.groups;

public class ListItem {
    private String name;
    private String desc;
    //private String Image;
    public ListItem(){}
    public ListItem(String name, String desc){
        this.name = name;
        this.desc = desc;
        //this.Image = Image;
    }
    public String getName(){return name;}
    public String getDesc(){return desc;}
    public void setName(String name){this.name=name;}
    public void setDesc(String desc){this.desc=desc;}
    //public String getImage(){return Image;}
}
