package com.example.parts_shop_be.user.card_details;

import com.example.parts_shop_be.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Base64;

@Converter
public class CardDetailsConverter implements AttributeConverter<CardDetails, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secretKey = "alabala123456789"; // 16-byte key for AES-128

    @Override
    public String convertToDatabaseColumn(CardDetails cardDetails) {
        try {
            if(cardDetails == null){
                return null;
            }
            // Serialize to JSON
            String json = objectMapper.writeValueAsString(cardDetails);
            // Encrypt the JSON
            return Base64.getEncoder().encodeToString(encrypt(json, secretKey));
        } catch (Exception e) {
            throw new RuntimeException("Error converting CardDetails to database column", e);
        }
    }

    @Override
    public CardDetails convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null){
                return null;
            }
            // Decrypt the JSON
            String json = decrypt(Base64.getDecoder().decode(dbData), secretKey);
            // Deserialize to CardDetails
            return objectMapper.readValue(json, CardDetails.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting database column to CardDetails", e);
        }
    }

    private byte[] encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes("UTF-8"));
    }

    private String decrypt(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(data), "UTF-8");
    }
}