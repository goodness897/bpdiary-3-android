package kr.co.openit.bpdiary.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class SettingProfileActivity extends BaseActivity {

    private MyProfileListAdapter mAdapter;

    private Map<String, String> map;

    private ListView lvProfile;

    private String[] lan;

    private List<String> mList;

    private LinearLayout llEmptyView;

    private LinearLayout llAds;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);
        initToolbar(getString(R.string.setting_activity_profile));

        AnalyticsUtil.sendScene(SettingProfileActivity.this, "3_M 프로필");

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(SettingProfileActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        lvProfile = (ListView) findViewById(R.id.lv_profile);

        lan = getResources().getStringArray(R.array.profile);

        lvProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0: // 이름
                        intent = new Intent(SettingProfileActivity.this, ProfileNameActivity.class);
                        startActivity(intent);
                        break;
                    case 1: // 생년월일
                        intent = new Intent(SettingProfileActivity.this, ProfileBirthActivity.class);
                        startActivity(intent);
                        break;
                    case 2: // 성별
                        intent = new Intent(SettingProfileActivity.this, ProfileGenderActivity.class);
                        startActivity(intent);
                        break;
                    case 3: // 키
                        intent = new Intent(SettingProfileActivity.this, ProfileTallActivity.class);
                        startActivity(intent);
                        break;
                    case 4: // 비밀번호 (구글 페북 가입시 사라짐)
                        if (!PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)
                                && !PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
                            intent = new Intent(SettingProfileActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(SettingProfileActivity.this, ProfileGlucoseActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 5: //당뇨관리
                        if (!PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)
                                && !PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
                            intent = new Intent(SettingProfileActivity.this, ProfileGlucoseActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(SettingProfileActivity.this, ProfileAccountActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 6: //계정

                        if (!PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)
                                && !PreferenceUtil.getLoginType(SettingProfileActivity.this)
                                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
                            intent = new Intent(SettingProfileActivity.this, ProfileAccountActivity.class);
                            startActivity(intent);
                        } else {
                        }
                        break;

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String userName = PreferenceUtil.getDecFirstName(SettingProfileActivity.this) + " "
                + PreferenceUtil.getDecLastName(SettingProfileActivity.this);
        String birth = PreferenceUtil.getDecDayOfBirth(SettingProfileActivity.this).replace("/", "");
        String transBirth = "";
        String strMeasureDt = "";
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date s = transFormat.parse(birth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            transBirth = format.format(s);
            Log.d("trans", transBirth);
            strMeasureDt = transBirth.replaceAll("/", "");

            String[] date =
                    ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(SettingProfileActivity.this)),
                            ManagerUtil.ShowFormatPosition.SECOND,
                            true,
                            "/",
                            ":",
                            "yyyyMMddHHmmss",
                            strMeasureDt + "000000");

            transBirth = date[0];

        } catch (Exception e) {
        }

        String height = "";
        String gender = PreferenceUtil.getGender(SettingProfileActivity.this);
        //키 단위가 feet이면
        if (PreferenceUtil.getHeightUnit(SettingProfileActivity.this).equals(ManagerConstants.Unit.FT_IN)) {
            String feetHeight =
                    ManagerUtil.inchToFeetInch(ManagerUtil.cmToInch(PreferenceUtil.getHeight(SettingProfileActivity.this)));
            //            String[] arrayHeight = feetHeight.split("\\.");
            //            StringBuilder sb = new StringBuilder();
            //            if (arrayHeight.length > 2) {
            //                height = sb.append(arrayHeight[0]).append(".").append(arrayHeight[1]).toString()
            //                        + getString(R.string.tall_feet);
            //            } else {
            //                height = sb.append(arrayHeight[0]).toString() + getString(R.string.tall_feet);
            height = feetHeight + getString(R.string.tall_feet);

        } else {
            height = PreferenceUtil.getHeight(SettingProfileActivity.this) + getString(R.string.tall_cm);
        }
        String email = PreferenceUtil.getDecEmail(SettingProfileActivity.this);
        boolean usingBloodGlucose = PreferenceUtil.getUsingBloodGlucose(SettingProfileActivity.this);

        User user = new User(userName,
                transBirth,
                gender.equals(ManagerConstants.Gender.FEMALE) ? getString(R.string.common_enter_profile_woman)
                        : getString(R.string.common_enter_profile_man),
                height,
                getString(R.string.profile_convert),
                usingBloodGlucose ? getString(R.string.common_profile_main_use)
                        : getString(R.string.common_profile_main_not_use),
                email);

        map = new LinkedHashMap<>();
        mList = new ArrayList<String>();
        map.put(lan[0], user.getName());
        mList.add(user.getName());
        map.put(lan[1], user.getBirthDate());
        mList.add(user.getBirthDate());
        map.put(lan[2], user.getGender());
        mList.add(user.getGender());
        Log.d("TEST", "gender: " + user.getGender());
        map.put(lan[3], user.getHeight());
        mList.add(user.getHeight());
        // 로그인 타입이 구글이나 페이스북이 아닐 시 비밀번호 수정 페이지 보임
        if (!PreferenceUtil.getLoginType(SettingProfileActivity.this)
                .equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)
                && !PreferenceUtil.getLoginType(SettingProfileActivity.this)
                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
            map.put(lan[4], user.getPassword());
            mList.add(user.getPassword());
        }
        map.put(lan[5], user.getIsManageBloodSugar());
        mList.add(user.getIsManageBloodSugar());

        map.put(lan[6], user.getAccountEmail());
        mList.add(user.getAccountEmail());

        mAdapter = new MyProfileListAdapter(this, map, (ArrayList<String>) mList);
        lvProfile.setAdapter(mAdapter);
    }

    public class MyProfileListAdapter extends BaseAdapter {

        Map<String, String> list = new HashMap<>();

        ArrayList<String> mList;

        public MyProfileListAdapter(Context context, Map<String, String> list, ArrayList<String> mList) {
            this.list = list;
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView =
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile_list, parent, false);
            }

            TextView titleView = (TextView) convertView.findViewById(R.id.tv_list_title);
            TextView userView = (TextView) convertView.findViewById(R.id.tv_user_info);
            ImageView mailView = (ImageView) convertView.findViewById(R.id.iv_login_type);
            mailView.setVisibility(View.GONE);
            titleView.setText((String) map.keySet().toArray()[position]);

            if ((map.keySet().toArray()[position]).equals(getString(R.string.setting_activity_password))) {
                userView.setText(getString(R.string.profile_convert));
            } else if ((map.keySet().toArray()[position]).equals(getString(R.string.common_txt_account))) {
                mailView.setVisibility(View.VISIBLE);
                if (PreferenceUtil.getLoginType(SettingProfileActivity.this)
                        .equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)) {
                    mailView.setBackgroundResource(R.drawable.ic_ex_menu_sns_facebook);
                } else if (PreferenceUtil.getLoginType(SettingProfileActivity.this)
                        .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
                    mailView.setBackgroundResource(R.drawable.ic_ex_menu_sns_google);
                } else {
                    mailView.setBackgroundResource(R.drawable.ic_ex_menu_sns_email);
                }
                userView.setText(mList.get(position));
            } else {
                userView.setText(mList.get(position));
            }

            return convertView;
        }

    }

    public class User {

        private String name;

        private String birthDate;

        private String gender;

        private String height;

        private String password;

        private String isManageBloodSugar;

        private String accountEmail;

        public User(String name,
                    String birthDate,
                    String gender,
                    String height,
                    String password,
                    String isManageBloodSugar,
                    String accountEmail) {
            this.name = name;
            this.birthDate = birthDate;
            this.gender = gender;
            this.height = height;
            this.password = password;
            this.isManageBloodSugar = isManageBloodSugar;
            this.accountEmail = accountEmail;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIsManageBloodSugar() {
            return isManageBloodSugar;
        }

        public void setIsManageBloodSugar(String isManageBloodSugar) {
            this.isManageBloodSugar = isManageBloodSugar;
        }

        public String getAccountEmail() {
            return accountEmail;
        }

        public void setAccountEmail(String accountEmail) {
            this.accountEmail = accountEmail;
        }
    }
}
