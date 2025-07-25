package gift.controller;

import gift.dto.LoginResponse;
import gift.service.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code) {
        LoginResponse response = oAuthService.loginWithKakao(code);
        return ResponseEntity.ok(response);
    }
}
