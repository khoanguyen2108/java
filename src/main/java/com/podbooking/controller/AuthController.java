package com.podbooking.controller;

import com.podbooking.dto.request.LoginRequest;
import com.podbooking.dto.request.RegisterRequest;
import com.podbooking.dto.response.ApiResponse;
import com.podbooking.dto.response.AuthResponse;
import com.podbooking.entity.User;
import com.podbooking.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Simple Auth Controller - Only Login & Register
 * @author KhoaHong-dev
 * @created 2025-08-07 11:49:15 UTC
 */
@RestController
@RequestMapping("/api/auth")  // ← ADD "/api" prefix
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowCredentials = "true")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            HttpSession session) {

        log.info("🔐 POST /auth/login - Login attempt for: {}", request.getEmail());

        // Validate input
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            ApiResponse<AuthResponse> response = ApiResponse.error("Dữ liệu không hợp lệ: " + errors, 400);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            AuthResponse authResponse = authService.login(request, session);
            ApiResponse<AuthResponse> response = ApiResponse.success("Đăng nhập thành công", authResponse);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Login failed: {}", e.getMessage());
            ApiResponse<AuthResponse> response = ApiResponse.error(e.getMessage(), 401);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            HttpSession session) {

        log.info("📝 POST /auth/register - Registration attempt for: {}", request.getEmail());

        // Validate input
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            ApiResponse<AuthResponse> response = ApiResponse.error("Dữ liệu không hợp lệ: " + errors, 400);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            AuthResponse authResponse = authService.register(request, session);
            ApiResponse<AuthResponse> response = ApiResponse.success("Đăng ký thành công", authResponse);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            ApiResponse<AuthResponse> response = ApiResponse.error(e.getMessage(), 400);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletRequest httpRequest,
            HttpSession session) {

        log.info("🚪 POST /auth/logout - User logout");

        try {
            authService.logout(session);
            ApiResponse<String> response = ApiResponse.success("Đăng xuất thành công", "Đã đăng xuất khỏi hệ thống");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = ApiResponse.error("Lỗi đăng xuất: " + e.getMessage(), 500);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getProfile(
            HttpServletRequest httpRequest,
            HttpSession session) {

        log.info("👤 GET /auth/profile - Get user profile");

        try {
            User user = authService.getCurrentUser(session);

            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .role(user.getRole() != null ? user.getRole().getName() : "USER")
                    .points(user.getPoints())
                    .isVip(user.getIsVip())
                    .isActive(user.getIsActive())
                    .createdAt(user.getCreatedAt())
                    .build();

            ApiResponse<AuthResponse.UserInfo> response = ApiResponse.success("Lấy thông tin thành công", userInfo);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Get profile failed: {}", e.getMessage());
            ApiResponse<AuthResponse.UserInfo> response = ApiResponse.error(e.getMessage(), 401);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> getStatus(
            HttpServletRequest httpRequest,
            HttpSession session) {

        try {
            User user = (User) session.getAttribute("LOGGED_USER");
            Instant loginTime = (Instant) session.getAttribute("LOGIN_TIME");

            if (user != null) {
                var userInfo = AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole() != null ? user.getRole().getName() : "USER")
                        .points(user.getPoints())
                        .isVip(user.getIsVip())
                        .build();

                ApiResponse<Object> response = ApiResponse.success("Đã đăng nhập", userInfo);
                response.setPath(httpRequest.getRequestURI());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = ApiResponse.error("Chưa đăng nhập", 401);
                response.setPath(httpRequest.getRequestURI());
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.error("Lỗi kiểm tra trạng thái", 500);
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test(HttpServletRequest httpRequest) {
        ApiResponse<String> response = ApiResponse.success("Auth system working!",
                "Simple POD Booking Auth by KhoaHong-dev - " + Instant.now());
        response.setPath(httpRequest.getRequestURI());
        return ResponseEntity.ok(response);
    }
}