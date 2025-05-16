package com.example.parts_shop_be.order.dto;

import com.example.parts_shop_be.product.Product;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderDetailsDto {

    private List<Product> items;

    private Double total;

    private String shippingAddress;

    private String paymentMethod;


    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
