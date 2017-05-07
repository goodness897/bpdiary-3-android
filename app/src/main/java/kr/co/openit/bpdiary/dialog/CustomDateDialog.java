package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.InputBPActivity;
import kr.co.openit.bpdiary.activity.glucose.InputGlucoseActivity;
import kr.co.openit.bpdiary.activity.intro.ProfileEnterActivity;
import kr.co.openit.bpdiary.activity.setting.ProfileBirthActivity;
import kr.co.openit.bpdiary.activity.weight.InputWeightActivity;

/**
 * Created by Mu on 2016-12-23.
 */

public class CustomDateDialog extends Dialog {

    private CustomDatePicker datePicker;

    Context context;

    private int year, monthOfYear, dayOfMonth;

    public CustomDateDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    public CustomDateDialog(Context context, int year, int monthOfYear, int dayOfMonth) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_date_dialog);

        TextView cancelText = (TextView)findViewById(R.id.tv_cancel);
        TextView checkText = (TextView)findViewById(R.id.tv_check);
        datePicker = (CustomDatePicker)findViewById(R.id.datePicker);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        if (year != 0 || monthOfYear != -1 || dayOfMonth != 0) {
            datePicker.init(year, monthOfYear, dayOfMonth, null);
        } else {
            datePicker.init(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            null);
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
                if (context instanceof ProfileBirthActivity) {
                    ProfileBirthActivity activity = (ProfileBirthActivity)context;
                    activity.receiveMessage(datePicker.getYear(),
                                            datePicker.getMonth() + 1,
                                            datePicker.getDayOfMonth());

                    dismiss();
                } else if (context instanceof ProfileEnterActivity) {
                    ProfileEnterActivity activity = (ProfileEnterActivity)context;
                    activity.receiveMessage(datePicker.getYear(),
                                            datePicker.getMonth() + 1,
                                            datePicker.getDayOfMonth());
                    dismiss();
                } else if (context instanceof InputBPActivity) {
                    InputBPActivity activity = (InputBPActivity)context;
                    activity.receiveMessage(datePicker.getYear(),
                                            datePicker.getMonth() + 1,
                                            datePicker.getDayOfMonth());
                    dismiss();
                } else if (context instanceof InputGlucoseActivity) {
                    InputGlucoseActivity activity = (InputGlucoseActivity)context;
                    activity.receiveMessage(datePicker.getYear(),
                                            datePicker.getMonth() + 1,
                                            datePicker.getDayOfMonth());
                    dismiss();
                } else if (context instanceof InputWeightActivity) {
                    InputWeightActivity activity = (InputWeightActivity)context;
                    activity.receiveMessage(datePicker.getYear(),
                                            datePicker.getMonth() + 1,
                                            datePicker.getDayOfMonth());
                    dismiss();
                }
            }
        });

    }
}
