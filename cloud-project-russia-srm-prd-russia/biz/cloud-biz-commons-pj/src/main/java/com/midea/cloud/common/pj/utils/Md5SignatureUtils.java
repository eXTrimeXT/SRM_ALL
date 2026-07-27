package com.midea.cloud.common.pj.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author GW00302625
 * {@link org.apache.commons.codec.digest.DigestUtils}
 * {@link org.apache.commons.codec.binary.Hex}
 * * *
 */
@Deprecated
@Slf4j
public class Md5SignatureUtils {
    private final static String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};


    /**
     * 生成签名
     * @param object object
     * @param appSecret appSecret
     * @return String
     */
    public static String getSign(Object object,String appSecret)  {
        Map<String, String> map = objectToMap(object);
        return getSign(map,appSecret);
    }

    /**
     * 对象转map
     * @param object object
     * @return Map
     */
    private static Map<String, String> objectToMap(Object object) {
        Map<String,String> map = new TreeMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                map.put(field.getName(), String.valueOf(field.get(object)));
            }catch (Exception e) {
                log.error("Md5SignatureUtils.objectToMap");
            }
        }
        return map;
    }

    /**
     * 签名
     * @param map map
     * @param appSecret appSecret
     * @return Stringl
     */
    public static String getSign(Map<String,String> map,String appSecret) {
        ArrayList<String> list = new ArrayList<>(map.size());
        for(Map.Entry<String,String> entry:map.entrySet()){
            //空字符串 entry.getValue()!="")
            if( entry.getValue() != null
                    && !StringUtils.isEmpty(entry.getValue())
                    && !"null".equals(entry.getValue())
                    && !"class".equals(entry.getKey())
                    && !"data".equals(entry.getKey()) ) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        //对参数排序
        Arrays.sort(arrayToSort);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        //过滤最后一个字符串&
        int lastIdx = result.lastIndexOf("&");
        result = result.substring(0,lastIdx);
        result += appSecret;
        try{
            result = MD5Encode(result).toUpperCase();
        }catch (Exception e) {
            log.error("Md5SignatureUtils.objectToMap");

        }
        return result;
    }

    /**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception e) {
            log.error("Md5SignatureUtils.MD5Encode");

        }
        return resultString;
    }


    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 验签
     * @param object object
     * @param appSecret appSecret
     * @return boolean
     */
    public static boolean checkIsSignValidFromResponseStringObject(Object object,String appSecret) {
        Map<String, String> map = objectToMap(object);
        return checkIsSignValidFromResponseString(map,appSecret);
    }

    /**
     * 验签
     * @param map map
     * @param appSecret appSecret
     * @return boolean
     */
    public static boolean checkIsSignValidFromResponseString(Map<String,String> map,String appSecret){
        String signFromAPIResponse = null;
        String sign = "sign";
        if(map.get(sign)!=null){
            signFromAPIResponse = map.get(sign);
        }
        if(Objects.equals(signFromAPIResponse, "") || signFromAPIResponse == null){
            return false;
        }

        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.put("sign","");

        //将API返回的数据根据用签名算法进行计算新签名，用来跟API返回的签名进行比较
        //重新签名
        String signForAPIResponse = Md5SignatureUtils.getSign(map,appSecret);

        //签名验不过，表示这个API返回的数据有可能已经被篡改了
        return signForAPIResponse.equals(signFromAPIResponse);
    }




}
