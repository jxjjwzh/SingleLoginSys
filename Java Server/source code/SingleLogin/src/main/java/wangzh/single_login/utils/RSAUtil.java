package wangzh.single_login.utils;


import com.thinkive.base.security.ConvertUtil;
import com.thinkive.base.security.sm2.SM2Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.logging.Logger;

/**
 * RSA加密工具类
 */
public class RSAUtil {

    private static final Logger sLogger = Logger.getLogger(RSAUtil.class.getName());

    public static void main(String[] args) {
        String originalText = "thinkive";
        Map<String, String> sm = SM2Utils.generateKeyPair();
        String xKey = sm.get(SM2Utils.PAIR_PUBLIC_KEY_X);
        String yKey = sm.get(SM2Utils.PAIR_PUBLIC_KEY_Y);
        String sm2PriKey = sm.get(SM2Utils.PAIR_PRIVATE_KEY);
        sLogger.info("\n国密测试，xKey：" + xKey + "，yKey：" + yKey + "，私钥：" + sm2PriKey);

        // 公钥加密
        String sm2EncryptResult = encryptByPublicKey(xKey, yKey, originalText);
        // 私钥解密
        decryptByPrivateKe(sm2PriKey, sm2EncryptResult);
    }

    /**
     * 生成RSA加密信息并返回。包括公钥的x、y坐标、私钥
     */
    public static RSAInfo generateRSAInfo() {
        RSAInfo resultRSAInfo = new RSAInfo();
        Map<String, String> sm = SM2Utils.generateKeyPair();
        resultRSAInfo.xKey = sm.get(SM2Utils.PAIR_PUBLIC_KEY_X);
        resultRSAInfo.yKey = sm.get(SM2Utils.PAIR_PUBLIC_KEY_Y);
        resultRSAInfo.privateKey = sm.get(SM2Utils.PAIR_PRIVATE_KEY);
        return resultRSAInfo;
    }

    /**
     * 国密RSA算法，私钥解密
     *
     * @param privateKey  RSA私钥
     * @param encryptText 要被解密的密文
     * @return 由encryptText解密后的明文
     */
    public static String decryptByPrivateKe(String privateKey, String encryptText) {
        if (StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(encryptText)) {
            return encryptText;
        }
        encryptText = ConvertUtil.byteToAsccIIString(ConvertUtil.hexStringToBytes(encryptText));
        String result = SMSecurityUtil.SM2DecryptByPrivateKey(privateKey, encryptText);
        sLogger.info("\n国密测试，SM2解密结果：" + result);
        return result;
    }

    /**
     * 国密RSA算法，公钥加密
     *
     * @param xKey         公钥X坐标
     * @param yKey         公钥Y坐标
     * @param originalText 要被加密的原文
     * @return originalText加密后的密文
     */
    public static String encryptByPublicKey(String xKey, String yKey, String originalText) {
        String sm2EncryptResult = SMSecurityUtil.SM2EncryptByPublicKey(xKey, yKey, originalText);
        if (StringUtils.isNotEmpty(sm2EncryptResult)) {
            sm2EncryptResult = ConvertUtil.byteToHex(ConvertUtil.ascIIStringToByte(sm2EncryptResult));
            sLogger.info("\n国密测试，SM2加密结果：" + sm2EncryptResult);
        } else {
            sLogger.info("\n国密测试，加密失败！转码后结果为空！：" + sm2EncryptResult);
        }
        return sm2EncryptResult;
    }

    public static class RSAInfo {
        public String xKey;
        public String yKey;
        public String privateKey;
    }
}
