package wangzh.single_login;

import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.constants.FuncNoConstants;
import wangzh.single_login.constants.ParamConstants;
import wangzh.single_login.functions.FunctionFactory;
import wangzh.single_login.functions.IFunction;
import wangzh.single_login.utils.RSAUtil;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Function层的测试
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/7/3
 */
public class FunctionTest {

    private static String xKey, yKey;

    private static final Logger sLogger = Logger.getLogger(FunctionTest.class.getName());

    public static void main(String[] args) {
        testRSA();
        sLogger.info("\n\n");
//        testRegister();
//        sLogger.info("\n\n");
//        testLogin();
        clientTest();
    }

    private static void testRSA() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamConstants.DEVICE_ID, "111111111111111");
        ResultVo resultVo = FunctionFactory.makeService(FuncNoConstants.FUNC_GENERATE_RSA_INFO).invoke(params);
        xKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_X_KEY);
        yKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_Y_KEY);
        sLogger.info("RSA信息请求接口调用结果：" + resultVo.toString());
    }

    private static void testRegister() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamConstants.ACCOUNT, "user01");
        params.put(ParamConstants.PASSWORD, RSAUtil.encryptByPublicKey(xKey, yKey, "123456"));
        params.put(ParamConstants.DEVICE_ID, "111111111111111");
        IFunction service = FunctionFactory.makeService(FuncNoConstants.FUNC_REGISTER);
        sLogger.info("注册接口调用结果：" + service.invoke(params).toString());
    }

    private static void testLogin() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamConstants.ACCOUNT, "user02");
        params.put(ParamConstants.PASSWORD, RSAUtil.encryptByPublicKey(xKey, yKey, "33344433"));
        params.put(ParamConstants.DEVICE_ID, "111111111111111");
        IFunction service = FunctionFactory.makeService(FuncNoConstants.FUNC_LOGIN);
        sLogger.info("登录接口调用结果：" + service.invoke(params).toString());
    }

    private static void clientTest() {
        RPCClientAgency rpcClientAgency = RPCClientAgency.getInstance();
        rpcClientAgency.start("localhost", 50052);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ParamConstants.DEVICE_ID, "device02");
        rpcClientAgency.invokeServer(FuncNoConstants.FUNC_GENERATE_RSA_INFO, params, new RPCClientAgency.ICallBack() {
            @Override
            public void callBack(ResultVo resultVo) {
                sLogger.info("RSA请求调用结束！结果：" + resultVo.toString());
                // 获取公钥
                String xKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_X_KEY);
                String yKey = resultVo.getOutPutsMap().get(ParamConstants.RSA_Y_KEY);

                // 调用登录接口
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(ParamConstants.DEVICE_ID, "device02");
                params.put(ParamConstants.ACCOUNT, "user01");
                params.put(ParamConstants.PASSWORD, RSAUtil.encryptByPublicKey(xKey, yKey, "123456"));
                RPCClientAgency rpcClientAgency = RPCClientAgency.getInstance();
                rpcClientAgency.invokeServer(FuncNoConstants.FUNC_LOGIN, params, new RPCClientAgency.ICallBack() {
                    @Override
                    public void callBack(ResultVo resultVo) {
                        if (resultVo.getError_no() == 2) {
                            sLogger.info("客户端被强制下线！");
                        } else {
                            sLogger.info("登录请求接口调用结束！结果：" + resultVo.toString());
                        }
                    }
                });
            }
        });

        // 客户端进程保持存活
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
