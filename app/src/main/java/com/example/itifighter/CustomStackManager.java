package com.example.itifighter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

public class CustomStackManager {
    public static int current_page = 2;
    private final int[] pageState;
    private static CustomStackManager instance = null;
    public static boolean ShowLoadingOverlay = true;
    //since sharedPreferences require context but context belongs to activity classes, therefore, getting one from parent activity class while creating instance of this class
    private Context context;

    //sp keys
    public static final String CURRENT_PAGE_KEY = "current_page";
    public static final String PP_STATE_KEY = "pp_state";
    public static final String MT_STATE_KEY = "mt_state";
    public static final String LT_STATE_KEY = "lt_state";
    public static final String TS_STATE_KEY = "ts_state";
    public static final String MTS_STATE_KEY = "mts_state";

    public static final String TARGET_SUBJECT_KEY = "_targetS";
    public static final String TARGET_CHAPTER_KEY = "_targetC";
    public static final String TARGET_EXAM_KEY = "_targetE";

    private static SharedPreferences sharedPreferences;

    private CustomStackManager(Context _context){
        context = _context;
        sharedPreferences =  context.getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        CreateSPData();
        pageState = new int[5];
        Arrays.fill(pageState, 0);
    }

    private void CreateSPData() {
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.putInt(CURRENT_PAGE_KEY, 2);
        sp_editor.putInt(PP_STATE_KEY, 0);
        sp_editor.putInt(MT_STATE_KEY, 0);
        sp_editor.putInt(LT_STATE_KEY, 0);
        sp_editor.putInt(TS_STATE_KEY, 0);
        sp_editor.putInt(MTS_STATE_KEY, 0);
        sp_editor.apply();
    }

    public static CustomStackManager GetInstance(Context context){
        if(instance == null)
            instance = new CustomStackManager(context);

        return instance;
    }

    public static int GetSPKeyValue(String key, int defV){
        return sharedPreferences.getInt(key, defV);
    }

    public static void SetSPKeyValue(String key, int val){
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.putInt(key, val);
        sp_editor.apply();
    }

    public static String GetSPKeyValue(String key, String defV){
        return sharedPreferences.getString(key, defV);
    }

    public static void SetSPKeyValue(String key, String val){
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.putString(key, val);
        sp_editor.apply();
    }

    public void ReloadCurrent(){
        ShowLoadingOverlay = false;
        switch(GetSPKeyValue(CURRENT_PAGE_KEY, 2)){
            case 0:
                if(PreviousPaper.instance != null) {
                    if(GetSPKeyValue(PP_STATE_KEY, 0) == 1)
                        PreviousPaper.instance.LoadExams();
                    else if(GetSPKeyValue(PP_STATE_KEY, 0) == 2)
                        PreviousPaper.instance.LoadPdfS();
                    else
                        PreviousPaper.instance.LoadSubjects();
                }
                break;
            case 1:
                if(MockTest.instance != null) {
                    if(GetSPKeyValue(MT_STATE_KEY, 0) == 1)
                        MockTest.instance.LoadChapters();
                    else
                        MockTest.instance.LoadSubjects();
                }
                break;
            case 2:
                if(LiveTest.instance != null) {
                    if(GetSPKeyValue(LT_STATE_KEY,0) == 1)
                        LiveTest.instance.LoadChapters();
                    else
                        LiveTest.instance.LoadSubjects();
                }
                break;
            case 3:
                if(TestSeries.instance != null) {
                    if(GetSPKeyValue(TS_STATE_KEY, 0) == 1)
                        TestSeries.instance.LoadChapters();
                    else
                        TestSeries.instance.LoadSubjects();
                }
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
            switch (GetSPKeyValue(CURRENT_PAGE_KEY, 2)){
                case 0:
                    return GetSPKeyValue(PP_STATE_KEY, 0);
                case 1:
                    return GetSPKeyValue(MT_STATE_KEY, 0);
                case 2:
                    return GetSPKeyValue(LT_STATE_KEY, 0);
                case 3:
                    return GetSPKeyValue(TS_STATE_KEY, 0);
                case 4:
                    return GetSPKeyValue(MTS_STATE_KEY, 0);
                default:
                    return 0;
            }
        }catch (Exception e){
            return 0;
        }
    }

    public void GoBack(){
        /*if(GetPageState() <= 0){
            return;
        }*/

        switch(GetSPKeyValue(CURRENT_PAGE_KEY, 2)){
            case 0:
                if(GetSPKeyValue(PP_STATE_KEY, 0) == 1)
                    PreviousPaper.instance.LoadSubjects();
                else if(GetSPKeyValue(PP_STATE_KEY, 0) == 2)
                    PreviousPaper.instance.LoadExams();
                break;
            case 1:
                if(GetSPKeyValue(MT_STATE_KEY, 0) == 1)
                    MockTest.instance.LoadSubjects();
                /*else if(GetPageState() == 2)
                    MockTest.instance.LoadChapters();*/
                break;
            case 2:
                if(GetSPKeyValue(LT_STATE_KEY, 0) == 1)
                    LiveTest.instance.LoadSubjects();
                break;
            case 3:
                if(GetSPKeyValue(TS_STATE_KEY, 0) == 1)
                    TestSeries.instance.LoadSubjects();
                else if(GetSPKeyValue(TS_STATE_KEY, 0) == 2)
                    TestSeries.instance.LoadChapters();
                break;
            case 4:
                if(GetSPKeyValue(MTS_STATE_KEY, 0) == 1)
                    MyTestSeries.instance.LoadChapters();
                break;
        }
    }
}
