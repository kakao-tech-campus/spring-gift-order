package gift.entity;

import gift.exception.InvalidOptionQuantityException;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "product_option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Option() {}

    public Option(String name, int quantity) {
        validate(name, quantity);
        this.name = name;
        this.quantity = quantity;
    }

    // --- Getter ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public Product getProduct() { return product; }

    // 연관관계 편의 메소드
    public void setProduct(Product product) {
        this.product = product;
    }

    // --- 비즈니스 로직 ---
    public void subtract(int amount) {
        if (this.quantity < amount) {
            throw new InvalidOptionQuantityException("재고가 부족합니다. 현재 재고: " + this.quantity);
        }
        this.quantity -= amount;
    }

    private void validate(String name, int quantity) {
        if (name == null || name.isBlank() || name.length() > 50) {
            throw new IllegalArgumentException("옵션 이름은 1자 이상 50자 이하이어야 합니다.");
        }
        if (!name.matches("^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$")) {
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다.");
        }
        if (quantity < 1 || quantity >= 100_000_000) {
            throw new IllegalArgumentException("옵션 수량은 1 이상 1억 미만이어야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return Objects.equals(id, option.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}