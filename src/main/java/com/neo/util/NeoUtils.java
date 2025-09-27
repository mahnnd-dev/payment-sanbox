package com.neo.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class NeoUtils {

    public static String hmacSHA512(final String secret, final String data) {
        try {
            if (secret == null || data == null) {
                throw new IllegalArgumentException("Secret key and data cannot be null");
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            log.info("hmacSHA512: {}", sb.toString());
            return sb.toString();
        } catch (Exception e) {
            // Log the exception in a real application
            throw new RuntimeException("Failed to generate HMAC-SHA512", e);
        }
    }

    public static String buildQueryString(Map<String, String> fieldsMap) {
        List<String> fieldNames = new ArrayList<>(fieldsMap.keySet());
        Collections.sort(fieldNames);
        return fieldNames.stream()
                .filter(fieldName -> fieldsMap.get(fieldName) != null && !fieldsMap.get(fieldName).isEmpty())
                .map(fieldName -> {
                    try {
                        return fieldName + "=" + URLEncoder.encode(fieldsMap.get(fieldName), StandardCharsets.US_ASCII.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return fieldName;
                })
                .collect(Collectors.joining("&"));
    }

    public static void main(String[] args) {
//        String hashData = NeoUtils.buildQueryString(vnp_Params);
//        String secureHash = NeoUtils.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
//        vnp_Params.put("vnp_SecureHash", secureHash);
    }
}
