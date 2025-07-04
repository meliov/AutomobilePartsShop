package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.dto.CreateOrderDetailsDto;
import com.example.parts_shop_be.order.dto.OrderDetailsDto;
import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.product.ProductService;
import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.user.UserRepository;
import com.example.parts_shop_be.utils.email.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailServiceImpl;
    @Transactional
    public OrderDetailsDto createOrder(CreateOrderDetailsDto createOrderDetailsDto, Long userId) {
        OrderDetails orderDetails = modelMapper.map(createOrderDetailsDto, OrderDetails.class);
        orderDetails.setDate(LocalDateTime.now());

        if(userId != null) {
            User user = userRepository.findById(userId).orElseGet(() -> null);

            if (user != null) {
                orderDetails.setUser(user);
            }else{
                throw new RuntimeException("User not found");
            }
        }
        Long max = orderDetailsRepository.findMaxTrackingNumber();
        orderDetails.setTrackingNumber(max == null ? 1 : max + 1);
        orderDetails.setStatus(OrderStatus.PENDING);
        orderDetails.setCustomerEmail(createOrderDetailsDto.getEmail());
        OrderDetails savedOrder = orderDetailsRepository.save(orderDetails);


        // Send email notification
        if (createOrderDetailsDto.getEmail() != null) {
            String emailContent = "Your order with tracking number " + orderDetails.getTrackingNumber() + " has been created successfully. \nWe will notify you once it is accepted and sent.\n Thanks for shopping with us!";
            emailServiceImpl.sendMail(createOrderDetailsDto.getEmail(), "Order Confirmation", emailContent);
        }

        return modelMapper.map(savedOrder, OrderDetailsDto.class);
    }

    public List<OrderDetailsDto> getAllOrders() {
        return orderDetailsRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDetailsDto.class))
                .collect(Collectors.toList());
    }

    public List<OrderDetailsDto> getUserOrders(Long userId) {
        return orderDetailsRepository.findByUserId(userId).stream()
                .map(order -> modelMapper.map(order, OrderDetailsDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptOrder(Long orderId) {
        OrderDetails orderDetails = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update product quantities
        orderDetails.getItems().forEach(product -> {
            Product existingProduct = productsService.getProduct(product.getId());
            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() - product.getQuantity());
            }
        });

        orderDetails.setStatus(OrderStatus.ACCEPTED);
        orderDetailsRepository.save(orderDetails);

        // Send email notification
        if (orderDetails.getCustomerEmail() != null) {
            String emailContent = "Your order with tracking number " + orderDetails.getTrackingNumber() + " has been accepted and sent. \nExpect it in the next few (1-5) days.\n Thanks for shopping with us!";
            emailServiceImpl.sendMail(orderDetails.getCustomerEmail(), "Order Accepted", emailContent);
        }
    }

    @Transactional
    public void rejectOrder(Long orderId) {
        OrderDetails orderDetails = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderDetails.setStatus(OrderStatus.REJECTED);
        orderDetailsRepository.save(orderDetails);

        // Send email notification
        if (orderDetails.getCustomerEmail() != null) {
            String emailContent = "We are sorry, but your order with tracking number " + orderDetails.getTrackingNumber() + " was rejected. The products might be out of stock.";
            emailServiceImpl.sendMail(orderDetails.getCustomerEmail(), "Order Rejected", emailContent);
        }
    }
}
