package gift.service;

import gift.client.KakaoApiClient;
import gift.client.KakaoMessageClient;
import gift.dto.KakaoTokenResponse;
import gift.entity.Member;
import gift.entity.Option;
import gift.event.OrderCompletedEvent;
import gift.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final KakaoMessageClient kakaoMessageClient;
    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;

    public NotificationService(KakaoMessageClient kakaoMessageClient, KakaoApiClient kakaoApiClient, MemberRepository memberRepository) {
        this.kakaoMessageClient = kakaoMessageClient;
        this.kakaoApiClient = kakaoApiClient;
        this.memberRepository = memberRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendKakaoTalkNotification(OrderCompletedEvent event) {
        log.info("주문 완료 이벤트 수신. 카카오톡 메시지 전송 시작. Order ID: {}", event.getOrder().getId());

        Member member = event.getOrder().getMember();
        String message = createOrderMessage(event);

        try {
            sendMessageWithTokenRefresh(member, message);
        } catch (Exception e) {
            log.error("카카오 메시지 전송 최종 실패: 사용자 이메일={}, 원인={}", member.getEmail(), e.getMessage());
        }
    }

    private void sendMessageWithTokenRefresh(Member member, String message) {
        String accessToken = member.getKakaoAccessToken();
        if (accessToken == null) {
            log.warn("사용자에게 카카오 액세스 토큰이 없습니다. 메시지 전송을 건너뜁니다. 사용자: {}", member.getEmail());
            return;
        }

        try {
            kakaoMessageClient.sendMessage(accessToken, message);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("카카오 액세스 토큰이 만료되었습니다. 갱신을 시도합니다. 사용자: {}", member.getEmail());
            String refreshToken = member.getKakaoRefreshToken();
            if (refreshToken == null) {
                log.error("리프레시 토큰이 없어 카카오 액세스 토큰을 갱신할 수 없습니다. 사용자: {}", member.getEmail());
                return;
            }

            KakaoTokenResponse newTokens = kakaoApiClient.refreshAccessToken(refreshToken);

            member.setKakaoAccessToken(newTokens.accessToken());
            if (newTokens.refreshToken() != null) {
                member.setKakaoRefreshToken(newTokens.refreshToken());
            }
            memberRepository.save(member);
            log.info("카카오 토큰이 성공적으로 갱신되었습니다. 사용자: {}", member.getEmail());

            kakaoMessageClient.sendMessage(newTokens.accessToken(), message);
        }
    }

    private String createOrderMessage(OrderCompletedEvent event) {
        Option option = event.getOrder().getOption();
        return String.format(
                "주문이 완료되었습니다!\\n\\n상품: %s\\n옵션: %s\\n수량: %d개\\n메시지: %s",
                option.getProduct().getName(),
                option.getName(),
                event.getOrder().getQuantity(),
                event.getOrder().getMessage()
        );
    }
}