package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;

public class DefaultOneButtonDialog extends Dialog implements OnClickListener {

    /**
     * 제목
     */
    private TextView txtTitle;

    /**
     * 내용
     */
    private TextView txtContent;

    /**
     * 왼쪽 버튼
     */
    private TextView txtLeftAnswer;

    /**
     * 오른쪽 버튼
     */
    private TextView txtRightAnser;

    private String strTitle;

    private String strContent;

    private String strLeftAnswer;

    private IDefaultOneButtonDialog iDefaultDialog;

    private Context context;

    public DefaultOneButtonDialog(Context context,
                                  String title,
                                  String content,
                                  String leftAnswer,
                                  IDefaultOneButtonDialog iDefaultDialog) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.strTitle = title;
        this.strContent = content;
        this.strLeftAnswer = leftAnswer;
        this.iDefaultDialog = iDefaultDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_default_one_button);

        // 언어팩 설정
        //ManagerUtil.setLocale(PreferenceUtil.getLanguage(context), context);

        // 화면 초기 설정
        setDialogLayout();

        // 초기값 설정
        setDialogData();

        txtLeftAnswer.setOnClickListener(this);
    }

    /**
     * 화면 초기 설정
     */
    private void setDialogLayout() {

        txtTitle = (TextView)findViewById(R.id.common_default_dialog_txt_title);
        txtContent = (TextView)findViewById(R.id.common_default_dialog_txt_content);
        txtLeftAnswer = (TextView)findViewById(R.id.common_default_dialog_txt_close);
    }

    /**
     * 초기값 설정
     */
    private void setDialogData() {

        txtTitle.setText(strTitle);
        txtContent.setText(strContent);
        txtLeftAnswer.setText(strLeftAnswer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_default_dialog_txt_close:
                iDefaultDialog.onConfirm();
                dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
