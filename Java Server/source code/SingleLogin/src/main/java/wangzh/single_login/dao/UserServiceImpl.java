package wangzh.single_login.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import wangzh.single_login.constants.ParamConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * 用户业务的数据库操作实现类
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public class UserServiceImpl extends JdbcDaoSupport implements IUserService {

    /**
     * 用户注册
     *
     * @param account  要注册的用户的账号
     * @param password 要注册的用户的密码
     * @return 1：注册成功；0：注册失败
     */
    @Override
    public int registerUser(String account, String password) {
        // 先检查是否已经有注册过
        String sql = "select account, password from t_user_info where account = ?";
        List<HashMap<String, String>> resultList = getJdbcTemplate().query(sql, new String[]{
                account
        }, new UserMapper());
        if (resultList != null && resultList.size() > 0) {
            return -2;
        }

        // 如果没有已经注册的同名账号，就将新账号数据入库
        sql = "insert into t_user_info values(?,?)";
        return getJdbcTemplate().update(sql, account, password);
    }

    @Override
    public String getEncryptPassword(String account) {
        String sql = "select account, password from t_user_info where account = ?";
        List<HashMap<String, String>> resultList = getJdbcTemplate().query(sql, new String[]{
                account
        }, new UserMapper());
        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0).get(ParamConstants.PASSWORD);
        } else {
            return "";
        }

    }

    //    /**
//     * 登录操作，校验用户名、密码
//     *
//     * @param account  登录用户的账号
//     * @param password 登录用户的密码
//     * @return >0：登录成功；<=0：登录失败！
//     */
//    @Override
//    public int getEncryptPassword(String account, String password) {
//        // 用sql语句查询库中用户名、密码都匹配的数据
//        String sql = "select account, password from t_user_info where account = ? and password = ?";
//        List<HashMap<String, String>> resultList = getJdbcTemplate().query(sql, new String[]{
//                account, password
//        }, new UserMapper());
//        return resultList.size();
//    }

//    /**
//     * 添加RSA信息到数据库
//     *
//     * @param device_id 设备唯一标识，不同客户端对应不同的RSA信息
//     * @param xKey      公钥x坐标
//     * @param yKey      公钥y坐标
//     * @return 是否成功添加本条数据
//     */
//    @Override
//    public int putRSAInfo(String device_id, String xKey, String yKey, String privateKey) {
//        // 需要覆盖之前表中已有的RSA加密信息
//        String sql = "delete from t_rsa_info where device_id = ?";
//        getJdbcTemplate().update(sql, device_id);
//
//        // 将新生成的加密信息存入数据库
//        sql = "insert into t_rsa_info values (?, ?, ?, ?)";
//        return getJdbcTemplate().update(sql, device_id, xKey, yKey, privateKey);
//    }
//
//    /**
//     * 获取某个客户端的RSA私钥
//     *
//     * @param device_id 要获取的是哪个客户端的RSA信息
//     * @return device_id对应的客户端的RSA私钥
//     */
//    @Override
//    public String getRSAPrivateKey(String device_id) {
//        String sql = "select rsa_private_key from t_rsa_info where device_id = ?";
//        List<HashMap<String, String>> resultList = getJdbcTemplate().query(sql, new String[]{
//                device_id
//        }, new RSAMapper());
//        if (resultList != null && resultList.size() > 0) {
//            return resultList.get(0).get(ParamConstants.RSA_PRIVATE_KEY);
//        } else {
//            return "";
//        }
//    }
}

/**
 * 用户账户的数据解析并打包
 */
class UserMapper implements RowMapper<HashMap<String, String>> {

    @Override
    public HashMap<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(ParamConstants.ACCOUNT, resultSet.getString(ParamConstants.ACCOUNT));
        resultMap.put(ParamConstants.PASSWORD, resultSet.getString(ParamConstants.PASSWORD));
        return resultMap;
    }
}

///**
// * RSA参数的数据解析并打包
// */
//class RSAMapper implements RowMapper<HashMap<String, String>> {
//
//    @Override
//    public HashMap<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
//        HashMap<String, String> resultMap = new HashMap<String, String>();
//        resultMap.put(ParamConstants.RSA_PRIVATE_KEY, resultSet.getString(ParamConstants.RSA_PRIVATE_KEY));
//        return resultMap;
//    }
//}