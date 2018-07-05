package wangzh.single_login.functions;

import wangzh.single_login.constants.FuncNoConstants;

/**
 * 生产业务类的简单工厂类
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public class FunctionFactory {

    /**
     * 根据参数因子，构造业务实现类对象的方法
     *
     * @param funcNo 通过这个功能号，确定构造哪个业务类
     * @return 具体的业务实现类对象，可能为空
     */
    public static IFunction makeService(int funcNo) {
        IFunction result = null;
        switch (funcNo) {
            case FuncNoConstants.FUNC_LOGIN:
                result = new LoginFunction();
                break;
            case FuncNoConstants.FUNC_REGISTER:
                result = new RegisterFunction();
                break;
            case FuncNoConstants.FUNC_GENERATE_RSA_INFO:
                result = new RSAInfoFunction();
                break;
            default:
                break;
        }
        return result;
    }
}
