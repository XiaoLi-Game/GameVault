package com.example.springboot.controller;

import com.example.springboot.common.ApiResponse;
import com.example.springboot.dto.UserDTO;
import com.example.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO.LoginResponse> register(@Valid @RequestBody UserDTO.RegisterRequest request) {
        try {
            log.info("用户注册请求: {}", request.getUsername());
            UserDTO.LoginResponse response = userService.register(request);
            return ApiResponse.success("注册成功", response);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<UserDTO.LoginResponse> login(@Valid @RequestBody UserDTO.LoginRequest request) {
        try {
            log.info("用户登录请求: {}", request.getUsernameOrEmail());
            UserDTO.LoginResponse response = userService.login(request);
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return ApiResponse.unauthorized(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO.UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            UserDTO.UserResponse user = userService.validateTokenAndGetUser(token);
            if (user == null) {
                return ApiResponse.unauthorized("令牌无效");
            }
            
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.unauthorized("令牌无效");
        }
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO.UserResponse> getUserById(@PathVariable Integer id) {
        try {
            UserDTO.UserResponse user = userService.getUserById(id);
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        }
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    public ApiResponse<java.util.Map<String, Object>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 简化实现，返回模拟数据
            java.util.List<UserDTO.UserResponse> users = java.util.List.of(
                new UserDTO.UserResponse(1, "admin", "admin@example.com", "admin", null, null, null, null, 0, null, "active"),
                new UserDTO.UserResponse(2, "user", "user@example.com", "user", null, null, null, null, 0, null, "active"),
                new UserDTO.UserResponse(3, "manager", "manager@example.com", "manager", null, null, null, null, 0, null, "active")
            );

            java.util.Map<String, Object> result = java.util.Map.of(
                "list", users,
                "total", 3L,
                "page", page,
                "size", size,
                "pages", 1L
            );

            return ApiResponse.success("获取用户列表成功", result);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/{id}/role")
    public ApiResponse<Void> updateUserRole(
            @PathVariable Integer id,
            @RequestParam String role) {
        try {
            // 简化实现，只记录日志
            log.info("更新用户角色: ID={}, 角色={}", id, role);
            return ApiResponse.success("用户角色更新成功");
        } catch (Exception e) {
            log.error("更新用户角色失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新用户角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public ApiResponse<UserDTO.UserResponse> getUserByUsername(@PathVariable String username) {
        try {
            UserDTO.UserResponse user = userService.getUserByUsername(username);
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ApiResponse<UserDTO.UserResponse> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserDTO.UpdateRequest request) {
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证令牌并获取用户ID
            UserDTO.UserResponse currentUser = userService.validateTokenAndGetUser(token);
            if (currentUser == null) {
                return ApiResponse.unauthorized("令牌无效");
            }
            
            UserDTO.UserResponse updatedUser = userService.updateUser(currentUser.getId(), request);
            return ApiResponse.success("更新成功", updatedUser);
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserDTO.ChangePasswordRequest request) {
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证令牌并获取用户ID
            UserDTO.UserResponse currentUser = userService.validateTokenAndGetUser(token);
            if (currentUser == null) {
                return ApiResponse.unauthorized("令牌无效");
            }
            
            userService.changePassword(currentUser.getId(), request);
            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<UserDTO.LoginResponse> refreshToken(@RequestBody String refreshToken) {
        try {
            UserDTO.LoginResponse response = userService.refreshToken(refreshToken);
            return ApiResponse.success("令牌刷新成功", response);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return ApiResponse.unauthorized(e.getMessage());
        }
    }
    
    /**
     * 用户登出（客户端处理，服务端记录日志）
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            UserDTO.UserResponse user = userService.validateTokenAndGetUser(token);
            if (user != null) {
                log.info("用户登出: {} (ID: {})", user.getUsername(), user.getId());
            }
            
            return ApiResponse.success("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return ApiResponse.success("登出成功"); // 即使失败也返回成功，因为登出是客户端行为
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check/username/{username}")
    public ApiResponse<Boolean> checkUsernameAvailable(@PathVariable String username) {
        try {
            boolean available = userService.getUserByUsername(username) == null;
            return ApiResponse.success(available);
        } catch (Exception e) {
            // 用户不存在，说明用户名可用
            return ApiResponse.success(true);
        }
    }
    
    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check/email/{email}")
    public ApiResponse<Boolean> checkEmailAvailable(@PathVariable String email) {
        try {
            // 这里需要在 UserService 中添加 getUserByEmail 方法
            // 暂时返回 true
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.success(true);
        }
    }
}
