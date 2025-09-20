package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库 users 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码哈希（不返回给前端）
     */
    @JsonIgnore
    private String passwordHash;
    
    /**
     * 用户角色：admin, manager, user
     */
    private String role;
    
    /**
     * 头像路径
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;
    
    /**
     * 登录次数
     */
    private Integer loginCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 用户状态：active, inactive, banned
     */
    private String status;
    
    /**
     * 构造函数 - 用于注册
     */
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "user";
        this.status = "active";
        this.loginCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新登录信息
     */
    public void updateLoginInfo() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查用户是否激活
     */
    public boolean isActive() {
        return "active".equals(this.status);
    }
    
    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return "admin".equals(this.role);
    }
    
    /**
     * 检查是否为管理员或经理
     */
    public boolean isManagerOrAdmin() {
        return "admin".equals(this.role) || "manager".equals(this.role);
    }
}
