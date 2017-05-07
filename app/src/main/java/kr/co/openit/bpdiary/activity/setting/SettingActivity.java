package kr.co.openit.bpdiary.activity.setting;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.ManagerUtil;

public class SettingActivity extends BaseActivity {

    MyGridViewAdpater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_setting);

        adapter = new MyGridViewAdpater();
        GridView gridView = (GridView) findViewById(R.id.gv_btn_main);

        ImageView imageButton = (ImageView) findViewById(R.id.btn_navi_back);
        imageButton.setVisibility(View.GONE);

        RelativeLayout profileLayout = (RelativeLayout) findViewById(R.id.rl_profile);
        profileLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(SettingActivity.this, SettingProfileActivity.class);
                    startActivity(intent);
                }

            }
        });
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_bluetooth, getResources().getString(R.string.setting_connect_device)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_shop, getResources().getString(R.string.setting_online_shop)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_target, getResources().getString(R.string.setting_activity_goal)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_alarm, getResources().getString(R.string.setting_activity_setting_alarm)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_data, getResources().getString(R.string.setting_activity_data)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_setting, getResources().getString(R.string.setting_activity_setting)));

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0: //연동기기 클릭
                        intent = new Intent(SettingActivity.this, ConnectDeviceActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(SettingActivity.this, OnlineShopActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(SettingActivity.this, NoticeActivity.class);
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(SettingActivity.this, AlarmActivity.class);
                        startActivity(intent);
                        break;

                    case 4:
                        intent = new Intent(SettingActivity.this, SettingGoalActivity.class);
                        startActivity(intent);
                        break;

                    case 5:
                        intent = new Intent(SettingActivity.this, LanguageActivity.class);
                        startActivity(intent);
                        break;

                }

            }
        });

    }

    private class MyGridViewAdpater extends BaseAdapter {

        List<ExtendData> list = new ArrayList();

        public void add(ExtendData extendData) {
            list.add(extendData);
            notifyDataSetChanged();

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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_grid_list, parent, false);

            }

            ExtendData extendData = list.get(position);

            TextView titleView = (TextView) convertView.findViewById(R.id.textView);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_facebook);

            titleView.setText(extendData.getTitle());

            imageView.setBackgroundResource(extendData.getResId());

            return convertView;
        }
    }

    private class ExtendData {

        private int resId;

        private String title;

        public ExtendData(int resId, String title) {
            this.resId = resId;
            this.title = title;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
