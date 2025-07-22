package gift.service;

import gift.dto.ProductResponse;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.ProductNotFoundException;
import gift.exception.WishAlreadyExistsException;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getWishes(Member member, Pageable pageable) {
        Page<Wish> wishes = wishRepository.findByMemberWithProduct(member, pageable);
        return wishes.map(Wish::getProduct).map(ProductResponse::from);
    }

    @Transactional
    public void addWish(Member member, Long productId) {
        Product product = findProductById(productId);

        wishRepository.findByMemberAndProduct(member, product)
                .ifPresent(wish -> {
                    throw new WishAlreadyExistsException("이미 위시리스트에 추가된 상품입니다.");
                });

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Transactional
    public void deleteWish(Member member, Long productId) {
        Product product = findProductById(productId);
        wishRepository.deleteByMemberAndProduct(member, product);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("해당 ID의 상품을 찾을 수 없습니다: " + productId));
    }
}