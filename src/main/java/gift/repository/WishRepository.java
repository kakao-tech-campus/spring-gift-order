package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    @Query(value = "SELECT w FROM Wish w JOIN FETCH w.product p WHERE w.member = :member",
            countQuery = "SELECT count(w) FROM Wish w WHERE w.member = :member")
    Page<Wish> findByMemberWithProduct(@Param("member") Member member, Pageable pageable);
    Optional<Wish> findByMemberAndProduct(Member member, Product product);
    void deleteByMemberAndProduct(Member member, Product product);
}
