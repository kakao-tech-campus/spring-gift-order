package gift.entity;

import gift.exception.InvalidOptionQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionTest {

    @Test
    @DisplayName("옵션 생성 성공 테스트")
    void createOption_Success() {
        // when
        Option option = new Option("기본 옵션", 100);
        // then
        assertThat(option.getName()).isEqualTo("기본 옵션");
        assertThat(option.getQuantity()).isEqualTo(100);
    }

    @DisplayName("유효하지 않은 이름으로 옵션 생성 시 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "이것은상품의이름이50자를훌쩍넘어버리는아주아주긴옵션이름입니다.", "금지된특수문자!@#$"})
    void createOption_WithInvalidName_ThrowsException(String invalidName) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            new Option(invalidName, 100);
        });
    }

    @Test
    @DisplayName("null 이름으로 옵션 생성 시 예외 발생")
    void createOption_WithNullName_ThrowsException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            new Option(null, 100);
        });
    }

    @DisplayName("유효하지 않은 수량으로 옵션 생성 시 예외 발생")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100_000_000})
    void createOption_WithInvalidQuantity_ThrowsException(int invalidQuantity) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            new Option("정상 이름", invalidQuantity);
        });
    }

    @Test
    @DisplayName("옵션 수량 차감 성공 테스트")
    void subtract_Success() {
        // given
        Option option = new Option("테스트 옵션", 10);
        // when
        option.subtract(3);
        // then
        assertThat(option.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("옵션 수량이 부족할 경우 예외 발생 테스트")
    void subtract_ThrowsException_WhenQuantityIsInsufficient() {
        // given
        Option option = new Option("테스트 옵션", 5);
        // when & then
        assertThrows(InvalidOptionQuantityException.class, () -> {
            option.subtract(10);
        });
    }
}
