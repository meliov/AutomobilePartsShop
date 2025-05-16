package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.dto.CreateOrderDetailsDto;
import com.example.parts_shop_be.order.dto.OrderDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

    @PostMapping("/save")
    public ResponseEntity<OrderDetailsDto> createOrder(@RequestBody CreateOrderDetailsDto createOrderDetailsDto) {
        OrderDetailsDto orderDetailsDto = orderDetailsService.createOrder(createOrderDetailsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailsDto);
    }

    @GetMapping("/get")
    public ResponseEntity<List<OrderDetailsDto>> getAllOrders() {
        List<OrderDetailsDto> orders = orderDetailsService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}