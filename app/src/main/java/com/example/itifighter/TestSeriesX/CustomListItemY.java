package com.example.itifighter.TestSeriesX;

public class CustomListItemY {
    private String currentSubject,currentChapter,expiryDate,TestId,TestName,TestDuration,TestQuetion,TestScore;
    public CustomListItemY(String currentSubject, String currentChapter, String expiryDate){
        this.currentChapter = currentChapter;
        this.currentSubject = currentSubject;
        this.expiryDate = expiryDate;
    }
    public CustomListItemY(String TestId, String TestName, String TestDuration,String TestQuetion, String TestScore){
        this.TestId = TestId;
        this.TestName = TestName;
        this.TestDuration =TestDuration;
        this.TestQuetion = TestQuetion;
        this.TestScore= TestScore;
    }

    public String getCurrentSubject(){return currentSubject;}
    public String getCurrentChapter() {return currentChapter;}
    public String getExpiryDate() {return expiryDate;}
    public String getTestDuration() {return TestDuration; }
    public String getTestId() {return TestId;}
    public String getTestName() {return TestName;}
    public String getTestQuetion() {return TestQuetion;}
    public String getTestScore() {return TestScore;}

}
