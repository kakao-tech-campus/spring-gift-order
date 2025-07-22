package gift.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ProductNameValidator implements ConstraintValidator<ValidProductName, String> {

    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 체크는 @NotBlank 와 같은 다른 어노테이션에 위임
        }

        // 1. 이름 길이 검사 (최대 15자)
        if (value.length() > 15) {
            return false;
        }

        // 2. "카카오" 문구 포함 검사
        if (value.contains("카카오")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("상품 이름에 '카카오'는 담당 MD와 협의 후 사용 가능합니다.")
                    .addConstraintViolation();
            return false;
        }

        // 3. 허용된 문자 패턴 검사
        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("상품 이름에 허용되지 않는 특수문자가 포함되어 있습니다.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}