package com.elink.health.mediciner.utils;


import android.os.Build;
import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shixin at 2018-01-10
 */
public class SecurityUtil {

    public static native byte[] getKeyValue();

    public static native byte[] getIv();

    interface SecureCompatImpl {
        //加密
        String encode(String msg) throws Exception;
        //解密
        String decode(String value) throws Exception;
    }

    static class BaseSecureCompatImpl implements SecureCompatImpl {

        @Override
        public String encode(String msg) throws Exception {
            return null;
        }

        @Override
        public String decode(String value) throws Exception {
            return null;
        }
    }

    static class MarshmallowSecureCompatImpl extends BaseSecureCompatImpl {
        private static byte[] keyValue;
        private static byte[] iv;
        private static SecretKey key;
        private static AlgorithmParameterSpec paramSpec;
        private static Cipher ecipher;

        static {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                System.loadLibrary("native");
                keyValue = getKeyValue();
                iv = getIv();
                String str = new String(keyValue);
                String str1 = new String(iv);
                System.out.println("keyValue=" + str
                        + "iv=" + str1);
                if (null != keyValue && null != iv) {
                    KeyGenerator kgen;
                    try {
                        // 获取秘钥生成器
                        kgen = KeyGenerator.getInstance("AES");
                        // Java项目中的写法
                        //SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

                        // Android项目中的写法
                        // 通过种子初始化  6.0以后这个方法不再适用
                        SecureRandom random = SecureRandom.
                                getInstance("SHA1PRNG", "Crypto");
                        random.setSeed(keyValue);
                        kgen.init(128, random);
                        //生成秘钥并返回
                        key = kgen.generateKey();
                        paramSpec = new IvParameterSpec(iv);
                        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String encode(String msg) {
            String str = "";
            try {
                //用密钥和一组算法参数初始化此 cipher
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                //加密并转换成16进制字符串
                str = asHex(ecipher.doFinal(msg.getBytes()));
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            return str;
        }


        public String decode(String value) {
            try {
                ecipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
                return new String(ecipher.doFinal(asBin(value)));
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            return "";
        }

        private static String asHex(byte buf[]) {
            StringBuffer strbuf = new StringBuffer(buf.length * 2);
            int i;
            for (i = 0; i < buf.length; i++) {
                if (((int) buf[i] & 0xff) < 0x10)//小于十前面补零
                    strbuf.append("0");
                strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
            }
            return strbuf.toString();
        }


        private static byte[] asBin(String src) {
            if (src.length() < 1)
                return null;
            byte[] encrypted = new byte[src.length() / 2];
            for (int i = 0; i < src.length() / 2; i++) {
                int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);//取高位字节
                int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);//取低位字节
                encrypted[i] = (byte) (high * 16 + low);
            }
            return encrypted;
        }

    }

    static class API23MoreCompatImpl extends BaseSecureCompatImpl {
        @Override
        public String encode(String msg) throws Exception {
            String message1 = Base64.encodeToString(msg.getBytes(), Base64.DEFAULT);
            String salt = "abcdefghijklmnop";
            SecretKeySpec key = new SecretKeySpec(salt.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(message1.getBytes());
            return Base64.encodeToString(encVal, Base64.DEFAULT);
        }

        @Override
        public String decode(String value) throws Exception {
            String salt = "abcdefghijklmnop";
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(salt.getBytes(), "AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(value.getBytes(), Base64.DEFAULT);
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return new String(Base64.decode(decryptedValue, Base64.DEFAULT));
        }
    }

    private static final SecureCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version > 23) {
            IMPL = new API23MoreCompatImpl();
        } else {
            IMPL = new MarshmallowSecureCompatImpl();
        }
    }

    public static String encrypt(String message) throws Exception {
        return IMPL.encode(message);
    }

    //Decryption
    public static String decrypt(String message) throws Exception {
        return IMPL.decode(message);
    }
}