package kr.co.openit.bpdiary.activity.setting;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.setting.item.NotiChildItem;
import kr.co.openit.bpdiary.activity.setting.item.NotiGroupItem;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.services.NoticeService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class NoticeActivity extends NonMeasureActivity {

    private CustomProgressDialog customDialogProgress;

    private List<NotiGroupItem> listData;

    private LinearLayout llEmptyView;

    /**
     * 공지사항 ListView
     */
    private ExpandableListView elvNotice;

    private LinearLayout llNetworkFalse;

    private LinearLayout llNetworkTryAgain;

    /**
     * 공지사항 Adapter
     */
    private NotiAdapter mAdapter;

    /**
     * 측정 결과 DB에서 조회
     */
    private SearchNoticeListSync sNoticeListSync;

    private LinearLayout llAds;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        initToolbar(getString(R.string.setting_notice));
        context = NoticeActivity.this;

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        elvNotice = (ExpandableListView)findViewById(R.id.lv_language);
        llNetworkFalse = (LinearLayout)findViewById(R.id.ll_is_network_false);
        llNetworkTryAgain = (LinearLayout)findViewById(R.id.ll_network_try_again);

        AnalyticsUtil.sendScene(NoticeActivity.this, "3_M 공지사항 리스트");

        /**
         * 광고
         */
        llAds = (LinearLayout)findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(NoticeActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        if (BPDiaryApplication.isNetworkState(context)) {
            llNetworkFalse.setVisibility(View.GONE);
            elvNotice.setVisibility(View.VISIBLE);
            sNoticeListSync = new SearchNoticeListSync();
            sNoticeListSync.execute();

        } else {
            llNetworkFalse.setVisibility(View.VISIBLE);
            elvNotice.setVisibility(View.GONE);
        }

        llNetworkTryAgain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    showLodingProgress();

                    if (BPDiaryApplication.isNetworkState(context)) {
                        llNetworkFalse.setVisibility(View.GONE);
                        elvNotice.setVisibility(View.VISIBLE);
                        sNoticeListSync = new SearchNoticeListSync();
                        sNoticeListSync.execute();

                    } else {
                        llNetworkFalse.setVisibility(View.VISIBLE);
                        elvNotice.setVisibility(View.GONE);
                        mHandler.postDelayed(mMyTask, 1500); // 3초후에 실행
                    }
                }
            }
        });

        elvNotice.setAdapter(mAdapter);

        elvNotice.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        elvNotice.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

    }

    private Runnable mMyTask = new Runnable() {

        @Override
        public void run() {
            hideLodingProgress();
        }
    };

    public class NotiAdapter extends BaseExpandableListAdapter {

        List<NotiGroupItem> items = new ArrayList<>();

        public void put(String groupTitle, String groupDate, String childContent) {
            NotiGroupItem groupItem = null;

            groupItem = new NotiGroupItem();
            groupItem.setTitle(groupTitle);

            Date date = new Date(Long.parseLong(groupDate));
            SimpleDateFormat simpleDateFormat = null;
            ManagerUtil.Localization localization =
                                                  ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(NoticeActivity.this));
            if (localization == ManagerUtil.Localization.TYPE_01) {
                simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
            } else if (localization == ManagerUtil.Localization.TYPE_02) {
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            } else if (localization == ManagerUtil.Localization.TYPE_03) {
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            }

            String strDate = " " + simpleDateFormat.format(date);
            groupItem.setDate(strDate);

            items.add(groupItem);

            if (!TextUtils.isEmpty(childContent)) {
                NotiChildItem child = new NotiChildItem();
                child.setChildContent(childContent);
                groupItem.getChildItems().add(child);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return items.get(groupPosition).getChildItems().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).getChildItems().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return ((long)groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return ((long)groupPosition) << 32 | childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            NotiGroupView view;
            if (convertView == null) {
                view = new NotiGroupView(parent.getContext());

            } else {
                view = (NotiGroupView)convertView;
            }
            view.setGroupItem(items.get(groupPosition));

            if (isExpanded) {
                view.getIndicatorView().setImageResource(R.drawable.ic_noti_acodi_sel);
            } else {
                view.getIndicatorView().setImageResource(R.drawable.ic_noti_acodi_not_sel);
            }

            return view;

        }

        @Override
        public View getChildView(int groupPosition,
                                 int childPosition,
                                 boolean isLastChild,
                                 View convertView,
                                 ViewGroup parent) {
            NotiChildView view;
            if (convertView == null) {
                view = new NotiChildView(parent.getContext());
            } else {
                view = (NotiChildView)convertView;
            }
            view.setChildItem(items.get(groupPosition).getChildItems().get(childPosition));

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class NotiGroupView extends FrameLayout {

        private TextView titleView;

        private TextView dateView;

        private ImageView indicatorView;

        private ImageView newImageView;

        private NotiGroupItem item;

        public NotiGroupView(Context context) {
            super(context);
            init();
        }

        private void init() {
            inflate(getContext(), R.layout.view_noti_group, this);
            titleView = (TextView)findViewById(R.id.tv_title);
            dateView = (TextView)findViewById(R.id.tv_date);
            indicatorView = (ImageView)findViewById(R.id.iv_list);
            newImageView = (ImageView)findViewById(R.id.iv_new);
        }

        public void setGroupItem(NotiGroupItem item) {
            this.item = item;
            titleView.setText(item.getTitle());
            dateView.setText(item.getDate());
            if (item.getDate().equals(DateUtil.getDateNow("yyyy.MM.dd"))) {
                newImageView.setVisibility(VISIBLE);
            } else {
                newImageView.setVisibility(GONE);
            }

        }

        public ImageView getIndicatorView() {
            return indicatorView;
        }
    }

    public class NotiChildView extends FrameLayout {

        private TextView contentView;

        private NotiChildItem item;

        public NotiChildView(Context context) {
            super(context);
            init();
        }

        private void init() {
            inflate(getContext(), R.layout.view_noti_child, this);
            contentView = (TextView)findViewById(R.id.tv_child_content);
        }

        public void setChildItem(NotiChildItem item) {
            this.item = item;
            contentView.setText(Html.fromHtml(item.getChildContent()));

        }
    }

    private class SearchNoticeListSync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            if (customDialogProgress == null) {
                customDialogProgress = new CustomProgressDialog(NoticeActivity.this);
            }
            customDialogProgress.show();
            customDialogProgress.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            NoticeService noticeService = new NoticeService(NoticeActivity.this);

            Map<Object, Object> requestJSON = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {

                resultJSON = noticeService.searchNoticeList(requestJSON);

            } catch (Exception e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            String title = "";
            String date = "";
            String content = "";

            mAdapter = new NotiAdapter();

            if (customDialogProgress != null && customDialogProgress.isShowing()) {
                customDialogProgress.hide();
            }

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {

                        if (resultJSON.has(ManagerConstants.ResponseParamName.BOARD_LIST)) {

                            JSONArray resultJSONArray =
                                                      new JSONArray(resultJSON.getString(ManagerConstants.ResponseParamName.BOARD_LIST));
                            listData = new ArrayList<NotiGroupItem>();

                            if (resultJSONArray.length() > 0) {

                                for (int i = 0; i < resultJSONArray.length(); i++) {

                                    JSONObject itemJSON = resultJSONArray.getJSONObject(i);

                                    if (itemJSON.has(ManagerConstants.ResponseParamName.BOARD_TITLE)) {
                                        title = itemJSON.getString(ManagerConstants.ResponseParamName.BOARD_TITLE);
                                    } else {
                                        title = "";
                                    }

                                    if (itemJSON.has(ManagerConstants.ResponseParamName.BOARD_CONTENT)) {
                                        content = itemJSON.getString(ManagerConstants.ResponseParamName.BOARD_CONTENT);
                                    } else {
                                        content = "";
                                    }

                                    if (itemJSON.has(ManagerConstants.ResponseParamName.BOARD_START_DT)) {
                                        date = itemJSON.getString(ManagerConstants.ResponseParamName.BOARD_START_DT);
                                    } else {
                                        date = "";
                                    }

                                    mAdapter.put(title, date, content);
                                }
                            }

                            elvNotice.setAdapter(mAdapter);

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
