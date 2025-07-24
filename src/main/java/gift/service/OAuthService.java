package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.LoginResponse;
import gift.entity.Member;
import gift.exception.KakaoApiException;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class OAuthService {

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public OAuthService(RestTemplate restTemplate, KakaoProperties kakaoProperties, MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse loginWithKakao(String authorizationCode) {
        // 1. 인가 코드로 액세스 토큰 받기
        String accessToken = getAccessToken(authorizationCode);
        // 2. 액세스 토큰으로 사용자 정보 받기
        KakaoUserInfoResponse userInfo = getUserInfo(accessToken);

        // 3. 사용자 정보로 회원 찾기 또는 신규 가입
        Member member = memberRepository.findByEmail(userInfo.kakaoAccount().email())
                .orElseGet(() -> {
                    // 카카오 로그인 사용자는 비밀번호를 랜덤 UUID로 설정하여 가입
                    String randomPassword = UUID.randomUUID().toString();
                    Member newMember = new Member(userInfo.kakaoAccount().email(), randomPassword, "USER");
                    return memberRepository.save(newMember);
                });

        // 4. 우리 시스템의 JWT 토큰 발급
        return new LoginResponse(jwtUtil.generateToken(member));
    }

    private String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(url, request, KakaoTokenResponse.class);
            return response.getBody().accessToken();
        } catch (RestClientException e) {
            throw new KakaoApiException("카카오 서버에서 액세스 토큰을 받아오는 중 오류가 발생했습니다.", e);
        }
    }

    private KakaoUserInfoResponse getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.postForEntity(url, request, KakaoUserInfoResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new KakaoApiException("카카오 서버에서 사용자 정보를 받아오는 중 오류가 발생했습니다.", e);
        }
    }
}
