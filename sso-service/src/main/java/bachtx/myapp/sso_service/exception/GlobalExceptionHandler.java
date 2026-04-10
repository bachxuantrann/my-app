package bachtx.myapp.sso_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * SSR-focused exception handler.
 * - AppException → renders error page with the specific message
 * - AccessDeniedException → renders 403 error page
 * - All other exceptions → renders generic 500 error page
 *
 * Note: endpoints under /oauth2/** and /introspect are REST-based and handled
 * separately by Spring Security / RestControllerAdvice if needed.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_VIEW = "error/page";

    @ExceptionHandler(AppException.class)
    public ModelAndView handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("AppException at [{}]: {} ({})", request.getRequestURI(), ex.getMessage(), ex.getErrorCode().getCode());

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("status", ex.getErrorCode().getStatus());
        mav.addObject("errorCode", ex.getErrorCode().getCode());
        mav.addObject("message", ex.getMessage());
        mav.addObject("requestUri", request.getRequestURI());
        mav.setStatus(HttpStatus.valueOf(ex.getErrorCode().getStatus()));
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied at [{}]: {}", request.getRequestURI(), ex.getMessage());

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("status", 403);
        mav.addObject("errorCode", "ACCESS_DENIED");
        mav.addObject("message", "Bạn không có quyền truy cập trang này.");
        mav.addObject("requestUri", request.getRequestURI());
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]:", request.getRequestURI(), ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("status", 500);
        mav.addObject("errorCode", "INTERNAL_ERROR");
        mav.addObject("message", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
        mav.addObject("requestUri", request.getRequestURI());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
