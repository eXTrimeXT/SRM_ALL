package com.midea.cloud.common.pj.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huangbf3
 * @description 签名生成的工具类
 */
public class HmacSignUtil {
    private final static String HEADER_APP_KEY = "X-HMAC-ACCESS-KEY";
    private final static String HEADER_SIGN = "X-HMAC-SIGNATURE";
    private final static String HEADER_DATE = "Date";
    private final static String HEADER_ALGORITHM = "X-HMAC-ALGORITHM";
    private final static String HEADER_ALGORITHMVAL = "hmac-sha256";
    private final static String HMAC_ALGORITHM = "HmacSHA256";
    private final static String SIGNED_HEADERS = "X-HMAC-SIGNED-HEADERS";

    /**
     * 获取签名所需的header各项
     *
     * @param appKey
     * @param appSecret
     * @param uri
     * @param method
     * @return 签名相关的map，访问时直接拼到header里
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Map<String, String> createSignHeader(String appKey, String appSecret,
                                                       String uri, String method)
            throws NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        return createSignHeader(appKey, appSecret, uri, method, null);
    }

    /**
     * 获取签名所需的header各项
     *
     * @param appKey
     * @param appSecret
     * @param uri
     * @param method
     * @param header
     * @return 签名相关的map，访问时直接拼到header里
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Map<String, String> createSignHeader(String appKey, String appSecret, String uri, String method, Map<String, String> header) throws NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {

        String dateStr = getDateStr();
        String httpText = "http://";
        String httpsText = "https://";
        String hengText = "/";

        if (uri.startsWith(httpText) || uri.startsWith(httpsText)) {
        } else {
            if (!uri.startsWith(hengText)) {
                uri = hengText + uri;
            }
            uri = "http://127.0.0.1" + uri;
        }

        URL url = new URL(uri);
        String signString = splicing(method.toUpperCase()
                , url.getPath()
                , getSortQuerysString(url.getQuery())
                , appKey
                , dateStr
                , getSortHeaderString(header));

        String sign = "";
        Mac hasher = Mac.getInstance(HMAC_ALGORITHM);
        hasher.init(new SecretKeySpec(appSecret.getBytes(), HMAC_ALGORITHM));
        byte[] hash = hasher.doFinal(signString.getBytes());
        sign = DatatypeConverter.printBase64Binary(hash);

        Map<String, String> headerMap = new HashMap<>(50);
        headerMap.put(HEADER_APP_KEY, appKey);
        headerMap.put(HEADER_SIGN, sign);
        headerMap.put(HEADER_DATE, dateStr);
        headerMap.put(HEADER_ALGORITHM, HEADER_ALGORITHMVAL);
        if (header != null && header.keySet().size() > 0) {
            headerMap.put(SIGNED_HEADERS, getSignedHeader(header));
            for (String key : header.keySet()) {
                String val = header.get(key) == null ? "" : header.get(key);
                headerMap.put(key, val);
            }
        }
        return headerMap;
    }

    private static String getSignedHeader(Map<String, String> header) {
        StringBuilder content = new StringBuilder();
        int i = 0;
        for (String key : header.keySet()) {
            if (i != 0) {
                content.append(';');
            }
            content.append(key);
            i++;
        }
        String headerStr = content.toString();
        return headerStr;
    }

    /**
     * 将url中的queryString参数排序后重新拼接
     *
     * @param queryStr
     * @return 已排序和重新拼装的queryString
     */
    private static String getSortQuerysString(String queryStr) {
        if (queryStr == null || "".equals(queryStr)) {
            return "";
        }
        Map<String, String> querys = new HashMap<>(50);
        String[] arr = queryStr.split("&");
        for (String item : arr) {
            if (item == null || "".equals(item)) {
                continue;
            }
            String[] kvs = item.split("=");
            if (kvs.length > 1) {
                querys.put(kvs[0], kvs[1]);
            } else {
                querys.put(kvs[0], "");
            }
        }


        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList(querys.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            String value = String.valueOf(querys.get(key));
            content.append(i == 0 ? "" : "&").append(key).append("=");
            if (value != null && !"".equals(value)) {
                content.append(value);
            }
        }

        return content.toString();
    }

    /**
     * 获取签名时间标签
     *
     * @return 签名时间标签字符串
     */
    public static String getDateStr() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
//        设置时区为GMT
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateStr = sdf.format(cd.getTime());
        return dateStr;
    }


    /**
     * 拼接字符串
     *
     * @param method        方法
     * @param uri           uri
     * @param queryString   请求uri中的参数串
     * @param accessKey     appKey
     * @param date          GMT时间
     * @param headersString 需要签名的请求头拼接
     * @return 待签名字符串
     */
    private static String splicing(String method, String uri, String queryString, String accessKey, String date, String headersString) {

        StringBuilder sb = new StringBuilder();
        sb.append(method.toUpperCase());
        sb.append("\n");
        sb.append(uri);
        sb.append("\n");
        /* 如果请求数据为url */
        if (queryString == null) {
            queryString = "";
        }
        sb.append(queryString);
        sb.append("\n");
        sb.append(accessKey);
        sb.append("\n");
        sb.append(date);
        sb.append("\n");
        if (headersString != null && !"".equals(headersString)) {
            sb.append(headersString);
            sb.append("\n");
        }
        /* 拼接字符串 */
        return sb.toString();
    }

    private static String getSortHeaderString(Map<String, String> header) {
        if (header == null || header.keySet().size() == 0) {return "";}
        List<String> keys = new ArrayList<>();
        for (String key : header.keySet()) {
            keys.add(key);
        }
        keys.sort((k1, k2) -> k1.compareTo(k2));
        StringBuilder content = new StringBuilder();
        int i = 0;
        for (String key : keys) {
            if (i != 0) {
                content.append('\n');
            }

            content.append(key);
            String value = header.get(key);
            if (value == null) {value = "";}
            content.append(":");
            content.append(value);

            i++;
        }
        String signStr = content.toString();
        return signStr;
    }

    /**
     * 封装加密请求头参数
     *
     * @param appKey    appKey
     * @param appSecret appSecret
     * @param url       url
     * @return org.springframework.http.HttpEntity<java.lang.String>
     * @author fumeifeng
     * @date 2021/10/28 17:58
     */
    public static HttpHeaders getHttpHeaders(String appKey, String appSecret, String url) {
        Map<String, String> headerMap = null;
        try {
            headerMap = createSignHeader(appKey, appSecret, url, "post");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headerMap.forEach((key, value) -> headers.put(key, Collections.singletonList(value)));
        return headers;
    }
}