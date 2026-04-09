package bachtx.myapp.sso_service.constant;

public enum UserStatusEnum {
    PENDING("Chờ xác nhận"),
    ACTIVE("Đã kích hoạt"),
    INACTIVE("Đã vô hiệu hóa"),
    DISABLED("Đã bị chặn"),
    LOCKED("Đã khóa"),
    DELETED("Đã xóa");
    private final String value;

    UserStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
