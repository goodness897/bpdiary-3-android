package kr.co.openit.bpdiary.activity.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.databinding.LayoutMainReportBinding;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * Created by srpark on 2017-01-23.
 */

public class ReportMainActivity extends BaseActivity {

    private LayoutMainReportBinding binding;

    private int reportViewNum = 2000;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsUtil.sendScene(ReportMainActivity.this, "3_레포트 메뉴");

        binding = DataBindingUtil.setContentView(this, R.layout.layout_main_report);
        initToolbar(getString(R.string.main_navigation_report));
        context = ReportMainActivity.this;
        reportViewNum = getIntent().getIntExtra(ManagerConstants.RequestParamName.REPORT_VIEW_NUM, reportViewNum);
        setLayout();
    }

    private void setLayout() {
        binding.toolbar.llNaviBack.setVisibility(View.GONE);
        binding.llVariationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent();
                    if ((reportViewNum % 2) != 0) {
                        reportViewNum += 1;
                        intent.putExtra(ManagerConstants.RequestParamName.REPORT_VIEW_NUM, reportViewNum);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        binding.llAverageValueBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if ((reportViewNum % 2) != 1) {
                    reportViewNum += 1;
                }
                intent.putExtra(ManagerConstants.RequestParamName.REPORT_VIEW_NUM, reportViewNum);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
