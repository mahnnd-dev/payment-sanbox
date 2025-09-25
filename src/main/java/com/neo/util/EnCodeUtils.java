package com.neo.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@UtilityClass
public class EnCodeUtils {
    private static final Logger logger = LogManager.getLogger(EnCodeUtils.class);

    public static String buildUrl(String url, String hashSecret, Map<String, String> vnpParams) {
        try {
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = EnCodeUtils.hmacSHA512(hashSecret, hashData.toString());
            queryUrl += "&Neo_SecureHash=" + vnp_SecureHash;
            return url + "?" + queryUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public String generateRawSignature(Map<String, String> params) {
        // V1 bỏ !entry.getKey().equals("requestType")
        // V2 bỏ !entry.getKey().equals("lang")
        StringBuilder rawSignature = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("requestType")) {
                rawSignature.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        // Xóa ký tự "&" cuối cùng
        if (rawSignature.length() > 0) {
            rawSignature.setLength(rawSignature.length() - 1);
        }

        return rawSignature.toString();
    }


    public String signHmacSHA256(String data, String key) throws Exception {
        Mac hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = hasher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // mã hóa MD5
    public static String getEncodeMD5(String value) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();
            logger.debug("MD5 Hash: {}", result);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    // mã hóa SHA256
    public static String getEncodeSHA256(String value) {
        String key = "";
        try {
            logger.debug("input: {}", value);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            key = bytesToHex(hash);
            logger.debug("ShaEncoder: {}", key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;//Error if length = 0
    }
}
