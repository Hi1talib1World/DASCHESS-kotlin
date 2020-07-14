package com.denzo.daschess.ui;


import android.content.Intent;
import android.os.Handler;

import com.denzo.daschess.MainActivity;
import com.denzo.daschess.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unstoppable.submitbuttonview.SubmitButton;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity<LoginPresenter>
        implements ILoginContract.View {

    private final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.user_name_et)
    TextInputEditText userNameEt;
    @BindView(R.id.user_name_layout)
    TextInputLayout userNameLayout;
    @BindView(R.id.password_et) TextInputEditText passwordEt;
    @BindView(R.id.password_layout) TextInputLayout passwordLayout;
    @BindView(R.id.login_bn)
    SubmitButton loginBn;

    private String userName;
    private String password;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.handleOauth(intent);
        setIntent(null);
    }

    @Override
    public void onGetTokenSuccess(BasicToken basicToken) {
        loginBn.doResult(true);
        mPresenter.getUserInfo(basicToken);
    }

    @Override
    public void onGetTokenError(String errorMsg) {
        loginBn.doResult(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginBn.reset();
                loginBn.setEnabled(true);
            }
        }, 1000);

        Toasty.error(getApplicationContext(), errorMsg).show();
    }

    @Override
    public void onLoginComplete() {
        delayFinish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    /**
     * 依赖注入的入口
     *
     * @param appComponent appComponent
     */
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(new ActivityModule(getActivity()))
                .build()
                .inject(this);
    }

    /**
     * 获取ContentView id
     *
     * @return
     */
    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    /**
     * 初始化view
     *
     * @param savedInstanceState
     */
    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        loginBn.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {

            }
        });


        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN)){
                    if(loginBn.isEnabled())
                        ViewUtils.virtualClick(loginBn);
                }
                return false;
            }
        });

    }


    @OnClick(R.id.oauth_login_bn)
    public void onOauthLoginClick(){
        AppOpener.openInCustomTabsOrBrowser(getActivity(), mPresenter.getOAuth2Url());
    }

    @OnClick(R.id.login_bn)
    public void onLoginClick(){
        if(loginCheck()){
            loginBn.setEnabled(false);
            mPresenter.basicLogin(userName, password);
        }else{
            loginBn.reset();
        }
    }

    private boolean loginCheck(){
        boolean valid = true;
        userName = userNameEt.getText().toString();
        password = passwordEt.getText().toString();
        if(StringUtils.isBlank(userName)){
            valid = false;
            userNameLayout.setError(getString(R.string.user_name_warning));
        }else{
            userNameLayout.setErrorEnabled(false);
        }
        if(StringUtils.isBlank(password)){
            valid = false;
            passwordLayout.setError(getString(R.string.password_warning));
        }else{
            passwordLayout.setErrorEnabled(false);
        }
        return valid;
    }
}