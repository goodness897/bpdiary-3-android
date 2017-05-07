package kr.co.openit.bpdiary.activity.setting.view;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.setting.item.SettingAlarmItem;
import kr.co.openit.bpdiary.customview.CustomSwitch;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * Created by hwangem on 2017-01-06.
 */

/**
 * 복약/측정 알림 뷰홀더
 */
public class SettingAlarmView extends FrameLayout implements Checkable {

    private ImageView medicineImage;

    private ImageView measureImage;

    private ImageView checkView;

    private TextView apmView;

    private TextView timeView;

    //    public SwitchCompat isSwitchOn;

    public CustomSwitch isSwitchOn;

    private SettingAlarmItem model;

    private boolean isChecked;

    public SettingAlarmView(Context context) {
        super(context);
        init();
    }

    /**
     * 초기화
     */
    private void init() {
        inflate(getContext(), R.layout.view_alarm_setting, this);
        medicineImage = (ImageView)findViewById(R.id.iv_alarm_medicine);
        measureImage = (ImageView)findViewById(R.id.iv_alarm_measure);
        checkView = (ImageView)findViewById(R.id.iv_check);
        apmView = (TextView)findViewById(R.id.tv_apm);
        timeView = (TextView)findViewById(R.id.tv_alarm_time);
        isSwitchOn = (CustomSwitch)findViewById(R.id.switch_alarm);
        isSwitchOn.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {

            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                changeListener.onMedicineSwitchChange(SettingAlarmView.this, switchView);
            }
        });
        //        isSwitchOn.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View view) {
        //                changeListener.onMedicineSwitchChange(SettingAlarmView.this, view);
        //            }
        //        });
    }

    /**
     * 데이터 셋팅 메소드
     */
    public void setBaseAlarmData(SettingAlarmItem model, boolean isDeleteView, boolean isMedicineView, boolean isDim) {
        this.model = model;

        // 시간으로 오전/오후 나누기
        String temp = ManagerUtil.convertTimeFormat(model.getTime());
        String dayOrNight = temp.substring(0, 2);

        apmView.setText(dayOrNight);

        StringBuilder builder = new StringBuilder();
        String tempHour = temp.substring(3, 5);
        String tempMinute = temp.substring(6, 8);

        String wholeTime = builder.append(tempHour).append(" ").append(":").append(" ").append(tempMinute).toString();
        timeView.setText(wholeTime);

        isSwitchOn.setDefaultChecked(model.isStatus());

        if (isDim) {
            isSwitchOn.setSwitchToggleNotCheckedDrawableRes(R.drawable.switch_knob_dim);
            isSwitchOn.setSwitchToggleCheckedDrawableRes(R.drawable.switch_knob_dim);
            isSwitchOn.setSwitchBkgCheckedDrawableRes(R.drawable.switch_track_dim);
            isSwitchOn.setSwitchBkgNotCheckedDrawableRes(R.drawable.switch_track_dim);
            //            isSwitchOn.setAlpha((float)0.2);
        }

        // 현재 뷰가 복약 뷰일시
        if (isMedicineView) {
            measureImage.setVisibility(View.GONE);
            medicineImage.setVisibility(View.VISIBLE);

        } else {
            medicineImage.setVisibility(View.GONE);
            measureImage.setVisibility(View.VISIBLE);
        }
        // 현재 뷰가 알림 삭제 뷰일시 
        if (isDeleteView) {
            isSwitchOn.setEnabled(false);
            isSwitchOn.setClickable(false);
            isSwitchOn.setVisibility(View.GONE);
            checkView.setVisibility(View.VISIBLE);

        } else {
            isSwitchOn.setEnabled(true);
            isSwitchOn.setClickable(true);
            isSwitchOn.setVisibility(View.VISIBLE);
            checkView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            drawCheck();
        }
    }

    private void drawCheck() {
        if (isChecked) {
            checkView.setImageResource(R.drawable.checkbox_on);

        } else {
            checkView.setImageResource(R.drawable.checkbox_off);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    // 토글 리스너 인터페이스
    public interface OnMedicineSwitchChangeListener {

        public void onMedicineSwitchChange(SettingAlarmView view, View testView);
    }

    OnMedicineSwitchChangeListener changeListener;

    public void setMedicineSwitchChangeListener(OnMedicineSwitchChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
