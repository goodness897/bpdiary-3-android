package kr.co.openit.bpdiary.activity.weight;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.utils.ManagerUtil;

public class WeightMeasureGuideActivity extends NonMeasureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ws_guide);

        Button btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });
    }
}
