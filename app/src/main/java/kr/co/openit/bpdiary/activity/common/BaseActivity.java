package kr.co.openit.bpdiary.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.setting.SettingTermsContentActivity;
import kr.co.openit.bpdiary.dialog.CustomDateDialog;
import kr.co.openit.bpdiary.dialog.CustomTimeDialog;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;
import kr.co.openit.healthup.common.device.vo.ObservationResult;
import kr.co.openit.healthup.common.hl7.HL7Message;
import kr.co.openit.healthup.common.hl7.HL7MessageResponse;
import kr.co.openit.healthup.common.utils.PCDUtil;
import kr.co.openit.healthup.common.ws.android.PCDConfig;
import kr.co.openit.healthup.common.ws.android.PCDSendTask;

/**
 * 최상단 Activity
 */
public class BaseActivity extends Activity {

    public static ArrayList<Activity> actList = new ArrayList<Activity>();

    /**
     * debugging
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * 인텐트 전달 네임
     */

    public static final String GLUCOSE = "glucose";

    public static final String GLUCOSE_MEMO = "glucoseMemo";

    /**
     * PCDConfig
     */
    private PCDConfig config;

    public Button btnFacebookLogin;

    public Button btnGoogleLogin;

    public Button btnEmailLogin;

    public TextView tvTerms;

    public TextView tvTermsService;

    public TextView tvTermsPrivacy;

    public Context context;

    public ImageView ivJoinType;

    public LinearLayout llLoginNaviBack;

    public LinearLayout llSignupNaviBack;

    public LinearLayout llNaviBack;

    public LinearLayout llTerms;

    public TextView tvJoinMethod;

    public LinearLayout llLoginDisplay;

    public LinearLayout llDate;

    public LinearLayout llTime;

    public TextView tvDate;

    public TextView tvTime;

    public String date = "";

    public String time = "";

    public int year = 1982, month = 1, day = 1;

    public int hour, minute;

    /**
     * 페이스북 연동
     */

    public CallbackManager callbackManager;

    public LoginManager mLoginManager;

