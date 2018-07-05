package wangzh.single_login.functions;

import org.apache.commons.lang3.StringUtils;
import wangzh.single_login.SingleLoginManager;
import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.constants.FuncNoConstants;
import wangzh.single_login.constants.ParamConstants;

import java.util.Map;

/**
 * 用户操作（例如登录、注册、修改密码等操作）的抽象父类
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/7/3
 */
public abstract class BaseUserAccountOpFunction implements IFunction {

    @Override
    public final ResultVo invoke(Map<String, String> params) {
        ResultVo resultVo = new ResultVo();
        // 入参校验
        String account = params.get(ParamConstants.ACCOUNT);
        String password = params.get(ParamConstants.PASSWORD);
        String device_id = params.get(ParamConstants.DEVICE_ID);
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            resultVo.setError_no(-104);
            resultVo.setError_info("账号或密码不得为空！");
            return resultVo;
        } else if (StringUtils.isEmpty(device_id)) {
            resultVo.setError_no(-105);
            resultVo.setError_info("设备唯一标识不得为空！");
            return resultVo;
        }
        if (SingleLoginManager.getInstance().checkContainsRSA(device_id)) {
            return UserAccountInvoke(device_id, account, password);
        } else {
            resultVo.setError_no(-4);
            resultVo.setError_info("请先申请RSA加密参数！");
            return resultVo;
        }
    }

//    /**
//     * 对客户端传来的RSA加密密码进行验证
//     *
//     * @param rsaEncryptPassword 客户端传来的，被RSA加密过的密码
//     * @return 密码校验是否通过
//     */
//    boolean checkPassword(String rsaEncryptPassword) {
//
//    }

    abstract ResultVo UserAccountInvoke(String device_id, String account, String password);
}
