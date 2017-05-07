package kr.co.openit.bpdiary.activity.weight;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseGraphActivity;
import kr.co.openit.bpdiary.adapter.common.CommonListAdapter;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomCircleView;
import kr.co.openit.bpdiary.customview.GraphViewOneStick;
import kr.co.openit.bpdiary.databinding.ActivityWsGraphBinding;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.ISHealthDialog;
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by srpark on 2017-01-16.
 */

public class WeightGraphActivity extends NonMeasureActivity {

    private ActivityWsGraphBinding binding;

    /**
     * 체중 측정 데이터를 DB에서 조회하는 AsyncTask
     */
    private SearchWeighingScaleDBSync swsDBSync;

    /**
     * 기간별 타입
     */
    private int nPeriodType = ManagerConstants.PeriodType.PERIOD_ALL;

    /**
     * 화면 표시 타입
     */
    private int nListViewType = ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH;

    private Context context;

    /**
     * weighing scale service
     */
    private WeighingScaleService wsService;

    /**
     * 리스트 아답터
     */
    private WeightScaleResultListAdapter wsListAdapter;

    /**
     * 측정 결과 데이터(그래프)
     */
    private List<Map<String, String>> listResultGraphData;

    /**
     * 측정 결과 데이터(리스트)
     */
    private List<Map<String, String>> listResultListData;

    private Calendar calendar;

    private Calendar calculationDate;

    private boolean listGraphFlag = true;

    /**
     * 그래프 유틸
     */
    private GraphViewOneStick graphUtil;

    /**
     * 메모 리턴 코드
     */
    private static final int MEMO_RESULT_CODE = 1000;

    private LinearLayout llAds;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (MEMO_RESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                if (intent.getStringExtra("position") != null) {
                    int position = Integer.parseInt(intent.getStringExtra("position"));
                    final Map pMap = (Map)listResultListData.get(position);
                    pMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, intent.getStringExtra("memo"));
                    wsListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ws_graph);
        context = WeightGraphActivity.this;

        AnalyticsUtil.sendScene(WeightGraphActivity.this, "WeightGraphActivity");

        /**
         * 광고
         */
        llAds = (LinearLayout)findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(WeightGraphActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        wsService = new WeighingScaleService(context);

        setDateInit();

        swsDBSync = new SearchWeighingScaleDBSync();
        swsDBSync.execute();

        binding.rlAll.setSelected(true);

        initToolbar(getString(R.string.main_navigation_weight) + " " + getString(R.string.common_txt_graph));

        binding.tvAvg.setText(getString(R.string.common_txt_all) + " " + getString(R.string.common_txt_avg));

        binding.toolbar.llGraphList.setVisibility(View.VISIBLE);
        binding.lvWsList.setSelector(getResources().getDrawable(R.drawable.selector_graph_list_item));
        binding.tvAvg.setText(getString(R.string.common_txt_all) + " " + getString(R.string.common_txt_avg));
        binding.rlAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
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
                    binding.tvAvg.setText(getString(R.string.common_txt_all) + " "
                                          + getString(R.string.common_txt_avg));
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlToday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    binding.rlToday.setSelected(true);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.tvAvg.setText(getString(R.string.common_txt_today) + " "
                                          + getString(R.string.common_txt_avg));
                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    nPeriodType = 0;
                    binding.tvAvg.setText(getString(R.string.common_txt_today) + " "
                                          + getString(R.string.common_txt_avg));
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(true);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.tvAvg.setText(getString(R.string.common_txt_week) + " "
                                          + getString(R.string.common_txt_avg));
                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    nPeriodType = 1;
                    binding.tvAvg.setText(getString(R.string.common_txt_week) + " "
                                          + getString(R.string.common_txt_avg));
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(true);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.tvAvg.setText(getString(R.string.common_txt_month) + " "
                                          + getString(R.string.common_txt_avg));
                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    nPeriodType = 2;
                    binding.tvAvg.setText(getString(R.string.common_txt_month) + " "
                                          + getString(R.string.common_txt_avg));
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(true);
                    binding.rlAll.setSelected(false);

                    binding.tvAvg.setText(getString(R.string.common_txt_year) + " "
                                          + getString(R.string.common_txt_avg));
                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    nPeriodType = 3;
                    binding.tvAvg.setText(getString(R.string.common_txt_year) + " "
                                          + getString(R.string.common_txt_avg));
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

        binding.lvWsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

                        wsListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
        // 가로 scroll 삭제
        binding.lvWsList.setHorizontalScrollBarEnabled(false);
    }

