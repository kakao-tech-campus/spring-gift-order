package gift.service;

import gift.client.KakaoApiClient;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.LoginResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OAuthService {

    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public OAuthService(KakaoApiClient kakaoApiClient, MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.kakaoApiClient = kakaoApiClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse loginWithKakao(String authorizationCode) {
        // 1. KakaoApiClient에게 액세스 토큰을 받아오도록 요청합니다.
        String accessToken = kakaoApiClient.getAccessToken(authorizationCode);

        // 2. KakaoApiClient에게 사용자 정보를 받아오도록 요청합니다.
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원 찾기 또는 신규 가입 (비즈니스 로직)
        Member member = memberRepository.findByEmail(userInfo.kakaoAccount().email())
                .orElseGet(() -> {
                    String randomPassword = UUID.randomUUID().toString();
                    Member newMember = new Member(userInfo.kakaoAccount().email(), randomPassword, "USER");
                    return memberRepository.save(newMember);
                });

        // 4. 우리 시스템의 JWT 토큰 발급 (비즈니스 로직)
        return new LoginResponse(jwtUtil.generateToken(member));
    }
}