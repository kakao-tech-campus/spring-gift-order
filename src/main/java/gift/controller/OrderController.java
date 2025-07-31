package gift.controller;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.resolver.LoginMember;
import gift.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@LoginMember Member member, @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(member, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}