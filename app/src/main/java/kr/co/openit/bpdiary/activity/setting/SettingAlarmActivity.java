package kr.co.openit.bpdiary.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.intro.LoginLogicActivity;
import kr.co.openit.bpdiary.activity.setting.item.SettingAlarmItem;
import kr.co.openit.bpdiary.activity.setting.view.SettingAlarmView;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.AlarmSyncFlag;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.services.MedicineMeasureAlarmService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/*복약/측정 알람*/
public class SettingAlarmActivity extends BaseActivity {

    /**
     * 알람이 없을때의 뷰
     */
    private LinearLayout mLLNoAlarmLayout;

    /**
     * 알람이 있을때의 뷰
     */
    private LinearLayout mLLAlarmLayout;

    private LinearLayout mLLDeleteButton;

    private LinearLayout mDeleteBtn;

    private LinearLayout mLLAllAlarmSelect;

    private LinearLayout mPlusBtn;

    private SettingAlarmAdapter mAdapter;

    private DefaultDialog mDialog;

    private ListView mListView;

    private LinearLayout mLLEmptyView;

    private LinearLayout mBtnBack;

    private TextView titleView;

    /**
     * 전체 선택 체크박스
     */
    private ImageView mAllAlarmCheckImage;

    /**
     * service
     */
    private MedicineMeasureAlarmService mAlarmService;

    /**
     * 복약/측정을 구분하는 플래그
     */
    private String mFlag = null;

    private int mCheckedCount = 0;

    private SparseBooleanArray mIsCheckedArray;

