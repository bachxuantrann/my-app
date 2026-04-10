package bachtx.myapp.sso_service.controller.admin;

import bachtx.myapp.sso_service.entity.AuditLog;
import bachtx.myapp.sso_service.entity.SystemSetting;
import bachtx.myapp.sso_service.repository.AuditLogRepository;
import bachtx.myapp.sso_service.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogAdminController {

    private final AuditLogRepository auditLogRepository;
    private final SystemSettingRepository systemSettingRepository;

    @GetMapping
    public String listAuditLogs(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                Model model) {

        if (username != null && username.isBlank()) username = null;
        
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        Page<AuditLog> logPage = auditLogRepository.findWithFilters(
                username, startDateTime, endDateTime,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        model.addAttribute("logPage", logPage);
        model.addAttribute("username", username);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());

        String retentionDays = systemSettingRepository.findById("audit_log_retention_days")
                .map(SystemSetting::getValue)
                .orElse("30"); // default
        model.addAttribute("retentionDays", retentionDays);

        return "admin/audit/list";
    }

    @PostMapping("/settings")
    public String saveSettings(@RequestParam("retentionDays") String retentionDays,
                               RedirectAttributes redirectAttributes) {
        try {
            int parsedDays = Integer.parseInt(retentionDays);
            if (parsedDays <= 0) throw new IllegalArgumentException("Must be > 0");
            
            SystemSetting setting = systemSettingRepository.findById("audit_log_retention_days")
                    .orElse(new SystemSetting("audit_log_retention_days", "30", "Số ngày lưu nhật ký Audit Logs"));
            setting.setValue(String.valueOf(parsedDays));
            systemSettingRepository.save(setting);
            
            redirectAttributes.addFlashAttribute("success", "Cấu hình tự động xoá lưu thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Số ngày không hợp lệ.");
        }
        return "redirect:/admin/audit-logs";
    }
}
