package kr.co.openit.bpdiary.activity.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class LanguageActivity extends BaseActivity {

    private LinearLayout mLLEmptyView;

    /**
     * 영어
     */
    private RelativeLayout mLLEnglish;

    private ImageView ivCheckEnglish;

    /**
     * 한국어
     */
    private RelativeLayout mLLKorean;

    private ImageView ivCheckKorean;

    /**
     * 스페인어
     */
    private RelativeLayout mLLSpanish;

    private ImageView ivCheckSpanish;

    /**
     * 일본어
     */
    private RelativeLayout mLLJapanese;

    private ImageView ivCheckJapanese;

    /**
     * 독일어
     */
    private RelativeLayout mLLGerman;

    private ImageView ivCheckGerman;

    /**
     * 러시아어
     */
    private RelativeLayout mLLRussian;

    private ImageView ivCheckRussian;

    /**
     * 프랑스어
     */
    private RelativeLayout mLLFrench;

    private ImageView ivCheckFrench;

    /**
     * 체코어
     */
    private RelativeLayout mLLCzech;

    private ImageView ivCheckCzech;

    /**
     * 포르투갈어
     */
    private RelativeLayout mLLPortuguese;

    private ImageView ivCheckPortuguese;

    /**
     * 이태리어
     */
    private RelativeLayout mLLItalian;

    private ImageView ivCheckItalian;

    /**
     * 인도어
     */
    private RelativeLayout mLLHindi;

    private ImageView ivCheckHindi;

    /**
     * 중국어 간체
     */
    private RelativeLayout mLLSimplifiedChinese;

    private ImageView ivCheckSimplifiedChinese;

    /**
     * 중국어 번체
     */
    private RelativeLayout mLLTraditionalChinese;

    private ImageView ivCheckTraditionalChinese;

    /**
     * 현재 언어
     */

    private CustomProgressDialog mCustomProgressDialog = null;

    private String strLanguage = "";

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        AnalyticsUtil.sendScene(LanguageActivity.this, "3_M 언어");

        initToolbar(getString(R.string.setting_activity_language));

        context = LanguageActivity.this;

        initView();

        mCustomProgressDialog = new CustomProgressDialog(context);
        mCustomProgressDialog.setCancelable(false);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(LanguageActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        strLanguage = PreferenceUtil.getLanguage(LanguageActivity.this);

        View v = new View(this);
        v.setBackgroundColor(Color.rgb(239, 235, 230));
        v.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1));

        if (strLanguage.equals(ManagerConstants.Language.ENG)) {
            ivCheckEnglish.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.KOR)) {
            ivCheckKorean.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.SPN)) {
            ivCheckSpanish.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.JPN)) {
            ivCheckJapanese.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.GER)) {
            ivCheckGerman.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.RUS)) {
            ivCheckRussian.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.FRN)) {
            ivCheckFrench.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.CZE)) {
            ivCheckCzech.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.POR)) {
            ivCheckPortuguese.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.ITA)) {
            ivCheckItalian.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.IND)) {
            ivCheckHindi.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.TPE)) {
            ivCheckSimplifiedChinese.setVisibility(View.VISIBLE);
        } else if (strLanguage.equals(ManagerConstants.Language.CHN)) {
            ivCheckTraditionalChinese.setVisibility(View.VISIBLE);
        }

        mLLEnglish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.ENG);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.ENG, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLKorean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.KOR);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.KOR, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLSpanish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.SPN);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.SPN, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLJapanese.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
                PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.JPN);
                PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                ManagerUtil.setLocale(ManagerConstants.Language.JPN, LanguageActivity.this);
                setResult(RESULT_OK);
                finish();
            }
        });

        mLLGerman.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.GER);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.GER, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLRussian.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.RUS);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.RUS, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLFrench.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.FRN);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.FRN, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLCzech.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.CZE);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.CZE, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLPortuguese.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.POR);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.POR, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLItalian.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.ITA);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.ITA, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLHindi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.IND);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.IND, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLSimplifiedChinese.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.TPE);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.TPE, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mLLTraditionalChinese.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    showDialog();
                    PreferenceUtil.setLanguage(LanguageActivity.this, ManagerConstants.Language.CHN);
                    PreferenceUtil.setLanguageState(LanguageActivity.this, true);
                    ManagerUtil.setLocale(ManagerConstants.Language.CHN, LanguageActivity.this);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void initView() {
        mLLEnglish = (RelativeLayout)findViewById(R.id.ll_english);
        mLLKorean = (RelativeLayout)findViewById(R.id.ll_korean);
        mLLSpanish = (RelativeLayout)findViewById(R.id.ll_spanish);
        mLLJapanese = (RelativeLayout)findViewById(R.id.ll_japanese);
        mLLGerman = (RelativeLayout)findViewById(R.id.ll_german);
        mLLRussian = (RelativeLayout)findViewById(R.id.ll_russian);
        mLLFrench = (RelativeLayout)findViewById(R.id.ll_french);
        mLLCzech = (RelativeLayout)findViewById(R.id.ll_czech);
        mLLPortuguese = (RelativeLayout)findViewById(R.id.ll_portuguese);
        mLLItalian = (RelativeLayout)findViewById(R.id.ll_italian);
        mLLHindi = (RelativeLayout)findViewById(R.id.ll_hindi);
        mLLSimplifiedChinese = (RelativeLayout)findViewById(R.id.ll_simplified_chinese);
        mLLTraditionalChinese = (RelativeLayout)findViewById(R.id.ll_traditional_chinese);
        mLLEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        mLLEmptyView.setVisibility(View.VISIBLE);

        ivCheckEnglish = (ImageView)findViewById(R.id.iv_check_english);
        ivCheckKorean = (ImageView)findViewById(R.id.iv_check_korean);
        ivCheckSpanish = (ImageView)findViewById(R.id.iv_check_spanish);
        ivCheckJapanese = (ImageView)findViewById(R.id.iv_check_japanese);
        ivCheckGerman = (ImageView)findViewById(R.id.iv_check_german);
        ivCheckRussian = (ImageView)findViewById(R.id.iv_check_russian);
        ivCheckFrench = (ImageView)findViewById(R.id.iv_check_french);
        ivCheckCzech = (ImageView)findViewById(R.id.iv_check_czech);
        ivCheckPortuguese = (ImageView)findViewById(R.id.iv_check_portuguse);
        ivCheckItalian = (ImageView)findViewById(R.id.iv_check_italian);
        ivCheckHindi = (ImageView)findViewById(R.id.iv_check_hindi);
        ivCheckSimplifiedChinese = (ImageView)findViewById(R.id.iv_check_simplified_chinese);
        ivCheckTraditionalChinese = (ImageView)findViewById(R.id.iv_check_traditional_chinese);
    }

    private void showDialog() {
        if (!mCustomProgressDialog.isShowing()) {
            mCustomProgressDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomProgressDialog.dismiss();
    }
}
