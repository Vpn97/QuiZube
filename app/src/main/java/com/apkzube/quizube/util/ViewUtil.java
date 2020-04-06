package com.apkzube.quizube.util;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

    //methid enable or disable all view that child of any VIewGroup
    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
}
