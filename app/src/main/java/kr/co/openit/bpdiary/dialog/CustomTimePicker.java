package kr.co.openit.bpdiary.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

import kr.co.openit.bpdiary.R;

/**
 * Created by Mu on 2016-12-26.
 */

public class CustomTimePicker extends TimePicker {

    public CustomTimePicker(Context context) {
        super(context);
        init(context, null);
    }

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            Class<?> clsParent = Class.forName("com.android.internal.R$id");
            NumberPicker clsMonth = (NumberPicker)findViewById(clsParent.getField("hour").getInt(null));
            NumberPicker clsDay = (NumberPicker)findViewById(clsParent.getField("minute").getInt(null));
            NumberPicker clsYear = (NumberPicker)findViewById(clsParent.getField("amPm").getInt(null));
            clsYear.setDisplayedValues(new String[] {"AM", "PM"});
            Class<?> clsNumberPicker = Class.forName("android.widget.NumberPicker");
            Field clsSelectionDivider = clsNumberPicker.getDeclaredField("mSelectionDivider");
            clsSelectionDivider.setAccessible(true);
            clsSelectionDivider.set(clsMonth, getResources().getDrawable(R.drawable.img_pickers_line));
            clsSelectionDivider.set(clsDay, getResources().getDrawable(R.drawable.img_pickers_line));
            clsSelectionDivider.set(clsYear, getResources().getDrawable(R.drawable.img_pickers_line));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
