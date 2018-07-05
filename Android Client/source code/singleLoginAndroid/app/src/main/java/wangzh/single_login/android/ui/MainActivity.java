package wangzh.single_login.android.ui;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import wangzh.single_login.android.R;
import wangzh.single_login.android.presenters.MainPresenter;
import wangzh.single_login.android.rpc.RPCClientAgency;
import wangzh.single_login.android.utils.DeviceUtil;
import wangzh.single_login.android.utils.LogUtil;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends Activity {

    /**
     * 账号输入框
     */
    private EditText mEdAccount;
    /**
     * 密码输入框
     */
    private EditText mEdPassword;
    /**
     * 服务器返回信息展示
     */
    private TextView mTvServerInfo;

    /**
     * 视图逻辑、业务逻辑处理
     */
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdAccount = findViewById(R.id.ed_account);
        mEdPassword = findViewById(R.id.ed_password);
        mTvServerInfo = findViewById(R.id.tv_server_info);

        Button mBuLogin = findViewById(R.id.bu_login);
        mBuLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.login(getInputAccount(), getInputPwdEncrypt());
            }
        });

        Button mBuRegister = findViewById(R.id.bu_register);
        mBuRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.register(getInputAccount(), getInputPwdEncrypt());
            }
        });

        // 初始化逻辑处理类
        mPresenter = new MainPresenter(this);

        LogUtil.i("本机设备唯一标识："+ DeviceUtil.getDeviceId(this));

        //TODO：测试代码
        mEdAccount.setText("user01");
        mEdPassword.setText("123456");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RPCClientAgency.getInstance().shutdown();
    }

    private String getInputPwdEncrypt() {
        return mEdPassword.getText().toString().trim();
    }

    private String getInputAccount() {
        return mEdAccount.getText().toString().trim();
    }

    private void generateRSAInfo() {

    }


    public void onLoginOrRegister(String info) {
        mTvServerInfo.setText(info);
    }


}

