package kr.co.openit.bpdiary.activity.intro;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.setting.AlarmActivity;
import kr.co.openit.bpdiary.activity.setting.AlarmUtils;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.EventInfoData;
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.services.MedicineMeasureAlarmService;
import kr.co.openit.bpdiary.utils.AesssUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PhoneUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by srpark on 2017-01-04.
 */

public class LoginLogicActivity extends NonMeasureActivity {

    /**
     * intent에서 받은 로그인 Type
     */
    private String loginType;

    /**
     * 자동 로그인 여부
     */
    private String autoLoginYn;

    /**
     * Api Call Service
     */
    private IntroService introService;

    /**
     * 복약 알림 리스트
     */
    private ArrayList<Map<String, String>> mDBMedicineArrayList;

    /**
     * 측정 알림 리스트
     */
    private ArrayList<Map<String, String>> mDBMeasureArrayList;

    /**
     * Alarm Call Service
     */
    private MedicineMeasureAlarmService alarmService;

    private Context context;

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideLodingProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_logic);

        showLodingProgress();

        context = LoginLogicActivity.this;
        alarmService = new MedicineMeasureAlarmService(context);
        introService = new IntroService(context);

        loginType = PreferenceUtil.getLoginType(context);
        if (PreferenceUtil.getIsLogin(context)) {
            autoLoginYn = "Y";

            PreferenceUtil.setWholeToggleState(LoginLogicActivity.this,
                    PreferenceUtil.getWholeToggleState(LoginLogicActivity.this));

        } else {
            autoLoginYn = "N";

            // DB에 알림 INIT DATA 셋팅
            alarmService.deleteMedicineMeasureWholeData();
            setInitDBData();
            // 복약/측정 알림 전체 플래그 DEFAULT 값 셋팅
            PreferenceUtil.setWholeToggleState(LoginLogicActivity.this, false);
        }

        mDBMedicineArrayList = new ArrayList<>();
        mDBMeasureArrayList = new ArrayList<>();

        // DB에 저장된 알람 셋팅하는 곳
        if (alarmService.searchMedicineData() != null) {
            mDBMedicineArrayList.addAll(alarmService.searchMedicineData());
        }

        if (mDBMedicineArrayList != null && mDBMedicineArrayList.size() > 0) { // 복약
            for (int i = 0; i < mDBMedicineArrayList.size(); i++) {
                if (autoLoginYn.equals("Y")) {
                    PendingIntent pendingIntent =
                                                AlarmUtils.getInstance(LoginLogicActivity.this)
                                                          .setCheckAlarm(LoginLogicActivity.this,
                                                                         Integer.parseInt(mDBMedicineArrayList.get(i)
                                                                                                              .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));
                    if (pendingIntent == null) { // 이미 설정된 알람이 없는 경우
                        if (mDBMedicineArrayList.get(i)
                                                .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                                .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {
                            AlarmUtils.getInstance(LoginLogicActivity.this).setAlarmManager(LoginLogicActivity.this,
                                                                                            mDBMedicineArrayList.get(i)
                                                                                                                .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                            ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y,
                                                                                            mDBMedicineArrayList.get(i)
                                                                                                                .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                            true);
                        }
                    }
                } else {
                    AlarmUtils.getInstance(LoginLogicActivity.this).setAlarmManager(LoginLogicActivity.this,
                            mDBMedicineArrayList.get(i)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                            ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y,
                            mDBMedicineArrayList.get(i)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                            true);
                }
            }
        }

        if (alarmService.searchMeasureData() != null) {
            mDBMeasureArrayList.addAll(alarmService.searchMeasureData());
        }

        if (mDBMeasureArrayList != null && mDBMeasureArrayList.size() > 0) { // 측정
            for (int i = 0; i < mDBMeasureArrayList.size(); i++) {
                if (autoLoginYn.equals("Y")) {
                    PendingIntent pendingIntent =
                                                AlarmUtils.getInstance(LoginLogicActivity.this)
                                                          .setCheckAlarm(LoginLogicActivity.this,
                                                                         Integer.parseInt(mDBMeasureArrayList.get(i)
                                                                                                             .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ)));

                    if (pendingIntent == null) { // 이미 설정된 알람이 없는 경우
                        if (mDBMeasureArrayList.get(i)
                                               .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                               .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) {
                            AlarmUtils.getInstance(LoginLogicActivity.this).setAlarmManager(LoginLogicActivity.this,
                                                                                            mDBMeasureArrayList.get(i)
                                                                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                                                                                            ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y,
                                                                                            mDBMeasureArrayList.get(i)
                                                                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                                                                                            true);
                        }
                    }
                } else {
                    AlarmUtils.getInstance(LoginLogicActivity.this).setAlarmManager(LoginLogicActivity.this,
                            mDBMeasureArrayList.get(i)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME),
                            ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y,
                            mDBMeasureArrayList.get(i)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ),
                            true);
                }
            }
        }

        LoginAsync loginAsync = new LoginAsync();
        loginAsync.execute();
    }

    /**
     * 로그인 AsyncTask
     */
    private class LoginAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                if (autoLoginYn == "N" || loginType.equals(ManagerConstants.LoginType.LOGIN_TYPE_EMAIL)) {
                    data.put(ManagerConstants.RequestParamName.PASSWORD, PreferenceUtil.getEncPassword(context));
                }
                data.put(ManagerConstants.RequestParamName.LOGIN_TYPE, loginType);
                data.put(ManagerConstants.RequestParamName.AUTO_YN, autoLoginYn);
                data.put(ManagerConstants.RequestParamName.MOBILE_OS, ManagerConstants.AppConfig.MOBILE_OS);
                data.put(ManagerConstants.RequestParamName.MOBILE_TOKEN, PreferenceUtil.getGcmToken(context));
                data.put(ManagerConstants.RequestParamName.NOTIFICATION, PreferenceUtil.getNotification(context));
                data.put(ManagerConstants.RequestParamName.BP_MONITOR_NAME, PreferenceUtil.getBPDeviceName(context));
                data.put(ManagerConstants.RequestParamName.OS_VERSION, Build.VERSION.RELEASE);
                data.put(ManagerConstants.RequestParamName.APP_VERSION_CREATE, PreferenceUtil.getVersion(context));
                data.put(ManagerConstants.RequestParamName.CARRIER,
                        PhoneUtil.getNetworkOperatorName(context).replaceAll("", "_"));
                data.put(ManagerConstants.RequestParamName.DEVICE_MODEL, Build.MODEL.replaceAll("", "_"));

                if (PreferenceUtil.getNotification(context)) {
                    data.put(ManagerConstants.RequestParamName.NOTIFICATION,
                            ManagerConstants.NotificationYN.NOTIFICATION_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.NOTIFICATION,
                            ManagerConstants.NotificationYN.NOTIFICATION_N);
                }

                if (PreferenceUtil.getGoogleFit(context)) {
                    data.put(ManagerConstants.RequestParamName.GOOGLE_FIT, ManagerConstants.GoogleFitYN.GOOGLE_FIT_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.GOOGLE_FIT, ManagerConstants.GoogleFitYN.GOOGLE_FIT_N);
                }

                if (PreferenceUtil.getSHealth(context)) {
                    data.put(ManagerConstants.RequestParamName.S_HEALTH, ManagerConstants.SHeathYN.S_HEALTH_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.S_HEALTH, ManagerConstants.SHeathYN.S_HEALTH_N);
                }

                resultJSON = introService.searchLoginCheck(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();
            try {
                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {
                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {
                        Log.d("srpark", "로그인 성공");
                        //로그인 완료
                        PreferenceUtil.setIsLogin(context, true);

                        if (resultJSON.has(ManagerConstants.ResponseParamName.PROFILE)) {
                            JSONObject profileData = new JSONObject();
                            profileData = (JSONObject) resultJSON.get(ManagerConstants.ResponseParamName.PROFILE);

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.FIRST_NAME))) {
                                PreferenceUtil.setEncFirstName(context,
                                        AesssUtil.decrypt((String) profileData.get(ManagerConstants.ResponseParamName.FIRST_NAME)));
                            }
                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.LAST_NAME))) {
                                PreferenceUtil.setEncLastName(context,
                                        AesssUtil.decrypt((String) profileData.get(ManagerConstants.ResponseParamName.LAST_NAME)));
                            }

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.GENDER))) {
                                PreferenceUtil.setGender(context,
                                        (String) profileData.get(ManagerConstants.ResponseParamName.GENDER));
                            }

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.DAY_OF_BIRTH))) {
                                PreferenceUtil.setEncDayOfBirth(context,
                                        AesssUtil.decrypt((String) profileData.get(ManagerConstants.ResponseParamName.DAY_OF_BIRTH)));
                            }

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.HEIGHT))) {
                                PreferenceUtil.setHeight(context,
                                        (String) profileData.get(ManagerConstants.ResponseParamName.HEIGHT));
                            }

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.HEIGHT_UNIT))) {
                                PreferenceUtil.setHeightUnit(context,
                                        (String) profileData.get(ManagerConstants.ResponseParamName.HEIGHT_UNIT));
                            }

                            if (ManagerUtil.mapDataNullCheck(profileData.get(ManagerConstants.ResponseParamName.BG_YN))) {
                                if (ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_Y.equals((String) profileData.get(ManagerConstants.ResponseParamName.BG_YN))) {
                                    PreferenceUtil.setUsingBloodGlucose(context, true);
                                } else {
                                    PreferenceUtil.setUsingBloodGlucose(context, false);
                                }
                            }
                        }

                        if (resultJSON.has(ManagerConstants.ResponseParamName.GOAL)) {
                            JSONObject goalData = new JSONObject();
                            goalData = (JSONObject) resultJSON.get(ManagerConstants.ResponseParamName.GOAL);

                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BP_SYS_MIN))) {
                                PreferenceUtil.setBPMinSystole(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BP_SYS_MIN));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BP_SYS_MAX))) {
                                PreferenceUtil.setBPMaxSystole(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BP_SYS_MAX));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BP_DIA_MIN))) {
                                PreferenceUtil.setBPMinDiastole(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BP_DIA_MIN));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BP_DIA_MAX))) {
                                PreferenceUtil.setBPMaxDiastole(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BP_DIA_MAX));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.WEIGHT))) {
                                PreferenceUtil.setWeightGoal(context,
                                        String.valueOf(goalData.get(ManagerConstants.ResponseParamName.WEIGHT)));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_B_MIN))) {
                                PreferenceUtil.setGlucoseMinBeforeMeal(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_B_MIN));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_B_MAX))) {
                                PreferenceUtil.setGlucoseMaxBeforeMeal(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_B_MAX));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_A_MIN))) {
                                PreferenceUtil.setGlucoseMinAfterMeal(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_A_MIN));
                            }
                            if (ManagerUtil.mapDataNullCheck(goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_A_MAX))) {
                                PreferenceUtil.setGlucoseMaxAfterMeal(context,
                                        (int) goalData.get(ManagerConstants.ResponseParamName.BG_MEAL_A_MAX));
                            }

                        }

                        if (resultJSON.has(ManagerConstants.ResponseParamName.PAY_TOKEN)
                                && null != resultJSON.getString(ManagerConstants.ResponseParamName.PAY_TOKEN)
                                && resultJSON.getString(ManagerConstants.ResponseParamName.PAY_TOKEN).length() > 0
                                && (!"null".equals(resultJSON.getString(ManagerConstants.ResponseParamName.PAY_TOKEN)))) {

                            PreferenceUtil.setIsPayment(context,
                                    resultJSON.get(ManagerConstants.ResponseParamName.PAY_TOKEN)
                                            .toString(),
                                    ManagerConstants.PaymentAdYN.PAYMENT_AD_Y);
                        } else {
                            PreferenceUtil.setIsPayment(context, "", ManagerConstants.PaymentAdYN.PAYMENT_AD_N);
                        }

                        if (resultJSON.has(ManagerConstants.ResponseParamName.BOARD_SEQ)) {
                            String strBoardSeq = resultJSON.getString(ManagerConstants.ResponseParamName.BOARD_SEQ);
                            PreferenceUtil.setNewNoticeNumber(context, strBoardSeq);
                        }

                        if (resultJSON.has(ManagerConstants.ResponseParamName.EVENT)) {
                            JSONObject eventData = new JSONObject();
                            eventData = (JSONObject) resultJSON.get(ManagerConstants.ResponseParamName.EVENT);

                            if (ManagerUtil.mapDataNullCheck(eventData.get("seq"))
                                    && ManagerUtil.mapDataNullCheck(eventData.get("snooze"))
                                    && ManagerUtil.mapDataNullCheck(eventData.get("type"))
                                    && ManagerUtil.mapDataNullCheck(eventData.get("endDt"))) {

                                EventInfoData eventInfoData = new EventInfoData((int) eventData.get("seq") + "",
                                        (int) eventData.get("snooze") + "",
                                        (String) eventData.get("type"),
                                        (String) eventData.get("url"),
                                        (String) eventData.get("endDt"));
                                Intent intent = new Intent();
                                intent.putExtra(CommonConstants.EVENT_DATA, eventInfoData);
                                setResult(RESULT_OK, intent);
                            } else {
                                setResult(RESULT_OK);
                            }
                        } else {
                            setResult(RESULT_OK);
                        }
                        finish();
                    } else {
                        if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            showValidationTitle(getString(R.string.common_required_value_error_comment)),
                                            getString(R.string.common_required_value_error_comment),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultOneButtonDialog() {

                                                @Override
                                                public void
                                                onConfirm() {
                                                    setResult(RESULT_CANCELED);
                                                    finish();
                                                }
                                            });
                            defaultOneButtonDialog.show();

                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_E.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            showValidationTitle(getString(R.string.common_error_comment)),
                                            getString(R.string.common_error_comment),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultOneButtonDialog() {

                                                @Override
                                                public void
                                                onConfirm() {
                                                    setResult(RESULT_CANCELED);
                                                    finish();
                                                }
                                            });
                            defaultOneButtonDialog.show();

                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_N.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultDialog defaultDialog = new DefaultDialog(context,
                                    showValidationTitle(getString(R.string.search_fail_common_no_account)),
                                    getString(R.string.search_fail_common_no_account),
                                    getString(R.string.common_txt_cancel),
                                    getString(R.string.common_sign_up),
                                    new IDefaultDialog() {

                                        @Override
                                        public void onCancel() {
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        }

                                        @Override
                                        public void onConfirm() {
                                            Intent intent =
                                                    new Intent(context,
                                                            ProfileEnterActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            defaultDialog.show();

                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_P.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            showValidationTitle(getString(R.string.fail_reason_content_missmatch_password)),
                                            getString(R.string.fail_reason_content_missmatch_password),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultOneButtonDialog() {

                                                @Override
                                                public void
                                                onConfirm() {
                                                    setResult(RESULT_CANCELED);
                                                    finish();
                                                }
                                            });
                            defaultOneButtonDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_L.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            if (ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                    .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(getString(R.string.fail_reason_content_google)),
                                                getString(R.string.fail_reason_content_google),
                                                getString(R.string.common_txt_confirm),
                                                new IDefaultOneButtonDialog() {

                                                    @Override
                                                    public void
                                                    onConfirm() {
                                                        setResult(RESULT_CANCELED);
                                                        finish();
                                                    }
                                                });
                                defaultOneButtonDialog.show();

                            } else if (ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                    .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(getString(R.string.fail_reason_content_facebook)),
                                                getString(R.string.fail_reason_content_facebook),
                                                getString(R.string.common_txt_confirm),
                                                new IDefaultOneButtonDialog() {

                                                    @Override
                                                    public void
                                                    onConfirm() {
                                                        setResult(RESULT_CANCELED);
                                                        finish();
                                                    }
                                                });
                                defaultOneButtonDialog.show();
                            } else {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(getString(R.string.fail_reason_content_email)),
                                                getString(R.string.fail_reason_content_email),
                                                getString(R.string.common_txt_confirm),
                                                new IDefaultOneButtonDialog() {

                                                    @Override
                                                    public void
                                                    onConfirm() {
                                                        setResult(RESULT_CANCELED);
                                                        finish();
                                                    }
                                                });
                                defaultOneButtonDialog.show();
                            }
                        }

                    }
                } else {
                    //TODO 에러 팝업
                    if (PreferenceUtil.getIsLogin(LoginLogicActivity.this)) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(context,
                                        showValidationTitle(getString(R.string.common_error_comment)),
                                        getString(R.string.common_error_comment),
                                        getString(R.string.common_txt_confirm),
                                        new IDefaultOneButtonDialog() {

                                            @Override
                                            public void
                                            onConfirm() {
                                                setResult(RESULT_CANCELED);
                                                finish();
                                            }
                                        });
                        defaultOneButtonDialog.show();
                    }
                }

            } catch (Resources.NotFoundException e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 유효성 검사에 따른 타이틀
     *
     * @param validationMessage
     * @return 타이틀
     */
    private String showValidationTitle(String validationMessage) {
        String title = "";
        if (validationMessage.equals(getResources().getString(R.string.profile_not_input_email))) {

            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_val_email))) {

            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_not_input_password))) {

            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_val_password))) {

            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_not_input_val_data))) {

            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_not_concurrence_password))) {

            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getString(R.string.common_required_value_error_comment))) {

            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getString(R.string.common_error_comment))) {

            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_google))) {

            title = getString(R.string.dialog_title_google_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_facebook))) {

            title = getString(R.string.dialog_title_facebook_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_email))) {

            title = getString(R.string.dialog_title_email_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_exist_join))) {

            title = getString(R.string.dialog_title_email_login);

        } else if (validationMessage.equals(getString(R.string.search_fail_common_no_account))) {

            title = getString(R.string.dialog_title_login_fail);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_missmatch_password))) {

            title = getString(R.string.dialog_title_check_password);
        }
        return title;
    }

    private void setInitDBData() {
        /* 복약 알림 INIT값 DB에 넣기 */
        Map<String, String> rMedicineMap = new HashMap<>();
        rMedicineMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y);
        rMedicineMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMedicineMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "0900");
        alarmService.insertMedicineMeasureAlarmData(rMedicineMap);

        Map<String, String> rMedicineMap2 = new HashMap<>();
        rMedicineMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y);
        rMedicineMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMedicineMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "1300");
        alarmService.insertMedicineMeasureAlarmData(rMedicineMap2);

        Map<String, String> rMedicineMap3 = new HashMap<>();
        rMedicineMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y);
        rMedicineMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMedicineMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "1900");
        alarmService.insertMedicineMeasureAlarmData(rMedicineMap3);

        /* 측정 알림 INIT값 DB에 넣기 */
        Map<String, String> rMeasureMap = new HashMap<>();
        rMeasureMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
        rMeasureMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMeasureMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "0700");
        alarmService.insertMedicineMeasureAlarmData(rMeasureMap);

        Map<String, String> rMeasureMap2 = new HashMap<>();
        rMeasureMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
        rMeasureMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMeasureMap2.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "0900");
        alarmService.insertMedicineMeasureAlarmData(rMeasureMap2);

        Map<String, String> rMeasureMap3 = new HashMap<>();
        rMeasureMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
        rMeasureMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMeasureMap3.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "1200");
        alarmService.insertMedicineMeasureAlarmData(rMeasureMap3);

        Map<String, String> rMeasureMap4 = new HashMap<>();
        rMeasureMap4.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
        rMeasureMap4.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMeasureMap4.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "1500");
        alarmService.insertMedicineMeasureAlarmData(rMeasureMap4);

        Map<String, String> rMeasureMap5 = new HashMap<>();
        rMeasureMap5.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
        rMeasureMap5.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMeasureMap5.put(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, "2200");
        alarmService.insertMedicineMeasureAlarmData(rMeasureMap5);

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
