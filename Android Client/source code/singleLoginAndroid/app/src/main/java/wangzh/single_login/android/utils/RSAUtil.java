package wangzh.single_login.android.utils;


import android.text.TextUtils;
import android.util.Log;

import com.thinkive.base.security.ConvertUtil;

/**
 * RSA加密工具类，
 */
public class RSAUtil {


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
        if (!TextUtils.isEmpty(sm2EncryptResult)) {
            sm2EncryptResult = ConvertUtil.byteToHex(ConvertUtil.ascIIStringToByte(sm2EncryptResult));
            Log.d("国密测试", "SM2加密结果：" + sm2EncryptResult);
        } else {
            Log.d("国密测试", "加密失败！转码后结果为空！：" + sm2EncryptResult);
        }
        return sm2EncryptResult;
    }
}
