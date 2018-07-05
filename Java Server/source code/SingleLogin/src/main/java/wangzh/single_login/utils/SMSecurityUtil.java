package wangzh.single_login.utils;


import com.thinkive.base.security.ConvertUtil;
import com.thinkive.base.security.sm2.SM2Utils;
import com.thinkive.base.security.sm3.SM3Digest;
import com.thinkive.base.security.sm4.SM4Utils;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 国密算法工具类
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @project thinkive-framework-master
 * @date 2018/3/15
 */
public class SMSecurityUtil {

    /**
     * 非对称加解密中的公钥加密。类似于RSA
     *
     * @param originalText 被加密的原始文本
     * @return 加密后的结果
     */
    public static String SM2EncryptByPublicKey(String xKey, String yKey, String originalText) {
        String pub = SM2Utils.getPublicKey(xKey, yKey);
        try {
            return SM2Utils.encrypt(ConvertUtil.hexStringToBytes(pub), originalText.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 非对称加解密中的私钥解密。类似于RSA
     *
     * @param privateKey 解密用的私钥
     * @param encrypt    要被解密的加密文本
     * @return encrypt解密后的结果
     */
    public static String SM2DecryptByPrivateKey(String privateKey, String encrypt) {
        try {
            return new String(
                    SM2Utils.decrypt(
                            ConvertUtil.hexStringToBytes(privateKey), ConvertUtil.ascIIStringToByte(encrypt)
                    ), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String SM3Digest(String originalText) {
        try {
            byte[] oriBytes = originalText.getBytes("UTF-8");
            byte[] md = new byte[32];
            SM3Digest sm3Digest = new SM3Digest();
            sm3Digest.update(oriBytes, 0, oriBytes.length);
            sm3Digest.doFinal(md, 0);
            return ConvertUtil.byteToHex(md);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String SM4EncryptECB(String secretKey, String originalText) {
        return checkAndPrepareSM4(secretKey).encryptData_ECB(originalText, "UTF-8", 0);
    }

    public static String SM4DecryptECB(String secretKey, String encrypt) {
        return checkAndPrepareSM4(secretKey).decryptData_ECB(encrypt, "UTF-8", 0);
    }

    public static String SM4EncryptCBC(String secretKey, String originalText) {
        SM4Utils sm4 = checkAndPrepareSM4(secretKey);
        sm4.setIv("UISwD9fW6cFh9SNS");
        return sm4.encryptData_CBC(originalText, "UTF-8", 0);
    }

    public static String SM4DecryptCBC(String secretKey, String encrypt) {
        SM4Utils sm4 = checkAndPrepareSM4(secretKey);
        sm4.setIv("UISwD9fW6cFh9SNS");
        return sm4.decryptData_CBC(encrypt, "UTF-8", 0);
    }

    /**
     * SM4加密的key检查及变量初始化等准备工作
     */
    private static SM4Utils checkAndPrepareSM4(String secretKey) {
        if (StringUtil.isNullOrEmpty(secretKey)) {
            throw new IllegalArgumentException("SM4加密key不得为空！");
        } else if (secretKey.length() != 16) {
            throw new IllegalArgumentException("SM4加密key长度必须是16！");
        } else {
            SM4Utils sm4 = new SM4Utils();
            sm4.setSecretKey(secretKey);
            sm4.setHexString(false);
            return sm4;
        }
    }

}
