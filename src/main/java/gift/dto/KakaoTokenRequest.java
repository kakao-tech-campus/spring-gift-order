package gift.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoTokenRequest {
    private final String grantType = "authorization_code";
    private final String clientId;
    private final String redirectUri;
    private final String code;

    public KakaoTokenRequest(String clientId, String redirectUri, String code) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.code = code;
    }

    public MultiValueMap<String, String> toBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        return body;
    }
}