    public void initToolbar(String title) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.ll_navi_back);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayout llTitle = (LinearLayout)findViewById(R.id.ll_title);
        TextView titleView = (TextView)findViewById(R.id.tv_title);

        if (title.length() > 26) {
            llTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            titleView.setText(title);
        } else {
            llTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            titleView.setGravity(Gravity.CENTER_VERTICAL);
            titleView.setText(title);
        }
    }

    /**
     * SSL 인증 무시
     */
    private class SSLTrustManager implements TrustManager, X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        /*
         * (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
         * java.lang.String)
         */
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // ignore
        }

        /*
         * (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
         * java.lang.String)
         */
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // ignore
        }
    }

    /**
     * 측정 데이터 저장을 위해 서버 호출
     *
     * @param profileName
     * @param deviceId
     * @param modelNumber
     * @param modelManufacturer
     * @param results
     */
    public void sendData(String profileName,
                         String deviceId,
                         String modelNumber,
                         String modelManufacturer,
                         List<ObservationResult> results) {

        //config = getPCDConfig();

        HL7Message msg = PCDUtil.makeHL7Message(profileName, deviceId, modelNumber, modelManufacturer, results, config);

        Log.i(TAG, msg.toString());

        PCDSendTask pcdSendTask = new PCDSendTask();
        pcdSendTask.setConfig(config);
        pcdSendTask.setCallback(new PCDConfig.Callback() {

            @Override
            public void start() {
                // nothing
            }

            @Override
            public void success(HL7MessageResponse resultMsg) {

                String acknowledgementCode = resultMsg.getAcknowledgementCode();
                Log.i(TAG, "\n\n*****************************************************************\n");
                Log.i(TAG, "acknowledgementCode = " + acknowledgementCode);
                Log.i(TAG, "\n*****************************************************************\n\n");

                // 전송 결과가 성공이 아니면...
                if ("AA".equals(acknowledgementCode)) {
                    //                    onSendSuccess();
                    onSendSuccess(resultMsg.getRequestMessageControlId());
                } else {
                    onSendFail();
                }
            }

            @Override
            public void fail() {
                // nothing
                onSendFail();
            }

        });

        pcdSendTask.execute(msg);
    }

    /**
     * 내용 입력
     */
    protected void onSendFail() {
        Log.d(TAG, "Callback 의 fail method 가 호출되어서...");
    }

    /**
     * 내용 입력
     */
    protected void onSendSuccess() {
        Log.d(TAG, "Callback 의 success method 가 호출되어서...");
    }

    /**
     * 내용 입력
     */
    protected void onSendSuccess(String getRequestMessageControlId) {
        //Log.d(TAG, "Callback 의 success method 가 호출되어서...");
    }

    /**
     * 레이아웃 셋팅
     */
    public void setLayout(RelativeLayout layout) {

        llTerms = (LinearLayout)layout.findViewById(R.id.ll_terms);
        tvTerms = (TextView)layout.findViewById(R.id.tv_terms);
        tvTermsService = (TextView)layout.findViewById(R.id.tv_terms_service);
        tvTermsPrivacy = (TextView)layout.findViewById(R.id.tv_terms_privacy);
        ivJoinType = (ImageView)layout.findViewById(R.id.iv_join_type);
        tvJoinMethod = (TextView)layout.findViewById(R.id.tv_join_method);
        btnFacebookLogin = (Button)layout.findViewById(R.id.btn_facebook_login);
        btnGoogleLogin = (Button)layout.findViewById(R.id.btn_google_login);
        btnEmailLogin = (Button)layout.findViewById(R.id.btn_email_login);
        llLoginDisplay = (LinearLayout)layout.findViewById(R.id.ll_login_display);
        llLoginNaviBack = (LinearLayout)findViewById(R.id.ll_login_navi_back);
        llSignupNaviBack = (LinearLayout)findViewById(R.id.ll_signup_navi_back);
        llNaviBack = (LinearLayout)findViewById(R.id.ll_navi_back);

    }

    /**
     * 체중, 혈압, 혈당 입력 부분 레이아웃 셋팅
     */

    public void setLayout(LinearLayout layout) {

        tvDate = (TextView)layout.findViewById(R.id.tv_date);

        tvTime = (TextView)layout.findViewById(R.id.tv_time);

        llDate = (LinearLayout)layout.findViewById(R.id.rl_date);

        llTime = (LinearLayout)layout.findViewById(R.id.ll_time);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("mm");
        String strTime = timeFormat.format(calendar.getTime());
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        if (hour > 12) {
            tvTime.setText("오후 " + (hour - 12) + ":" + strTime);
        } else {
            tvTime.setText("오전 " + (hour) + ":" + strTime);

        }

        String strMeasureDt = ManagerUtil.getCurrentDate().replaceAll("/", "");
        String strMeasureTime = ManagerUtil.getCurrentTime().replaceAll("/", "");
        date = strMeasureDt;
        time = strMeasureTime;

        year = Integer.parseInt(strMeasureDt.substring(0, 4));
        month = Integer.parseInt(strMeasureDt.substring(4, 6)) - 1;
        day = Integer.parseInt(strMeasureDt.substring(6, 8));

        hour = Integer.parseInt(strMeasureTime.substring(0, 2));
        minute = Integer.parseInt(strMeasureTime.substring(2, 4));

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BaseActivity.this)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   strMeasureDt + strMeasureTime + "00");
        tvDate.setText(date[0]);
        tvTime.setText(ManagerUtil.convertTimeFormat(strMeasureTime));

        llDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomDateDialog dateDialog = new CustomDateDialog(v.getContext(), year, month, day);
                dateDialog.show();
            }
        });

        llTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomTimeDialog timeDialog = new CustomTimeDialog(v.getContext(), hour, minute);
                timeDialog.show();
            }
        });
    }

    public void receiveMessage(int year, int month, int day) {

        this.year = year;
        this.month = (month - 1);
        this.day = day;

        String strMeasureDt = String.format("%02d", year) + String.format("%02d", month) + String.format("%02d", day);
        String strMeasureTime = "000000";
        date = strMeasureDt;

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BaseActivity.this)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   strMeasureDt + strMeasureTime + "00");
        tvDate.setText(date[0]);
    }

    public void receiveTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        String strMeasureTime = String.format("%02d", hour) + String.format("%02d", minute);
        time = strMeasureTime;

        tvTime.setText(ManagerUtil.convertTimeFormat(strMeasureTime));
        //        if (hour > 12) {
        //            tvTime.setText("오후 " + (hour - 12) + ":" + minute);
        //        } else {
        //            tvTime.setText("오전 " + hour + ":" + minute);
        //        }
    }

    /**
     * 언어 설정에 따른 약관표시
     *
     * @param context
     */
    public void LanguageSpecificTerms(final Context context) {
        //언어 설정
        tvTerms.setText(R.string.terms_text);
        tvTermsService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SettingTermsContentActivity.class);
                intent.putExtra("webViewSeq", "1");
                context.startActivity(intent);

            }
        });
        tvTermsPrivacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingTermsContentActivity.class);
                intent.putExtra("webViewSeq", "2");
                context.startActivity(intent);
            }
        });
    }

    /**
     * 약관 spanable 세팅
     *
     * @param context
     * @param serviceStart 서비스 이용 약관 시작 Position
     * @param serviceend 서비스 이용 약관 끝 Position
     * @param personalDataStart 개인정보 수집 약관 시작 Position
     * @param personalDataEnd 개인정보 수집 약관 끝 Position
     */
    private void setTermSpanble(final Context context,
                                int serviceStart,
                                int serviceend,
                                int personalDataStart,
                                int personalDataEnd) {
        SpannableString styledString = new SpannableString(context.getResources().getString(R.string.terms_text));
        ClickableSpan serviceTermSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, SettingTermsContentActivity.class);
                intent.putExtra("webViewSeq", "1");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.color_0246a6));
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan personalDataTermSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, SettingTermsContentActivity.class);
                intent.putExtra("webViewSeq", "2");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.color_0246a6));
                ds.setUnderlineText(true);
            }

        };
        styledString.setSpan(serviceTermSpan, serviceStart, serviceend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledString.setSpan(personalDataTermSpan,
                             personalDataStart,
                             personalDataEnd,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTerms.setText(styledString);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }

    }
}
