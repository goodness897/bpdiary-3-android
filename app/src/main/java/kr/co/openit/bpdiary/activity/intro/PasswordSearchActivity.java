package kr.co.openit.bpdiary.activity.intro;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by Hwang on 2016-12-27.
 */

public class PasswordSearchActivity extends NonMeasureActivity {

    private static final int RESULT_FIND_PASSWORD_SUCCESS = 1000;

    private EditText etEmail;

    private Button btnPasswordInput;

    private TextView tvTitle;

    private ImageView ivTitle;

    private TextView tvContent;

    private LinearLayout llBack;

    private boolean isEnable = false;

    private IntroService introService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_password_or_shard_report);

        AnalyticsUtil.sendScene(PasswordSearchActivity.this, "3_로그인 비밀번호 찾기");

        introService = new IntroService(PasswordSearchActivity.this);

        tvTitle = (TextView)findViewById(R.id.tv_title);
        ivTitle = (ImageView)findViewById(R.id.iv_title);
        tvContent = (TextView)findViewById(R.id.tv_content);
        etEmail = (EditText)findViewById(R.id.et_email);
        btnPasswordInput = (Button)findViewById(R.id.btn_send);
        llBack = (LinearLayout)findViewById(R.id.ll_back);

        tvTitle.setText(R.string.search_password);
        ivTitle.setBackgroundResource(R.drawable.ic_start_screen_findpw);
        tvContent.setText(R.string.search_default);
        btnPasswordInput.setText(R.string.search_btn_send);
        btnPasswordInput.setEnabled(false);

        llBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });

        btnPasswordInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    if (isEnable) {
                        if (BPDiaryApplication.isNetworkState(PasswordSearchActivity.this)) {
                            new FindPasswordAsync().execute();
                        } else {
                            //다이얼로그 호출 후 앱 종료
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                            "",
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

        TextWatcher firstNameWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ManagerUtil.isEmail(etEmail.getText().toString())) {
                        btnPasswordInput.setEnabled(true);
                        isEnable = true;
                        PreferenceUtil.setEncEmail(PasswordSearchActivity.this, etEmail.getText().toString());
                    } else {
                        btnPasswordInput.setEnabled(false);
                        isEnable = false;
                    }
                } else {
                    btnPasswordInput.setEnabled(false);
                    isEnable = false;
                }
            }
        };
        etEmail.addTextChangedListener(firstNameWatcher);
    }

    /**
     * 사용자 정보 수정
     */
    private class FindPasswordAsync extends AsyncTask<Void, Void, JSONObject> {

        String strEmail = "";

        @Override
        protected void onPreExecute() {
            showLodingProgress();

            strEmail = etEmail.getText().toString();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                data.put(ManagerConstants.RequestParamName.UUID,
                         PreferenceUtil.getEncEmail(PasswordSearchActivity.this));
                //TODO Profile 이미지
                resultJSON = introService.findPassword(data);

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
                        Intent intent = new Intent(PasswordSearchActivity.this, SendSuccessActivity.class);
                        intent.putExtra(ManagerConstants.SharedPreferencesName.SHARED_EMAIL, strEmail);
                        startActivityForResult(intent, RESULT_FIND_PASSWORD_SUCCESS);
                    } else {
                        if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                  .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                                                          new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                                                                                     getString(R.string.search_fail_find_password_title),
                                                                                                     getString(R.string.common_required_value_error_comment),
                                                                                                     getString(R.string.common_txt_confirm),
                                                                                                     new IDefaultOneButtonDialog() {

                                                                                                         @Override
                                                                                                         public void
                                                                                                                onConfirm() {
                                                                                                         }
                                                                                                     });
                            defaultOneButtonDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_E.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                         .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                                                          new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                                                                                     "",
                                                                                                     getString(R.string.common_error_comment),
                                                                                                     getString(R.string.common_txt_confirm),
                                                                                                     new IDefaultOneButtonDialog() {

                                                                                                         @Override
                                                                                                         public void
                                                                                                                onConfirm() {
                                                                                                         }
                                                                                                     });
                            defaultOneButtonDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_N.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                         .toString())) {
                            DefaultDialog defaultDialog = new DefaultDialog(PasswordSearchActivity.this,
                                                                            getString(R.string.search_fail_find_password_title),
                                                                            getString(R.string.search_fail_common_no_account),
                                                                            getString(R.string.common_txt_cancel),
                                                                            getString(R.string.common_sign_up),
                                                                            new IDefaultDialog() {

                                                                                @Override
                                                                                public void onCancel() {
                                                                                }

                                                                                @Override
                                                                                public void onConfirm() {
                                                                                    Intent intent =
                                                                                                  new Intent(PasswordSearchActivity.this,
                                                                                                             SignUpActivity.class);
                                                                                    startActivity(intent);
                                                                                }
                                                                            });
                            defaultDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_L.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                         .toString())) {
                            if (ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                                                                              .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                                                                                         "",
                                                                                                         getString(R.string.fail_reason_content_google),
                                                                                                         getString(R.string.common_login),
                                                                                                         new IDefaultOneButtonDialog() {

                                                                                                             @Override
                                                                                                             public void
                                                                                                                    onConfirm() {
                                                                                                                 Intent intent =
                                                                                                                               new Intent(PasswordSearchActivity.this,
                                                                                                                                          LoginActivity.class);
                                                                                                                 startActivity(intent);
                                                                                                             }
                                                                                                         });
                                defaultOneButtonDialog.show();
                            } else if (ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                                                                                       .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                                                                                         "",
                                                                                                         getString(R.string.fail_reason_content_facebook),
                                                                                                         getString(R.string.common_login),
                                                                                                         new IDefaultOneButtonDialog() {

                                                                                                             @Override
                                                                                                             public void
                                                                                                                    onConfirm() {
                                                                                                                 Intent intent =
                                                                                                                               new Intent(PasswordSearchActivity.this,
                                                                                                                                          LoginActivity.class);
                                                                                                                 startActivity(intent);
                                                                                                             }
                                                                                                         });
                                defaultOneButtonDialog.show();
                            } else {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(context,
                                                                                                         "",
                                                                                                         getString(R.string.fail_reason_content_email),
                                                                                                         getString(R.string.common_login),
                                                                                                         new IDefaultOneButtonDialog() {

                                                                                                             @Override
                                                                                                             public void
                                                                                                                    onConfirm() {
                                                                                                                 Intent intent =
                                                                                                                               new Intent(PasswordSearchActivity.this,
                                                                                                                                          LoginActivity.class);
                                                                                                                 startActivity(intent);
                                                                                                             }
                                                                                                         });
                                defaultOneButtonDialog.show();
                            }
                        }
                    }
                } else {
                    //TODO 에러 팝업
                    DefaultOneButtonDialog defaultOneButtonDialog =
                                                                  new DefaultOneButtonDialog(PasswordSearchActivity.this,
                                                                                             "",
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

            } catch (Resources.NotFoundException e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_FIND_PASSWORD_SUCCESS) {
            finish();
        }
    }
}
