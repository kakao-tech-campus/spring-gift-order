package gift.service;

import gift.dto.MemberRequest;
import gift.exception.MemberAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외 발생 테스트")
    void registerWithDuplicateEmail_ThrowsException() {
        MemberRequest request = new MemberRequest("duplicate@example.com", "password123");
        memberService.register(request);

        MemberAlreadyExistsException exception = assertThrows(MemberAlreadyExistsException.class, () -> {
            memberService.register(request);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 가입된 이메일입니다.");
    }
}
