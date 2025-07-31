package gift.service;

import gift.client.KakaoApiClient;
import gift.config.KakaoProperties;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.LoginResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OAuthService {

    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final KakaoProperties kakaoProperties; // URL 생성을 위해 유지

    public OAuthService(KakaoApiClient kakaoApiClient, MemberRepository memberRepository, JwtUtil jwtUtil, KakaoProperties kakaoProperties) {
        this.kakaoApiClient = kakaoApiClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.kakaoProperties = kakaoProperties;
    }

    public String getKakaoAuthorizationUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code" +
                "&client_id=" + kakaoProperties.clientId() +
                "&redirect_uri=" + kakaoProperties.redirectUri();
    }

    @Transactional
    public LoginResponse loginWithKakao(String authorizationCode) {
        // 1. KakaoApiClient에게 액세스 토큰을 받아오도록 요청합니다.
        String accessToken = kakaoApiClient.getAccessToken(authorizationCode);

        // 2. KakaoApiClient에게 사용자 정보를 받아오도록 요청합니다.
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);

        // 3. 사용자 정보로 회원 찾기 또는 신규 가입 (비즈니스 로직)
        Member member = memberRepository.findByEmail(userInfo.kakaoAccount().email())
                .orElseGet(() -> {
                    String randomPassword = UUID.randomUUID().toString();
                    // 카카오 로그인 사용자는 기본 USER 역할 부여
                    Member newMember = new Member(userInfo.kakaoAccount().email(), randomPassword, "USER");
                    return memberRepository.save(newMember);
                });

        // 4. Member 엔티티에 카카오 액세스 토큰을 저장하고 DB에 반영합니다.
        member.setKakaoAccessToken(accessToken);
        memberRepository.save(member);

        // 5. 우리 시스템의 JWT 토큰을 발급합니다.
        return new LoginResponse(jwtUtil.generateToken(member));
    }
}
