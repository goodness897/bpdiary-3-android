package kr.co.openit.bpdiary.utils;

import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 128 bit
 */
public class AesssUtil {

    //신규
    private static String key = "!cufit!megamen!!";

    private static String iv = "283485!!283485!!";

    public static String encrypt(String content) throws Exception {

        if (null == content || content.length() < 1) {

            return "";

        } else {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE,
                        new SecretKeySpec(key.getBytes(), "AES"),
                        new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));

            //return DatatypeConverter.printHexBinary(encrypted);

            return new String(Hex.encodeHex(encrypted)).toUpperCase();
            //return Hex.encodeHexString(encrypted);

        }
    }

    public static String decrypt(String content) throws Exception {

        if (null == content || content.length() < 1) {

            return "";

        } else {

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE,
                        new SecretKeySpec(key.getBytes("UTF-8"), "AES"),
                        new IvParameterSpec(iv.getBytes()));

            byte[] clearbyte = cipher.doFinal(Hex.decodeHex(content.toCharArray()));

            return new String(clearbyte);

        }
    }

    //기존
    static String encryptionKeyOld = "!openit!dogfood!";

    static String IVOld = "dhvmsdltdlqslek!";

    public static String decryptOld(String cipherText) throws Exception {
        byte[] bt = stringToBytesOld(cipherText);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKeyOld.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IVOld.getBytes()));
        return new String(cipher.doFinal(bt), "UTF-8");
    }

    private static byte[] stringToBytesOld(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }
}
