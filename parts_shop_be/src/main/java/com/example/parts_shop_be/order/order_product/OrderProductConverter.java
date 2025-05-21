package com.example.parts_shop_be.order.order_product;

import com.example.parts_shop_be.product.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class OrderProductConverter implements AttributeConverter<List<Product>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(List<Product> attribute) {
        if(attribute == null){
            return null;
        }
        // Serialize to JSON
        try {
            return objectMapper.writeValueAsString(attribute);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List<Product> to database column", e);
        }
    }

    @Override
    public List<Product> convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null){
                return null;
            }
            // Deserialize to CardDetails
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
        } catch (Exception e) {
            throw new RuntimeException("Error converting database column to List<Product>", e);
        }
    }
}
