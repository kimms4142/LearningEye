package io.reactivex.android.samples;

import java.text.Collator;
import java.util.Comparator;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.content.Intent;


public class AppInfo {
    public static interface AppFilter {
        public void init();
        public boolean filterApp(ApplicationInfo info);
    }

    public Drawable mIcon = null;
    public String mAppNaem = null;
    public String mAppPackge = null;


    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter() {
        public void init() {
        }
/////////////////////
        String filter = ".reactivex.";
/////////////////////


        @Override
        public boolean filterApp(ApplicationInfo info) {
            String pName = info.packageName;
            if (pName.contains(filter)) {
                    return true;
            }
            return false;
        }
    };

/*
    @Override
    public boolean filterApp(ApplicationInfo info) {
                if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    return true;
                } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    return true;
                }
                return false;
            }
        };
   */



    public static final Comparator<AppInfo> ALPHA_COMPARATOR = new Comparator<AppInfo>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(AppInfo object1, AppInfo object2) {
            return sCollator.compare(object1.mAppNaem, object2.mAppNaem);
        }
    };




}
