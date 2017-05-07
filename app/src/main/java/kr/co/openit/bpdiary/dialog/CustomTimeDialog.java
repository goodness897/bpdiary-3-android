package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.InputBPActivity;
import kr.co.openit.bpdiary.activity.glucose.InputGlucoseActivity;
import kr.co.openit.bpdiary.activity.weight.InputWeightActivity;

/**
 * Created by Mu on 2016-12-23.
 */

public class CustomTimeDialog extends Dialog {

    private CustomTimePicker timePicker;

    Context context;

    private int hour, minute;

    public CustomTimeDialog(Context context, int hour, int minute) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.hour = hour;
        this.minute = minute;
    }

    public CustomTimeDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_time_dialog);

        TextView cancelText = (TextView)findViewById(R.id.tv_cancel);
        TextView checkText = (TextView)findViewById(R.id.tv_check);
        timePicker = (CustomTimePicker)findViewById(R.id.timePicker);

        if (hour != 0 || minute != 0) {

            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);

        }

        cancelText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        checkText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (context instanceof InputBPActivity) {
                    InputBPActivity activity = (InputBPActivity)context;
                    activity.receiveTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    dismiss();
                } else if (context instanceof InputWeightActivity) {
                    InputWeightActivity activity = (InputWeightActivity)context;
                    activity.receiveTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    dismiss();
                } else if (context instanceof InputGlucoseActivity) {
                    InputGlucoseActivity activity = (InputGlucoseActivity)context;
                    activity.receiveTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    dismiss();
                }
            }
        });

    }
}
