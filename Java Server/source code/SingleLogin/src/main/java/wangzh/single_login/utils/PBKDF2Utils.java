package wangzh.single_login.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

/**
 * 用于存储密码的PBKDF2密码哈希加密算法
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/7/5
 */
public class PBKDF2Utils {

    public static void main(String[] args) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        String originalPassword = "thinkive"; // 原始密码
        System.out.println("原始密码：" + originalPassword);
        String generatedSecuredPasswordHash = generateStrongPasswordHash(originalPassword);
        System.out.println("原始密码的加密密钥：" + generatedSecuredPasswordHash);
        String inputPassword;
        Scanner in = new Scanner(System.in);
        do {
            System.out.print("请输入密码：");
            inputPassword = in.nextLine();
            boolean matched = validatePassword(inputPassword,
                    generatedSecuredPasswordHash);
            System.out.println("匹配结果：" + matched);
        } while (!inputPassword.equals("q"));

    }

    /**
     * 说明 :判断加密数据与原始数据是否一致
     */
    public static boolean validatePassword(String originalPassword,
                                            String storedPassword) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = toByte(parts[1]);
        byte[] hash = toByte(parts[2]);
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt,
                iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    /**
     * 说明 :16进制字符串转byte数组
     */
    private static byte[] toByte(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    /**
     * 说明 :PBKDF2WithHmacSHA1加盐加密
     */
    public static String generateStrongPasswordHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt().getBytes();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 12 * 8);
        SecretKeyFactory skf = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + bytes2Hex(salt) + ":" + bytes2Hex(hash);
    }

    /**
     * 说明 :生成随机盐
     */
    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    /**
     * 说明 :byte数组转换为16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }


}
