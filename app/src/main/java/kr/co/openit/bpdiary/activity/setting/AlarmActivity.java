package kr.co.openit.bpdiary.activity.setting;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.intro.LoginLogicActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomSwitch;
import kr.co.openit.bpdiary.services.MedicineMeasureAlarmService;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class AlarmActivity extends BaseActivity implements View.OnClickListener {

    /**
     * service
     */
    private MedicineMeasureAlarmService mAlarmService;

    /**
     * 복약 알림 리스트
     */
    private ArrayList<Map<String, String>> mDBMedicineArrayList;

    /**
     * 측정 알림 리스트
     */
    private ArrayList<Map<String, String>> mDBMeasureArrayList;

    /**
     * 토글 ON/OFF
     */
    private boolean mIsTrue = false;

    //    private SwitchCompat isSwitchOn;

    private CustomSwitch isSwitchOn;

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initToolbar(getString(R.string.setting_activity_alarm));

        mAlarmService = new MedicineMeasureAlarmService(AlarmActivity.this);
        context = AlarmActivity.this;
        LinearLayout llSwitchAlarm = (LinearLayout)findViewById(R.id.ll_sc_alarm);
        llSwitchAlarm.setVisibility(View.VISIBLE);
        //        isSwitchOn = (SwitchCompat)findViewById(R.id.sc_alarm);
        isSwitchOn = (CustomSwitch)findViewById(R.id.sc_alarm);
        isSwitchOn.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {

            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                PreferenceUtil.setWholeToggleState(AlarmActivity.this, isChecked);
                setAlarmFlagState(isChecked);
                if (isChecked) {
                    Toast.makeText(context, getString(R.string.alarm_toggle_state_on_message), Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast.makeText(context, getString(R.string.alarm_toggle_state_off_message), Toast.LENGTH_SHORT)
                         .show();
                }
            }
        });
        //                 알림 플래그 ON/OFF 리스너
        //        isSwitchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //
        //            @Override
        //            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecking) {
        //                PreferenceUtil.setWholeToggleState(AlarmActivity.this, isChecking);
        //                setAlarmFlagState(isChecking);
        //    }
        //        });

        RelativeLayout medicineLayout = (RelativeLayout)findViewById(R.id.rl_alarm_medicine);
        medicineLayout.setOnClickListener(this);

        RelativeLayout messureLayout = (RelativeLayout)findViewById(R.id.rv_alarm_messure);
        messureLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!ManagerUtil.isClicking()) {
            int id = view.getId();
            Intent intent;

            switch (id) {
                // 복약알림으로 이동
                case R.id.rl_alarm_medicine:
                    intent = new Intent(AlarmActivity.this, SettingAlarmActivity.class);
                    intent.putExtra(ManagerConstants.IntentData.ALARM_FLAG,
                                    ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y);
                    startActivity(intent);
                    break;

                // 측정알림으로 이동
                case R.id.rv_alarm_messure:
                    intent = new Intent(AlarmActivity.this, SettingAlarmActivity.class);
                    intent.putExtra(ManagerConstants.IntentData.ALARM_FLAG,
                                    ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
                    startActivity(intent);
                    break;
            }
        }
    }

    /**
     * 알람 플래그 셋팅하는 메소드
     */
    private void setAlarmFlagState(boolean isChecking) {

        if (isChecking) {
            // 복약 알림 플래그 ON
            if (mDBMedicineArrayList != null && mDBMedicineArrayList.size() > 0) {
                for (int i = 0; i < mDBMedicineArrayList.size(); i++) {

                    if (mDBMedicineArrayList.get(i)
                                            .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                            .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {
                        mIsTrue = true;

                    } else {
                        mIsTrue = false;
                    }

                    if (mIsTrue) {
                        AlarmUtils.getInstance(AlarmActivity.this).setAlarmManager(AlarmActivity.this,
                                                                                   mDBMedicineArrayList.get(i)
                                                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                   ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y,
                                                                                   mDBMedicineArrayList.get(i)
                                                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                   mIsTrue);
                    }
                }
            }

            // 측정 알림 플래그 ON
            if (mDBMeasureArrayList != null && mDBMeasureArrayList.size() > 0) {
                for (int i = 0; i < mDBMeasureArrayList.size(); i++) {

                    if (mDBMeasureArrayList.get(i)
                                           .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                           .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {
                        mIsTrue = true;

                    } else {
                        mIsTrue = false;
                    }

                    if (mIsTrue) {
                        AlarmUtils.getInstance(AlarmActivity.this).setAlarmManager(AlarmActivity.this,
                                                                                   mDBMeasureArrayList.get(i)
                                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                   ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y,
                                                                                   mDBMeasureArrayList.get(i)
                                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                   mIsTrue);
                    }
                }
            }

        } else {
            // 복약 알림 플래그 OFF
            if (mDBMedicineArrayList != null && mDBMedicineArrayList.size() > 0) {
                for (int i = 0; i < mDBMedicineArrayList.size(); i++) {

                    AlarmUtils.getInstance(AlarmActivity.this)
                              .setCancelAlarm(AlarmActivity.this,
                                              Integer.parseInt(mDBMedicineArrayList.get(i)
                                                                                   .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                }
            }

            // 측정 알림 플래그 OFF
            if (mDBMeasureArrayList != null && mDBMeasureArrayList.size() > 0) {
                for (int i = 0; i < mDBMeasureArrayList.size(); i++) {

                    AlarmUtils.getInstance(AlarmActivity.this)
                              .setCancelAlarm(AlarmActivity.this,
                                              Integer.parseInt(mDBMeasureArrayList.get(i)
                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDBMedicineArrayList = new ArrayList<>();
        mDBMeasureArrayList = new ArrayList<>();

        // DB에 저장된 복약 값 가져오기
        if (mAlarmService.searchMedicineData() != null) {
            mDBMedicineArrayList.addAll(mAlarmService.searchMedicineData());
        }

        if (mAlarmService.searchMeasureData() != null) {
            mDBMeasureArrayList.addAll(mAlarmService.searchMeasureData());
        }

        // 전체 (Switch)플래그 디폴트값 받아오기 
        if (PreferenceUtil.getWholeToggleState(AlarmActivity.this)) {
            isSwitchOn.setDefaultChecked(true);
            PreferenceUtil.setWholeToggleState(AlarmActivity.this, true);

            if (mDBMedicineArrayList != null && mDBMedicineArrayList.size() > 0) {
                for (int i = 0; i < mDBMedicineArrayList.size(); i++) {
                     if (mDBMedicineArrayList.get(i)
                                            .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                            .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {

                        PendingIntent pendingIntent =
                                                    AlarmUtils.getInstance(AlarmActivity.this)
                                                              .setCheckAlarm(AlarmActivity.this,
                                                                             Integer.parseInt(mDBMedicineArrayList.get(i)
                                                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                        if (pendingIntent == null) { // 이미 설정된 알람이 없는 경우
                            AlarmUtils.getInstance(AlarmActivity.this).setAlarmManager(AlarmActivity.this,
                                                                                       mDBMedicineArrayList.get(i)
                                                                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                       ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y,
                                                                                       mDBMedicineArrayList.get(i)
                                                                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                       true);
                        }
                    }
                }
            }

            if (mDBMeasureArrayList != null && mDBMeasureArrayList.size() > 0) {
                for (int i = 0; i < mDBMeasureArrayList.size(); i++) {
                    if (mDBMeasureArrayList.get(i)
                                           .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                           .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {

                        PendingIntent pendingIntent =
                                                    AlarmUtils.getInstance(AlarmActivity.this)
                                                              .setCheckAlarm(AlarmActivity.this,
                                                                             Integer.parseInt(mDBMeasureArrayList.get(i)
                                                                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                        if (pendingIntent == null) { // 이미 설정된 알람이 없는 경우
                            AlarmUtils.getInstance(AlarmActivity.this).setAlarmManager(AlarmActivity.this,
                                                                                       mDBMeasureArrayList.get(i)
                                                                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                       ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y,
                                                                                       mDBMeasureArrayList.get(i)
                                                                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                       true);
                        }
                    }
                }
            }

        } else {
            isSwitchOn.setDefaultChecked(false);
            PreferenceUtil.setWholeToggleState(AlarmActivity.this, false);
            if (mDBMedicineArrayList != null && mDBMedicineArrayList.size() > 0) {
                for (int i = 0; i < mDBMedicineArrayList.size(); i++) {
                    PendingIntent pendingIntent =
                                                AlarmUtils.getInstance(AlarmActivity.this)
                                                          .setCheckAlarm(AlarmActivity.this,
                                                                         Integer.parseInt(mDBMedicineArrayList.get(i)
                                                                                                              .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));

                    if (pendingIntent != null) { // 이미 설정된 알람이 있는 경우
                        AlarmUtils.getInstance(AlarmActivity.this)
                                  .setCancelAlarm(AlarmActivity.this,
                                                  Integer.parseInt(mDBMedicineArrayList.get(i)
                                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                    }
                }
            }

            if (mDBMeasureArrayList != null && mDBMeasureArrayList.size() > 0) {
                for (int i = 0; i < mDBMeasureArrayList.size(); i++) {
                    PendingIntent pendingIntent =
                                                AlarmUtils.getInstance(AlarmActivity.this)
                                                          .setCheckAlarm(AlarmActivity.this,
                                                                         Integer.parseInt(mDBMeasureArrayList.get(i)
                                                                                                             .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));

                    if (pendingIntent != null) { // 이미 설정된 알람이 있는 경우
                        AlarmUtils.getInstance(AlarmActivity.this)
                                  .setCancelAlarm(AlarmActivity.this,
                                                  Integer.parseInt(mDBMeasureArrayList.get(i)
                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                    }
                }
            }
        }
    }
}
