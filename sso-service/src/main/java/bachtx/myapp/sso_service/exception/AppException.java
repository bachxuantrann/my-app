package bachtx.myapp.sso_service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private String customMessage;
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage == null || customMessage.isBlank()
                ? errorCode.getMessage()
                : errorCode.getMessage() + ": " + customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
