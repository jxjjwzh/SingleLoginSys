package wangzh.single_login.functions;

import com.mysql.cj.core.util.LogUtils;
import com.mysql.cj.core.util.StringUtils;
import wangzh.single_login.SingleLoginManager;
import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.dao.DaoFactory;
import wangzh.single_login.dao.IUserService;
import wangzh.single_login.utils.PBKDF2Utils;
import wangzh.single_login.utils.RSAUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 登录功能号
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/29
 */
public class LoginFunction extends BaseUserAccountOpFunction {

    LoginFunction() {
    }

    @Override
    protected ResultVo UserAccountInvoke(String device_id, String account, String password) {
        ResultVo resultVo = new ResultVo();
        // 业务操作执行
        IUserService userDao = DaoFactory.getInstance().getSpringBean(IUserService.class, "userJDBCTemplate");
        // 查出RSA私钥，用以解密密码
        String privateKey = SingleLoginManager.getInstance().getRSAPrivate(device_id);
        // 用户输入的密码原文
        String originalPassword = RSAUtil.decryptByPrivateKe(privateKey, password);
        // 从数据库中获取，经过PBKDF2加密的密码
        String encryptPassword = userDao.getEncryptPassword(
                account
        );
        try {
            if (StringUtils.isNullOrEmpty(encryptPassword)) { // 用户名不存在！
                resultVo.setError_no(-114);
                resultVo.setError_info("账号不存在！");
            } else if (PBKDF2Utils.validatePassword(originalPassword, encryptPassword)) { // 用户名密码校验通过
                resultVo.setError_no(100);
                resultVo.setError_info("登录成功！");
                // 销毁RSA加密参数
                SingleLoginManager.getInstance().clearRSAInfo(device_id);
            } else { // 用户名或密码错误
                resultVo.setError_no(-111);
                resultVo.setError_info("用户名或密码错误！");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            resultVo.setError_no(-112);
            resultVo.setError_info("服务器内部错误！");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            resultVo.setError_no(-113);
            resultVo.setError_info("服务器内部错误！");
        }
        return resultVo;
    }
}
