package com.example.parts_shop_be.user.role;

import com.example.parts_shop_be.user.card_details.CardDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Base64;
import java.util.List;

@Converter
public class RoleConverter implements AttributeConverter<List<UserRole>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UserRole> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting list of roles to JSON", e);
        }
    }

    @Override
    public List<UserRole> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, UserRole.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to list of roles", e);
        }
    }
}