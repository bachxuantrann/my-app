package bachtx.myapp.sso_service.service;

import bachtx.myapp.sso_service.dto.request.RegisterRequest;

public interface UserService {
    void registerUser(RegisterRequest request);
    void processForgotPassword(String email);
}
