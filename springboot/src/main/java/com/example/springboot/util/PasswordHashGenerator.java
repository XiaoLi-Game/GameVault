package com.example.springboot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成器
 * 用于生成测试用户的正确密码哈希
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        
        System.out.println("=== 密码哈希生成器 ===");
        System.out.println("原密码: " + password);
        System.out.println();
        
        // 生成多个哈希值（每次都不同）
        for (int i = 1; i <= 4; i++) {
            String hash = encoder.encode(password);
            System.out.println("用户" + i + " 哈希: " + hash);
            
            // 验证哈希是否正确
            boolean matches = encoder.matches(password, hash);
            System.out.println("验证结果: " + (matches ? "✅ 正确" : "❌ 错误"));
            System.out.println();
        }
        
        System.out.println("=== SQL更新语句 ===");
        System.out.println("-- 更新测试用户密码哈希（密码：123456）");
        
        String adminHash = encoder.encode(password);
        String managerHash = encoder.encode(password);
        String developerHash = encoder.encode(password);
        String designerHash = encoder.encode(password);
        
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password_hash = '" + managerHash + "' WHERE username = 'manager';");
        System.out.println("UPDATE users SET password_hash = '" + developerHash + "' WHERE username = 'developer';");
        System.out.println("UPDATE users SET password_hash = '" + designerHash + "' WHERE username = 'designer';");
        
        System.out.println();
        System.out.println("=== 验证测试 ===");
        
        // 测试验证
        System.out.println("admin 密码验证: " + encoder.matches(password, adminHash));
        System.out.println("manager 密码验证: " + encoder.matches(password, managerHash));
        System.out.println("developer 密码验证: " + encoder.matches(password, developerHash));
        System.out.println("designer 密码验证: " + encoder.matches(password, designerHash));
    }
}
