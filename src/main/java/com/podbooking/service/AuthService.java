package com.podbooking.service;

import com.podbooking.dto.request.LoginRequest;
import com.podbooking.dto.request.RegisterRequest;
import com.podbooking.dto.response.AuthResponse;
import com.podbooking.entity.Role;
import com.podbooking.entity.User;
import com.podbooking.repository.RoleRepository;
import com.podbooking.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Simple Auth Service - Only Login & Register
 * @author KhoaHong-dev
 * @created 2025-08-07 11:49:15 UTC
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request, HttpSession session) {
        try {
            log.info("🔐 Login attempt for: {}", request.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            // Update last login time
            user.setLastLogin(Instant.now());
            userRepository.save(user);

            // Store user in session
            session.setAttribute("LOGGED_USER", user);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("LOGIN_TIME", Instant.now());

            log.info("✅ Login successful for: {} (ID: {})", user.getName(), user.getId());

            return AuthResponse.builder()
                    .sessionId(session.getId())
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .role(user.getRole() != null ? user.getRole().getName() : "USER")
                            .points(user.getPoints())
                            .isVip(user.getIsVip())
                            .isActive(user.getIsActive())
                            .createdAt(user.getCreatedAt())
                            .build())
                    .loginTime(Instant.now())
                    .build();

        } catch (BadCredentialsException e) {
            log.error("❌ Invalid credentials for: {}", request.getEmail());
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        } catch (Exception e) {
            log.error("❌ Login error: {}", e.getMessage());
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    public AuthResponse register(RegisterRequest request, HttpSession session) {
        log.info("📝 Registration attempt for: {}", request.getEmail());

        try {
            // Validate passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp");
            }

            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được đăng ký");
            }

            // Get or create default role
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseGet(this::createDefaultRole);

            // Create new user
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhone(request.getPhone());
            user.setRole(defaultRole);
            user.setIsActive(true);
            user.setEmailVerified(false);
            user.setIsVip(false);
            user.setPoints(100); // Welcome bonus
            user.setVipTier("NONE");
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());

            user = userRepository.save(user);

            // Auto login after registration
            session.setAttribute("LOGGED_USER", user);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("LOGIN_TIME", Instant.now());

            log.info("✅ Registration successful for: {} (ID: {})", user.getName(), user.getId());

            return AuthResponse.builder()
                    .sessionId(session.getId())
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .role(user.getRole().getName())
                            .points(user.getPoints())
                            .isVip(user.getIsVip())
                            .isActive(user.getIsActive())
                            .createdAt(user.getCreatedAt())
                            .build())
                    .loginTime(Instant.now())
                    .build();

        } catch (Exception e) {
            log.error("❌ Registration error for {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Đăng ký thất bại: " + e.getMessage());
        }
    }

    public void logout(HttpSession session) {
        try {
            log.info("🚪 Logout for session: {}", session.getId());
            SecurityContextHolder.clearContext();
            session.invalidate();
        } catch (Exception e) {
            log.error("❌ Logout error: {}", e.getMessage());
            throw new RuntimeException("Đăng xuất thất bại");
        }
    }

    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_USER");
        if (user == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        // Refresh user data from database
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    private Role createDefaultRole() {
        log.info("📋 Creating default USER role");
        Role role = new Role();
        role.setName("USER");
        role.setDescription("Default user role");
        role.setCreatedAt(Instant.now());
        return roleRepository.save(role);
    }
}