package wangzh.single_login.dao;

/**
 * 用户业务的数据库操作接口
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public interface IUserService {

    /**
     * 用户注册
     *
     * @param account  要注册的用户的账号
     * @param password 要注册的用户的密码，经过PBKDF2加密处理
     * @return 1：注册成功；0：注册失败
     */
    int registerUser(String account, String password);

    /**
     * 获取被加密过后的密码
     *
     * @param account  登录用户的账号
     * @return >0：登录成功；<=0：登录失败！
     */
    String getEncryptPassword(String account);

//    /**
//     * 添加RSA信息到数据库
//     *
//     * @param device_id 设备唯一标识，不同客户端对应不同的RSA信息
//     * @param xKey      公钥x坐标
//     * @param yKey      公钥y坐标
//     * @return 是否成功添加本条数据
//     */
//    int putRSAInfo(String device_id, String xKey, String yKey, String privateKey);
//
//    /**
//     * 获取某个客户端的RSA私钥
//     *
//     * @param device_id 要获取的是哪个客户端的RSA信息
//     * @return device_id对应的客户端的RSA私钥
//     */
//    String getRSAPrivateKey(String device_id);
}
