package gift.entity.vo;

import gift.exception.InvalidMoneyException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Money {
    @Column(name = "price", nullable = false)
    private Integer value;

    protected Money() {}

    public Money(Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(Integer value) {
        if (value == null || value < 0) {
            throw new InvalidMoneyException("가격은 0 이상이어야 합니다.");
        }
    }

    public Integer getValue() {
        return value;
    }
}