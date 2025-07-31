package gift.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoMessageClient {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageClient.class);
    private final RestTemplate restTemplate;

    public KakaoMessageClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(String accessToken, String message) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String templateObject = String.format(
                "{\"object_type\": \"text\", \"text\": \"%s\", \"link\": {\"web_url\": \"http://localhost:8080\"}}",
                message
        );

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateObject);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            log.info("카카오 메시지 전송을 시도합니다.");
            restTemplate.postForEntity(url, request, String.class);
            log.info("카카오 메시지 전송에 성공했습니다.");
        } catch (RestClientException e) {
            log.error("카카오 메시지 전송에 실패했습니다. 에러: {}", e.getMessage());
        }
    }
}