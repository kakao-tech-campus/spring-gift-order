package gift.exception;

public class KakaoApiException extends RuntimeException {
    public KakaoApiException(String message, Throwable cause) {
        super(message, cause);
    }
}