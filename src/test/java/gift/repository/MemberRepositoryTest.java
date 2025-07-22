package gift.repository;

import gift.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 및 이메일로 조회 테스트")
    void saveAndFindByEmail() {
        // given
        String expectedEmail = "test@example.com";
        Member member = new Member(expectedEmail, "password", "USER");

        // when
        memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(expectedEmail).orElse(null);

        // then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo(expectedEmail);
    }
}
