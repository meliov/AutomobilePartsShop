package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.order_product.OrderProductConverter;
import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class OrderDetails extends BaseEntity {

    @Convert(converter = OrderProductConverter.class)
    @Column(length = 10000)
    private List<Product> items;

    private Double total;

    private LocalDateTime date;

    private String shippingAddress;

    private String paymentMethod;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private User user;

    @Column(name = "tracking_number", unique = true)
    private Long trackingNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String customerEmail;

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public Long getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(Long trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // Getters and Setters
}