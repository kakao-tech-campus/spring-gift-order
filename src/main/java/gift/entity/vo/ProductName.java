package gift.entity.vo;

import gift.exception.InvalidProductNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
public class ProductName {
    private static final int MAX_LENGTH = 15;
    // 허용된 특수문자: ( ) [ ] + - & / _
    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$");
    private static final String FORBIDDEN_WORD = "카카오";

    @Column(name = "name", nullable = false, length = MAX_LENGTH)
    private String value;

    protected ProductName() {}

    public ProductName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        validateIsNotBlank(value);
        validateMaxLength(value);
        validateNoForbiddenWords(value);
        validateAllowedPattern(value);
    }

    private void validateIsNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidProductNameException("상품명은 비워둘 수 없습니다.");
        }
    }

    private void validateMaxLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new InvalidProductNameException("상품명은 15자를 초과할 수 없습니다.");
        }
    }

    private void validateNoForbiddenWords(String value) {
        if (value.contains(FORBIDDEN_WORD)) {
            throw new InvalidProductNameException("상품명에 '카카오'는 담당 MD와 협의 후 사용 가능합니다.");
        }
    }

    private void validateAllowedPattern(String value) {
        Matcher matcher = ALLOWED_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new InvalidProductNameException("상품 이름에 허용되지 않는 특수문자가 포함되어 있습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}