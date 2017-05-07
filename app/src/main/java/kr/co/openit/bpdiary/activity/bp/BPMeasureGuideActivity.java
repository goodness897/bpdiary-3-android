package kr.co.openit.bpdiary.activity.bp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;

public class BPMeasureGuideActivity extends NonMeasureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_guide);

        AnalyticsUtil.sendScene(BPMeasureGuideActivity.this, "3_혈압 측정안내 팝업");

        Button btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
