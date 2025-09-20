package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.dto.UserDTO;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.JwtUtil;
import com.example.springboot.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 用户注册 - 简化版
     */
    @Transactional
    public UserDTO.LoginResponse register(UserDTO.RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());

        // 检查用户名是否已存在
        if (userMapper.existsByUsername(request.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 如果提供了邮箱，检查是否已存在
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (userMapper.existsByEmail(request.getEmail()) > 0) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 创建新用户 - 简化版
        String encodedPassword = PasswordUtil.encode(request.getPassword());
        User user = new User();
        user.setUsername(request.getUsername());

        // 处理邮箱：如果为空，生成唯一的占位邮箱
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = "user" + System.currentTimeMillis() + "@placeholder.com";
        }
        user.setEmail(email);

        user.setPasswordHash(encodedPassword);
        user.setRole("user");
        user.setStatus("active");
        user.setBio(request.getBio() != null ? request.getBio() : "");
        user.setLoginCount(0);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());

        // 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("用户注册失败");
        }

        log.info("用户注册成功: {} (ID: {})", user.getUsername(), user.getId());

        // 生成令牌并返回登录响应
        return generateLoginResponse(user);
    }
    
    /**
     * 用户登录 - 简化版
     */
    @Transactional
    public UserDTO.LoginResponse login(UserDTO.LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsernameOrEmail());

        // 查找用户 - 先按用户名查找，再按邮箱查找
        User user = userMapper.findByUsername(request.getUsernameOrEmail());
        if (user == null && request.getUsernameOrEmail().contains("@")) {
            user = userMapper.findByEmail(request.getUsernameOrEmail());
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        log.info("用户登录成功: {} (ID: {})", user.getUsername(), user.getId());

        // 生成令牌并返回登录响应
        return generateLoginResponse(user);
    }
    
    /**
     * 根据ID获取用户信息
     */
    public UserDTO.UserResponse getUserById(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToUserResponse(user);
    }
    
    /**
     * 根据用户名获取用户信息
     */
    public UserDTO.UserResponse getUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToUserResponse(user);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO.UserResponse updateUser(Integer userId, UserDTO.UpdateRequest request) {
        log.info("更新用户信息: {}", userId);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            User existingUser = userMapper.findByUsername(request.getUsername());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new RuntimeException("用户名已被使用");
            }
            user.setUsername(request.getUsername());
        }
        
        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            User existingUser = userMapper.findByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 更新其他信息
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateUser(user);
        if (result <= 0) {
            throw new RuntimeException("更新用户信息失败");
        }
        
        log.info("用户信息更新成功: {}", userId);
        return convertToUserResponse(user);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Integer userId, UserDTO.ChangePasswordRequest request) {
        log.info("修改密码请求: {}", userId);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 加密新密码
        String newPasswordHash = PasswordUtil.encode(request.getNewPassword());
        
        // 更新密码
        int result = userMapper.updatePassword(userId, newPasswordHash, LocalDateTime.now());
        if (result <= 0) {
            throw new RuntimeException("密码修改失败");
        }
        
        log.info("密码修改成功: {}", userId);
    }
    
    /**
     * 生成登录响应
     */
    private UserDTO.LoginResponse generateLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        
        UserDTO.UserResponse userResponse = convertToUserResponse(user);
        
        return new UserDTO.LoginResponse(token, refreshToken, userResponse, 86400000L);
    }
    
    /**
     * 转换为用户响应对象
     */
    private UserDTO.UserResponse convertToUserResponse(User user) {
        return new UserDTO.UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getAvatar(),
            user.getPhone(),
            user.getBio(),
            user.getLastLoginAt() != null ? user.getLastLoginAt().format(formatter) : null,
            user.getLoginCount(),
            user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null,
            user.getStatus()
        );
    }
    
    /**
     * 验证令牌并获取用户信息
     */
    public UserDTO.UserResponse validateTokenAndGetUser(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                return getUserByUsername(username);
            }
        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 刷新令牌
     */
    public UserDTO.LoginResponse refreshToken(String refreshToken) {
        try {
            if (jwtUtil.isRefreshToken(refreshToken) && !jwtUtil.isTokenExpired(refreshToken)) {
                String username = jwtUtil.getUsernameFromToken(refreshToken);
                User user = userMapper.findByUsername(username);
                if (user != null && user.isActive()) {
                    return generateLoginResponse(user);
                }
            }
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
        }
        throw new RuntimeException("刷新令牌无效");
    }
}
