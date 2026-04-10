package bachtx.myapp.sso_service.constant;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ADMIN(0, "ADMIN", "Quản trị viên"),
    USER(1, "USER", "Người dùng");

    private long id;
    private String code;
    private String description;

    UserRoleEnum(long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }


}
