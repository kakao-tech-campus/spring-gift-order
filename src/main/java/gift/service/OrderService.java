package gift.service;

import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.event.OrderCompletedEvent;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
                        WishRepository wishRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        Option option = optionRepository.findByIdWithPessimisticLock(request.optionId())
                .orElseThrow(() -> new OptionNotFoundException("해당 ID의 옵션을 찾을 수 없습니다."));

        option.subtract(request.quantity());

        wishRepository.findByMemberAndProduct(member, option.getProduct())
                .ifPresent(wishRepository::delete);

        Order order = new Order(member, option, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCompletedEvent(savedOrder));

        return OrderResponse.from(savedOrder);
    }
}
