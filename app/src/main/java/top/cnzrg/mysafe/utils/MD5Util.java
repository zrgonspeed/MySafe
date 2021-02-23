package top.cnzrg.mysafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5Util {

    public static void main(String[] args) {
        String psd = "123";
        System.out.println(encoder(psd));
    }

    /**
     * 给指定字符串用MD5加密
     *
     * @param psd
     */
    public static String encoder(String psd) {
        try {
            // 加盐,增强密码安全性
            psd = psd + "mysafe";

            StringBuffer stringBuffer;
            // 1.指定加密算法类型
            MessageDigest instance = MessageDigest.getInstance("MD5");

            // 2.将需要加密的字符串转换成byte类型的数组,然后进行随机哈希
            byte[] digest = instance.digest(psd.getBytes());

            stringBuffer = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;
                // int类型的i需要转化成16进制字符
                String hexString = Integer.toHexString(i);

                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }

            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return psd;
    }
}
