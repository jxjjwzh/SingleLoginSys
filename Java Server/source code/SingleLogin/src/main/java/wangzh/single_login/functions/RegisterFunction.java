package wangzh.single_login.functions;

import org.apache.commons.lang3.StringUtils;
import wangzh.single_login.SingleLoginManager;
import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.constants.FuncNoConstants;
import wangzh.single_login.constants.ParamConstants;
import wangzh.single_login.dao.DaoFactory;
import wangzh.single_login.dao.IUserService;
import wangzh.single_login.utils.PBKDF2Utils;
import wangzh.single_login.utils.RSAUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册功能号
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public class RegisterFunction extends BaseUserAccountOpFunction {

    RegisterFunction() {
    }

    @Override
    protected ResultVo UserAccountInvoke(String device_id, String account, String password) {
        ResultVo resultVo = new ResultVo();
        // 业务操作执行
        IUserService userDao = DaoFactory.getInstance().getSpringBean(IUserService.class, "userJDBCTemplate");
        // 查出RSA私钥，用以解密密码
        String privateKey = SingleLoginManager.getInstance().getRSAPrivate(device_id);
        password = RSAUtil.decryptByPrivateKe(privateKey, password);
        // 添加用户信息
        int insertResult = 0;
        try {
            insertResult = userDao.registerUser( // 将账号密码存入数据库
                    account,
                    PBKDF2Utils.generateStrongPasswordHash(password) // 密码经PBKDF2哈希算法处理后再存入
            );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            resultVo.setError_no(-103);
            resultVo.setError_info("注册失败！服务器内部错误导致密码不发保存！");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            resultVo.setError_no(-104);
            resultVo.setError_info("注册失败！服务器内部错误导致密码不发保存");
        }

        // 出参封装
        if (insertResult > 0) {
            resultVo.setError_no(100);
            resultVo.setError_info("注册成功！");
            // 销毁RSA加密参数
            SingleLoginManager.getInstance().clearRSAInfo(device_id);
        } else if (insertResult == -2) {
            resultVo.setError_no(-101);
            resultVo.setError_info("注册失败！账号已经存在！");
        } else {
            resultVo.setError_no(-102);
            resultVo.setError_info("注册失败！");
        }
        return resultVo;
    }
}
