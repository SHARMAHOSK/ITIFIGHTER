package com.example.itifighter;

class CustomListItem {
    private String topicHeader;
    private String description;
    private String imageUrl;
    private Double price;
    private int subjectCount;
    private String imagex;
    public CustomListItem(String topicHeader, String description, Double price, String imageUrl, int subjectCount,String imagex){
        this.topicHeader = topicHeader;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imagex = imagex;
    }


    public String getTopicHeader() {return topicHeader; }
    public String getDescription() {return description; }
    public String getImageUrl() { return imageUrl; }
    public String getImagex(){return  imagex;}
}
