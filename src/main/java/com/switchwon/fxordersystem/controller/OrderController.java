package com.switchwon.fxordersystem.controller;

import com.switchwon.fxordersystem.common.ApiResponse;
import com.switchwon.fxordersystem.dto.CreateOrderRequest;
import com.switchwon.fxordersystem.dto.CreateOrderResponse;
import com.switchwon.fxordersystem.dto.OrderItem;
import com.switchwon.fxordersystem.dto.OrderListResponse;
import com.switchwon.fxordersystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<CreateOrderResponse> order(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(CreateOrderResponse.fromEntity(orderService.createOrder(request)));
    }

    @GetMapping("/list")
    public ApiResponse<OrderListResponse> list() {
        List<OrderItem> orderList = orderService.listOrders().stream().map(OrderItem::fromEntity).toList();
        return ApiResponse.ok(new OrderListResponse(orderList));
    }
}
