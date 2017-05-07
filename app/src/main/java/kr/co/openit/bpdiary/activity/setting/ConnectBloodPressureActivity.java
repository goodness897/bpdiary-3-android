package kr.co.openit.bpdiary.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;

public class ConnectBloodPressureActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_blood_pressure);

        initToolbar(getString(R.string.setting_connect_blood_pressure));
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
    }
}
