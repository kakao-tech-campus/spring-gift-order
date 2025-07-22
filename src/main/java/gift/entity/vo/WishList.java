package gift.entity.vo;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class WishList {

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    // 위시리스트에 상품 추가
    public void add(Member member, Product product) {
        Wish wish = new Wish(member, product);
        // 중복 추가 방지
        if (wishes.contains(wish)) {
            return;
        }
        wishes.add(wish);
    }

    // 위시리스트에서 상품 삭제
    public void remove(Product product) {
        wishes.removeIf(wish -> wish.getProduct().equals(product));
    }

    // 위시리스트의 모든 상품 목록을 반환
    public List<Product> getProducts() {
        return wishes.stream()
                .map(Wish::getProduct)
                .collect(Collectors.toList());
    }
}
