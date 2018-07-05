package wangzh.single_login.functions;

import org.apache.commons.lang3.StringUtils;
import wangzh.single_login.SingleLoginManager;
import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.constants.ParamConstants;
import wangzh.single_login.dao.DaoFactory;
import wangzh.single_login.dao.IUserService;
import wangzh.single_login.utils.RSAUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * RSA加密相关操作的业务类
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public class RSAInfoFunction implements IFunction {

    private static final Logger sLogger = Logger.getLogger(RSAInfoFunction.class.getName());

    RSAInfoFunction() {
    }

    @Override
    public ResultVo invoke(Map<String, String> params) {
        ResultVo resultVo = new ResultVo();
        // 入参校验
        String device_id = params.get(ParamConstants.DEVICE_ID);
        if (StringUtils.isEmpty(device_id)) {
            resultVo.setError_no(-3);
            resultVo.setError_info("设备唯一标识不得为空！");
            return resultVo;
        }

        // 业务操作执行
//        RSAUtil.RSAInfo rsaInfo = RSAUtil.generateRSAInfo();
//        sLogger.info("国密非对称性加密参数生成，xKey：" + rsaInfo.xKey + "，yKey：" + rsaInfo.yKey + "，私钥：" + rsaInfo.privateKey);
//        IUserService userDao = DaoFactory.getInstance().getSpringBean(IUserService.class, "userJDBCTemplate");
//        int insertResult = userDao.putRSAInfo( // 保存该客户端的RSA信息到数据库
//                device_id,
//                rsaInfo.xKey,
//                rsaInfo.yKey,
//                rsaInfo.privateKey
//        );

        RSAUtil.RSAInfo rsaInfo = SingleLoginManager.getInstance().generateRSAInfo(device_id);
        sLogger.info("国密非对称性加密参数生成，xKey：" + rsaInfo.xKey + "，yKey：" + rsaInfo.yKey + "，私钥：" + rsaInfo.privateKey);

//        // 打包出参：
//        if (insertResult == 1) {
//
//        } else if (insertResult == -13) {
//
//        } else {
//            resultVo.setError_no(-14);
//            resultVo.setError_info("数据库操作失败！");
//        }
        resultVo.setError_no(101); // 大于等于100表示调用成功并保持连接
        resultVo.setError_info("调用成功！");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(ParamConstants.RSA_X_KEY, rsaInfo.xKey);
        resultMap.put(ParamConstants.RSA_Y_KEY, rsaInfo.yKey);
        resultVo.setOutPutsMap(resultMap);
        return resultVo;
    }
}
