package kr.co.openit.bpdiary.activity.glucose;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.BPGraphActivity;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.adapter.common.CommonListAdapter;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomCircleView;
import kr.co.openit.bpdiary.customview.GraphViewOneStick;
import kr.co.openit.bpdiary.customview.GraphViewOneStickForGlucose;
import kr.co.openit.bpdiary.databinding.ActivityGlucoseGraphBinding;
import kr.co.openit.bpdiary.dialog.DefaultChoiceDialog;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IChoiceDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.GlucoseGraphModel;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by hslee on 2017-01-06.
 */

public class GlucoseGraphActivity extends NonMeasureActivity {

    private ActivityGlucoseGraphBinding binding;

    private GlucoseGraphModel glucoseGraphModel;

    /**
     * 상세보기 리턴 코드
     */
    private static final int VIEW_RESULT_CODE = 0;

    /**
     * 메모 리턴 코드
     */
    private static final int MEMO_RESULT_CODE = 1000;

    /**
     * 기간별 타입
     */
    private int nPeriodType = ManagerConstants.PeriodType.PERIOD_ALL;

    /**
     * 화면 표시 타입
     */
    private int nListViewType = ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH;

    /**
     * BloodPressureService
     */
    private GlucoseService glucoseService;

    /**
     * 혈압 측정 데이터를 DB에서 조회하는 AsyncTask
     */
    private SearchGlucoseDBSync sbpDBSync;

    /**
     * 측정 결과 데이터(리스트)
     */
    private List<Map<String, String>> listResultListData;

    /**
     * 측정 결과 데이터(그래프)
     */
    private List<Map<String, String>> listResultGraphData;

    /**
     * 측정 결과 데이터(그래프 식전만)
     */
    private List<Map<String, String>> listResultBeforeGraphData;

    /**
     * 측정 결과 데이터(그래프 식후만)
     */
    private List<Map<String, String>> listResultAfterGraphData;

    /**
     * 리스트 아답터
     */
    private GlucoseResultListAdapter glucoseListAdapter;

    /**
     * 그래프
     */
    private GraphViewOneStick graphUtil;

    private GraphViewOneStickForGlucose graphGlucoseUtil;

    private Map<String, String> mapData;

    private Calendar calendar;

    private Calendar calculationDate;

    private boolean listGraphFlag = true;

    private LinearLayout llAds;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (VIEW_RESULT_CODE == requestCode) {

            if (resultCode == RESULT_OK) {

                /**
                 * DB 조회
                 */
                sbpDBSync = new SearchGlucoseDBSync();
                sbpDBSync.execute();

            }
        } else if (MEMO_RESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                if (intent.getStringExtra("position") != null) {
                    int position = Integer.parseInt(intent.getStringExtra("position"));
                    String memo = intent.getStringExtra("memo");
                    final Map pMap = (Map)listResultListData.get(position);
                    pMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, memo);
                    glucoseListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_glucose_graph);
        glucoseGraphModel = new GlucoseGraphModel();
        context = GlucoseGraphActivity.this;

        /**
         * 광고
         */
        llAds = (LinearLayout)findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(GlucoseGraphActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        /**
         * blood pressure service
         */
        glucoseService = new GlucoseService(GlucoseGraphActivity.this);
        setDateInit();
        /**
         * DB 조회
         */
        sbpDBSync = new SearchGlucoseDBSync();
        sbpDBSync.execute();

        glucoseGraphModel.setScreenType(true);
        glucoseGraphModel.setPeriodType(4);
        binding.rlAll.setSelected(true);
        binding.setGlucoseGraph(glucoseGraphModel);

        binding.llBeforeMeal.setSelected(true);
        binding.llAfterMeal.setSelected(true);
        binding.llBeforeValue.setVisibility(View.VISIBLE);
        binding.llAfterValue.setVisibility(View.VISIBLE);

        initToolbar(getString(R.string.main_navigation_glucose) + " " + getString(R.string.common_txt_graph));

        binding.tvAvg.setText(getString(R.string.common_txt_all) + " " + getString(R.string.common_txt_avg));
        binding.toolbar.llGraphList.setVisibility(View.VISIBLE);
        binding.lvGlucoseList.setSelector(getResources().getDrawable(R.drawable.selector_graph_list_item));

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
            binding.tvGlucoseUnit.setText(ManagerConstants.Unit.MGDL);
        } else {
            binding.tvGlucoseUnit.setText(ManagerConstants.Unit.MMOL);
        }

        binding.rlAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    glucoseGraphModel.setPeriodType(4);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(true);

