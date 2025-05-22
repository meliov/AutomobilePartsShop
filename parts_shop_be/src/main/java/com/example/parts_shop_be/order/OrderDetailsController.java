package com.example.parts_shop_be.order;

import com.example.parts_shop_be.order.dto.CreateOrderDetailsDto;
import com.example.parts_shop_be.order.dto.OrderDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save/{userId}")
    public ResponseEntity<OrderDetailsDto> createOrder(@RequestBody CreateOrderDetailsDto createOrderDetailsDto, @PathVariable String userId) {
        Long parsedUserId = "null".equals(userId) ? null : Long.valueOf(userId);
        OrderDetailsDto orderDetailsDto = orderDetailsService.createOrder(createOrderDetailsDto, parsedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailsDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<List<OrderDetailsDto>> getAllOrders() {
        List<OrderDetailsDto> orders = orderDetailsService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<OrderDetailsDto>> getUserOrders( @PathVariable Long userId) {
        List<OrderDetailsDto> orders = orderDetailsService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

}