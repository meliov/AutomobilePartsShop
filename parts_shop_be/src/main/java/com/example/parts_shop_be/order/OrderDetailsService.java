package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.dto.CreateOrderDetailsDto;
import com.example.parts_shop_be.order.dto.OrderDetailsDto;
import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.product.ProductService;
import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.user.UserRepository;
import com.example.parts_shop_be.utils.email.EmailService;
import com.example.parts_shop_be.utils.email.EmailServiceImpl;
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
        // Update product quantities
        List<Product> updatedProducts = createOrderDetailsDto.getItems().stream().map(product -> {
            Product existingProduct = productsService.getProduct(product.getId());
            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() - product.getQuantity());
            }
            return existingProduct;
        }).collect(Collectors.toList());
        Long max = orderDetailsRepository.findMaxTrackingNumber();
        orderDetails.setTrackingNumber(max == null ? 1 : max + 1);
        orderDetails.setItems(updatedProducts);
        OrderDetails savedOrder = orderDetailsRepository.save(orderDetails);

        // Send email notification
        if (createOrderDetailsDto.getEmail() != null) {
            String emailContent = "Your order with tracking number " + orderDetails.getTrackingNumber() + " has been created successfully.\nIt is expected to arrive in 3-5 business days.";
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
}