    /**
     * 체중 결과 DB 조회 Sync
     */
    private class SearchWeighingScaleDBSync extends AsyncTask<Void, Void, Void> {

        List<Map<String, String>> resultData;

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... param) {

            try {

                Map<String, String> pMap = new HashMap<String, String>();

                if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH) {
                    //그래프 검색
                    // 조건에 맞는 체중 데이터 가져옴
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
                        AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 그래프 오늘");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        calculationDay = lastDateCalendar.get(Calendar.DATE);

                        day = calendar.get(Calendar.DATE);
                        resultData = wsService.searchWeighinhScaleDataListPeriodGrapth(pMap,
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
                    } else if (nPeriodType == 1) {
                        AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 그래프 일주일");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        year = lastDateCalendar.get(Calendar.YEAR);
                        month = lastDateCalendar.get(Calendar.MONTH) + 1;
                        max = lastDateCalendar.get(Calendar.DATE);

                        calculationYear = calculationDate.get(Calendar.YEAR);
                        calculationMonth = calculationDate.get(Calendar.MONTH) + 1;
                        min = calculationDate.get(Calendar.DATE);
                        resultData =
                                   wsService.searchWeighinhScaleDataListPeriodGrapth(pMap,
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
                    } else if (nPeriodType == 2) {
                        AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 그래프 한달");
                        lastDateCalendar.add(Calendar.MONTH, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        resultData = wsService.searchWeighinhScaleDataListPeriodGrapth(pMap,
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
                    } else if (nPeriodType == 3) {
                        AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 그래프 년간");
                        lastDateCalendar.add(Calendar.YEAR, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        resultData =
                                   wsService.searchWeighinhScaleDataListPeriodGrapth(pMap,
                                                                                     nPeriodType,
                                                                                     "6",
                                                                                     String.format("%04d%02d", year, 1),
                                                                                     String.format("%04d%02d",
                                                                                                   calculationYear,
                                                                                                   1));
                    } else if (nPeriodType == 4) {
                        AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 그래프 전체");
                        resultData = wsService.searchWeighinhScaleDataGraphAll(pMap, nPeriodType);
                    }
                } else if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_LIST) {
                    //리스트 검색
                    AnalyticsUtil.sendScene(WeightGraphActivity.this, "3_체중 리스트");

                    resultData = wsService.searchWeighinhScaleDataListPeriodList(pMap, nPeriodType);

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
                //그래프 검색

                if (getResources().getString(R.string.weight_lbs).equals(PreferenceUtil.getWeightUnit(context))) {
                    for (int i = 0; i < resultData.size(); i++) {
                        String strWeight =
                                         ManagerUtil.kgToLbs(resultData.get(i)
                                                                       .get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                        resultData.get(i).put(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT, strWeight);
                    }
                }

                listResultGraphData = resultData;
                displayResultGraph();

                if (nPeriodType == 4) {
                    ManagerUtil.Localization getLocalization =
                                                             ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context));

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
                                           ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(WeightGraphActivity.this)),
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
                //리스트 검색
                listResultListData = resultData;
                displayResultList();

            } else {
                displayResultGraph();
            }
        }
    }

    /**
     * 리스트뷰 아답터
     */
    private class WeightScaleResultListAdapter extends CommonListAdapter {

        public WeightScaleResultListAdapter(Context context, List<Map<String, String>> list) {
            super(context, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Map<String, String> data = getItem(position);

            ViewHolder holder;

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.item_ws_graph, parent, false);

                holder = new ViewHolder();

                holder.ccvResult = (CustomCircleView)convertView.findViewById(R.id.ccv_result);
                holder.ccvResultMore = (CustomCircleView)convertView.findViewById(R.id.ccv_result_more);
                holder.tvWeight = (TextView)convertView.findViewById(R.id.tv_ws_item_weight);
                holder.tvBmi = (TextView)convertView.findViewById(R.id.tv_ws_item_bmi);
                holder.tvDate = (TextView)convertView.findViewById(R.id.tv_ws_item_date);
                holder.tvTime = (TextView)convertView.findViewById(R.id.tv_ws_item_time);
                holder.tvResult = (TextView)convertView.findViewById(R.id.tv_result);
                holder.tvMemo = (TextView)convertView.findViewById(R.id.tv_memo);
                holder.tvArm = (TextView)convertView.findViewById(R.id.tv_arm);
                holder.llRowOpen = (LinearLayout)convertView.findViewById(R.id.ll_bp_list_img_bg_open);
                holder.rlRowClose = (RelativeLayout)convertView.findViewById(R.id.rl_bp_list_img_bg_close);
                holder.ivDelete = (ImageView)convertView.findViewById(R.id.iv_delete);
                holder.ivLine = (ImageView)convertView.findViewById(R.id.iv_line);
                holder.ivMemo = (ImageView)convertView.findViewById(R.id.iv_memo);
                holder.llMemo = (LinearLayout)convertView.findViewById(R.id.ll_memo);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if (data != null) {
                if ("close".equals(data.get("isOpen"))) {

                    holder.llRowOpen.setVisibility(View.GONE);
                    holder.ivLine.setVisibility(View.GONE);

                    String strState =
                                    HealthcareUtil.getWeighingScaleBmiType(data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));

                    if (HealthcareConstants.WeighingScaleState.WS_LOW.equals(strState)) {
                        //저체중
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_e9da64));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_e9da64));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_low));
                    } else if (HealthcareConstants.WeighingScaleState.WS_NORMAL.equals(strState)) {
                        //정상
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    } else if (HealthcareConstants.WeighingScaleState.WS_OVER_WEIGHT.equals(strState)) {
                        //과체중
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_approach));
                    } else if (HealthcareConstants.WeighingScaleState.WS_OBESITY.equals(strState)) {
                        //비만
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_a180de));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_a180de));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_obesity));
                    } else if (HealthcareConstants.WeighingScaleState.WS_VERY_OBESITY.equals(strState)) {
                        //고도비만
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_ed5967));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_ed5967));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_very_obesity));
                    } else {
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    }

                } else if ("open".equals(data.get("isOpen"))) {
                    holder.llRowOpen.setVisibility(View.VISIBLE);
                    holder.ivLine.setVisibility(View.VISIBLE);

                    String strState =
                                    HealthcareUtil.getWeighingScaleBmiType(data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));

                    if (HealthcareConstants.WeighingScaleState.WS_LOW.equals(strState)) {
                        //저체중
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_e9da64));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_e9da64));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_low));
                    } else if (HealthcareConstants.WeighingScaleState.WS_NORMAL.equals(strState)) {
                        //정상
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    } else if (HealthcareConstants.WeighingScaleState.WS_OVER_WEIGHT.equals(strState)) {
                        //과체중
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_approach));
                    } else if (HealthcareConstants.WeighingScaleState.WS_OBESITY.equals(strState)) {
                        //비만
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_a180de));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_a180de));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_obesity));
                    } else if (HealthcareConstants.WeighingScaleState.WS_VERY_OBESITY.equals(strState)) {
                        //고도비만
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_ed5967));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_ed5967));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_very_obesity));
                    } else {
                        holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_82d589));
                        holder.tvResult.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    }

                    String strMessage = data.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);
                    holder.tvMemo.setText(strMessage);
                }

                String[] date =
                              ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                                           ManagerUtil.ShowFormatPosition.SECOND,
                                                           true,
                                                           "/",
                                                           ":",
                                                           "yyyyMMddHHmmss",
                                                           data.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                holder.tvDate.setText(date[0]);
                holder.tvTime.setText(date[1]);
                String strWeight = "0";
                double weight = Double.parseDouble(data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                if (getResources().getString(R.string.weight_kg).equals(PreferenceUtil.getWeightUnit(context))) {
                    if (weight > 0) {
                        strWeight = String.format("%.1f", weight);
                        if (strWeight.contains(",")) {
                            strWeight = strWeight.replace(",", ".");
                        }
                    }
                    holder.tvWeight.setText(strWeight);
                } else if (getResources().getString(R.string.weight_lbs)
                                         .equals(PreferenceUtil.getWeightUnit(context))) {
                    holder.tvWeight.setText(ManagerUtil.kgToLbs(weight + ""));
                }
                String strBmi = "0";
                double bmi = Double.parseDouble(data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                if (weight > 0) {
                    strBmi = String.format("%.1f", bmi);
                    if (strBmi.contains(",")) {
                        strBmi = strBmi.replace(",", ".");
                    }
                }
                holder.tvBmi.setText(strBmi);
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (data != null) {
                        if (!ManagerUtil.isClicking()) {

                            if (BPDiaryApplication.isNetworkState(context)) {
                                DefaultDialog deleteDialog = new DefaultDialog(context,
                                                                               getString(R.string.common_txt_noti),
                                                                               getString(R.string.common_dialog_txt_content),
                                                                               getString(R.string.common_txt_cancel),
                                                                               getString(R.string.common_txt_confirm),
                                                                               new IDefaultDialog() {

                                                                                   @Override
                                                                                   public void onConfirm() {
                                                                                       new DeleteWeighingScaleDBSync().execute(data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ)
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
                                                                              new DefaultOneButtonDialog(context,
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

                        Intent intent = new Intent(context, WeightMemoActivity.class);
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE,
                                        data.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
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

            RelativeLayout rlRowClose;

            LinearLayout llRowOpen;

            LinearLayout llMemo;

            CustomCircleView ccvResult;

            CustomCircleView ccvResultMore;

            TextView tvWeight;

            TextView tvBmi;

            TextView tvDate;

            TextView tvTime;

            TextView tvResult;

            TextView tvMemo;

            TextView tvArm;

            ImageView ivDelete;

            ImageView ivLine;

            ImageView ivMemo;
        }
    }

    private void setDateInit() {
        calendar = Calendar.getInstance();
        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   DateUtil.getLongToStringDate("yyyyMMddHHmmss",
                                                                                calendar.getTimeInMillis()));
        binding.tvDate.setText(date[0]);
    }

    /**
     * 날자 셋팅
     *
     * @param diffDate
     * @param isDateInit
     */
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
                              ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
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
        ManagerUtil.Localization getLocalization = ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context));
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
        swsDBSync = new SearchWeighingScaleDBSync();
        swsDBSync.execute();
    }

    /**
     * 상단 탭 메뉴 UI 변경
     */
    private void switchTabSide(int nIndex) {
        binding.llNoData.setVisibility(View.GONE);
        setDateInit();
        if (ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH == nIndex) {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvWsList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_weight) + " "
                                            + getString(R.string.common_txt_graph));
            listGraphFlag = true;
        } else if (ManagerConstants.ResultViewType.RESULT_VIEW_LIST == nIndex) {
            binding.rlGraphAll.setVisibility(View.GONE);
            binding.lvWsList.setVisibility(View.VISIBLE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_graph));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_graph_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_weight) + " "
                                            + getString(R.string.common_txt_list));
            listGraphFlag = false;
        } else {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvWsList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_weight) + " "
                                            + getString(R.string.common_txt_graph));
            listGraphFlag = true;
        }

        nListViewType = nIndex;

        /**
         * DB 조회
         */
        swsDBSync = new SearchWeighingScaleDBSync();
        swsDBSync.execute();
    }

    /**
     * 결과 표시(그래프)
     */
    private void displayResultGraph() {
        final GraphViewOneStick.CustomDate customDateType;
        float yMax = 100;
        if (listResultGraphData != null) {
            if (nPeriodType == 0) {
                customDateType = GraphViewOneStick.CustomDate.TODAY;
            } else if (nPeriodType == 1) {
                customDateType = GraphViewOneStick.CustomDate.WEEK;
            } else if (nPeriodType == 2) {
                customDateType = GraphViewOneStick.CustomDate.MONTH;
            } else if (nPeriodType == 3) {
                customDateType = GraphViewOneStick.CustomDate.YEAR;
            } else if (nPeriodType == 4) {
                customDateType = GraphViewOneStick.CustomDate.ALL;
            } else {
                customDateType = GraphViewOneStick.CustomDate.TODAY;
            }
            if (!listResultGraphData.isEmpty()) {

                Float fWeight = 0f;
                Float fSumWeight = 0f;
                Float fSumBmi = 0f;
                int nCount = 0;

                for (int i = 0; i < listResultGraphData.size(); i++) {
                    if (isStringFloat(listResultGraphData.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))
                        && isStringFloat(listResultGraphData.get(i)
                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI))) {

                        if ((!"0".equals(listResultGraphData.get(i)
                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT)))
                            && (!"0.0".equals(listResultGraphData.get(i)
                                                                 .get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT)))) {

                            nCount += 1;

                            try {
                                fWeight =
                                        Float.parseFloat(listResultGraphData.get(i)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                            } catch (Exception e) {
                                fWeight = 0f;
                            }
                            fSumWeight += fWeight;

                            try {
                                fSumBmi +=
                                        Float.parseFloat(listResultGraphData.get(i)
                                                                            .get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                            } catch (Exception e) {
                                fSumBmi = 0f;
                            }

                            if (yMax < fWeight) {
                                yMax = fWeight;
                            }
                        }
                    }
                }

                if (nCount < 1) {
                    setAvgGraphData("0", "0");
                    setGraphData("0", "0", "");
                } else {
                    String strAvgWeight = "";
                    if (getResources().getString(R.string.weight_kg).equals(PreferenceUtil.getWeightUnit(context))) {
                        strAvgWeight = String.format("%.1f", fSumWeight / nCount);
                        if (strAvgWeight.contains(",")) {
                            strAvgWeight = strAvgWeight.replace(",", ".");
                        }
                    } else {
                        strAvgWeight = String.format("%.1f", fSumWeight / nCount);
                        if (strAvgWeight.contains(",")) {
                            strAvgWeight = strAvgWeight.replace(",", ".");
                        }
                    }
                    String strAvgBmi = String.format("%.1f", fSumBmi / nCount);
                    if (strAvgBmi.contains(",")) {
                        strAvgBmi = strAvgBmi.replace(",", ".");
                    }

                    setAvgGraphData(strAvgWeight, strAvgBmi);
                }

            } else {
                setAvgGraphData(getString(R.string.common_txt_default), getString(R.string.common_txt_default));
                setGraphData(getString(R.string.common_txt_default), getString(R.string.common_txt_default), "");
            }

            // 그래프 표시
            graphUtil = new GraphViewOneStick(context, changeUI);
            final int finalYMax = (int)yMax;

            binding.llWsGraphView.post(new Runnable() {

                @Override
                public void run() {

                    binding.llWsGraphView.removeAllViews();
                    binding.llWsGraphView.addView(graphUtil);
                    graphUtil.setStayDataX(true);
                    graphUtil.setCustomDataX(true);
                    graphUtil.setBottomWidely(true);
                    graphUtil.setCustomDataX(customDateType);
                    graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                    graphUtil.setInitialize(GraphViewOneStick.LINE,
                                            7,
                                            finalYMax + 60,
                                            0,
                                            5,
                                            0,
                                            0,
                                            binding.llWsGraphView.getWidth(),
                                            binding.llWsGraphView.getHeight());

                    graphUtil.addRange((int)Double.parseDouble(ManagerUtil.kgToLbs(PreferenceUtil.getWeight(WeightGraphActivity.this))),
                                       (int)Double.parseDouble(ManagerUtil.kgToLbs(PreferenceUtil.getWeight(WeightGraphActivity.this))) + 1,
                                       Color.rgb(130, 213, 137));

                    graphUtil.addObjectList(listResultGraphData,
                                            ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                            ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                            ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                            Color.rgb(51, 173, 197),
                                            R.drawable.weight_graph_dot_blue,
                                            R.drawable.weight_graph_double_dot_blue,
                                            R.drawable.weight_graph_double_dot_blue);

                    graphUtil.addObjectList(listResultGraphData,
                                            ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                                            ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                                            ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                            Color.rgb(201, 13, 212),
                                            R.drawable.weight_graph_dot_purple,
                                            R.drawable.weight_graph_double_dot_purple,
                                            R.drawable.weight_graph_double_dot_purple);
                }
            });
        }
    }

    private void setAvgGraphData(String strAvgWeight, String strAvgBmi) {
        if (isStringFloat(strAvgWeight) && isStringFloat(strAvgBmi)) {
            if ("0".equals(strAvgWeight)) {
                binding.tvAvgWeight.setText(String.format("%.1f", Float.parseFloat("0")));
            } else {
                binding.tvAvgWeight.setText(String.format("%.1f", Float.parseFloat(strAvgWeight)));
            }
            binding.tvAvgBmi.setText(strAvgBmi.equals("0") ? String.format("%.1f", Float.parseFloat("0"))
                                                           : String.format("%.1f", Float.parseFloat(strAvgBmi)));
        }
    }

    private void setGraphData(String strWeight, String strBmi, String strDate) {
        if (isStringFloat(strWeight) && isStringFloat(strBmi) && isStringFloat(strDate)) {
            if (binding.tvWeight != null) {
                if ("0".equals(strWeight)) {
                    binding.tvWeight.setText(String.format("%.1f", Float.parseFloat("0")));
                } else {
                    binding.tvWeight.setText(String.format("%.1f", Float.parseFloat(strWeight)));
                }
            }
            if (binding.tvWsBmi != null) {
                binding.tvWsBmi.setText(String.format("%.1f", Float.parseFloat(strBmi)));
            }

            if (binding.tvWsDate != null) {
                if (TextUtils.isEmpty(strDate)) {
                    binding.tvWsDate.setText("");
                } else {
                    if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                        String[] date =
                                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(WeightGraphActivity.this)),
                                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                                   true,
                                                                   "/",
                                                                   ":",
                                                                   "yyyyMMddHHmmss",
                                                                   strDate);
                        binding.tvWsDate.setText(date[0] + " " + date[1].substring(0, 5));
                    } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_DAY) {
                        String[] date =
                                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(WeightGraphActivity.this)),
                                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                                   true,
                                                                   "/",
                                                                   ":",
                                                                   "yyyyMMddHH",
                                                                   strDate);
                        binding.tvWsDate.setText(date[0] + " " + strDate.substring(8, 10) + ":00");
                    } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_WEEK
                               || nPeriodType == ManagerConstants.PeriodType.PERIOD_MONTH) {
                        String[] date =
                                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(WeightGraphActivity.this)),
                                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                                   true,
                                                                   "/",
                                                                   ":",
                                                                   "yyyyMMdd",
                                                                   strDate);
                        binding.tvWsDate.setText(date[0]);
                    } else {
                        String[] date =
                                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(WeightGraphActivity.this)),
                                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                                   true,
                                                                   "/",
                                                                   ":",
                                                                   "yyyyMM",
                                                                   strDate);
                        binding.tvWsDate.setText(date[0]);
                    }
                }
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

                    index = msg.getData().getInt(ManagerConstants.Graph.INDEX);

                    if (getResources().getString(R.string.weight_kg).equals(PreferenceUtil.getWeightUnit(context))) {
                        binding.tvAvgWeightUnit.setText(getResources().getString(R.string.weight_kg));
                        binding.tvWeightUnit.setText(getResources().getString(R.string.weight_kg));
                    } else if (getResources().getString(R.string.weight_lbs)
                                             .equals(PreferenceUtil.getWeightUnit(context))) {
                        binding.tvAvgWeightUnit.setText(getResources().getString(R.string.weight_lbs));
                        binding.tvWeightUnit.setText(getResources().getString(R.string.weight_lbs));
                    }

                    if (listResultGraphData.size() > 0) {
                        try {
                            String strWeight = listResultGraphData.get(index)
                                                                  .get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT);
                            String strBmi = listResultGraphData.get(index)
                                                               .get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI);
                            String strDate = listResultGraphData.get(index)
                                                                .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);
                            double weight = Double.parseDouble(strWeight);
                            double bmi = Double.parseDouble(strBmi);
                            if (weight > 0) {
                                if (getResources().getString(R.string.weight_kg)
                                                  .equals(PreferenceUtil.getWeightUnit(context))) {
                                    strWeight = String.format("%.1f", weight);
                                    if (strWeight.contains(",")) {
                                        strWeight = strWeight.replace(",", ".");
                                    }
                                } else {
                                    strWeight = String.format("%.1f", weight);
                                    if (strWeight.contains(",")) {
                                        strWeight = strWeight.replace(",", ".");
                                    }
                                }
                            }
                            if (bmi > 0) {
                                strBmi = String.format("%.1f", bmi);
                                if (strBmi.contains(",")) {
                                    strBmi = strBmi.replace(",", ".");
                                }
                            }
                            if ("0".equals(strWeight) && "0".equals(strBmi)) {
                            } else {
                                setGraphData(strWeight, strBmi, strDate);
                            }
                        } catch (Exception e) {
                            // nothing
                            setGraphData("0", "0", "");
                        }

                    } else {
                        setAvgGraphData("0", "0");
                        setGraphData("0", "0", "");
                    }

                    break;

            }
        }
    };

    /**
     * 결과 표시(리스트)
     */
    private void displayResultList() {

        if (listResultListData != null) {
            if (listResultListData.size() == 0) {
                binding.llNoData.setVisibility(View.VISIBLE);
                binding.lvWsList.setVisibility(View.GONE);
            } else {
                binding.llNoData.setVisibility(View.GONE);
                binding.lvWsList.setVisibility(View.VISIBLE);
                wsListAdapter = new WeightScaleResultListAdapter(context, listResultListData);
                binding.lvWsList.setAdapter(wsListAdapter);
            }
        }
    }

    /**
     * 체중 결과 DB 삭제 Sync
     */
    private class DeleteWeighingScaleDBSync extends AsyncTask<String, Void, Void> {

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

                    data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                    data.put(ManagerConstants.RequestParamName.INS_DT, strInsDt);

                    resultJSON = wsService.removeMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {

                            wsService.deleteMeasureData(strDbSeq);
                        }
                    }

                } else {
                    wsService.deleteMeasureData(strDbSeq);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideLodingProgress();
            /**
             * DB 조회
             */
            swsDBSync = new SearchWeighingScaleDBSync();
            swsDBSync.execute();
        }
    }

    private boolean isStringFloat(String s) {
        try {
            if (s != null || s.length() != 0) {
                Float.parseFloat(s);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
