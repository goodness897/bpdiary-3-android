package kr.co.openit.bpdiary.activity.bp;


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
import kr.co.openit.bpdiary.activity.setting.ProfileBirthActivity;
import kr.co.openit.bpdiary.adapter.common.CommonListAdapter;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomCircleView;
import kr.co.openit.bpdiary.customview.GraphViewOneStick;
import kr.co.openit.bpdiary.databinding.ActivityBpGraphBinding;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.BpGraphModel;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by hslee on 2017-01-06.
 */

public class BPGraphActivity extends NonMeasureActivity {

    private ActivityBpGraphBinding binding;

    private BpGraphModel bpGraphModel;

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
    private BloodPressureService bpService;

    /**
     * 혈압 측정 데이터를 DB에서 조회하는 AsyncTask
     */
    private SearchBloodPressureDBSync sbpDBSync;

    /**
     * 측정 결과 데이터(리스트)
     */
    private List<Map<String, String>> listResultListData;

    /**
     * 측정 결과 데이터(그래프)
     */
    private List<Map<String, String>> listResultGraphData;

    /**
     * 리스트 아답터
     */
    private BloodPressureResultListAdapter bpListAdapter;

    /**
     * 그래프
     */
    private GraphViewOneStick graphUtil;

    private Map<String, String> mapData;

    private Calendar calendar;

    private Calendar calculationDate;

