package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;

/**
 * Created by hwangem on 2017-01-18.
 */

public class ConditionDialog extends Dialog implements OnClickListener {

    /**
     * 다이얼로그 확인 버튼
     */

    private TextView tvPopUpOk;

    /**
     * 다이얼로그 취소 버튼
     */
    private TextView tvPopUpCancel;

    /**
     * 다이얼로그 인터페이스
     */
    private IDefaultDialog iDefaultDialog;

    /**
     * Context
     */
    private Context context;

    /**
     * 체크박스 첫번째 조건
     */
    private CheckBox cbFirstCondition;

    /**
     * 체크박스 두번째 조건
     */
    private CheckBox cbSecondCondition;

    /**
     * 다이얼로그 타이틀 TextView
     */
    private TextView tvTitle;

    /**
     * 다이얼로그 타이틀 String
     */
    private String title;

    /**
     * 첫번째 조건 String
     */
    private String firstCondition;

    /**
     * 두번째 조건 String
     */
    private String secondCondition;

    public ConditionDialog(Context context,
                           String title,
                           String firstCondition,
                           String secondCondition,
                           IDefaultDialog iDefaultDialog) {
        super(context, R.style.MyDialog);
        this.iDefaultDialog = iDefaultDialog;
        this.context = context;
        this.title = title;
        this.firstCondition = firstCondition;
        this.secondCondition = secondCondition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.layout_dialog_condition);

        setDialogLayout();

        tvTitle.setText(title);
        cbFirstCondition.setText(firstCondition);
        cbSecondCondition.setText(secondCondition);

        cbFirstCondition.setOnClickListener(this);
        cbSecondCondition.setOnClickListener(this);
        tvPopUpOk.setOnClickListener(this);
        tvPopUpCancel.setOnClickListener(this);
    }

    /**
     * 화면 초기 설정
     */
    private void setDialogLayout() {
        tvPopUpOk = (TextView)findViewById(R.id.tv_cancel);
        tvPopUpCancel = (TextView)findViewById(R.id.tv_ok);
        cbFirstCondition = (CheckBox)findViewById(R.id.cb_first_condition);
        cbSecondCondition = (CheckBox)findViewById(R.id.cb_second_condition);
        tvTitle = (TextView)findViewById(R.id.tv_title);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                iDefaultDialog.onConfirm();
                dismiss();
                break;

            case R.id.tv_ok:
                iDefaultDialog.onCancel();
                dismiss();
                break;

            case R.id.cb_first_condition:
                cbFirstCondition.setSelected(true);
                cbSecondCondition.setSelected(false);
                break;

            case R.id.cb_second_condition:
                cbFirstCondition.setSelected(false);
                cbSecondCondition.setSelected(true);
                break;

            default:
                break;
        }
    }
}
