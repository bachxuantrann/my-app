package bachtx.myapp.sso_service.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHENTICATED(401,"AUTH_001", "Chưa xác thực"),
    UNCATEGORIZED_EXCEPTION(500,"UNCATEGORIZED_ERROR", "Lỗi hệ thống, chưa xác định"),
    ACCESS_DENIED(403,"AUTH_002", "Không có quyền truy cập"),
    INVALID_ENUM_VALUE(400,"VALIDATION_001", "Giá trị không hợp lệ"),
    INVALID_KEY(400,"VALIDATION_002", "Key không hợp lệ"),
    INIT_ROLE_ERROR(500, "INIT_ROLE_001"," Không tìm thấy giá trị ROLE được khởi tạo"),
    USER_EXISTED(400, "USER_001", "Tên đăng nhập đã tồn tại"),
    CLIENT_NOT_FOUND(404, "CLIENT_001", "Không tìm thấy thông tin Client")


    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
