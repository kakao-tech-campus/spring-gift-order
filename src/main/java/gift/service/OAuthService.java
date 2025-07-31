package gift.service;

import gift.client.KakaoApiClient;
import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.LoginResponse;
import gift.entity.Member;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class OAuthService {

    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final KakaoProperties kakaoProperties;

    public OAuthService(KakaoApiClient kakaoApiClient, MemberRepository memberRepository, JwtUtil jwtUtil, KakaoProperties kakaoProperties) {
        this.kakaoApiClient = kakaoApiClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.kakaoProperties = kakaoProperties;
    }

    public String getKakaoAuthorizationUrl() {
        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoProperties.clientId())
                .queryParam("redirect_uri", kakaoProperties.redirectUri())
                .toUriString();
    }

    @Transactional
    public LoginResponse loginWithKakao(String authorizationCode) {
        // 1. KakaoApiClient를 통해 AccessToken과 RefreshToken이 모두 담긴 객체를 받습니다.
        KakaoTokenResponse tokenResponse = kakaoApiClient.getAccessTokenAsObject(authorizationCode);
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(tokenResponse.accessToken());

        // 2. 사용자 정보로 회원을 찾거나, 없으면 새로 가입시킵니다.
        Member member = memberRepository.findByKakaoId(userInfo.id())
                .orElseGet(() -> {
                    // 카카오로부터 받은 이메일 (없을 수도 있으므로 null 처리)
                    String email = (userInfo.kakaoAccount() != null) ? userInfo.kakaoAccount().email() : null;
                    String randomPassword = UUID.randomUUID().toString();
                    String encodedPassword = BCrypt.hashpw(randomPassword, BCrypt.gensalt());

                    // 카카오 ID와 함께 새로운 회원을 생성합니다.
                    Member newMember = new Member(email, encodedPassword, "USER", userInfo.id());
                    return memberRepository.save(newMember);
                });

        // 3. Member 엔티티에 AccessToken과 RefreshToken을 모두 저장하고 DB에 반영합니다.
        member.setKakaoAccessToken(tokenResponse.accessToken());
        member.setKakaoRefreshToken(tokenResponse.refreshToken());

        // 4. 우리 시스템의 JWT 토큰을 발급합니다.
        return new LoginResponse(jwtUtil.generateToken(member));
    }
}
