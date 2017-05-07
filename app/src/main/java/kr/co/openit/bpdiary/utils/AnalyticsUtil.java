/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2014, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.utils;

import android.app.Activity;
import android.content.res.Resources;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.co.openit.bpdiary.BPDiaryApplication;

/**
 * Analytics 유틸
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2014. 7. 14.
 */
public class AnalyticsUtil {

    //버튼 선택시 Context와 버튼 이름 선택
    public static void sendEvent(Activity activity, int category, int action, int label) {
        //        EasyTracker.getInstance(context).send(MapBuilder.createEvent("ui_action", "button_press", name, null).build());
        Resources r = activity.getResources();
        Tracker t = ((BPDiaryApplication)activity.getApplication()).getTracker(BPDiaryApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(r.getString(category))
                                             .setAction(r.getString(action))
                                             .setLabel(r.getString(label))
                                             .build());
    }

    public static void sendEvent(Activity activity, String category, String action, String label) {
        //        EasyTracker.getInstance(context).send(MapBuilder.createEvent("ui_action", "button_press", name, null).build());
        Tracker t = ((BPDiaryApplication)activity.getApplication()).getTracker(BPDiaryApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(category)
                                             .setAction(action)
                                             .setLabel(label)
                                             .build());
    }

    //화면 전환시 호출
    public static void sendScene(Activity activity, String name) {
        //        EasyTracker t = EasyTracker.getInstance(context);
        //        t.set(Fields.SCREEN_NAME, name);
        //        t.send(MapBuilder.createAppView().build());
        Tracker t = ((BPDiaryApplication)activity.getApplication()).getTracker(BPDiaryApplication.TrackerName.APP_TRACKER);
        t.setScreenName(name);
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
