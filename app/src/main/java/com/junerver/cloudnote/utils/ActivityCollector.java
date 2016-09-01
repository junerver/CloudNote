package com.junerver.cloudnote.utils;
import android.app.Activity ;

import java.util.ArrayList ;
import java.util .List;

/**
 * Created by Junerver on 2015/6/30.
 * Use to finish All Activity.
 */
public class ActivityCollector {
    public static List<Activity> activities =new ArrayList<Activity> ();

    public static void addActivity(Activity activity ) {
        activities. add(activity) ;
    }
    public static void removeActivity(Activity activity ) {
        activities. remove(activity) ;
    }
    public static void finishAll(){
        for ( Activity activity: activities){
            if ( !activity. isFinishing()){
                activity. finish();
            }
        }
    }


}