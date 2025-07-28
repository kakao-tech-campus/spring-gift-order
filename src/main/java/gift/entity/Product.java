package gift.entity;

import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductName name;

    @Embedded
    private Money price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    protected Product() {
    }

    public Product(ProductName name, Money price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public Integer getPrice() {
        return price.getValue();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void update(ProductName newName, Money newPrice, String newImageUrl) {
        this.name = newName;
        this.price = newPrice;
        this.imageUrl = newImageUrl;
    }

    public void setOptions(List<Option> newOptions) {
        validateHasOptions(newOptions);

        long
                distinctCount = newOptions.stream().map(Option::getName).distinct().count();
        if (distinctCount != newOptions.size()) {
            throw new IllegalArgumentException("새로 추가하려는 옵션 목록에 중복된 이름이 존재합니다.");
        }

        this.options.clear();
        newOptions.forEach(this::addOption);
    }

    public void addOption(Option option) {
        if (this.options.stream()
                .anyMatch(o -> o.getName().equals(option.getName()))) {
            throw new IllegalArgumentException("동일한 상품 내에 중복된 옵션 이름이 존재할 수 없습니다: " + option.getName());
        }
        this.options.add(option);
        option.setProduct(this);
    }

    private void validateHasOptions(List<Option> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("상품에는 최소 하나 이상의 옵션이 있어야 합니다.");
        }
    }
}