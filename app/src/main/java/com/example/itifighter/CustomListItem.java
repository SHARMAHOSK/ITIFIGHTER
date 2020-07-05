package com.example.itifighter;

class CustomListItem {
    private String topicHeader;
    private String description;
    private String image;
    private Double price;
    private int subjectCount;

    //constructor
    public CustomListItem(String topicHeader, String description, Double price, String image, int subjectCount){

        this.topicHeader = topicHeader;
        this.description = description;
        this.price = price;
        this.image = image;
        this.subjectCount = subjectCount;
    }

    //getters
    public String getTopicHeader() {return topicHeader; }
    public String getDescription() {return description; }
    public Double getPrice() {return price; }
    public String getImage() { return image; }
    public int getSubjectCount(){ return subjectCount; }
}
