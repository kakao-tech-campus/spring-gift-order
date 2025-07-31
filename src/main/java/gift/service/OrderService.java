package gift.service;

import gift.client.KakaoMessageClient;
import gift.dto.OrderRequest;
import gift.dto.OrderResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.OptionNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageClient kakaoMessageClient;
    private final OAuthService oAuthService;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
                        WishRepository wishRepository, KakaoMessageClient kakaoMessageClient,
                        OAuthService oAuthService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageClient = kakaoMessageClient;
        this.oAuthService = oAuthService;
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new OptionNotFoundException("해당 ID의 옵션을 찾을 수 없습니다."));

        option.subtract(request.quantity());

        wishRepository.findByMemberAndProduct(member, option.getProduct())
                .ifPresent(wishRepository::delete);

        Order order = new Order(member, option, request.quantity(), request.message());
        Order savedOrder = orderRepository.save(order);

        String kakaoAccessToken = member.getKakaoAccessToken();
        if (kakaoAccessToken != null) {
            String message = String.format(
                    "주문이 완료되었습니다!\\n\\n상품: %s\\n옵션: %s\\n수량: %d개\\n메시지: %s",
                    option.getProduct().getName(), option.getName(), request.quantity(), request.message()
            );
            kakaoMessageClient.sendMessage(kakaoAccessToken, message);
        }

        return OrderResponse.from(savedOrder);
    }
}