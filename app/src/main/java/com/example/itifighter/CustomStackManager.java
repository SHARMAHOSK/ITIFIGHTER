package com.example.itifighter;

import java.util.Arrays;

public class CustomStackManager {
    public static int current_page = 2;
    private final int[] pageState;
    private static CustomStackManager instance = null;

    private CustomStackManager(){
        pageState = new int[5];
        Arrays.fill(pageState, 0);
    }

    public static CustomStackManager GetInstance(){
        if(instance == null)
            instance = new CustomStackManager();

        return instance;
    }

    public void ReloadCurrent(){
        switch(current_page){
            case 0:
                if(PreviousPaper.instance != null)
                    PreviousPaper.instance.LoadSubjects();
                break;
            case 1:
                if(MockTest.instance != null)
                    MockTest.instance.LoadSubjects();
                break;
            case 2:
                if(LiveTest.instance != null)
                    LiveTest.instance.LoadSubjects();
                break;
            case 3:
                if(TestSeries.instance != null)
                    TestSeries.instance.LoadSubjects();
                break;
            case 4:
                if(MyTestSeries.instance != null)
                    MyTestSeries.instance.LoadChapters();
                break;
        }
    }

    public void SetPageState(int level){
        if(level > 2 || level < 0)
            return;
        pageState[current_page] = level;
    }

    public int GetPageState(){
        try{
            return pageState[current_page];
        }catch (Exception e){
            return 0;
        }
    }

    public void GoBack(){
        if(GetPageState() <= 0){
            return;
        }

        switch(current_page){
            case 0:
                if(GetPageState() == 1)
                    PreviousPaper.instance.LoadSubjects();
                else if(GetPageState() == 2)
                    PreviousPaper.instance.LoadExams();
                break;
            case 1:
                if(GetPageState() == 1)
                    MockTest.instance.LoadSubjects();
                else if(GetPageState() == 2)
                    MockTest.instance.LoadChapters();
                break;
            case 2:
                if(GetPageState() == 1)
                    LiveTest.instance.LoadSubjects();
                break;
            case 3:
                if(GetPageState() == 1)
                    TestSeries.instance.LoadSubjects();
                else if(GetPageState() == 2)
                    TestSeries.instance.LoadChapters();
                break;
            case 4:
                if(GetPageState() == 1)
                    MyTestSeries.instance.LoadChapters();
                break;
        }
    }
}
