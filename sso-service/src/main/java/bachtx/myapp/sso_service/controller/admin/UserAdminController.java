package bachtx.myapp.sso_service.controller.admin;

import bachtx.myapp.sso_service.constant.UserStatusEnum;
import bachtx.myapp.sso_service.entity.Role;
import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.exception.AppException;
import bachtx.myapp.sso_service.exception.ErrorCode;
import bachtx.myapp.sso_service.repository.RoleRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();
        Map<Long, String> roleMap = roles.stream()
                .collect(Collectors.toMap(Role::getId, Role::getCode));
        model.addAttribute("users", users);
        model.addAttribute("roleMap", roleMap);
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String username,
                         @RequestParam(required = false) String email,
                         @RequestParam String password,
                         @RequestParam String status,
                         @RequestParam Long roleId,
                         RedirectAttributes redirectAttr) {
        try {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .status(status)
                    .roleId(roleId)
                    .emailVerified(false)
                    .failedLoginAttempts(0)
                    .build();
            userRepository.save(user);
            redirectAttr.addFlashAttribute("successMsg", "Tạo người dùng '" + username + "' thành công!");
        } catch (AppException e) {
            redirectAttr.addFlashAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating user", e);
            redirectAttr.addFlashAttribute("errorMsg", "Lỗi khi tạo người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("isEdit", true);
        return "admin/users/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam(required = false) String email,
                         @RequestParam String status,
                         @RequestParam Long roleId,
                         RedirectAttributes redirectAttr) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            user.setEmail(email);
            user.setStatus(status);
            user.setRoleId(roleId);
            userRepository.save(user);
            redirectAttr.addFlashAttribute("successMsg", "Cập nhật người dùng thành công!");
        } catch (Exception e) {
            log.error("Error updating user id={}", id, e);
            redirectAttr.addFlashAttribute("errorMsg", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttr) {
        try {
            userRepository.deleteById(id);
            redirectAttr.addFlashAttribute("successMsg", "Đã xóa người dùng!");
        } catch (Exception e) {
            redirectAttr.addFlashAttribute("errorMsg", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Long id, RedirectAttributes redirectAttr) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            user.setStatus(UserStatusEnum.ACTIVE.name());
            userRepository.save(user);
            redirectAttr.addFlashAttribute("successMsg", "Đã mở khóa tài khoản!");
        } catch (Exception e) {
            redirectAttr.addFlashAttribute("errorMsg", "Lỗi mở khóa: " + e.getMessage());
        }
        return "redirect:/admin/users/" + id + "/edit";
    }
}
