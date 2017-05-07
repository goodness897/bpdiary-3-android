package kr.co.openit.bpdiary.activity.intro;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.customview.CustomCircleIndicator;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * Created by hslee on 2016-12-19.
 */

public class OnboardActivity extends BaseActivity implements View.OnClickListener {
    private static final int LOGIN_REQUEST = 1000;

    private static final int SIGN_UP_REQUEST = 2000;

    private ViewPager vpRanding;

    private OnboardPageAdapter pageAdapter;

    private Button loginBtn;

    private Button signUpBtn;

    private CustomCircleIndicator circleIndicator;

    private Context context;

    private AnimationDrawable onboardAnimationOne;

    private AnimationDrawable onboardAnimationTwo;

    private AnimationDrawable onboardAnimationThree;

    private ImageView ivOnboardOne;

    private ImageView ivOnboardTwo;

    private ImageView ivOnboardThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        actList.add(this);

        context = OnboardActivity.this;
        setLayout();

        pageAdapter = new OnboardPageAdapter(getLayoutInflater());

        vpRanding.setAdapter(pageAdapter);
        vpRanding.setOffscreenPageLimit(CommonConstants.RANDING_COUNT);
        vpRanding.setCurrentItem(0);
        vpRanding.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                circleIndicator.selectDot(position);
                animationStop();
                switch (position) {
                    case 0:
                        onboardAnimationOne.start();
                        AnalyticsUtil.sendScene(OnboardActivity.this, "3_시작하기 랜딩1");
                        break;
                    case 1:
                        onboardAnimationTwo.start();
                        AnalyticsUtil.sendScene(OnboardActivity.this, "3_시작하기 랜딩2");
                        break;
                    case 2:
                        onboardAnimationThree.start();
                        AnalyticsUtil.sendScene(OnboardActivity.this, "3_시작하기 랜딩3");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initIndicaotor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationStop();
    }

    private void animationStop() {
        if(onboardAnimationOne != null) {
            if (onboardAnimationOne.isRunning()) {
                onboardAnimationOne.stop();
            }
        }
        if(onboardAnimationTwo != null) {
            if (onboardAnimationTwo.isRunning()) {
                onboardAnimationTwo.stop();
            }
        }
        if(onboardAnimationThree != null) {
            if (onboardAnimationThree.isRunning()) {
                onboardAnimationThree.stop();
            }
        }
    }

    private void setLayout() {
        vpRanding = (ViewPager) findViewById(R.id.vp_randing);
        circleIndicator = (CustomCircleIndicator) findViewById(R.id.circle_indicator);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
    }

    /**
     * Indicator 초기화
     */
    private void initIndicaotor() {
        //원사이의 간격
        circleIndicator.setItemMargin(20);
        //애니메이션 속도
        circleIndicator.setAnimDuration(300);
        //indecator 생성
        circleIndicator.createDotPanel(CommonConstants.RANDING_COUNT,
                R.drawable.non_selected_item_dot,
                R.drawable.selected_item_dot);
    }

    @Override
    public void onClick(View view) {
        if (!ManagerUtil.isClicking()) {
            Intent intent;
            switch (view.getId()) {
                case R.id.login_btn:
                    intent = new Intent(context, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                    break;
                case R.id.sign_up_btn:
                    intent = new Intent(context, SignUpActivity.class);
                    startActivityForResult(intent, SIGN_UP_REQUEST);
                    break;
            }
        }
    }

    private class OnboardPageAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public OnboardPageAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return CommonConstants.RANDING_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View layout = null;

            switch (position) {
                case 0:
                    layout = inflater.inflate(R.layout.view_randing_one, null);
                    ivOnboardOne = (ImageView) layout.findViewById(R.id.iv_onboard_one);
                    onboardAnimationOne = (AnimationDrawable) ivOnboardOne.getBackground();
                    onboardAnimationOne.start();
                    break;
                case 1:
                    layout = inflater.inflate(R.layout.view_randing_two, null);
                    ivOnboardTwo = (ImageView) layout.findViewById(R.id.iv_onboard_two);
                    onboardAnimationTwo = (AnimationDrawable) ivOnboardTwo.getBackground();
                    break;
                case 2:
                    layout = inflater.inflate(R.layout.view_randing_three, null);
                    ivOnboardThree = (ImageView) layout.findViewById(R.id.iv_onboard_three);
                    onboardAnimationThree = (AnimationDrawable) ivOnboardThree.getBackground();
                    break;
            }
            container.addView(layout, position);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == LOGIN_REQUEST) {
//                finish();
//            } else if (requestCode == SIGN_UP_REQUEST) {
//                finish();
//            }
//        }
//    }
}
