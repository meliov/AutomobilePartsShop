package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.dto.CreateOrderDetailsDto;
import com.example.parts_shop_be.order.dto.OrderDetailsDto;
import com.example.parts_shop_be.product.Product;
import com.example.parts_shop_be.product.ProductService;
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

    @Transactional
    public OrderDetailsDto createOrder(CreateOrderDetailsDto createOrderDetailsDto) {
        OrderDetails orderDetails = modelMapper.map(createOrderDetailsDto, OrderDetails.class);
        orderDetails.setDate(LocalDateTime.now());

        // Update product quantities
        List<Product> updatedProducts = createOrderDetailsDto.getItems().stream().map(product -> {
            Product existingProduct = productsService.getProduct(product.getId());
            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() - product.getQuantity());
            }
            return existingProduct;
        }).collect(Collectors.toList());

        orderDetails.setItems(updatedProducts);
        OrderDetails savedOrder = orderDetailsRepository.save(orderDetails);
        return modelMapper.map(savedOrder, OrderDetailsDto.class);
    }

    public List<OrderDetailsDto> getAllOrders() {
        return orderDetailsRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDetailsDto.class))
                .collect(Collectors.toList());
    }
}