    private boolean listGraphFlag = true;

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
                sbpDBSync = new SearchBloodPressureDBSync();
                sbpDBSync.execute();

            }
        } else if (MEMO_RESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                if (intent.getStringExtra("position") != null) {
                    int position = Integer.parseInt(intent.getStringExtra("position"));
                    String arm = intent.getStringExtra("arm");
                    String memo = intent.getStringExtra("memo");
                    final Map pMap = (Map) listResultListData.get(position);
                    pMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, memo);
                    pMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM, arm);
                    bpListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bp_graph);
        context = BPGraphActivity.this;
        bpGraphModel = new BpGraphModel();

        /**
         * 광고
         */
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(BPGraphActivity.this)) {
            binding.llAds.setVisibility(View.GONE);
        } else {
            binding.llAds.setVisibility(View.VISIBLE);
        }

        /**
         * blood pressure service
         */
        bpService = new BloodPressureService(BPGraphActivity.this);
        setDateInit();
        /**
         * DB 조회
         */
        sbpDBSync = new SearchBloodPressureDBSync();
        sbpDBSync.execute();

        bpGraphModel.setScreenType(true);
        bpGraphModel.setPeriodType(4);
        binding.rlAll.setSelected(true);
        binding.setBpGraph(bpGraphModel);

        initToolbar(getString(R.string.main_navigation_bp) + " " + getString(R.string.common_txt_graph));

        binding.tvAvg.setText(getString(R.string.common_txt_all) + " " + getString(R.string.common_txt_avg));
        binding.toolbar.llGraphList.setVisibility(View.VISIBLE);
        binding.lvBpList.setSelector(getResources().getDrawable(R.drawable.selector_graph_list_item));

        binding.rlAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    bpGraphModel.setPeriodType(4);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(true);

                    binding.llLeft.setVisibility(View.INVISIBLE);
                    binding.llRight.setVisibility(View.INVISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_all) + " " + getString(R.string.common_txt_avg));
                    nPeriodType = 4;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlToday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    bpGraphModel.setPeriodType(0);
                    binding.rlToday.setSelected(true);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_today) + " " + getString(R.string.common_txt_avg));
                    nPeriodType = 0;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    bpGraphModel.setPeriodType(1);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(true);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_week) + " " + getString(R.string.common_txt_avg));
                    nPeriodType = 1;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    bpGraphModel.setPeriodType(2);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(true);
                    binding.rlYear.setSelected(false);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_month) + " " + getString(R.string.common_txt_avg));
                    nPeriodType = 2;
                    setSearchDate(0, false);
                }
            }
        });

        binding.rlYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    bpGraphModel.setPeriodType(3);
                    binding.rlToday.setSelected(false);
                    binding.rlWeek.setSelected(false);
                    binding.rlMonth.setSelected(false);
                    binding.rlYear.setSelected(true);
                    binding.rlAll.setSelected(false);

                    binding.llLeft.setVisibility(View.VISIBLE);
                    binding.llRight.setVisibility(View.VISIBLE);
                    binding.tvAvg.setText(getString(R.string.common_txt_year) + " " + getString(R.string.common_txt_avg));
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

        binding.lvBpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!ManagerUtil.isClicking()) {
                    final Map pMap = (Map) parent.getItemAtPosition(position);

                    if (pMap != null) {
                        if (pMap.get("isOpen").equals("close")) {

                            listResultListData.get(position).put("isOpen", "open");

                        } else if (pMap.get("isOpen").equals("open")) {

                            listResultListData.get(position).put("isOpen", "close");

                        } else {
                            //nothing
                        }

                        bpListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

        // 가로 scroll 삭제
        binding.lvBpList.setHorizontalScrollBarEnabled(false);
    }

    /**
     * 상단 탭 메뉴 UI 변경
     */
    private void switchTabSide(int nIndex) {
        binding.llNoData.setVisibility(View.GONE);
        setDateInit();
        if (ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH == nIndex) {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvBpList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(kr.co.openit.bpdiary.R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(kr.co.openit.bpdiary.R.string.main_navigation_bp) + " "
                    + getString(R.string.common_txt_graph));

            listGraphFlag = true;
        } else if (ManagerConstants.ResultViewType.RESULT_VIEW_LIST == nIndex) {
            binding.rlGraphAll.setVisibility(View.GONE);
            binding.lvBpList.setVisibility(View.VISIBLE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_graph));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_graph_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_bp) + " "
                    + getString(R.string.common_txt_list));
            listGraphFlag = false;
        } else {
            binding.rlGraphAll.setVisibility(View.VISIBLE);
            binding.lvBpList.setVisibility(View.GONE);
            binding.toolbar.tvGraph.setText(getString(R.string.common_txt_list));
            binding.toolbar.ivGraphList.setBackgroundResource(R.drawable.selector_graph_list_button);
            binding.toolbar.tvTitle.setText(getString(R.string.main_navigation_bp) + " "
                    + getString(R.string.common_txt_graph));
            listGraphFlag = true;
        }

        nListViewType = nIndex;

        /**
         * DB 조회
         */
        sbpDBSync = new SearchBloodPressureDBSync();
        sbpDBSync.execute();

    }

    private void setDateInit() {
        calendar = Calendar.getInstance();
        String[] date =
                ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
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
                    ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
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
                ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this));
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
        sbpDBSync = new SearchBloodPressureDBSync();
        sbpDBSync.execute();
    }

    /**
     * 결과 표시(그래프)
     */
    private void displayResultGraph() {
        final GraphViewOneStick.CustomDate customDateType;
        int yMax = 100;
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

                Float fSumSys = 0f;
                Float fSumDia = 0f;
                Float fSumPul = 0f;
                int nCount = 0;

                for (int i = 0; i < listResultGraphData.size(); i++) {

                    float sys = 0f;
                    float dia = 0f;
                    float pul = 0f;
                    if (!"0".equals(listResultGraphData.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS))) {

                        nCount += 1;

                        sys = Float.parseFloat(listResultGraphData.get(i)
                                .get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                        dia = Float.parseFloat(listResultGraphData.get(i)
                                .get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                        pul = Float.parseFloat(listResultGraphData.get(i)
                                .get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));

                        fSumSys += sys;
                        fSumDia += dia;
                        fSumPul += pul;

                    }
                    if (yMax < sys || yMax < dia || yMax < pul) {
                        if (sys > dia && sys > pul) {
                            yMax = (int) sys;
                        } else if (dia > pul) {
                            yMax = (int) dia;
                        } else {
                            yMax = (int) pul;
                        }
                    }
                }

                if (nCount < 1) {
                    setAvgGraphData("0", "0", "0");
                    setGraphData("0", "0", "0", "");
                } else {

                    String strAvgSys = String.valueOf(fSumSys / nCount);
                    String strAvgDia = String.valueOf(fSumDia / nCount);
                    String strAvgPul = String.valueOf(fSumPul / nCount);

                    if (nPeriodType == 4) {
                        if (strAvgSys.indexOf(".") > -1) {
                            strAvgSys = strAvgSys.substring(0, strAvgSys.indexOf("."));
                        }

                        if (strAvgDia.indexOf(".") > -1) {
                            strAvgDia = strAvgDia.substring(0, strAvgDia.indexOf("."));
                        }

                        if (strAvgPul.indexOf(".") > -1) {
                            strAvgPul = strAvgPul.substring(0, strAvgPul.indexOf("."));
                        }
                    } else {
                        strAvgSys = mapData.get("sys");
                        strAvgDia = mapData.get("dia");
                        strAvgPul = mapData.get("pulse");
                    }
                    setAvgGraphData(strAvgSys, strAvgDia, strAvgPul);
                }

            } else {
                setAvgGraphData("0", "0", "0");
                setGraphData("0", "0", "0", "");
            }

            // 그래프 표시
            graphUtil = new GraphViewOneStick(BPGraphActivity.this, changeUI);
            final int finalYMax = yMax;
            binding.llBpGraphView.post(new Runnable() {

                @Override
                public void run() {
                    binding.llBpGraphView.removeAllViews();
                    binding.llBpGraphView.addView(graphUtil);
                    graphUtil.setStayDataX(true);
                    graphUtil.setCustomDataX(true);
                    graphUtil.setBottomWidely(true);
                    graphUtil.setCustomDataX(customDateType);
                    graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)));
                    graphUtil.setInitialize(GraphViewOneStick.LINE,
                            7,
                            finalYMax + 40,
                            0,
                            5,
                            0,
                            0,
                            binding.llBpGraphView.getWidth(),
                            binding.llBpGraphView.getHeight());

                    //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                    //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                    graphUtil.addObjectList(listResultGraphData,
                            ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                            ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                            ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                            Color.parseColor("#f46a2b"),
                            R.drawable.bp_grap_dot_red,
                            R.drawable.bp_graph_double_dot_red,
                            R.drawable.bp_graph_double_dot_red);

                    graphUtil.addObjectList(listResultGraphData,
                            ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                            ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                            ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                            Color.parseColor("#12bcef"),
                            R.drawable.bp_grap_dot_blue,
                            R.drawable.bp_graph_double_dot_blue,
                            R.drawable.bp_graph_double_dot_blue);

                }
            });
        }
    }

    private void setAvgGraphData(String strAvgSys, String strAvgDia, String strAvgPul) {
        if (binding.tvAvgSys != null) {
            binding.tvAvgSys.setText(strAvgSys);
        }
        if (binding.tvAvgDia != null) {
            binding.tvAvgDia.setText(strAvgDia);
        }
        if (binding.tvAvgPulse != null) {
            binding.tvAvgPulse.setText(strAvgPul);
        }
    }

    private void setGraphData(String strSys, String strDia, String strPul, String strDate) {
        if (binding.tvBpSysDia != null) {
            binding.tvBpSysDia.setText(strSys + "/" + strDia);
        }
        if (binding.tvBpPulse != null) {
            binding.tvBpPulse.setText(strPul);
        }
        if (binding.tvBpDate != null) {
            if (TextUtils.isEmpty(strDate)) {
                binding.tvBpDate.setText("");
            } else {
                if (nPeriodType == ManagerConstants.PeriodType.PERIOD_ALL) {
                    String[] date =
                            ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
                                    ManagerUtil.ShowFormatPosition.SECOND,
                                    true,
                                    "/",
                                    ":",
                                    "yyyyMMddHHmmss",
                                    strDate);
                    binding.tvBpDate.setText(date[0] + " " + date[1].substring(0, 5));
                } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_DAY) {
                    String[] date =
                            ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
                                    ManagerUtil.ShowFormatPosition.SECOND,
                                    true,
                                    "/",
                                    ":",
                                    "yyyyMMddHH",
                                    strDate);
                    binding.tvBpDate.setText(date[0] + " " + strDate.substring(8, 10) + ":00");
                } else if (nPeriodType == ManagerConstants.PeriodType.PERIOD_WEEK
                        || nPeriodType == ManagerConstants.PeriodType.PERIOD_MONTH) {
                    String[] date =
                            ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
                                    ManagerUtil.ShowFormatPosition.SECOND,
                                    true,
                                    "/",
                                    ":",
                                    "yyyyMMdd",
                                    strDate);
                    binding.tvBpDate.setText(date[0]);
                } else {
                    String[] date =
                            ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
                                    ManagerUtil.ShowFormatPosition.SECOND,
                                    true,
                                    "/",
                                    ":",
                                    "yyyyMM",
                                    strDate);
                    binding.tvBpDate.setText(date[0]);
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
                binding.lvBpList.setVisibility(View.GONE);
            } else {
                binding.llNoData.setVisibility(View.GONE);
                binding.lvBpList.setVisibility(View.VISIBLE);
                bpListAdapter = new BloodPressureResultListAdapter(BPGraphActivity.this, listResultListData);
                binding.lvBpList.setAdapter(bpListAdapter);
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
                            binding.llBpGraphAvg.setVisibility(View.VISIBLE);
                            String strSys = listResultGraphData.get(index)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS);
                            String strDia = listResultGraphData.get(index)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA);
                            String strPul = listResultGraphData.get(index)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE);
                            String strDate = listResultGraphData.get(index)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);
                            if (strSys.indexOf(".") > -1) {
                                strSys = strSys.substring(0, strSys.indexOf("."));
                            }

                            if (strDia.indexOf(".") > -1) {
                                strDia = strDia.substring(0, strDia.indexOf("."));
                            }

                            if (strPul.indexOf(".") > -1) {
                                strPul = strPul.substring(0, strPul.indexOf("."));
                            }

                            setGraphData(strSys, strDia, strPul, strDate);
                        } catch (Exception e) {
                            // nothing
                            setGraphData(getString(R.string.common_txt_default),
                                    getString(R.string.common_txt_default),
                                    getString(R.string.common_txt_default),
                                    "");
                        }

                    } else {
                        setAvgGraphData(getString(R.string.common_txt_default),
                                getString(R.string.common_txt_default),
                                getString(R.string.common_txt_default));
                        setGraphData(getString(R.string.common_txt_default),
                                getString(R.string.common_txt_default),
                                getString(R.string.common_txt_default),
                                "");
                    }

                    break;

            }
        }
    };

    /**
     * 혈압 결과 DB 조회 Sync
     */
    private class SearchBloodPressureDBSync extends AsyncTask<Void, Void, Void> {

        List<Map<String, String>> resultData;

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... param) {

            try {

                Map<String, String> pMap = new HashMap<String, String>();
                mapData = new HashMap<String, String>();
                if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_GRAPH) {
                    //그래프 검색

                    // 조건에 맞는 혈압 데이터 가져옴
                    //                    resultData = bpService.searchBloodPressureDataListPeriodGraph(pMap, nPeriodType);
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
                        AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 오늘");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        calculationDay = lastDateCalendar.get(Calendar.DATE);

                        day = calendar.get(Calendar.DATE);
                        resultData = bpService.searchBloodPressureDataListPeriodGraph(pMap,
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
                        mapData = bpService.searchBpAvg(pMap,
                                nPeriodType,
                                "10",
                                String.format("%04d%02d%02d%02d", year, month, day, 0),
                                String.format("%04d%02d%02d%02d",
                                        calculationYear,
                                        calculationMonth,
                                        calculationDay,
                                        0));
                    } else if (nPeriodType == 1) {
                        AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 일주일");
                        lastDateCalendar.add(Calendar.DATE, 1);
                        year = lastDateCalendar.get(Calendar.YEAR);
                        month = lastDateCalendar.get(Calendar.MONTH) + 1;
                        max = lastDateCalendar.get(Calendar.DATE);

                        calculationYear = calculationDate.get(Calendar.YEAR);
                        calculationMonth = calculationDate.get(Calendar.MONTH) + 1;
                        min = calculationDate.get(Calendar.DATE);
                        resultData = bpService.searchBloodPressureDataListPeriodGraph(pMap,
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
                        mapData = bpService.searchBpAvg(pMap,
                                nPeriodType,
                                "8",
                                String.format("%04d%02d%02d",
                                        calculationYear,
                                        calculationMonth,
                                        min),
                                String.format("%04d%02d%02d", year, month, max));
                    } else if (nPeriodType == 2) {
                        AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 한달");
                        lastDateCalendar.add(Calendar.MONTH, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        calculationMonth = lastDateCalendar.get(Calendar.MONTH) + 1;
                        resultData = bpService.searchBloodPressureDataListPeriodGraph(pMap,
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
                        mapData = bpService.searchBpAvg(pMap,
                                nPeriodType,
                                "8",
                                String.format("%04d%02d%02d", year, month, 1),
                                String.format("%04d%02d%02d",
                                        calculationYear,
                                        calculationMonth,
                                        1));
                    } else if (nPeriodType == 3) {
                        AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 년간");
                        lastDateCalendar.add(Calendar.YEAR, 1);
                        calculationYear = lastDateCalendar.get(Calendar.YEAR);
                        resultData =
                                bpService.searchBloodPressureDataListPeriodGraph(pMap,
                                        nPeriodType,
                                        "6",
                                        String.format("%04d%02d", year, 1),
                                        String.format("%04d%02d",
                                                calculationYear,
                                                1));
                        mapData = bpService.searchBpAvg(pMap,
                                nPeriodType,
                                "6",
                                String.format("%04d%02d", year, 1),
                                String.format("%04d%02d", calculationYear, 1));
                    } else if (nPeriodType == 4) {
                        AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 전체");
                        resultData = bpService.searchBloodPressureDataGraphAll(pMap, nPeriodType);
                    }
                } else if (nListViewType == ManagerConstants.ResultViewType.RESULT_VIEW_LIST) {
                    //리스트 검색
                    AnalyticsUtil.sendScene(BPGraphActivity.this, "3_혈압 그래프 리스트");

                    resultData = bpService.searchBloodPressureDataListPeriodList(pMap, nPeriodType);

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
                            ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this));
                    if (listResultGraphData != null && listResultGraphData.size() > 0
                            && listResultGraphData.get(0).get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT) != null) {
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
                            Log.d("TEST", "date: "+listResultGraphData.get(0)
                                    .get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                        String strMeasureDt = listResultGraphData.get(listResultGraphData.size()-1).
                                get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).substring(0,8);

                        String[] dateArray =
                                ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
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
     * 혈압 삭제 DB Sync
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

                    data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(BPGraphActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT, strInsDt);

                    resultJSON = bpService.removeMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                .toString())) {

                            bpService.deleteMeasureData(strDbSeq);

                        }
                    }

                } else {
                    bpService.deleteMeasureData(strDbSeq);
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
            sbpDBSync = new SearchBloodPressureDBSync();
            sbpDBSync.execute();
        }
    }

    /**
     * 리스트뷰 아답터
     */
    private class BloodPressureResultListAdapter extends CommonListAdapter {

        public BloodPressureResultListAdapter(Context context, List<Map<String, String>> list) {
            super(context, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Map<String, String> data = getItem(position);

            ViewHolder holder;

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.item_bp_graph, parent, false);

                holder = new ViewHolder();

                holder.ccvResult = (CustomCircleView) convertView.findViewById(R.id.ccv_result);
                holder.ccvResultMore = (CustomCircleView) convertView.findViewById(R.id.ccv_result_more);
                holder.tvSysDia = (TextView) convertView.findViewById(R.id.tv_bp_item_sys_dia);
                holder.tvPul = (TextView) convertView.findViewById(R.id.tv_bp_item_pulse);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_bp_item_date);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tv_bp_item_time);
                holder.tvResult = (TextView) convertView.findViewById(R.id.tv_result);
                holder.tvMemo = (TextView) convertView.findViewById(R.id.tv_memo);
                holder.tvArm = (TextView) convertView.findViewById(R.id.tv_arm);
                holder.llRowOpen = (LinearLayout) convertView.findViewById(R.id.ll_bp_list_img_bg_open);
                holder.rlRowClose = (RelativeLayout) convertView.findViewById(R.id.rl_bp_list_img_bg_close);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holder.ivLine = (ImageView) convertView.findViewById(R.id.iv_line);
                holder.ivMemo = (ImageView) convertView.findViewById(R.id.iv_memo);
                holder.llMemo = (LinearLayout) convertView.findViewById(R.id.ll_memo);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            if (data != null) {
                if ("close".equals(data.get("isOpen"))) {
                    holder.llRowOpen.setVisibility(View.GONE);
                    holder.ivLine.setVisibility(View.GONE);
                } else if ("open".equals(data.get("isOpen"))) {
                    holder.llRowOpen.setVisibility(View.VISIBLE);
                    holder.ivLine.setVisibility(View.VISIBLE);
                }

                String[] date =
                        ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(BPGraphActivity.this)),
                                ManagerUtil.ShowFormatPosition.SECOND,
                                true,
                                "/",
                                ":",
                                "yyyyMMddHHmmss",
                                data.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                holder.tvDate.setText(date[0]);
                holder.tvTime.setText(date[1]);

                String strType = data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE);
                String strMessage = data.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);
                String strArm = "";

                if ("L".equals(data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM))) {
                    strArm = getString(R.string.bp_where_arm) + getString(R.string.bp_left_arm);
                } else if ("R".equals(data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM))) {
                    strArm = getString(R.string.bp_where_arm) + getString(R.string.bp_right_arm);
                } else {
                    strArm = getString(R.string.bp_where_arm) + getString(R.string.bp_no_arm);
                }
                holder.tvMemo.setText(strMessage);
                holder.tvArm.setText(strArm);
                if (HealthcareConstants.BloodPressureState.BP_LOW.equals(strType)) {
                    //저혈압
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_76adef));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_76adef));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_low));
                } else if (HealthcareConstants.BloodPressureState.BP_NORMAL.equals(strType)) {
                    //정상
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_normal));
                } else if (HealthcareConstants.BloodPressureState.BP_APPROACH.equals(strType)) {
                    //고혈압 전단계
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_f4dc2e));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_f4dc2e));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_approach));
                } else if (HealthcareConstants.BloodPressureState.BP_HIGH_ONE.equals(strType)) {
                    //1기 고혈압
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_f49b40));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_f49b40));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_high_one));
                } else if (HealthcareConstants.BloodPressureState.BP_HIGH_TWO.equals(strType)) {
                    //2기 고혈압
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_ee4633));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_ee4633));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_high_two));
                } else if (HealthcareConstants.BloodPressureState.BP_VERY_HIGH.equals(strType)) {
                    //높은 고혈압
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_c8084c));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_c8084c));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_very_high));
                } else {
                    //기본 정상
                    holder.ccvResult.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    holder.ccvResultMore.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    holder.tvResult.setText(getResources().getString(R.string.bp_main_txt_status_normal));
                }

                holder.tvSysDia.setText(data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS) + "/"
                        + data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                holder.tvPul.setText(data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));

                holder.ivDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!ManagerUtil.isClicking()) {

                            if (data != null) {
                                if (BPDiaryApplication.isNetworkState(BPGraphActivity.this)) {
                                    DefaultDialog deleteDialog = new DefaultDialog(BPGraphActivity.this,
                                            getString(R.string.common_txt_noti),
                                            getString(R.string.common_dialog_txt_content),
                                            getString(R.string.common_txt_cancel),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultDialog() {

                                                @Override
                                                public void onConfirm() {

                                                    new DeleteBloodPressureDBSync().execute(data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ)
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
                                            new DefaultOneButtonDialog(BPGraphActivity.this,
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

                            Intent intent = new Intent(BPGraphActivity.this, BPMemoActivity.class);
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                            intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM,
                                    data.get(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM));
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
            }

            return convertView;
        }

        class ViewHolder {

            RelativeLayout rlRowClose;

            LinearLayout llRowOpen;

            CustomCircleView ccvResult;

            CustomCircleView ccvResultMore;

            LinearLayout llMemo;

            TextView tvSysDia;

            TextView tvPul;

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
}