                    binding.llLeft.setVisibility(View.INVISIBLE);
                    binding.llRight.setVisibility(View.INVISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_all) + " "
                                          + getString(R.string.common_txt_avg));
                    nPeriodType = 4;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlToday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    glucoseGraphModel.setPeriodType(0);
                    binding.rlToday.setSelected(true);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_today) + " "
                                          + getString(R.string.common_txt_avg));
                    nPeriodType = 0;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    glucoseGraphModel.setPeriodType(1);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(true);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_week) + " "
                                          + getString(R.string.common_txt_avg));
                    nPeriodType = 1;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    glucoseGraphModel.setPeriodType(2);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(true);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_month) + " "
                                          + getString(R.string.common_txt_avg));
                    nPeriodType = 2;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    glucoseGraphModel.setPeriodType(3);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(true);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_year) + " "
                                          + getString(R.string.common_txt_avg));
                    nPeriodType = 3;
                    setSearchDate(0, false);
                }
            }
        });

        binding.llLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    setSearchDate(-1, false);
                }
            }
        });

        binding.llRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    setSearchDate(1, false);
                }
            }
        });

        binding.llBeforeMeal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (binding.llBeforeMeal.isSelected()) {
                        binding.llBeforeMeal.setSelected(false);
                        binding.llBeforeValue.setVisibility(View.INVISIBLE);
                    } else {
                        binding.llBeforeMeal.setSelected(true);
                        binding.llBeforeValue.setVisibility(View.VISIBLE);
                    }
                    displayResultGraph();
                }
            }
        });

        binding.llAfterMeal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (binding.llAfterMeal.isSelected()) {
                        binding.llAfterMeal.setSelected(false);
                        binding.llAfterValue.setVisibility(View.INVISIBLE);
                    } else {
                        binding.llAfterMeal.setSelected(true);
                        binding.llAfterValue.setVisibility(View.VISIBLE);
                    }
                    displayResultGraph();
                }
            }
        });

        binding.toolbar.llGraphList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (listGraphFlag) {
                        switchTabSide(ManagerConstants.ResultViewType.RESULT_VIEW_LIST);
                    } else {
                        switchTabSide(ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH);
                    }
                }
            }
        });

        binding.lvGlucoseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!ManagerUtil.isClicking()) {

                    final Map pMap = (Map)parent.getItemAtPosition(position);

                    if (pMap != null) {
                        if (pMap.get("isOpen").equals("close")) {

                            listResultListData.get(position).put("isOpen", "open");

                        } else if (pMap.get("isOpen").equals("open")) {

                            listResultListData.get(position).put("isOpen", "close");

                        } else {
                            //nothing
                        }

                        glucoseListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // 가로 scroll 삭제
        binding.lvGlucoseList.setHorizontalScrollBarEnabled(false);
    }

    /**
     * 상단 탭 메뉴 UI 변경
     */
    private void switchTabSide(int nIndex) {
        binding.llNoData.setVisibility(View.GONE);
        setDateInit();
        if (ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH == nIndex) {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvGlucoseList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_glucose) + " "
                                            + getString(R.string.common_txt_graph));
            listGraphFlag = true;
        } else if (ManagerConstants.ResultViewType.RESULT_VIEW_LIST == nIndex) {
            binding.rlGraphAll.setVisibility(View.GONE);
            binding.lvGlucoseList.setVisibility(View.VISIBLE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_graph));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_graph_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_glucose) + " "
                                            + getString(R.string.common_txt_list));
            listGraphFlag = false;
        } else {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvGlucoseList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_glucose) + " "
                                            + getString(R.string.common_txt_graph));
            listGraphFlag = true;
        }

        nListViewType = nIndex;

        /**
         * DB 조회
         */
        sbpDBSync = new SearchGlucoseDBSync();
        sbpDBSync.execute();

    }

    private void setDateInit() {
        calendar = Calendar.getInstance();
        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   DateUtil.getLongToStringDate("yyyyMMddHHmmss",
                                                                                calendar.getTimeInMillis()));
        binding.tvDate.setText(date[0]);
    }

    private void setSearchDate(int diffDate, boolean isDateInit) {
        if (isDateInit) {
            setDateInit();
        }
        String[] nextStDate;
        String[] calculationStDate = null;
        if (nPeriodType == 0) {
            calendar.add(Calendar.DATE, diffDate);
        } else if (nPeriodType == 1) {
            calendar.add(Calendar.DATE, diffDate * 7);
            calculationDate = Calendar.getInstance();
            calculationDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            calculationDate.add(Calendar.DATE, -6);
            calculationStDate =
                              ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                           ManagerUtil.ShowFormatPosition.SECOND,
                                                           true,
                                                           "/",
                                                           ":",
                                                           "yyyyMMddHHmmss",
                                                           DateUtil.getLongToStringDate("yyyyMMddHHmmss",
                                                                                        calculationDate.getTimeInMillis()));
        } else if (nPeriodType == 2) {
            calendar.add(Calendar.MONTH, diffDate);
        } else if (nPeriodType == 3) {
            calendar.add(Calendar.YEAR, diffDate);
        }
        ManagerUtil.Localization getLocalization =
                                                 ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this));
        nextStDate = ManagerUtil.getDateCharacter(getLocalization,
                                                  ManagerUtil.ShowFormatPosition.SECOND,
                                                  true,
                                                  "/",
                                                  ":",
                                                  "yyyyMMddHHmmss",
                                                  DateUtil.getLongToStringDate("yyyyMMddHHmmss",
                                                                               calendar.getTimeInMillis()));
        if (nPeriodType == 0) {
            binding.tvDate.setText(nextStDate[0]);
        } else if (nPeriodType == 1) {
            if (calculationStDate != null) {
                binding.tvDate.setText(calculationStDate[0] + " ~ " + nextStDate[0]);
            }
        } else if (nPeriodType == 2) {
            int max = calendar.getActualMaximum(Calendar.DATE);
            int min = calendar.getActualMinimum(Calendar.DATE);
            if (getLocalization == ManagerUtil.Localization.TYPE_01) {
                binding.tvDate.setText(nextStDate[0].substring(0, 8) + String.format("%02d", min)
                                       + " ~ "
                                       + nextStDate[0].substring(0, 8)
                                       + String.format("%02d", max));
            } else if (getLocalization == ManagerUtil.Localization.TYPE_02) {
                binding.tvDate.setText(nextStDate[0].substring(0, 4) + String.format("%02d", min)
                                       + nextStDate[0].substring(6, 11)
                                       + " ~ "
                                       + nextStDate[0].substring(0, 4)
                                       + String.format("%02d", max)
                                       + nextStDate[0].substring(6, 11));
            } else {
                binding.tvDate.setText(String.format("%02d", min) + nextStDate[0].substring(2, 10)
                                       + " ~ "
                                       + String.format("%02d", max)
                                       + nextStDate[0].substring(2, 10));
            }
        } else if (nPeriodType == 3) {
            String year = String.format("%04d", calendar.get(Calendar.YEAR));
            if (getLocalization == ManagerUtil.Localization.TYPE_01) {
                binding.tvDate.setText(nextStDate[0].substring(0, 5) + String.format("%02d", 1)
                                       + " ~ "
                                       + nextStDate[0].substring(0, 5)
                                       + String.format("%02d", 12));
            } else if (getLocalization == ManagerUtil.Localization.TYPE_02) {
                binding.tvDate.setText(ManagerUtil.getMonth(String.format("%02d", 1)) + "/"
                                       + year
                                       + " ~ "
                                       + ManagerUtil.getMonth(String.format("%02d", 12))
                                       + "/"
                                       + year);
            } else {
                binding.tvDate.setText(String.format("%02d", 1) + "/"
                                       + year
                                       + " ~ "
                                       + String.format("%02d", 12)
                                       + "/"
                                       + year);
            }
        } else if (nPeriodType == 4) {
            setDateInit();
        }
        sbpDBSync = new SearchGlucoseDBSync();
        sbpDBSync.execute();
    }

    /**
     * 결과 표시(그래프)
     */
    private void displayResultGraph() {
        final GraphViewOneStick.CustomDate customDateType;
        final GraphViewOneStickForGlucose.CustomDate customDateType1;
        int yMax = 100;
        if (listResultGraphData != null) {
            if (nPeriodType == 0) {
                customDateType = GraphViewOneStick.CustomDate.TODAY;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.TODAY;
            } else if (nPeriodType == 1) {
                customDateType = GraphViewOneStick.CustomDate.WEEK;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.WEEK;
            } else if (nPeriodType == 2) {
                customDateType = GraphViewOneStick.CustomDate.MONTH;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.MONTH;
            } else if (nPeriodType == 3) {
                customDateType = GraphViewOneStick.CustomDate.YEAR;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.YEAR;
            } else if (nPeriodType == 4) {
                customDateType = GraphViewOneStick.CustomDate.ALL;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.ALL;
            } else {
                customDateType = GraphViewOneStick.CustomDate.TODAY;
                customDateType1 = GraphViewOneStickForGlucose.CustomDate.TODAY;
            }

            if (!listResultGraphData.isEmpty()) {

                Float fSumGlucoseBefore = 0f;
                Float fSumGlucoseAfter = 0f;
                int nCountBefore = 0;
                int nCountAfter = 0;

                for (int i = 0; i < listResultGraphData.size(); i++) {
                    float glucoseBefore = 0f;
                    float glucoseAfter = 0f;
                    if ((listResultGraphData.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE) != null
                         && !"0".equals(listResultGraphData.get(i)
                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE))
                         || (listResultGraphData.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER) != null
                             && !"0".equals(listResultGraphData.get(i)
                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER))))) {

                        if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                            if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(i)
                                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                glucoseBefore =
                                              Float.parseFloat(listResultGraphData.get(i)
                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE));

                                fSumGlucoseBefore += glucoseBefore;
                                nCountBefore++;
                            } else if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(i)
                                                                                                        .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                glucoseAfter =
                                             Float.parseFloat(listResultGraphData.get(i)
                                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE));

                                fSumGlucoseAfter += glucoseAfter;
                                nCountAfter++;
                            }

                        } else {
                            if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(i)
                                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                glucoseBefore =
                                              Float.parseFloat(listResultGraphData.get(i)
                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE));

                                fSumGlucoseBefore += glucoseBefore;
                                nCountBefore++;
                            } else if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(i)
                                                                                                        .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                glucoseAfter =
                                             Float.parseFloat(listResultGraphData.get(i)
                                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER));

                                fSumGlucoseAfter += glucoseAfter;
                                nCountAfter++;
                            } else if (ManagerConstants.EatType.GLUCOSE_ALL.equals(listResultGraphData.get(i)
                                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                glucoseBefore =
                                              Float.parseFloat(listResultGraphData.get(i)
                                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE));
                                glucoseAfter =
                                             Float.parseFloat(listResultGraphData.get(i)
                                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER));

                                fSumGlucoseBefore += glucoseBefore;
                                fSumGlucoseAfter += glucoseAfter;

                                nCountBefore++;
                                nCountAfter++;
                            }
                        }

                    }
                    if (yMax < glucoseBefore) {
                        if (glucoseBefore > glucoseAfter) {
                            yMax = (int)glucoseBefore;
                        }
                    }
                }

                if (nCountBefore < 1 && nCountAfter < 1) {
                    setAvgGraphData("0", "0");
                    setGraphData("0", "0", "");
                } else {

                    String strAvgGlucoseBefore = "0";
                    String strAvgGlucoseAfter = "0";

                    if (nPeriodType == 4) {
                        if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                            if (nCountBefore > 0) {
                                strAvgGlucoseBefore = String.format("%.1f", fSumGlucoseBefore / nCountBefore);
                            }
                            if (nCountAfter > 0) {
                                strAvgGlucoseAfter = String.format("%.1f", fSumGlucoseAfter / nCountAfter);
                            }
                        } else if (binding.llBeforeMeal.isSelected()) {
                            if (nCountBefore > 0) {
                                strAvgGlucoseBefore = String.format("%.1f", fSumGlucoseBefore / nCountBefore);
                            }
                        } else if (binding.llAfterMeal.isSelected()) {
                            if (nCountAfter > 0) {
                                strAvgGlucoseAfter = String.format("%.1f", fSumGlucoseAfter / nCountAfter);
                            }
                        } else {

                        }
                    } else {
                        if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                            strAvgGlucoseBefore = mapData.get("glucoseBefore");
                            strAvgGlucoseAfter = mapData.get("glucoseAfter");
                        } else if (binding.llBeforeMeal.isSelected()) {
                            strAvgGlucoseBefore = mapData.get("glucoseBefore");
                        } else if (binding.llAfterMeal.isSelected()) {
                            strAvgGlucoseAfter = mapData.get("glucoseAfter");
                        } else {
                        }
                    }
                    setAvgGraphData(strAvgGlucoseBefore, strAvgGlucoseAfter);
                }

            } else {
                setAvgGraphData("0", "0");
                setGraphData("0", "0", "");
            }

            // 그래프 표시
            graphUtil = new GraphViewOneStick(GlucoseGraphActivity.this, changeUI);
            graphGlucoseUtil = new GraphViewOneStickForGlucose(GlucoseGraphActivity.this, changeUI);
            if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
                yMax = yMax + 60;
            } else {
                yMax = 25;
            }
            final int finalYMax = yMax;

            binding.llGlucoseGraphView.post(new Runnable() {

                @Override
                public void run() {
                    if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                        binding.llGlucoseGraphView.removeAllViews();
                        binding.llGlucoseGraphView.addView(graphGlucoseUtil);
                        graphGlucoseUtil.setStayDataX(true);
                        graphGlucoseUtil.setCustomDataX(true);
                        graphGlucoseUtil.setBottomWidely(true);
                        graphGlucoseUtil.setCustomDataX(customDateType1);
                        graphGlucoseUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)));
                        graphGlucoseUtil.setInitialize(GraphViewOneStick.LINE,
                                                       7,
                                                       finalYMax,
                                                       0,
                                                       5,
                                                       0,
                                                       0,
                                                       binding.llGlucoseGraphView.getWidth(),
                                                       binding.llGlucoseGraphView.getHeight());

                        /**
                         * 측정 결과 데이터(그래프)
                         */
                        List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();
                        listResultAfterGraphData = new ArrayList<Map<String, String>>();
                        listResultBeforeGraphData = new ArrayList<Map<String, String>>();

                        if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                            tempList = listResultGraphData;
                        } else if (binding.llBeforeMeal.isSelected()) {
                            for (int i = 0; i < listResultGraphData.size(); i++) {
                                if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(i)
                                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    tempList.add(listResultGraphData.get(i));
                                    listResultBeforeGraphData.add(listResultGraphData.get(i));
                                }
                            }
                        } else if (binding.llAfterMeal.isSelected()) {
                            for (int i = 0; i < listResultGraphData.size(); i++) {
                                if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(i)
                                                                                                     .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    tempList.add(listResultGraphData.get(i));
                                    listResultAfterGraphData.add(listResultGraphData.get(i));
                                }
                            }
                        } else {

                        }

                        graphGlucoseUtil.addObjectList(tempList,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                       ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                                       Color.parseColor("#f3d785"),
                                                       R.drawable.blood_suger_grap_dot_blue,
                                                       R.drawable.blood_suger_grap_double_dot_blue,
                                                       R.drawable.blood_suger_grap_double_dot_blue,
                                                       R.drawable.blood_suger_grap_dot_orange,
                                                       R.drawable.blood_suger_grap_double_dot_orange,
                                                       R.drawable.blood_suger_grap_double_dot_orange);

                        graphGlucoseUtil.addObjectList(tempList,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                       ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                       ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                                       Color.parseColor("#f3d785"),
                                                       R.drawable.blood_suger_grap_dot_blue,
                                                       R.drawable.blood_suger_grap_double_dot_blue,
                                                       R.drawable.blood_suger_grap_double_dot_blue,
                                                       R.drawable.blood_suger_grap_dot_orange,
                                                       R.drawable.blood_suger_grap_double_dot_orange,
                                                       R.drawable.blood_suger_grap_double_dot_orange);
                    } else {
                        binding.llGlucoseGraphView.removeAllViews();
                        binding.llGlucoseGraphView.addView(graphUtil);
                        graphUtil.setStayDataX(true);
                        graphUtil.setCustomDataX(true);
                        graphUtil.setBottomWidely(true);
                        graphUtil.setCustomDataX(customDateType);
                        graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)));
                        graphUtil.setInitialize(GraphViewOneStick.LINE,
                                                7,
                                                finalYMax,
                                                0,
                                                5,
                                                0,
                                                0,
                                                binding.llGlucoseGraphView.getWidth(),
                                                binding.llGlucoseGraphView.getHeight());
                        if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#7cb3f0"),
                                                    R.drawable.blood_suger_grap_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue);

                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#f0a115"),
                                                    R.drawable.blood_suger_grap_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange);
                        } else if (binding.llBeforeMeal.isSelected()) {
                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#7cb3f0"),
                                                    R.drawable.blood_suger_grap_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue);

                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#7cb3f0"),
                                                    R.drawable.blood_suger_grap_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue);
                        } else if (binding.llAfterMeal.isSelected()) {
                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#f0a115"),
                                                    R.drawable.blood_suger_grap_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange);

                            graphUtil.addObjectList(listResultGraphData,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#f0a115"),
                                                    R.drawable.blood_suger_grap_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange,
                                                    R.drawable.blood_suger_grap_double_dot_orange);
                        } else {
                            graphUtil.addObjectList(listResultGraphData,
                                                    "1",
                                                    "1",
                                                    ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    Color.parseColor("#7cb3f0"),
                                                    R.drawable.blood_suger_grap_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue,
                                                    R.drawable.blood_suger_grap_double_dot_blue);
                        }
                    }
                }
            });
        }
    }

    private void setAvgGraphData(String strAvgGlucoseBefore, String strAvgGlucoseAfter) {
        strAvgGlucoseBefore = strAvgGlucoseBefore.replace(",", ".");
        strAvgGlucoseAfter = strAvgGlucoseAfter.replace(",", ".");
        if (binding.tvAvgGlucoseBefore != null) {
            if (strAvgGlucoseBefore.equals("0")) {
                binding.tvAvgGlucoseBefore.setText(String.format("%.1f", Float.parseFloat("0")));
            } else {
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
                    if (!strAvgGlucoseBefore.equals("0") && strAvgGlucoseBefore.contains(".")) {
                        binding.tvAvgGlucoseBefore.setText(String.format("%.1f", Float.parseFloat(strAvgGlucoseBefore))
                                                                 .replace(",", "."));
                    }
                } else {
                    binding.tvAvgGlucoseBefore.setText(String.format("%.1f", Float.parseFloat(strAvgGlucoseBefore))
                                                             .replace(",", "."));
                }
            }

        }
        if (binding.tvAvgGlucoseAfter != null) {
            if (strAvgGlucoseAfter.equals("0")) {
                binding.tvAvgGlucoseAfter.setText(String.format("%.1f", Float.parseFloat("0")));
            } else {
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
                    if (!strAvgGlucoseAfter.equals("0") && strAvgGlucoseAfter.contains(".")) {
                        binding.tvAvgGlucoseAfter.setText(String.format("%.1f", Float.parseFloat(strAvgGlucoseAfter))
                                                                .replace(",", "."));
                    }
                } else {
                    binding.tvAvgGlucoseAfter.setText(String.format("%.1f", Float.parseFloat(strAvgGlucoseAfter))
                                                            .replace(",", "."));
                }
            }
        }
    }

    private void setGraphData(String strGlucoseBefore, String strGlucoseAfter, String strDate) {
        if (binding.tvGlucoseBefore != null) {
            if (TextUtils.isEmpty(strGlucoseBefore)) {
                binding.tvGlucoseBefore.setText(String.format("%.1f", Float.parseFloat("0")));
            } else {
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
                    binding.tvGlucoseBefore.setText(String.format("%.1f", Float.parseFloat(strGlucoseBefore))
                                                          .replace(",", "."));
                } else {
                    binding.tvGlucoseBefore.setText(String.format("%.1f", Float.parseFloat(strGlucoseBefore))
                                                          .replace(",", "."));
                }
            }
        }
        if (binding.tvGlucoseAfter != null) {
            if (TextUtils.isEmpty(strGlucoseAfter)) {
                binding.tvGlucoseAfter.setText(String.format("%.1f", Float.parseFloat("0")));
            } else {
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))) {
                    binding.tvGlucoseAfter.setText(String.format("%.1f", Float.parseFloat(strGlucoseAfter))
                                                         .replace(",", "."));
                } else {
                    binding.tvGlucoseAfter.setText(String.format("%.1f", Float.parseFloat(strGlucoseAfter))
                                                         .replace(",", "."));
                }
            }
        }

        if (binding.tvGlucoseDate != null) {
            if (TextUtils.isEmpty(strDate)) {
                binding.tvGlucoseDate.setText("");
            } else {
                if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                    String[] date =
                                  ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                               ManagerUtil.ShowFormatPosition.SECOND,
                                                               true,
                                                               "/",
                                                               ":",
                                                               "yyyyMMddHHmmss",
                                                               strDate);
                    binding.tvGlucoseDate.setText(date[0] + " " + date[1].substring(0, 5));
                } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_DAY) {
                    String[] date =
                                  ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                               ManagerUtil.ShowFormatPosition.SECOND,
                                                               true,
                                                               "/",
                                                               ":",
                                                               "yyyyMMddHH",
                                                               strDate);
                    binding.tvGlucoseDate.setText(date[0] + " " + strDate.substring(8, 10) + ":00");
                } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_WEEK
                           || nPeriodType == ManagerConstants.PeriodType.PERIOD_MONTH) {
                    String[] date =
                                  ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                               ManagerUtil.ShowFormatPosition.SECOND,
                                                               true,
                                                               "/",
                                                               ":",
                                                               "yyyyMMdd",
                                                               strDate);
                    binding.tvGlucoseDate.setText(date[0]);
                } else {
                    String[] date =
                                  ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                               ManagerUtil.ShowFormatPosition.SECOND,
                                                               true,
                                                               "/",
                                                               ":",
                                                               "yyyyMM",
                                                               strDate);
                    binding.tvGlucoseDate.setText(date[0]);
                }
            }
        }
    }

    /**
     * 결과 표시(리스트)
     */
    private void displayResultList() {

        if (listResultListData != null) {
            if (listResultListData.size() == 0) {
                binding.llNoData.setVisibility(View.VISIBLE);
                binding.lvGlucoseList.setVisibility(View.GONE);
            } else {
                binding.llNoData.setVisibility(View.GONE);
                binding.lvGlucoseList.setVisibility(View.VISIBLE);
                glucoseListAdapter = new GlucoseResultListAdapter(GlucoseGraphActivity.this, listResultListData);
                binding.lvGlucoseList.setAdapter(glucoseListAdapter);
            }
        }
    }

    /**
     * 그래픽 컨트롤 변경하는 핸들러
     */
    private final Handler changeUI = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int index = -1;

            switch (msg.what) {
                case GraphViewOneStick.MESSAGE_WHAT_RESULT_TWO:

                    // 현재 인덱스를 저장

                    index = msg.getData().getInt(ManagerConstants.Graph.INDEX);

                    if (listResultGraphData.size() > 0) {
                        try {
                            binding.llGlucoseGraph.setVisibility(View.VISIBLE);
                            String strGlucoseBefore = "";
                            String strGlucoseAfter = "";
                            String date = listResultGraphData.get(index)
                                                             .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);

                            if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                                if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(index)
                                                                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    strGlucoseBefore =
                                                     listResultGraphData.get(index)
                                                                        .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                }
                                if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(index)
                                                                                                     .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    strGlucoseAfter =
                                                    listResultGraphData.get(index)
                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                }

                            } else {
                                if (ManagerConstants.EatType.GLUCOSE_ALL.equals(listResultGraphData.get(index)
                                                                                                   .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    strGlucoseBefore =
                                                     listResultGraphData.get(index)
                                                                        .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    strGlucoseAfter =
                                                    listResultGraphData.get(index)
                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER);
                                } else {
                                    if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(index)
                                                                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseBefore =
                                                         listResultGraphData.get(index)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    }
                                    if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(index)
                                                                                                         .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseAfter =
                                                        listResultGraphData.get(index)
                                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER);
                                    }
                                }
                            }

                            setGraphData(strGlucoseBefore, strGlucoseAfter, date);
                        } catch (Exception e) {
                            // nothing
                            setGraphData("0", "0", "");
                        }

                    } else {
                        setAvgGraphData(getString(R.string.common_txt_default), "0");
                        setGraphData(getString(R.string.common_txt_default), "0", "");
                    }

                    break;

                case GraphViewOneStickForGlucose.MESSAGE_WHAT_RESULT_TWO:

                    // 현재 인덱스를 저장

                    index = msg.getData().getInt(ManagerConstants.Graph.INDEX);

                    if (listResultGraphData.size() > 0) {
                        try {
                            binding.llGlucoseGraph.setVisibility(View.VISIBLE);
                            String strGlucoseBefore = "";
                            String strGlucoseAfter = "";
                            String date = "";
                            if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                                date = listResultGraphData.get(index)
                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);
                            } else if (binding.llBeforeMeal.isSelected()) {
                                date = listResultBeforeGraphData.get(index)
                                                                .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);
                            } else if (binding.llAfterMeal.isSelected()) {
                                date = listResultAfterGraphData.get(index)
                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);
                            }

                            if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                                if (binding.llBeforeMeal.isSelected() && binding.llAfterMeal.isSelected()) {
                                    if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(index)
                                                                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseBefore =
                                                         listResultGraphData.get(index)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    }
                                    if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(index)
                                                                                                         .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseAfter =
                                                        listResultGraphData.get(index)
                                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    }
                                } else if (binding.llBeforeMeal.isSelected()) {
                                    strGlucoseBefore =
                                                     listResultBeforeGraphData.get(index)
                                                                              .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                } else if (binding.llAfterMeal.isSelected()) {
                                    strGlucoseAfter =
                                                    listResultAfterGraphData.get(index)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                }
                            } else {
                                if (ManagerConstants.EatType.GLUCOSE_ALL.equals(listResultGraphData.get(index)
                                                                                                   .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                    strGlucoseBefore =
                                                     listResultGraphData.get(index)
                                                                        .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    strGlucoseAfter =
                                                    listResultGraphData.get(index)
                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER);
                                } else {
                                    if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(listResultGraphData.get(index)
                                                                                                          .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseBefore =
                                                         listResultGraphData.get(index)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE);
                                    }
                                    if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(listResultGraphData.get(index)
                                                                                                         .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                        strGlucoseAfter =
                                                        listResultGraphData.get(index)
                                                                           .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_AFTER);
                                    }
                                }
                            }

                            setGraphData(strGlucoseBefore, strGlucoseAfter, date);
                        } catch (Exception e) {
                            // nothing
                            setGraphData("0", "0", "");
                        }

                    } else {
                        setAvgGraphData(getString(R.string.common_txt_default), "0");
                        setGraphData(getString(R.string.common_txt_default), "0", "");
                    }

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 혈압 결과 DB 조회 Sync
     */
    private class SearchGlucoseDBSync extends AsyncTask<Void, Void, Void> {

        List<Map<String, String>> resultData;

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... param) {

            try {

                Map<String, String> pMap = new HashMap<String, String>();
                String mealType = "";
                mapData = new HashMap<String, String>();
                if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH) {
                    //그래프 검색

                    // 조건에 맞는 혈압 데이터 가져옴
                    //                    resultData = glucoseService.searchBloodPressureDataListPeriodGraph(pMap, nPeriodType);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = 0;
                    int max = 0;
                    int min = 0;
                    int calculationYear = 0;
                    int calculationMonth = 0;
                    int calculationDay = 0;

                    Calendar lastDateCalendar = Calendar.getInstance();
                    lastDateCalendar.setTime(calendar.getTime());
                    if (nPeriodType == 0) {
                        AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 그래프 오늘");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        calculationDay = lastDateCalendar.get(Calendar.DATE);

                        day = calendar.get(Calendar.DATE);
                        resultData = glucoseService.searchGlucoseDataListPeriodGraph(pMap,
                                                                                     nPeriodType,
                                                                                     "10",
                                                                                     String.format("%04d%02d%02d%02d",
                                                                                                   year,
                                                                                                   month,
                                                                                                   day,
                                                                                                   0),
                                                                                     String.format("%04d%02d%02d%02d",
                                                                                                   calculationYear,
                                                                                                   calculationMonth,
                                                                                                   calculationDay,
                                                                                                   0));
                        mapData =
                                glucoseService.searchGlucoseAvg(pMap,
                                                                nPeriodType,
                                                                "10",
                                                                String.format("%04d%02d%02d%02d", year, month, day, 0),
                                                                String.format("%04d%02d%02d%02d",
                                                                              calculationYear,
                                                                              calculationMonth,
                                                                              calculationDay,
                                                                              0));
                    } else if (nPeriodType == 1) {
                        AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 그래프 일주일");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        year = lastDateCalendar.get(Calendar.YEAR);
                        month = lastDateCalendar.get(Calendar.MONTH) + 1;
                        max = lastDateCalendar.get(Calendar.DATE);

                        calculationYear = calculationDate.get(Calendar.YEAR);
                        calculationMonth = calculationDate.get(Calendar.MONTH) + 1;
                        min = calculationDate.get(Calendar.DATE);
                        resultData = glucoseService.searchGlucoseDataListPeriodGraph(pMap,
                                                                                     nPeriodType,
                                                                                     "8",
                                                                                     String.format("%04d%02d%02d",
                                                                                                   calculationYear,
                                                                                                   calculationMonth,
                                                                                                   min),
                                                                                     String.format("%04d%02d%02d",
                                                                                                   year,
                                                                                                   month,
                                                                                                   max));
                        mapData = glucoseService.searchGlucoseAvg(pMap,
                                                                  nPeriodType,
                                                                  "8",
                                                                  String.format("%04d%02d%02d",
                                                                                calculationYear,
                                                                                calculationMonth,
                                                                                min),
                                                                  String.format("%04d%02d%02d", year, month, max));
                    } else if (nPeriodType == 2) {
                        AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 그래프 한달");
                        lastDateCalendar.add(Calendar.MONTH, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        resultData = glucoseService.searchGlucoseDataListPeriodGraph(pMap,
                                                                                     nPeriodType,
                                                                                     "8",
                                                                                     String.format("%04d%02d%02d",
                                                                                                   year,
                                                                                                   month,
                                                                                                   1),
                                                                                     String.format("%04d%02d%02d",
                                                                                                   calculationYear,
                                                                                                   calculationMonth,
                                                                                                   1));
                        mapData = glucoseService.searchGlucoseAvg(pMap,
                                                                  nPeriodType,
                                                                  "8",
                                                                  String.format("%04d%02d%02d", year, month, 1),
                                                                  String.format("%04d%02d%02d",
                                                                                calculationYear,
                                                                                calculationMonth,
                                                                                1));
                    } else if (nPeriodType == 3) {
                        AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 그래프 년간");
                        lastDateCalendar.add(Calendar.YEAR, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        resultData = glucoseService.searchGlucoseDataListPeriodGraph(pMap,
                                                                                     nPeriodType,
                                                                                     "6",
                                                                                     String.format("%04d%02d", year, 1),
                                                                                     String.format("%04d%02d",
                                                                                                   calculationYear,
                                                                                                   1));
                        mapData = glucoseService.searchGlucoseAvg(pMap,
                                                                  nPeriodType,
                                                                  "6",
                                                                  String.format("%04d%02d", year, 1),
                                                                  String.format("%04d%02d", calculationYear, 1));
                    } else if (nPeriodType == 4) {
                        AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 그래프 전체");
                        resultData = glucoseService.searchGlucoseDataGraphAll(pMap, nPeriodType);
                    }
                } else if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_LIST) {
                    //리스트 검색
                    AnalyticsUtil.sendScene(GlucoseGraphActivity.this, "3_혈당 리스트");

                    resultData = glucoseService.searchGlucoseDataListPeriodList(pMap, nPeriodType);

                } else {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideLodingProgress();

            if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH) {
                //그래프 표시
                listResultGraphData = resultData;
                displayResultGraph();

                if (nPeriodType == 4) {
                    ManagerUtil.Localization getLocalization =
                                                             ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this));
                    if (listResultGraphData != null && listResultGraphData.size() > 0) {
                        String[] date =
                                      ManagerUtil.getDateCharacter(getLocalization,
                                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                                   true,
                                                                   "/",
                                                                   ":",
                                                                   "yyyyMMddHHmmss",
                                                                   DateUtil.getLongToStringDate("yyyyMMddHHmmss",
                                                                                                DateUtil.getMilliSecondDate(listResultGraphData.get(0)
                                                                                                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT))));

                        String strMeasureDt = listResultGraphData.get(listResultGraphData.size() - 1)
                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)
                                                                 .substring(0, 8);
                        String[] dateArray =
                                           ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                                        ManagerUtil.ShowFormatPosition.SECOND,
                                                                        true,
                                                                        "/",
                                                                        ":",
                                                                        "yyyyMMddHHmmss",
                                                                        strMeasureDt + "000000");

                        binding.tvDate.setText(date[0] + " ~ " + dateArray[0]);
                    }

                }
            } else if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_LIST) {
                //리스트 표시
                listResultListData = resultData;
                displayResultList();

            } else {
                displayResultGraph();
            }
        }
    }

    /**
     * 혈당 식전 식후 수정
     */
    private class ModGlucoseMeal extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();
            String strMeal = "";
            String strGlucoseSeq = params[0].toString();
            String strInsDt = params[1].toString();
            String strGlucose = params[2].toString();
            String strGlucoseType = params[3].toString();
            String strMeasureDt = params[4].toString();
            String strServerYn = params[5].toString();
            String strMealType = params[6].toString();

            try {

                //DB 메세지 업데이트
                if (getString(R.string.glucose_main_txt_meal_before).equals(strMealType)) {
                    strMeal = ManagerConstants.EatType.GLUCOSE_BEFORE;
                } else {
                    strMeal = ManagerConstants.EatType.GLUCOSE_AFTER;
                }
                int nRow = glucoseService.updateMeal(strGlucoseSeq, strMeal, strGlucoseType);

                if (nRow > 0) {

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.UUID,
                             PreferenceUtil.getEncEmail(GlucoseGraphActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT, strInsDt);
                    data.put(ManagerConstants.RequestParamName.GLUCOSE, strGlucose);
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_MEAL, strMeal);
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_TYPE, strGlucoseType);
                    data.put(ManagerConstants.RequestParamName.RECORD_DT, strMeasureDt);

                    resultJSON = glucoseService.modifyMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            glucoseService.updateSendToServerYN(strGlucoseSeq);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {
                        glucoseListAdapter.notifyDataSetChanged();
                    } else {
                    }
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    /**
     * 혈당 DB 삭제 Sync
     */
    private class DeleteBloodPressureDBSync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                String strDbSeq = params[0].toString();
                String strInsDt = params[1].toString();
                String strServerToSend = params[2].toString();

                if (ManagerConstants.ServerSyncYN.SERVER_SYNC_Y.equals(strServerToSend)) {

                    Map<Object, Object> data = new HashMap<Object, Object>();
                    JSONObject resultJSON = new JSONObject();

                    data.put(ManagerConstants.RequestParamName.UUID,
                             PreferenceUtil.getEncEmail(GlucoseGraphActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT, strInsDt);

                    resultJSON = glucoseService.removeMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {

                            glucoseService.deleteMeasureData(strDbSeq);

                        }
                    }

                } else {
                    glucoseService.deleteMeasureData(strDbSeq);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideLodingProgress();

            //DB 조회
            sbpDBSync = new SearchGlucoseDBSync();
            sbpDBSync.execute();
        }
    }

    /**
     * 리스트뷰 아답터
     */
    private class GlucoseResultListAdapter extends CommonListAdapter {

        public GlucoseResultListAdapter(Context context, List<Map<String, String>> list) {
            super(context, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Map<String, String> data = getItem(position);

            final ViewHolder holder;

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.item_glucose_graph, parent, false);

                holder = new ViewHolder();

                holder.ccvResult = (CustomCircleView)convertView.findViewById(R.id.ccv_result);
                holder.ccvResultMore = (CustomCircleView)convertView.findViewById(R.id.ccv_result_more);
                holder.tvGlucose = (TextView)convertView.findViewById(R.id.tv_glucose);
                holder.tvMealType = (TextView)convertView.findViewById(R.id.tv_meal_type);
                holder.tvEatType = (TextView)convertView.findViewById(R.id.tv_eat_type);
                holder.tvDate = (TextView)convertView.findViewById(R.id.tv_bp_item_date);
                holder.tvTime = (TextView)convertView.findViewById(R.id.tv_bp_item_time);
                holder.tvResult = (TextView)convertView.findViewById(R.id.tv_result);
                holder.tvMemo = (TextView)convertView.findViewById(R.id.tv_memo);
                holder.llRowOpen = (LinearLayout)convertView.findViewById(R.id.ll_glucose_list_img_bg_open);
                holder.llRowClose = (LinearLayout)convertView.findViewById(R.id.ll_glucose_list_img_bg_open);
                holder.llEat = (LinearLayout)convertView.findViewById(R.id.ll_eat);
                holder.ivDelete = (ImageView)convertView.findViewById(R.id.iv_delete);
                holder.ivLine = (ImageView)convertView.findViewById(R.id.iv_line);
                holder.ivMemo = (ImageView)convertView.findViewById(R.id.iv_memo);
                holder.llMemo = (LinearLayout)convertView.findViewById(R.id.ll_memo);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder)convertView.getTag();

            }

            if (data != null) {
            }
            if ("close".equals(data.get("isOpen"))) {
                holder.llRowOpen.setVisibility(View.GONE);
                holder.ivLine.setVisibility(View.GONE);
            } else if ("open".equals(data.get("isOpen"))) {
                holder.llRowOpen.setVisibility(View.VISIBLE);
                holder.ivLine.setVisibility(View.VISIBLE);
            }

            String[] date =
                          ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(GlucoseGraphActivity.this)),
                                                       ManagerUtil.ShowFormatPosition.SECOND,
                                                       true,
                                                       "/",
                                                       ":",
                                                       "yyyyMMddHHmmss",
                                                       data.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
            holder.tvDate.setText(date[0]);
            holder.tvTime.setText(date[1]);

            String strType = data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE);
            if (HealthcareConstants.GlucoseState.GLUCOSE_LOW.equals(strType)) {
                //저혈당
                holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                holder.tvResult.setText(getResources().getString(R.string.glucose_main_txt_status_low));
            } else if (HealthcareConstants.GlucoseState.GLUCOSE_NORMAL.equals(strType)) {
                //정상
                holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                holder.tvResult.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
            } else if (HealthcareConstants.GlucoseState.GLUCOSE_OVER.equals(strType)) {
                //고혈당 전단계
                holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_f06515));
                holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_f06515));
                holder.tvResult.setText(getResources().getString(R.string.glucose_main_txt_status_over));
            } else {
                //기본 정상
                holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                holder.tvResult.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
            }

            String strMessage = data.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);
            holder.tvMemo.setText(strMessage);
            holder.tvGlucose.setText(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));

            if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                holder.tvMealType.setText(getString(R.string.glucose_main_txt_meal_before));
                holder.tvEatType.setText(getString(R.string.glucose_meal_type) + " "
                                         + getString(R.string.glucose_main_txt_meal_before));
            } else if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                holder.tvMealType.setText(getString(R.string.glucose_main_txt_meal_after));
                holder.tvEatType.setText(getString(R.string.glucose_meal_type) + " "
                                         + getString(R.string.glucose_main_txt_meal_after));
            }

            holder.llEat.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!ManagerUtil.isClicking()) {

                        if (BPDiaryApplication.isNetworkState(GlucoseGraphActivity.this)) {
                            final int chkUnit;
                            if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                                chkUnit = 0;
                            } else {
                                chkUnit = 1;
                            }

                            if (ManagerConstants.Unit.MMOL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))
                                && data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).contains(".")) {
                                data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                         ManagerUtil.mmolToMg(String.valueOf(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))));
                            }

                            final DefaultChoiceDialog dialog = new DefaultChoiceDialog(GlucoseGraphActivity.this,
                                                                                       chkUnit,
                                                                                       getString(R.string.dialog_glucose_check_condition),
                                                                                       getString(R.string.glucose_main_txt_meal_before),
                                                                                       getString(R.string.glucose_main_txt_meal_after),
                                                                                       new IChoiceDialog() {

                                                                                           @Override
                                                                                           public void onCancel() {
                                                                                           }

                                                                                           @Override
                                                                                           public void
                                                                                                  onConfirm(int chkItem) {

                                                                                               if (chkItem == 0) {
                                                                                                   String strTypeValue =
                                                                                                                       HealthcareUtil.getGlucoseType(GlucoseGraphActivity.this,
                                                                                                                                                     data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE),
                                                                                                                                                     ManagerConstants.EatType.GLUCOSE_BEFORE);
                                                                                                   holder.tvMealType.setText(getString(R.string.glucose_main_txt_meal_before));
                                                                                                   holder.tvEatType.setText(getString(R.string.glucose_meal_type)
                                                                                                                            + " "
                                                                                                                            + getString(R.string.glucose_main_txt_meal_before));
                                                                                                   data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                                                                                            ManagerConstants.EatType.GLUCOSE_BEFORE);
                                                                                                   data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                                                                                                            strTypeValue);

                                                                                               } else {
                                                                                                   String strTypeValue =
                                                                                                                       HealthcareUtil.getGlucoseType(GlucoseGraphActivity.this,
                                                                                                                                                     data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE),
                                                                                                                                                     ManagerConstants.EatType.GLUCOSE_AFTER);
                                                                                                   holder.tvMealType.setText(getString(R.string.glucose_main_txt_meal_after));
                                                                                                   holder.tvEatType.setText(getString(R.string.glucose_meal_type)
                                                                                                                            + " "
                                                                                                                            + getString(R.string.glucose_main_txt_meal_after));
                                                                                                   data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                                                                                            ManagerConstants.EatType.GLUCOSE_AFTER);
                                                                                                   data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                                                                                                            strTypeValue);
                                                                                               }

                                                                                               if (ManagerConstants.Unit.MMOL.equals(PreferenceUtil.getGlucoseUnit(GlucoseGraphActivity.this))
                                                                                                   && !data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE)
                                                                                                           .contains(".")) {
                                                                                                   data.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                                                                                            ManagerUtil.mgToMmol(String.valueOf(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))));
                                                                                               }

                                                                                               if (chkUnit != chkItem) {
                                                                                                   new ModGlucoseMeal().execute(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ),
                                                                                                                                data.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT),
                                                                                                                                data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE),
                                                                                                                                data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE),
                                                                                                                                data.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT),
                                                                                                                                data.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)
                                                                                                                                    .toString(),
                                                                                                                                holder.tvMealType.getText()
                                                                                                                                                 .toString());
                                                                                               }
                                                                                           }
                                                                                       });
                            dialog.show();
                        } else {

                            DefaultOneButtonDialog defaultOneButtonDialog =
                                                                          new DefaultOneButtonDialog(GlucoseGraphActivity.this,
                                                                                                     getString(R.string.common_txt_noti),
                                                                                                     getString(R.string.common_error_comment),
                                                                                                     getString(R.string.common_txt_confirm),
                                                                                                     new IDefaultOneButtonDialog() {

                                                                                                         @Override
                                                                                                         public void
                                                                                                                onConfirm() {
                                                                                                         }
                                                                                                     });
                            defaultOneButtonDialog.show();

                        }
                    }
                }

            });

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!ManagerUtil.isClicking()) {

                        if (data != null) {
                            if (BPDiaryApplication.isNetworkState(GlucoseGraphActivity.this)) {
                                DefaultDialog deleteDialog = new DefaultDialog(GlucoseGraphActivity.this,
                                                                               getString(R.string.common_txt_noti),
                                                                               getString(R.string.common_dialog_txt_content),
                                                                               getString(R.string.common_txt_cancel),
                                                                               getString(R.string.common_txt_confirm),
                                                                               new IDefaultDialog() {

                                                                                   @Override
                                                                                   public void onConfirm() {

                                                                                       new DeleteBloodPressureDBSync().execute(data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ)
                                                                                                                                   .toString(),
                                                                                                                               data.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT)
                                                                                                                                   .toString(),
                                                                                                                               data.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)
                                                                                                                                   .toString());
                                                                                   }

                                                                                   @Override
                                                                                   public void onCancel() {
                                                                                       //nothing
                                                                                   }
                                                                               });

                                deleteDialog.show();
                            } else {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(GlucoseGraphActivity.this,
                                                                                                         getString(R.string.common_txt_noti),
                                                                                                         getString(R.string.common_error_comment),
                                                                                                         getString(R.string.common_txt_confirm),
                                                                                                         new IDefaultOneButtonDialog() {

                                                                                                             @Override
                                                                                                             public void
                                                                                                                    onConfirm() {
                                                                                                             }
                                                                                                         });
                                defaultOneButtonDialog.show();
                            }
                        }
                    }
                }
            });

            holder.llMemo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!ManagerUtil.isClicking()) {

                        Intent intent = new Intent(GlucoseGraphActivity.this, GlucoseMemoActivity.class);
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

                        intent.putExtra("position", position + "");
                        intent.putExtra("memo", "memo");
                        startActivityForResult(intent, MEMO_RESULT_CODE);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {

            LinearLayout llRowClose;

            LinearLayout llRowOpen;

            LinearLayout llEat;

            LinearLayout llMemo;

            CustomCircleView ccvResult;

            CustomCircleView ccvResultMore;

            TextView tvGlucose;

            TextView tvMealType;

            TextView tvEatType;

            TextView tvDate;

            TextView tvTime;

            TextView tvResult;

            TextView tvMemo;

            ImageView ivDelete;

            ImageView ivLine;

            ImageView ivMemo;
        }
    }
}
