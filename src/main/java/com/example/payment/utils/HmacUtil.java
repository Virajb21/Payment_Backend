package com.example.payment.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HmacUtil {

    private final ObjectMapper objectMapper;

    public HmacUtil() {
        objectMapper = new ObjectMapper();
        // 🔥 Enforce sorted keys for deterministic JSON → fixed signature mismatch
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }
    public String getNormalizedJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "JSON_PARSE_ERROR";
        }
    }

    // HMAC on already-formatted payload string
    public static String calculateHMAC(String payload, String secret) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC", e);
        }
    }

    // HMAC on Java object → first serialize to sorted JSON string
    public String calculateHMAC(Object payload, String secret) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);  // sorted JSON
            return calculateHMAC(jsonPayload, secret);
        } catch (Exception e) {
            throw new RuntimeException("Error while converting payload to JSON", e);
        }
    }

    // Constant-time string comparison
    public boolean equalsConstTime(String a, String b) {
        if (a == null || b == null) return false;
        byte[] aa = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        if (aa.length != bb.length) return false;
        int result = 0;
        for (int i = 0; i < aa.length; i++) {
            result |= aa[i] ^ bb[i];
        }
        return result == 0;
    }
}
