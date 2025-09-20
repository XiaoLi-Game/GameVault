package com.example.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户数据传输对象
 */
public class UserDTO {
    
    /**
     * 用户注册请求 - 简化版
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {

        @NotBlank(message = "用户名不能为空")
        @Size(min = 2, max = 20, message = "用户名长度在2-20个字符之间")
        private String username;

        // 邮箱改为可选
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 3, max = 20, message = "密码长度在3-20个字符之间")
        private String password;

        private String bio;
    }
    
    /**
     * 用户登录请求 - 简化版
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "用户名不能为空")
        private String usernameOrEmail;

        @NotBlank(message = "密码不能为空")
        private String password;
    }
    
    /**
     * 用户信息更新请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        
        @Size(max = 50, message = "用户名长度不能超过50个字符")
        private String username;
        
        @Email(message = "邮箱格式不正确")
        private String email;
        
        @Size(max = 20, message = "手机号长度不能超过20个字符")
        private String phone;
        
        @Size(max = 500, message = "个人简介长度不能超过500个字符")
        private String bio;
        
        private String avatar;
    }
    
    /**
     * 密码修改请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
        private String newPassword;
    }
    
    /**
     * 用户响应信息（不包含敏感信息）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Integer id;
        private String username;
        private String email;
        private String role;
        private String avatar;
        private String phone;
        private String bio;
        private String lastLoginAt;
        private Integer loginCount;
        private String createdAt;
        private String status;
    }
    
    /**
     * 登录响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String refreshToken;
        private UserResponse user;
        private Long expiresIn;
    }
}
