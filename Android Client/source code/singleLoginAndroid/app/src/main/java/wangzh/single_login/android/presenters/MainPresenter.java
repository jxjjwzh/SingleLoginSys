package wangzh.single_login.android.presenters;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import wangzh.single_login.android.R;
import wangzh.single_login.android.bean.ResultVo;
import wangzh.single_login.android.constants.FuncNoConstants;
import wangzh.single_login.android.constants.ParamConstants;
import wangzh.single_login.android.rpc.RPCClientAgency;
import wangzh.single_login.android.ui.MainActivity;
import wangzh.single_login.android.utils.DeviceUtil;
import wangzh.single_login.android.utils.LogUtil;
import wangzh.single_login.android.utils.RSAUtil;

/**
 * 相关注释待本类代码稳定后添加，敬请期待！
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @project singleLoginAndroid
 * @date 2018/7/4
 */
public class MainPresenter {

    private static final String HOST = "192.168.3.4";
    private static final int PORT = 50052;

    private MainHandler mMainHandler;

    private MainActivity mActivity;

    public MainPresenter(MainActivity activity) {
        mMainHandler = new MainHandler(activity);
        mActivity = activity;
        RPCClientAgency.getInstance().start(HOST, PORT);
    }

    private void generateRSAInfo(final IRSACallback irsaCallback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                RPCClientAgency rpcClientAgency = RPCClientAgency.getInstance();
                HashMap<String, String> params = new HashMap<>();
                params.put(ParamConstants.DEVICE_ID, DeviceUtil.getDeviceId(mActivity));
                LogUtil.d("RSA接口调用开始！入参：" + params);
                rpcClientAgency.invokeServer(FuncNoConstants.FUNC_GENERATE_RSA_INFO, params,
                        new RPCClientAgency.ICallBack() {
                            @Override
                            public void callBack(ResultVo resultVo) {
                                LogUtil.d("RSA接口调用结束！结果集：" + resultVo.toString());
                                // 获取公钥
                                String xKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_X_KEY);
                                String yKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_Y_KEY);
                                irsaCallback.onGetRSAInfo(xKey, yKey);
                            }
                        });
            }
        }.start();
    }

    /**
     * 登录操作
     */
    public void login(final String account, final String password) {
        if (!checkInput(account, password)) {
            return;
        }
        // 先获取RSA公钥
        generateRSAInfo(new IRSACallback() {
            @Override
            public void onGetRSAInfo(String xKey, String yKey) {
                // 获取了RSA公钥后开始登录
                HashMap<String, String> userParams = new HashMap<>();
                userParams.put(ParamConstants.DEVICE_ID, DeviceUtil.getDeviceId(mActivity));
                userParams.put(ParamConstants.ACCOUNT, account);
                userParams.put(ParamConstants.PASSWORD, RSAUtil.encryptByPublicKey(xKey, yKey, password));
                LogUtil.d("登录接口调用开始！入参：" + userParams);
                RPCClientAgency.getInstance().invokeServer(FuncNoConstants.FUNC_LOGIN, userParams, new RPCClientAgency.ICallBack() {
                    @Override
                    public void callBack(ResultVo resultVo) {
                        if (resultVo.getError_no() == 2) {
                            LogUtil.w("客户端被强制下线！");
                        } else {
                            LogUtil.d("登录请求接口调用结束！结果：" + resultVo.toString());
                        }

                        Message msg = Message.obtain();
                        msg.what = FuncNoConstants.FUNC_LOGIN;
                        msg.obj = resultVo;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });

    }

    /**
     * 注册操作
     */
    public void register(final String account, final String password) {
        if (!checkInput(account, password)) {
            return;
        }
        // 先获取RSA公钥
        generateRSAInfo(new IRSACallback() {
            @Override
            public void onGetRSAInfo(String xKey, String yKey) {
                HashMap<String, String> params = new HashMap<>();
                params.put(ParamConstants.DEVICE_ID, DeviceUtil.getDeviceId(mActivity));
                params.put(ParamConstants.ACCOUNT, account);
                params.put(ParamConstants.PASSWORD, RSAUtil.encryptByPublicKey(xKey, yKey, password));
                RPCClientAgency rpcClientAgency = RPCClientAgency.getInstance();
                LogUtil.d("注册接口调用开始！入参：" + params);
                rpcClientAgency.invokeServer(FuncNoConstants.FUNC_REGISTER, params, new RPCClientAgency.ICallBack() {
                    @Override
                    public void callBack(ResultVo resultVo) {
                        if (resultVo.getError_no() == 2) {
                            LogUtil.w("账号[" + account + "]在其他设备上登录，强制下线！");
                        } else {
                            LogUtil.d("注册请求接口调用结束！结果：" + resultVo.toString());
                        }

                        Message msg = Message.obtain();
                        msg.what = FuncNoConstants.FUNC_REGISTER;
                        msg.obj = resultVo;
                        mMainHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    /**
     * 当获取到RSA加密公钥后回调数据的接口
     */
    interface IRSACallback {
        void onGetRSAInfo(String xKey, String yKey);
    }

    /**
     * 用户账户相关接口的输入检查
     * return true：入参无误，可以调用服务器。false：入参有误，不能调用服务器
     */
    private boolean checkInput(String account, String pwd) {
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(mActivity, R.string.input_error_account_no_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(mActivity, R.string.input_error_pwd_no_empty, Toast.LENGTH_LONG).show();
            return false;
        }
//        if (TextUtils.isEmpty(mXKey) || TextUtils.isEmpty(mYKey)) {
//            Toast.makeText(mActivity, R.string.input_error_network_busy, Toast.LENGTH_LONG).show();
//            generateRSAInfo();
//            return false;
//        }
        return true;
    }

    /**
     * 用来回调主线程的Handler
     */
    public static class MainHandler extends Handler {

        private WeakReference<MainActivity> mActivityWeakReference;

        MainHandler(MainActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ResultVo resultVo = (ResultVo) msg.obj;
            switch (msg.what) {
                case FuncNoConstants.FUNC_LOGIN:
                case FuncNoConstants.FUNC_REGISTER:
                    mActivityWeakReference.get().onLoginOrRegister(resultVo.getError_info());
                    break;
            }
        }
    }
}
