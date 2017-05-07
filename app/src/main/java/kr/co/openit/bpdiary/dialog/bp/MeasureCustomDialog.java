package kr.co.openit.bpdiary.dialog.bp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.BPMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.Language;
import kr.co.openit.bpdiary.customview.CustomCircleView;
import kr.co.openit.bpdiary.interfaces.bp.MeasureInterface;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class MeasureCustomDialog extends Dialog implements android.view.View.OnClickListener {

    private ViewPager mPager;

    private ManualPagerAdapter mAdapter;

    private final Context context;

    private CustomCircleView imgFirst;

    private CustomCircleView imgSecond;

    private CustomCircleView imgThird;

    private Button btnOk;

    private Button btnClose;

    private int pagePosition;

    private MeasureInterface measureInterface;

    /**
     * 생성자
     *
     * @param context
     */
    public MeasureCustomDialog(Context context, MeasureInterface defaultConfig) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.measureInterface = defaultConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_measure);

        initView();
    }

    private void initView() {

        imgFirst = (CustomCircleView)findViewById(R.id.img_first);

        imgSecond = (CustomCircleView)findViewById(R.id.img_second);

        imgThird = (CustomCircleView)findViewById(R.id.img_third);

        btnOk = (Button)findViewById(R.id.btn_ok);

        btnClose = (Button)findViewById(R.id.btn_close);

        mPager = (ViewPager)findViewById(R.id.main_pager);

        mAdapter = new ManualPagerAdapter();

        mPager.setAdapter(mAdapter);

        btnOk.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                pagePosition = position;
                if (PreferenceUtil.getLanguage(context).equals(Language.KOR)) {
                    //                    music.stop();
                    if (position == 0) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 1");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        btnOk.setText(context.getResources().getString(R.string.common_txt_next));
                    } else if (position == 1) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 2");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        btnOk.setText(context.getResources().getString(R.string.common_txt_next));
                    } else if (position == 2) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 3");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        btnOk.setVisibility(View.GONE);
                    }
                    //                    playMusic();
                } else {
                    if (position == 0) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 1");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        btnOk.setText(context.getResources().getString(R.string.common_txt_next));
                    } else if (position == 1) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 2");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        btnOk.setText(context.getResources().getString(R.string.common_txt_next));
                    } else if (position == 2) {
                        AnalyticsUtil.sendScene((Activity) context, "3_혈압 자동측정안내 3");
                        imgFirst.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgSecond.setCircleColor(context.getResources().getColor(R.color.color_f3e7da));
                        imgThird.setCircleColor(context.getResources().getColor(R.color.color_f2521f));
                        btnOk.setText(context.getResources().getString(R.string.bp_guide_btn));
                        btnOk.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_ok:
                if (pagePosition == 0) {
                    int lCount = mPager.getCurrentItem();
                    if (lCount == 2) {
                    } else {
                        mPager.setCurrentItem(++lCount);
                    }
                    break;
                } else if (pagePosition == 1) {
                    int lCount = mPager.getCurrentItem();
                    if (lCount == 2) {
                    } else {
                        mPager.setCurrentItem(++lCount);
                    }
                    break;
                } else {
                    onBackPressed();
                }
                break;
            case R.id.btn_close:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public class ManualPagerAdapter extends PagerAdapter {

        private final int IMGID[] = {R.layout.layout_dialog_measure_slide1,
                                     R.layout.layout_dialog_measure_slide2,
                                     R.layout.layout_dialog_measure_slide3};

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewPager pager = (ViewPager)container;

            LayoutInflater mInflater = LayoutInflater.from(pager.getContext());
            View mView = mInflater.inflate(IMGID[position], null);
            pager.addView(mView);

            return mView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            ((ViewPager)container).removeView((View)view);
        }

        @Override
        public int getCount() {
            return IMGID.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
