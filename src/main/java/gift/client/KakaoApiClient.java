package gift.client;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenRequest;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.exception.KakaoApiException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiClient {

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    public KakaoApiClient(RestTemplate restTemplate, KakaoProperties kakaoProperties) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoTokenResponse getAccessTokenAsObject(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        KakaoTokenRequest kakaoTokenRequest = new KakaoTokenRequest(
                kakaoProperties.clientId(),
                kakaoProperties.redirectUri(),
                code
        );
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(kakaoTokenRequest.toBody(), headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(url, request, KakaoTokenResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new KakaoApiException("카카오 서버에서 액세스 토큰을 받아오는 중 오류가 발생했습니다.", e);
        }
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
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

    public KakaoTokenResponse refreshAccessToken(String refreshToken) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoProperties.clientId());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForObject(url, request, KakaoTokenResponse.class);
        } catch (RestClientException e) {
            throw new KakaoApiException("카카오 서버에서 액세스 토큰을 갱신하는 중 오류가 발생했습니다.", e);
        }
    }
}