    private ArrayList<Map<String, String>> mDBArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_medicine);

        titleView = (TextView)findViewById(R.id.tv_title);
        mAlarmService = new MedicineMeasureAlarmService(SettingAlarmActivity.this);
        mLLNoAlarmLayout = (LinearLayout)findViewById(R.id.ll_empty_list);
        mLLAlarmLayout = (LinearLayout)findViewById(R.id.ll_alarm_list);
        mPlusBtn = (LinearLayout)findViewById(R.id.ll_img_plus);
        mPlusBtn.setVisibility(View.VISIBLE);

        mAdapter = new SettingAlarmAdapter();
        mListView = (ListView)findViewById(R.id.lv_medicine);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mAdapter);
        mLLEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        mDeleteBtn = (LinearLayout)findViewById(R.id.ll_img_delete);
        mBtnBack = (LinearLayout)findViewById(R.id.ll_navi_back);
        mAllAlarmCheckImage = (ImageView)findViewById(R.id.iv_alarm_all);

        // 복약/측정 알림 추가 뷰로 이동
        mPlusBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    mCheckedCount = 0;

                    Intent intent = new Intent(SettingAlarmActivity.this, AddAlarmActivity.class);

                    if (mFlag.equals(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y)) {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                                        AlarmSyncFlag.MEDICINE_SYNC_Y); // 복약 알림

                    } else {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                                        AlarmSyncFlag.MEASURE_SYNC_Y); // 측정 알림
                    }

                    startActivity(intent);
                }
            }
        });

        // 복약/측정 알림 삭제 뷰로 이동
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    mAdapter.setIsDeleteView();

                    mLLEmptyView.setVisibility(View.VISIBLE);
                    mBtnBack.setVisibility(View.GONE);
                    mPlusBtn.setVisibility(View.GONE);
                    mDeleteBtn.setVisibility(View.GONE);
                    mLLDeleteButton = (LinearLayout)findViewById(R.id.ll_text_delete);
                    mLLAllAlarmSelect = (LinearLayout)findViewById(R.id.ll_alarm_all);
                    mLLAllAlarmSelect.setVisibility(View.VISIBLE);
                    mAllAlarmCheckImage.setSelected(false);

                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        mListView.setItemChecked(i, false);
                    }

                    // 알림 리스트 클릭 리스너
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            mIsCheckedArray = mListView.getCheckedItemPositions();

                            if (mIsCheckedArray.get(position)) {
                                mCheckedCount++;

                            } else {
                                mCheckedCount--;
                                mAllAlarmCheckImage.setSelected(false);
                            }

                            if (mCheckedCount == mListView.getCount()) {
                                mAllAlarmCheckImage.setSelected(true);
                            }

                            if (mListView.getCheckedItemCount() > 0) {
                                mLLDeleteButton.setVisibility(View.VISIBLE);
                                mLLEmptyView.setVisibility(View.GONE);
                            } else {
                                mLLDeleteButton.setVisibility(View.GONE);
                                mLLEmptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    // 전체 선택 버튼 클릭 리스너
                    mLLAllAlarmSelect.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (mAllAlarmCheckImage.isSelected()) {
                                mLLEmptyView.setVisibility(View.GONE);
                                mLLDeleteButton.setVisibility(View.GONE);
                                mAllAlarmCheckImage.setSelected(false);
                                mCheckedCount = 0;

                            } else {
                                mLLDeleteButton.setVisibility(View.VISIBLE);
                                mLLEmptyView.setVisibility(View.GONE);
                                mAllAlarmCheckImage.setSelected(true);
                                mCheckedCount = mListView.getCount();
                            }

                            for (int i = 0; i < mAdapter.getCount(); i++) {
                                mListView.setItemChecked(i, mAllAlarmCheckImage.isSelected());
                            }
                        }
                    });

                    // 삭제버튼 클릭 리스너
                    mLLDeleteButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            setConfirmDeleteDialog();
                        }
                    });
                }
            }
        });

        // 토글 상태 리스너
        // 토글상태가 dim이 아닐 경우에만 토글 상태가 바뀜
        if (!mAdapter.isDim) {
            mAdapter.setOnAdapterAlarmChangeListener(new SettingAlarmAdapter.OnAdapterAlarmChangeListener() {

                @Override
                public void onAdapterAlarmChange(SettingAlarmAdapter adapter, SettingAlarmView view, View testView) {
                    int position = (int)view.getTag();
                    setSettingAlarmState(position, view.isSwitchOn.isChecked());
                }
            });
        }
    }

    /**
     * 복약/측정 알림 토글상태 저장
     */
    private void setSettingAlarmState(int position, boolean isChecking) {
        String toggleStatus = null;

        if (isChecking) {
            toggleStatus = ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON;

        } else {
            toggleStatus = ManagerConstants.AlarmToggleState.ALARM_TOGGLE_OFF;
        }

        // 해당 뷰의 정보를 가져옴
        SettingAlarmItem temp = (SettingAlarmItem)mListView.getItemAtPosition(position);
        String notifySEQ = temp.getSeq();

        if (mFlag.equals(AlarmSyncFlag.MEDICINE_SYNC_Y)) { // 복약알림인 경우
            mAlarmService.updateMedicinMeasureAlarmData(AlarmSyncFlag.MEDICINE_SYNC_Y, notifySEQ, toggleStatus);
            AlarmUtils.getInstance(SettingAlarmActivity.this)
                      .setAlarmManager(SettingAlarmActivity.this,
                                       temp.getTime(),
                                       AlarmSyncFlag.MEDICINE_SYNC_Y,
                                       notifySEQ,
                                       isChecking);

        } else { // 측정알림인 경우
            mAlarmService.updateMedicinMeasureAlarmData(AlarmSyncFlag.MEASURE_SYNC_Y, notifySEQ, toggleStatus);
            AlarmUtils.getInstance(SettingAlarmActivity.this)
                      .setAlarmManager(SettingAlarmActivity.this,
                                       temp.getTime(),
                                       AlarmSyncFlag.MEASURE_SYNC_Y,
                                       notifySEQ,
                                       isChecking);
        }
    }

    /**
     * 복약/측정 알림 리스트에 셋팅
     */
    private void setSettingAlarmData(int dbSize) {
        mAdapter.clear();

        for (int i = 0; i < dbSize; i++) {
            SettingAlarmItem model = new SettingAlarmItem();
            model.setSeq(mDBArrayList.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ));
            model.setTime(mDBArrayList.get(i)
                                      .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME));

            if (mDBArrayList.get(i)
                            .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                            .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON)) { // 토글 ON

                model.setStatus(true);

            } else if (mDBArrayList.get(i)
                                   .get(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)
                                   .equals(ManagerConstants.AlarmToggleState.ALARM_TOGGLE_OFF)) { // 토글 OFF

                model.setStatus(false);
            }

            /*
             * 복약/측정 구분하는 플래그 어댑터에 보내기 (뷰의 이미지 때문에)
             */
            if (mFlag != null && mFlag.equals(AlarmSyncFlag.MEDICINE_SYNC_Y)) {
                mAdapter.setIsMedicineView(true);
            } else {
                mAdapter.setIsMedicineView(false);
            }

            mAdapter.add(model);
        }
    }

    /**
     * 삭제버튼 누를시 뜨는 다이얼로그
     */
    private void setConfirmDeleteDialog() {
        mDialog = new DefaultDialog(SettingAlarmActivity.this,
                                    getString(R.string.dialog_title_alarm),
                                    getString(R.string.common_dialog_txt_content),
                                    getString(R.string.dialog_cancel),
                                    getString(R.string.dialog_delete),
                                    new IDefaultDialog() {

                                        @Override
                                        public void onCancel() {
                                            mDialog.dismiss();
                                        }

                                        @Override
                                        public void onConfirm() {
                                            if (mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {

                                                SparseBooleanArray array = mListView.getCheckedItemPositions();
                                                List<SettingAlarmItem> removeList = new ArrayList<>();

                                                for (int index = 0; index < array.size(); index++) {
                                                    int position = array.keyAt(index);

                                                    if (array.get(position)) {
                                                        // 해당뷰의 정보를 가져옴
                                                        SettingAlarmItem temp =
                                                                              (SettingAlarmItem)mListView.getItemAtPosition(position);

                                                        // 알람을 구별하는 노티SEQ
                                                        int notifySEQ = Integer.parseInt(temp.getSeq());

                                                        AlarmUtils.getInstance(SettingAlarmActivity.this)
                                                                  .setCancelAlarm(SettingAlarmActivity.this, notifySEQ);

                                                        removeList.add(temp);

                                                        // 복약알림인 경우 DB에서 해당값 삭제
                                                        if (mFlag.equals(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y)) {
                                                            mAlarmService.deleteMedicineMeasureData(AlarmSyncFlag.MEDICINE_SYNC_Y,
                                                                                                    notifySEQ);

                                                            // 측정알림인 경우 DB에서 해당값 삭제
                                                        } else {
                                                            mAlarmService.deleteMedicineMeasureData(AlarmSyncFlag.MEASURE_SYNC_Y,
                                                                                                    notifySEQ);
                                                        }
                                                    }
                                                }

                                                if (removeList.size() > 0) {
                                                    for (SettingAlarmItem m : removeList) {
                                                        mAdapter.remove(m);
                                                    }
                                                }

                                                mCheckedCount = 0;
                                                mLLEmptyView.setVisibility(View.GONE);
                                                mListView.clearChoices();
                                                mLLDeleteButton.setVisibility(View.GONE);
                                                mAdapter.setIsListView();
                                                mListView.setOnItemClickListener(null);
                                            }

                                            setAlarmLayout();
                                        }
                                    });

        mDialog.show();
    }

    /**
     * 복약/측정 알림 리스트 화면 ON
     */
    private void setAlarmLayout() {
        mAdapter.setIsListView();

        mListView.setOnItemClickListener(null);
        mBtnBack.setVisibility(View.VISIBLE);
        mPlusBtn.setVisibility(View.VISIBLE);
        mDeleteBtn.setVisibility(View.VISIBLE);
        mLLEmptyView.setVisibility(View.GONE);
        mLLDeleteButton.setVisibility(View.GONE);
        mLLAllAlarmSelect.setVisibility(View.GONE);

        onResume();
    }

    /**
     * DB에서 DATA 가져오기
     */
    private void setDBData() {
        Intent intent = getIntent();
        mFlag = intent.getStringExtra(ManagerConstants.IntentData.ALARM_FLAG);

        // 저장된 DB값 Array에 저장
        mDBArrayList = new ArrayList<>();

        // 복약알림인 경우
        if (mFlag != null && mFlag.equals(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y)) {
            if (mAlarmService.searchMedicineData() != null) {
                mDBArrayList.addAll(mAlarmService.searchMedicineData());
            }
            // 측정알림인 경우
        } else {
            if (mAlarmService.searchMeasureData() != null) {
                mDBArrayList.addAll(mAlarmService.searchMeasureData());
            }
        }

        // 저장된 알람이 없으면 emptyLayout.setVisible, 있으면 alarmLayout.setVisible
        if (mDBArrayList.size() == 0) {
            mLLNoAlarmLayout.setVisibility(View.VISIBLE);
            mDeleteBtn.setVisibility(View.GONE);
            initToolbar(getString(R.string.setting_activity_no_alarm_list));

        } else {
            mLLNoAlarmLayout.setVisibility(View.GONE);
            mLLAlarmLayout.setVisibility(View.VISIBLE);

            if (mAdapter.isDeleteView == true) {
                if (mFlag.equals(AlarmSyncFlag.MEDICINE_SYNC_Y)) { // 복약알림
                    titleView.setText(R.string.setting_activity_alarm_medicine);
                    AnalyticsUtil.sendScene(SettingAlarmActivity.this, "3_M 알림 복약");

                } else { // 측정알림
                    titleView.setText(R.string.main_blutooth_input);
                    AnalyticsUtil.sendScene(SettingAlarmActivity.this, "3_M 알림 측정");
                }

            } else {
                mDeleteBtn.setVisibility(View.VISIBLE);

                if (mFlag.equals(AlarmSyncFlag.MEDICINE_SYNC_Y)) { // 복약알림
                    initToolbar(getString(R.string.setting_activity_alarm_medicine));
                    AnalyticsUtil.sendScene(SettingAlarmActivity.this, "3_M 알림 복약");

                } else { // 측정알림
                    initToolbar(getString(R.string.main_blutooth_input));
                    AnalyticsUtil.sendScene(SettingAlarmActivity.this, "3_M 알림 측정");
                }
            }

            // List에 데이터 셋팅
            setSettingAlarmData(mDBArrayList.size());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 전체 토글 상태가 OFF면 dim용 Layout Visible 시킴
        if (!PreferenceUtil.getWholeToggleState(SettingAlarmActivity.this)) {
            mAdapter.isDim = true;
        }

        setDBData();
    }

    /**
     * 복약/측정 알림 어댑터
     */
    static class SettingAlarmAdapter extends BaseAdapter implements SettingAlarmView.OnMedicineSwitchChangeListener {

        List<SettingAlarmItem> items = new ArrayList<>();

        boolean isDeleteView;

        boolean isMedicineView;

        boolean isDim = false;

        // 복약/측정 알림 삭제 화면
        public void setIsDeleteView() {
            isDeleteView = true;
            notifyDataSetChanged();
        }

        // 복약/측정 알림 리스트 화면
        public void setIsListView() {
            isDeleteView = false;
            notifyDataSetChanged();
        }

        // 복약/측정 구분 플래그
        public void setIsMedicineView(boolean isMedicineView) {
            this.isMedicineView = isMedicineView;
            notifyDataSetChanged();
        }

        public void add(SettingAlarmItem model) {
            items.add(model);
            notifyDataSetChanged();
        }

        public void remove(SettingAlarmItem model) {
            items.remove(model);
            notifyDataSetChanged();
        }

        public void clear() {
            items.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SettingAlarmView view;

            if (convertView == null) {
                view = new SettingAlarmView(parent.getContext());
                convertView = view;

            } else {
                view = (SettingAlarmView)convertView;
            }

            view.setTag(position);
            view.setBaseAlarmData(items.get(position), isDeleteView, isMedicineView, isDim);

            if (!isDim) {
                view.setMedicineSwitchChangeListener(this);

            } else {
                view.isSwitchOn.setClickable(false);
            }
            return view;
        }

        // Switch ChangeListener
        @Override
        public void onMedicineSwitchChange(SettingAlarmView view, View testView) {
            if (mChangeListener != null) {
                mChangeListener.onAdapterAlarmChange(this, view, testView);
            }
        }

        public interface OnAdapterAlarmChangeListener {

            public void onAdapterAlarmChange(SettingAlarmAdapter adapter, SettingAlarmView view, View testView);
        }

        OnAdapterAlarmChangeListener mChangeListener;

        public void setOnAdapterAlarmChangeListener(OnAdapterAlarmChangeListener changeListener) {
            this.mChangeListener = changeListener;
        }
    }

    @Override
    public void onBackPressed() {
        // 현재뷰가 삭제뷰일 경우
        if (mAdapter.isDeleteView == true) {
            mAdapter.setIsListView();

            mCheckedCount = 0;
            mLLEmptyView.setVisibility(View.GONE);
            mListView.setOnItemClickListener(null);
            mBtnBack.setVisibility(View.VISIBLE);
            mPlusBtn.setVisibility(View.VISIBLE);
            mDeleteBtn.setVisibility(View.VISIBLE);
            mLLAllAlarmSelect.setVisibility(View.GONE);
            mLLDeleteButton.setVisibility(View.GONE);

            // 현재뷰가 리스트뷰인 경우
        } else {
            mCheckedCount = 0;
            super.onBackPressed();
        }
    }
}
