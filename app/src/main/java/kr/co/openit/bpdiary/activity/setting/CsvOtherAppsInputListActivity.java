package kr.co.openit.bpdiary.activity.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.RequestParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseResult;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ServerSyncYN;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class CsvOtherAppsInputListActivity extends NonMeasureActivity {

    /**
     * debugging
     */
    protected final String TAG = getClass().getSimpleName();

    private String path1 = "";

    private String path2 = "";

    private String path3 = "";

    private String path4 = "";

    private String ext = "";

    private String dirCheck = "";

    private int nReadCount = 0;

    private final List<String> mDirNames = new ArrayList<String>();

    private ArrayList<Map> mFileNames = new ArrayList<Map>();

    private ArrayList<String> mGroupList = null;

    private ArrayList<ArrayList<Map>> mChildList = null;

    private Map<String, String> data = null;

    private ExpandableListView elvCsvFile;

    private java.util.Date currentDate;

    private java.text.SimpleDateFormat format;

    private DateFormat sdFormat;

    private Date tempDate = null;

    private LinearLayout llEmptyView;

    private LinearLayout llayoutNoFile;

    private CsvInputAdapter csvInputAdapter;

    public static CsvOtherAppsInputListActivity csvOtherAppsInputListActivity;

    /**
     * 광고
     */
    private LinearLayout llAds;

    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_ohter_apps);

        initToolbar(getString(R.string.setting_data_receive_list));

        AnalyticsUtil.sendScene(CsvOtherAppsInputListActivity.this, "CsvOtherAppsInputListActivity");

        csvOtherAppsInputListActivity = CsvOtherAppsInputListActivity.this;

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<Map>>();

        elvCsvFile = (ExpandableListView) findViewById(R.id.elv_csv_file);
        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llayoutNoFile = (LinearLayout) findViewById(R.id.ll_no_file);
        llAds = (LinearLayout) findViewById(R.id.ll_ads);

        /**
         * 광고
         */
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(CsvOtherAppsInputListActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        bpService = new BloodPressureService(CsvOtherAppsInputListActivity.this);
        updateFileList();

        csvInputAdapter = new CsvInputAdapter(CsvOtherAppsInputListActivity.this, elvCsvFile, mGroupList, mChildList);

        elvCsvFile.setAdapter(csvInputAdapter);

        if (mGroupList.size() == 0) {
            llayoutNoFile.setVisibility(View.VISIBLE);
            elvCsvFile.setVisibility(View.GONE);
            DefaultDialog dataFailDialog = new DefaultDialog(CsvOtherAppsInputListActivity.this,
                    getString(R.string.csv_other_apps_input),
                    getString(R.string.csv_data_fail),
                    "",
                    getString(R.string.common_txt_confirm),
                    new IDefaultDialog() {

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm() {

                        }
                    });
            dataFailDialog.show();

        }

        elvCsvFile.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        elvCsvFile.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v,
                                        final int groupPosition,
                                        final int childPosition,
                                        long id) {

                DefaultDialog csvInputCustomDialog = new DefaultDialog(CsvOtherAppsInputListActivity.this,
                        getString(R.string.csv_other_apps_input),
                        getString(R.string.csv_input_read_check),
                        getString(R.string.common_txt_cancel),
                        getString(R.string.common_txt_confirm),
                        new IDefaultDialog() {

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onConfirm() {

                                Map pMap =
                                        (Map) csvInputAdapter.getChild(groupPosition,
                                                childPosition);

                                String fileName =
                                        (String) pMap.get("FileName");
                                dirCheck = (String) pMap.get("DirCheck");

                                if ("BP Log".equals(dirCheck)) {
                                    //csvReader(path1 + fileName);

                                    new ReaderBckSync().execute(path1
                                            + fileName);

                                } else if ("MyHeart".equals(dirCheck)) {
                                    //csvReader(path2 + "/" + fileName);

                                    String filePath =
                                            (String) pMap.get("FilePath");
                                    new ReaderCsvSync().execute(path2
                                            + filePath
                                            + "/"
                                            + fileName);

                                } else if ("BPWatch".equals(dirCheck)) {
                                    //csvReader(path3 + fileName);

                                    new ReaderCsvSync().execute(path3
                                            + fileName);

                                } else if ("BloodPressureTracker".equals(dirCheck)) {
                                    //csvReader(path4 + fileName);

                                    String filePath =
                                            (String) pMap.get("FilePath");
                                    new ReaderDBSync().execute(path4
                                            + filePath
                                            + "/"
                                            + fileName);
                                }
                            }

                        });

                csvInputCustomDialog.show();

                return true;
            }
        });

        int groupCount = csvInputAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            elvCsvFile.expandGroup(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindDoNothingService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindDoNothingService();
    }

    /**
     * 툴바 세팅
     *
     * @param title 툴바 타이틀틀
     */

    public void initToolbar(String title) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_navi_back);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(title);
    }

    public void updateFileList() {

        ext = Environment.getExternalStorageState();

        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Blood Pressure/";
            path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyHeart/";
            path3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BPWatch/";
            path4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AadhkBloodPresure/";

        } else {
            path1 = Environment.MEDIA_UNMOUNTED;
        }

        //app1
        File files1 = new File(path1);

        if (files1.isDirectory()) {
            if (files1.listFiles(new JavaFileFilterForBCK()).length > 0) {
                mFileNames = new ArrayList<Map>();
                for (File file : files1.listFiles(new JavaFileFilterForBCK())) {
                    data = new HashMap<String, String>();
                    data.put("FileName", file.getName());
                    data.put("DirCheck", "BP Log");
                    mFileNames.add(data);
                }
                if (mFileNames.size() > 0) {
                    mGroupList.add("BP Log");
                    mChildList.add(mFileNames);
                }
            }
        }

        //app2
        File files2 = new File(path2);

        if (files2.isDirectory()) {
            if (files2.listFiles(new JavaDirFilter()).length > 0) {
                mFileNames = new ArrayList<Map>();
                for (File file : files2.listFiles(new JavaDirFilter())) {
                    mDirNames.add(file.getName());
                    //path2 = path2 + "/" + file.getName();
                    String strPath = "/" + file.getName();
                    File dir = new File(path2 + strPath);
                    if (dir.isDirectory()) {
                        if (dir.listFiles(new JavaFileFilter()).length > 0) {
                            for (File nextDir : dir.listFiles(new JavaFileFilter())) {
                                data = new HashMap<String, String>();
                                data.put("FileName", nextDir.getName());
                                data.put("FilePath", strPath);
                                data.put("DirCheck", "MyHeart");
                                mFileNames.add(data);
                            }
                        }
                    }
                }

                if (mFileNames.size() > 0) {
                    mGroupList.add("MyHeart");
                    mChildList.add(mFileNames);
                }
            }
        }

        //app3
        File files3 = new File(path3);

        if (files3.isDirectory()) {
            if (files3.listFiles(new JavaFileFilter()).length > 0) {
                mFileNames = new ArrayList<Map>();
                for (File file : files3.listFiles(new JavaFileFilter())) {
                    data = new HashMap<String, String>();
                    data.put("FileName", file.getName());
                    data.put("DirCheck", "BPWatch");
                    mFileNames.add(data);
                }
                if (mFileNames.size() > 0) {
                    mGroupList.add("BPWatch");
                    mChildList.add(mFileNames);
                }
            }
        }

        //app4
        File files4 = new File(path4);

        if (files4.isDirectory()) {
            if (files4.listFiles(new JavaDirFilter()).length > 0) {
                mFileNames = new ArrayList<Map>();
                for (File file : files4.listFiles(new JavaDirFilter())) {
                    mDirNames.add(file.getName());
                    //path2 = path2 + "/" + file.getName();
                    String strPath = "/" + file.getName();
                    File dir = new File(path4 + strPath);
                    if (dir.isDirectory()) {
                        if (dir.listFiles(new JavaFileFilterForDB()).length > 0) {
                            for (File nextDir : dir.listFiles(new JavaFileFilterForDB())) {
                                data = new HashMap<String, String>();
                                data.put("FileName", nextDir.getName());
                                data.put("FilePath", strPath);
                                data.put("DirCheck", "BloodPressureTracker");
                                mFileNames.add(data);
                            }
                        }
                    }
                    if (mFileNames.size() > 0) {
                        mGroupList.add("BloodPressureTracker");
                        mChildList.add(mFileNames);
                    }
                }
            }
        }
    }

    private static class CsvInputAdapter extends BaseExpandableListAdapter {

        private ArrayList<String> groupList = null;

        private ArrayList<ArrayList<Map>> childList = null;

        private LayoutInflater mInflater = null;

        private ViewHolder viewHolder = null;

        private ExpandableListView elv;

        public CsvInputAdapter(Context context,
                               ExpandableListView elv,
                               ArrayList<String> groupList,
                               ArrayList<ArrayList<Map>> childList) {
            super();
            this.elv = elv;
            this.mInflater = LayoutInflater.from(context);
            this.groupList = groupList;
            this.childList = childList;
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = convertView;
            viewHolder = new ViewHolder();

            if (v == null) {
                v = mInflater.inflate(R.layout.list_csv_input_group_item, parent, false);
                viewHolder.txtGroupName = (TextView) v.findViewById(R.id.tv_group_title);
                viewHolder.llMarginView = (LinearLayout) v.findViewById(R.id.ll_margin_view);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            if (groupPosition == 0) {
                viewHolder.llMarginView.setVisibility(View.GONE);
            } else {
                viewHolder.llMarginView.setVisibility(View.VISIBLE);
            }

            viewHolder.txtGroupName.setText(getGroup(groupPosition) + " (" + getChildrenCount(groupPosition) + ")");

            return v;
        }

        @Override
        public View getChildView(int groupPosition,
                                 int childPosition,
                                 boolean isLastChild,
                                 View convertView,
                                 ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                viewHolder = new ViewHolder();
                v = mInflater.inflate(R.layout.list_csv_input_child_item, null);
                viewHolder.txtTitle = (TextView) v.findViewById(R.id.tv_csv_child_title);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            Map data = (Map) getChild(groupPosition, childPosition);

            viewHolder.txtTitle.setText((String) data.get("FileName"));

            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    static class ViewHolder {

        TextView txtTitle;

        TextView txtGroupName;

        LinearLayout llMarginView;
    }

    Handler syncDbHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                //최대치값 설정
                progressDialog.setMax(msg.arg1);
            } else if (msg.what == 1) {
                progressDialog.setProgress(msg.arg1);
            }
        }
    };

    /**
     * 외부 CSV 파일 Sync
     */
    private class ReaderCsvSync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(CsvOtherAppsInputListActivity.this, R.style.ProgressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.csv_file_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... param) {

            try {

                String strFilePath = param[0].toString();

                CSVReader readerCount = new CSVReader(new FileReader(strFilePath));

                nReadCount = readerCount.readAll().size() - 1;

                Message progressBarInitMsg = Message.obtain();

                progressBarInitMsg.what = 0;
                progressBarInitMsg.arg1 = nReadCount;
                syncDbHandler.sendMessage(progressBarInitMsg);

                currentDate = new java.util.Date();

                format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

                String[] nextLine;

                int nCount = 0;

                CSVReader reader = new CSVReader(new FileReader(strFilePath));

                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line

                    Map<String, String> dbData = readBloopPressureMeasureData(nextLine);

                    if (dbData.size() > 0) {

                        if (saveBloopPressureMeasureData(dbData)) {

                            nCount++;

                            Message progressBarInitMsg2 = Message.obtain();

                            progressBarInitMsg2.what = 1;
                            progressBarInitMsg2.arg1 = nCount;
                            syncDbHandler.sendMessage(progressBarInitMsg2);

                        }
                    }
                }

            } catch (IOException ie) {
                ie.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            progressDialog.dismiss();
        }

        /**
         * CSV 파일 불러오기
         */
        private Map<String, String> readBloopPressureMeasureData(String[] nextLine) {

            Map<String, String> readData = new HashMap<String, String>();

            if ("MyHeart".equals(dirCheck)) {
                try {
                    if (!nextLine[1].startsWith("SYSTOLIC")) {
                        if (!nextLine[1].isEmpty()) {
                            readData.put("Systolic", nextLine[1]);
                        } else {
                            readData.put("Systolic", "0");
                            Crashlytics.getInstance().core.log(3,
                                    "BPDiaryError",
                                    "MyHeart's systolic is null or isEmpty");
                        }
                    }
                    if (!nextLine[2].startsWith("DIASTOLIC")) {
                        if (!nextLine[2].isEmpty()) {
                            readData.put("Diastolic", nextLine[2]);
                        } else {
                            readData.put("Diastolic", "0");
                            Crashlytics.getInstance().core.log(3,
                                    "BPDiaryError",
                                    "MyHeart's diastolic is null or isEmpty");
                        }
                    }
                    if (!nextLine[3].startsWith("PULSE")) {
                        if (!nextLine[3].isEmpty()) {
                            readData.put("Pulse", nextLine[3]);
                        } else {
                            readData.put("Pulse", "0");
                            Crashlytics.getInstance().core.log(3, "BPDiaryError", "MyHeart's pulse is null or isEmpty");
                        }
                    }
                    if (!nextLine[0].startsWith("DATE")) {
                        sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        try {
                            tempDate = sdFormat.parse(nextLine[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String strDate = format.format(tempDate);
                        readData.put("Date", strDate);
                    }

                    readData.put("Memo", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("BPWatch".equals(dirCheck)) {

                if (!nextLine[0].startsWith("Systolic")) {
                    if (!nextLine[0].isEmpty()) {
                        readData.put("Systolic", nextLine[0]);
                    } else {
                        readData.put("Systolic", "0");
                        Crashlytics.getInstance().core.log(3, "BPDiaryError", "BPWatch's systolic is null or isEmpty");
                    }
                }
                if (!nextLine[1].startsWith("Diastolic")) {
                    if (!nextLine[1].isEmpty()) {
                        readData.put("Diastolic", nextLine[1]);
                    } else {
                        readData.put("Diastolic", "0");
                        Crashlytics.getInstance().core.log(3, "BPDiaryError", "BPWatch's diastolic is null or isEmpty");
                    }
                }
                if (!nextLine[2].startsWith("Pulse")) {
                    if (!nextLine[2].isEmpty()) {
                        readData.put("Pulse", nextLine[2]);
                    } else {
                        readData.put("Pulse", "0");
                        Crashlytics.getInstance().core.log(3, "BPDiaryError", "BPWatch's pulse is null or isEmpty");
                    }
                }
                if (!nextLine[3].startsWith("Date")) {

                    try {
                        //구버전
                        sdFormat = new SimpleDateFormat("MM-dd-yy HH:mm");
                        tempDate = sdFormat.parse(nextLine[3]);

                        String strDate = format.format(tempDate);
                        readData.put("Date", strDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                        //신버전
                        try {
                            if (!nextLine[4].startsWith("Time")) {
                                sdFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");

                                String[] strDate = nextLine[3].split("/");
                                String[] strTime = nextLine[4].split(":");

                                String strTempDate = null;
                                String strTempTime = null;

                                if (strDate.length > 0) {
                                    String strMonth = strDate[0];
                                    String strDay = strDate[1];
                                    String strYear = strDate[2];

                                    strYear = "20" + strYear;
                                    strTempDate = strMonth + "-" + strDay + "-" + strYear;
                                }

                                if (strTime.length > 0) {
                                    String strHour = strTime[0];
                                    String strMin = strTime[1];
                                    String strAmPm = strTime[2].split(" ")[0];

                                    if (Integer.parseInt(strHour) < 10) {
                                        //                                        if (strAmPm.equals("PM")) {
                                        //                                            int nHour = Integer.parseInt(strHour) + 12;
                                        //                                            strHour = String.valueOf(nHour);
                                        //                                        }
                                        strHour = "0" + strHour;
                                    }
                                    if (Integer.parseInt(strMin) < 10) {
                                        strMin = "0" + strMin;
                                    }
                                    //                                    if (Integer.parseInt(strSec) < 10) {
                                    //                                        strSec = "0" + strSec;
                                    //                                    }
                                    strTempTime = strHour + ":" + strMin + ":00";
                                }
                                tempDate = sdFormat.parse(strTempDate + " " + strTempTime);
                                readData.put("Date", format.format(tempDate));
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }

                if (!nextLine[6].startsWith("Comment")) {
                    readData.put("Memo", nextLine[6]);
                } else {
                    readData.put("Memo", "");
                }
            }
            return readData;
        }

        /**
         * DB 저장하기
         */
        private boolean saveBloopPressureMeasureData(Map<String, String> pMap) {

            boolean isSuccess = true;

            try {
                Map<Object, Object> requestJSON = new HashMap<Object, Object>();
                JSONObject resultJSON = new JSONObject();

                String strDbSeq = null;
                String strSysValue = pMap.get("Systolic").toString();
                String strDiaValue = pMap.get("Diastolic").toString();
                String strPulValue = pMap.get("Pulse").toString();
                String strTypeValue = HealthcareUtil.getBloodPressureType(CsvOtherAppsInputListActivity.this,
                        pMap.get("Systolic").toString(),
                        pMap.get("Diastolic").toString());
                String strMeasureDt = pMap.get("Date").toString();
                String strMemoValue = pMap.get("Memo").toString();

                //평균혈압 = 최저혈압 + ((최고혈압 - 최저혈압) / 3)
                String strMeanValue = String.valueOf(Double.parseDouble(strDiaValue)
                        + ((Double.parseDouble(strSysValue)
                        - Double.parseDouble(strDiaValue))
                        / 3));

                //DB에 넣을 Data 작성
                Map<String, String> requestMap = new HashMap<String, String>();
                requestMap.put(DataBase.COLUMN_NAME_BP_SYS, strSysValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_DIA, strDiaValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_MEAN, strMeanValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_PULSE, strPulValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_ID, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
                requestMap.put(DataBase.COLUMN_NAME_MESSAGE, strMemoValue);
                requestMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt);
                requestMap.put(DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, ServerSyncYN.SERVER_SYNC_N);

                //DB 저장
                int nRow = bpService.createBloodPressureData(requestMap);

                if (nRow > 0) {

                    isSuccess = true;

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    requestJSON.put(RequestParamName.UUID,
                            PreferenceUtil.getEncEmail(CsvOtherAppsInputListActivity.this));
                    requestJSON.put(RequestParamName.INS_DT, requestMap.get(DataBase.COLUMN_NAME_INS_DT));
                    requestJSON.put(RequestParamName.BP_SYS, requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                    requestJSON.put(RequestParamName.BP_DIA, requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                    requestJSON.put(RequestParamName.BP_MEAN, requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                    requestJSON.put(RequestParamName.BP_PULSE, requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                    requestJSON.put(RequestParamName.BP_TYPE, requestMap.get(DataBase.COLUMN_NAME_BP_TYPE));
                    requestJSON.put(RequestParamName.SENSOR_COMPANY,
                            requestMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    requestJSON.put(RequestParamName.SENSOR_MODEL, requestMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
                    requestJSON.put(RequestParamName.SENSOR_SN, requestMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
                    requestJSON.put(RequestParamName.MESSAGE, requestMap.get(DataBase.COLUMN_NAME_MESSAGE));
                    requestJSON.put(RequestParamName.RECORD_DT,
                            ManagerUtil.convertDateFormatToServer(requestMap.get(DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = bpService.sendBloodPressureData(requestJSON);

                    if (resultJSON.has(ResponseParamName.RESULT)) {

                        if (ResponseResult.RESULT_TRUE.equals(resultJSON.get(ResponseParamName.RESULT).toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            bpService.updateSendToServerYN(strDbSeq);

                        }
                    }

                } else {
                    isSuccess = false;
                }

            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            }

            return isSuccess;

        }
    }

    /**
     * 외부 CSV 파일 Sync
     */
    private class ReaderBckSync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(CsvOtherAppsInputListActivity.this, R.style.ProgressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.csv_file_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... param) {

            try {

                String strFilePath = param[0].toString();

                CSVReader readerCount = new CSVReader(new FileReader(strFilePath));
                CSVReader reader = new CSVReader(new FileReader(strFilePath));

                String[] pData = reader.readNext();
                if ("All data".equals(pData)) {
                    nReadCount = readerCount.readAll().size() - 2;
                } else {
                    nReadCount = readerCount.readAll().size() - 4;
                }

                Message progressBarInitMsg = Message.obtain();

                progressBarInitMsg.what = 0;
                progressBarInitMsg.arg1 = nReadCount;
                syncDbHandler.sendMessage(progressBarInitMsg);

                currentDate = new java.util.Date();

                format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

                String[] nextLine;

                int nCount = 0;

                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line

                    Map<String, String> dbData = readBloopPressureMeasureData(nextLine);

                    if (dbData.size() > 0) {

                        if (saveBloopPressureMeasureData(dbData)) {

                            nCount++;

                            Message progressBarInitMsg2 = Message.obtain();

                            progressBarInitMsg2.what = 1;
                            progressBarInitMsg2.arg1 = nCount;
                            syncDbHandler.sendMessage(progressBarInitMsg2);

                        }
                    }
                }

            } catch (IOException ie) {
                ie.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            progressDialog.dismiss();
        }

        /**
         * CSV 파일 불러오기
         */
        private Map<String, String> readBloopPressureMeasureData(String[] nextLine) {

            Map<String, String> readData = new HashMap<String, String>();

            String strAllData = nextLine[0];
            String strAllDataFrom = "";
            String strAllDataTo = "";

            if (strAllData.length() > 10) {
                strAllDataFrom = strAllData.substring(0, 4);
                strAllDataTo = strAllData.substring(0, 2);
            }
            if (!"All data".equals(strAllData)) {
                if (!"From".equals(strAllDataFrom)) {
                    if (!"To".equals(strAllDataTo)) {
                        if (!"".equals(strAllData)) {
                            if ("BP Log".equals(dirCheck)) {
                                String[] data = nextLine[0].split(";", 10);
                                String date = data[0];
                                String sys = data[2];
                                String dia = data[3];
                                String pulse = data[4];
                                String memo = data[7];

                                if (!sys.isEmpty()) {
                                    readData.put("Systolic", sys);
                                } else {
                                    readData.put("Systolic", "0");
                                    Crashlytics.getInstance().core.log(3,
                                            "BPDiaryError",
                                            "BP Log's systolic is null or isEmpty");
                                }

                                if (!dia.isEmpty()) {
                                    readData.put("Diastolic", dia);
                                } else {
                                    readData.put("Diastolic", "0");
                                    Crashlytics.getInstance().core.log(3,
                                            "BPDiaryError",
                                            "BP Log's diastolic is null or isEmpty");
                                }

                                if (!pulse.isEmpty()) {
                                    readData.put("Pulse", pulse);
                                } else {
                                    readData.put("Pulse", "0");
                                    Crashlytics.getInstance().core.log(3,
                                            "BPDiaryError",
                                            "BP Log's pulse is null or isEmpty");
                                }

                                if (!date.isEmpty()) {
                                    sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    try {

                                        tempDate = sdFormat.parse(date);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String strDate = format.format(tempDate);
                                    readData.put("Date", strDate);

                                }

                                if (!memo.isEmpty()) {
                                    readData.put("Memo", memo);
                                } else {
                                    readData.put("Memo", "");
                                }
                            }
                        } else {

                        }
                    }
                }
            } else {

            }

            return readData;

        }

        /**
         * DB 저장하기
         */
        private boolean saveBloopPressureMeasureData(Map<String, String> pMap) {

            boolean isSuccess = true;

            try {
                Map<Object, Object> requestJSON = new HashMap<Object, Object>();
                JSONObject resultJSON = new JSONObject();

                String strDbSeq = null;

                String strSysValue = pMap.get("Systolic").toString();
                String strDiaValue = pMap.get("Diastolic").toString();
                String strPulValue = pMap.get("Pulse").toString();
                String strTypeValue = HealthcareUtil.getBloodPressureType(CsvOtherAppsInputListActivity.this,
                        pMap.get("Systolic").toString(),
                        pMap.get("Diastolic").toString());
                String strMeasureDt = pMap.get("Date").toString();
                String strMemoValue = pMap.get("Memo").toString();

                //평균혈압 = 최저혈압 + ((최고혈압 - 최저혈압) / 3)
                String strMeanValue = String.valueOf(Double.parseDouble(strDiaValue)
                        + ((Double.parseDouble(strSysValue)
                        - Double.parseDouble(strDiaValue))
                        / 3));

                //DB에 넣을 Data 작성
                Map<String, String> requestMap = new HashMap<String, String>();
                requestMap.put(DataBase.COLUMN_NAME_BP_SYS, strSysValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_DIA, strDiaValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_MEAN, strMeanValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_PULSE, strPulValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_ID, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
                requestMap.put(DataBase.COLUMN_NAME_MESSAGE, strMemoValue);
                requestMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt);
                requestMap.put(DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, ServerSyncYN.SERVER_SYNC_N);

                //DB 저장
                int nRow = bpService.createBloodPressureData(requestMap);

                if (nRow > 0) {

                    isSuccess = true;

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    requestJSON.put(RequestParamName.UUID,
                            PreferenceUtil.getEncEmail(CsvOtherAppsInputListActivity.this));
                    requestJSON.put(RequestParamName.INS_DT, requestMap.get(DataBase.COLUMN_NAME_INS_DT));
                    requestJSON.put(RequestParamName.BP_SYS, requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                    requestJSON.put(RequestParamName.BP_DIA, requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                    requestJSON.put(RequestParamName.BP_MEAN, requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                    requestJSON.put(RequestParamName.BP_PULSE, requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                    requestJSON.put(RequestParamName.BP_TYPE, requestMap.get(DataBase.COLUMN_NAME_BP_TYPE));
                    requestJSON.put(RequestParamName.SENSOR_COMPANY,
                            requestMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    requestJSON.put(RequestParamName.SENSOR_MODEL, requestMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
                    requestJSON.put(RequestParamName.SENSOR_SN, requestMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
                    requestJSON.put(RequestParamName.MESSAGE, requestMap.get(DataBase.COLUMN_NAME_MESSAGE));
                    requestJSON.put(RequestParamName.RECORD_DT,
                            ManagerUtil.convertDateFormatToServer(requestMap.get(DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = bpService.sendBloodPressureData(requestJSON);

                    if (resultJSON.has(ResponseParamName.RESULT)) {

                        if (ResponseResult.RESULT_TRUE.equals(resultJSON.get(ResponseParamName.RESULT).toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            bpService.updateSendToServerYN(strDbSeq);

                        }
                    }

                } else {
                    isSuccess = false;
                }

            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            }

            return isSuccess;

        }
    }

    /**
     * 외부 CSV 파일 Sync
     */
    private class ReaderDBSync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(CsvOtherAppsInputListActivity.this, R.style.ProgressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.csv_file_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... param) {

            String strFilePath = param[0].toString();

            SQLiteDatabase db = SQLiteDatabase.openDatabase(strFilePath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = null;
            try {
                ArrayList<Map<String, String>> arrList = new ArrayList<Map<String, String>>();
                cursor = db.rawQuery("select * from tranx;", null);

                int maxCnt = 0;
                int nowCnt = 0;

                while (cursor.moveToNext()) {
                    nowCnt++;

                    Map<String, String> data = new HashMap<String, String>();
                    sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

                    try {
                        tempDate = sdFormat.parse(cursor.getString(cursor.getColumnIndex("tranxDate")) + " "
                                + cursor.getString(cursor.getColumnIndex("tranxTime")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String strDate = format.format(tempDate);

                    data.put("sys", cursor.getString(cursor.getColumnIndex("sys")));
                    data.put("dia", cursor.getString(cursor.getColumnIndex("dia")));
                    data.put("pulse", cursor.getString(cursor.getColumnIndex("pulse")));
                    data.put("date", strDate);

                    arrList.add(data);
                }

                Message progressBarInitMsg = Message.obtain();

                progressBarInitMsg.what = 0;
                progressBarInitMsg.arg1 = nowCnt;
                syncDbHandler.sendMessage(progressBarInitMsg);

                for (int i = 0; i < arrList.size(); i++) {
                    if (saveBloopPressureMeasureData(arrList.get(i))) {
                        Message progressBarInitMsg2 = Message.obtain();

                        nowCnt++;
                        progressBarInitMsg2.what = 1;
                        progressBarInitMsg2.arg1 = nowCnt;
                        syncDbHandler.sendMessage(progressBarInitMsg2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

                if (db != null) {
                    db.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            progressDialog.dismiss();
        }

        /**
         * DB 저장하기
         */
        private boolean saveBloopPressureMeasureData(Map<String, String> pMap) {

            boolean isSuccess = true;

            try {
                Map<Object, Object> requestJSON = new HashMap<Object, Object>();
                JSONObject resultJSON = new JSONObject();

                String strDbSeq = null;

                Log.i("1234", "1234 sys : " + pMap.get("sys").toString());
                Log.i("1234", "1234 dia : " + pMap.get("dia").toString());
                Log.i("1234", "1234 pulse : " + pMap.get("pulse").toString());
                Log.i("1234", "1234 date : " + pMap.get("date").toString());

                String strSysValue = pMap.get("sys").toString();
                String strDiaValue = pMap.get("dia").toString();
                String strPulValue = pMap.get("pulse").toString();
                String strTypeValue = HealthcareUtil.getBloodPressureType(CsvOtherAppsInputListActivity.this,
                        pMap.get("sys").toString(),
                        pMap.get("dia").toString());
                String strMeasureDt = pMap.get("date").toString();
                //                String strMemoValue = pMap.get("Memo").toString();
                String strMemoValue = "";

                //평균혈압 = 최저혈압 + ((최고혈압 - 최저혈압) / 3)
                String strMeanValue = String.valueOf(Double.parseDouble(strDiaValue)
                        + ((Double.parseDouble(strSysValue)
                        - Double.parseDouble(strDiaValue))
                        / 3));

                //DB에 넣을 Data 작성
                Map<String, String> requestMap = new HashMap<String, String>();
                requestMap.put(DataBase.COLUMN_NAME_BP_SYS, strSysValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_DIA, strDiaValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_MEAN, strMeanValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_PULSE, strPulValue);
                requestMap.put(DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_ID, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL, "");
                requestMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
                requestMap.put(DataBase.COLUMN_NAME_MESSAGE, strMemoValue);
                requestMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt);
                requestMap.put(DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, ServerSyncYN.SERVER_SYNC_N);

                //DB 저장
                int nRow = bpService.createBloodPressureData(requestMap);

                if (nRow > 0) {

                    isSuccess = true;

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    requestJSON.put(RequestParamName.UUID,
                            PreferenceUtil.getEncEmail(CsvOtherAppsInputListActivity.this));
                    requestJSON.put(RequestParamName.INS_DT, requestMap.get(DataBase.COLUMN_NAME_INS_DT));
                    requestJSON.put(RequestParamName.BP_SYS, requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                    requestJSON.put(RequestParamName.BP_DIA, requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                    requestJSON.put(RequestParamName.BP_MEAN, requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                    requestJSON.put(RequestParamName.BP_PULSE, requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                    requestJSON.put(RequestParamName.BP_TYPE, requestMap.get(DataBase.COLUMN_NAME_BP_TYPE));
                    requestJSON.put(RequestParamName.SENSOR_COMPANY,
                            requestMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    requestJSON.put(RequestParamName.SENSOR_MODEL, requestMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
                    requestJSON.put(RequestParamName.SENSOR_SN, requestMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
                    requestJSON.put(RequestParamName.MESSAGE, requestMap.get(DataBase.COLUMN_NAME_MESSAGE));
                    requestJSON.put(RequestParamName.RECORD_DT,
                            ManagerUtil.convertDateFormatToServer(requestMap.get(DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = bpService.sendBloodPressureData(requestJSON);

                    if (resultJSON.has(ResponseParamName.RESULT)) {

                        if (ResponseResult.RESULT_TRUE.equals(resultJSON.get(ResponseParamName.RESULT).toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            bpService.updateSendToServerYN(strDbSeq);

                        }
                    }

                } else {
                    isSuccess = false;
                }

            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            }

            return isSuccess;

        }
    }
}

class JavaFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {

        return (filename.endsWith(".csv")); // 확장자가 csv인지 확인
    }

}

class JavaFileFilterForDB implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {

        return (filename.endsWith(".db")); // 확장자가 db 확인
    }
}

class JavaFileFilterForBCK implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {

        return (filename.endsWith(".bck")); // 확장자가 bck 확인
    }

}

class JavaDirFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}
