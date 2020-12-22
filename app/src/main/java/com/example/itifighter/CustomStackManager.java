package com.example.itifighter;

import java.util.Arrays;

public class CustomStackManager {
    public static int current_page = 2;
    private int[] pageState;
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
        }
    }
}
