package gift.entity;

import gift.exception.InvalidOptionQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionTest {

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