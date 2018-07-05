package wangzh.single_login.functions;

import wangzh.single_login.bean.ResultVo;

import java.util.Map;

/**
 * 业务类功能号
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/29
 */
public interface IFunction {

    ResultVo invoke(Map<String, String> params);
}
