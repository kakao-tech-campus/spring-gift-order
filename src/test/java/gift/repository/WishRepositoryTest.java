package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    private Member member;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전에 필요한 엔티티들을 미리 저장
        member = memberRepository.save(new Member("test@example.com", "pw", "USER"));
        product1 = productRepository.save(new Product(new ProductName("상품1"), new Money(100), "1.jpg"));
        product2 = productRepository.save(new Product(new ProductName("상품2"), new Money(200), "2.jpg"));
    }

    @Test
    @DisplayName("위시리스트 저장 및 멤버로 조회 테스트 (페이지네이션 적용)")
    void saveAndFindByMemberWithProduct() {
        // given
        wishRepository.save(new Wish(member, product1));
        wishRepository.save(new Wish(member, product2));
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Wish> wishesPage = wishRepository.findByMemberWithProduct(member, pageable);

        // then
        assertThat(wishesPage.getTotalElements()).isEqualTo(2);
        assertThat(wishesPage.getContent()).hasSize(2);
        assertThat(wishesPage.getContent().get(0).getProduct().getName()).isEqualTo("상품1");
    }

    @Test
    @DisplayName("위시리스트 삭제 테스트")
    void deleteByMemberAndProduct() {
        // given
        Wish savedWish = wishRepository.save(new Wish(member, product1));
        assertThat(wishRepository.findById(savedWish.getId())).isPresent();

        // when
        wishRepository.deleteByMemberAndProduct(member, product1);

        // then
        assertThat(wishRepository.findByMemberAndProduct(member, product1)).isEmpty();
    }
}
