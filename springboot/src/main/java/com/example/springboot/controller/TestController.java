package com.example.springboot.controller;

import com.example.springboot.util.PasswordUtil;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证 Spring Boot 应用是否正常启动
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "GameVault Backend is running!");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 数据库连接测试
     */
    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里可以添加数据库连接测试逻辑
            result.put("status", "SUCCESS");
            result.put("message", "Database connection is working!");
            result.put("database", "gamevault");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Database connection failed: " + e.getMessage());
        }
        return result;
    }

    /**
     * 配置信息测试
     */
    @GetMapping("/config")
    public Map<String, Object> testConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("application", "GameVault Backend");
        result.put("version", "1.0.0");
        result.put("environment", "development");
        result.put("port", 8080);
        result.put("contextPath", "/api");
        return result;
    }

    /**
     * POST 请求测试
     */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("message", "Echo test successful");
        result.put("received", request);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 生成密码哈希（仅用于开发测试）
     */
    @GetMapping("/generate-password-hash")
    public Map<String, Object> generatePasswordHash(@RequestParam(defaultValue = "123456") String password) {
        Map<String, Object> result = new HashMap<>();

        // 生成BCrypt哈希
        String adminHash = PasswordUtil.encode(password);
        String managerHash = PasswordUtil.encode(password);
        String developerHash = PasswordUtil.encode(password);
        String designerHash = PasswordUtil.encode(password);

        result.put("password", password);
        result.put("adminHash", adminHash);
        result.put("managerHash", managerHash);
        result.put("developerHash", developerHash);
        result.put("designerHash", designerHash);

        // 验证哈希
        result.put("adminVerify", PasswordUtil.matches(password, adminHash));
        result.put("managerVerify", PasswordUtil.matches(password, managerHash));
        result.put("developerVerify", PasswordUtil.matches(password, developerHash));
        result.put("designerVerify", PasswordUtil.matches(password, designerHash));

        // 生成SQL更新语句
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE users SET password_hash = '").append(adminHash).append("' WHERE username = 'admin';\n");
        sql.append("UPDATE users SET password_hash = '").append(managerHash).append("' WHERE username = 'manager';\n");
        sql.append("UPDATE users SET password_hash = '").append(developerHash).append("' WHERE username = 'developer';\n");
        sql.append("UPDATE users SET password_hash = '").append(designerHash).append("' WHERE username = 'designer';");

        result.put("sqlUpdate", sql.toString());

        return result;
    }

    /**
     * 直接修复数据库中的用户密码哈希（仅用于开发测试）
     */
    @PostMapping("/fix-user-passwords")
    public Map<String, Object> fixUserPasswords(@RequestParam(defaultValue = "123456") String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成BCrypt哈希
            String adminHash = PasswordUtil.encode(password);
            String managerHash = PasswordUtil.encode(password);
            String developerHash = PasswordUtil.encode(password);
            String designerHash = PasswordUtil.encode(password);
            String userHash = PasswordUtil.encode(password);

            // 直接更新数据库
            int adminUpdated = userMapper.updatePasswordByUsername("admin", adminHash);
            int managerUpdated = userMapper.updatePasswordByUsername("manager", managerHash);
            int developerUpdated = userMapper.updatePasswordByUsername("developer", developerHash);
            int designerUpdated = userMapper.updatePasswordByUsername("designer", designerHash);

            // 尝试插入user用户（如果不存在）
            int userExists = userMapper.existsByUsername("user");
            int userUpdated = 0;
            if (userExists > 0) {
                userUpdated = userMapper.updatePasswordByUsername("user", userHash);
            } else {
                // 插入新用户
                try {
                    userMapper.insertTestUser("user", "user@gamevault.com", userHash, "user", "普通测试用户", "active");
                    userUpdated = 1;
                } catch (Exception e) {
                    // 用户可能已存在，尝试更新
                    userUpdated = userMapper.updatePasswordByUsername("user", userHash);
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "用户密码哈希修复完成");
            result.put("password", password);
            result.put("adminUpdated", adminUpdated > 0);
            result.put("managerUpdated", managerUpdated > 0);
            result.put("developerUpdated", developerUpdated > 0);
            result.put("designerUpdated", designerUpdated > 0);
            result.put("userUpdated", userUpdated > 0);
            result.put("totalUpdated", adminUpdated + managerUpdated + developerUpdated + designerUpdated + userUpdated);

            // 验证修复结果
            result.put("adminVerify", PasswordUtil.matches(password, adminHash));
            result.put("managerVerify", PasswordUtil.matches(password, managerHash));
            result.put("developerVerify", PasswordUtil.matches(password, developerHash));
            result.put("designerVerify", PasswordUtil.matches(password, designerHash));
            result.put("userVerify", PasswordUtil.matches(password, userHash));

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "修复失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 验证用户密码哈希（用于调试）
     */
    @GetMapping("/verify-user-password")
    public Map<String, Object> verifyUserPassword(@RequestParam String username, @RequestParam(defaultValue = "123456") String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从数据库获取用户
            var user = userMapper.findByUsername(username);
            if (user == null) {
                result.put("status", "ERROR");
                result.put("message", "用户不存在: " + username);
                return result;
            }

            // 获取密码哈希
            String storedHash = user.getPasswordHash();

            // 验证密码
            boolean matches = PasswordUtil.matches(password, storedHash);

            result.put("status", "SUCCESS");
            result.put("username", username);
            result.put("password", password);
            result.put("storedHashPreview", storedHash != null && storedHash.length() > 0 ?
                (storedHash.length() > 20 ? storedHash.substring(0, 20) + "..." : storedHash) : "null");
            result.put("storedHashLength", storedHash != null ? storedHash.length() : 0);
            result.put("storedHashFull", storedHash); // 显示完整的存储密码
            result.put("passwordMatches", matches);
            result.put("userActive", user.isActive());
            result.put("userRole", user.getRole());
            result.put("userStatus", user.getStatus());

            // 生成新哈希进行对比
            String newHash = PasswordUtil.encode(password);
            result.put("newHashPreview", newHash != null && newHash.length() > 20 ? newHash.substring(0, 20) + "..." : newHash);
            result.put("newHashMatches", PasswordUtil.matches(password, newHash));

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "验证失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 直接更新用户密码为明文（超级简化版）
     */
    @PostMapping("/update-plaintext-passwords")
    public Map<String, Object> updatePlaintextPasswords() {
        Map<String, Object> result = new HashMap<>();

        try {
            String[] usernames = {"admin", "manager", "developer", "designer", "user"};
            int updatedCount = 0;

            for (String username : usernames) {
                var user = userMapper.findByUsername(username);
                if (user != null) {
                    // 使用专门的方法更新密码
                    int updated = userMapper.updatePasswordByUsername(username, "123");
                    if (updated > 0) {
                        updatedCount++;
                    }
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "成功更新 " + updatedCount + " 个用户的密码为明文 '123'");
            result.put("updatedCount", updatedCount);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 检查数据库表结构（用于调试）
     */
    @GetMapping("/check-database")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取所有用户
            var users = userMapper.findAll(0, 10);
            result.put("status", "SUCCESS");
            result.put("totalUsers", users.size());

            Map<String, Object> userDetails = new HashMap<>();
            for (var user : users) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole());
                userInfo.put("status", user.getStatus());
                userInfo.put("passwordHashLength", user.getPasswordHash() != null ? user.getPasswordHash().length() : 0);
                userInfo.put("passwordHashPreview", user.getPasswordHash() != null ?
                    user.getPasswordHash().substring(0, Math.min(20, user.getPasswordHash().length())) + "..." : "null");
                userInfo.put("createdAt", user.getCreatedAt());
                userDetails.put(user.getUsername(), userInfo);
            }
            result.put("users", userDetails);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "检查失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 创建简单测试用户（用于毕业设计演示）
     */
    @PostMapping("/create-simple-users")
    public Map<String, Object> createSimpleUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 简单的测试用户数据
            String[][] testUsers = {
                {"admin", "admin@test.com", "123", "admin", "管理员账户"},
                {"user", "user@test.com", "123", "user", "普通用户账户"},
                {"test", "", "123", "user", "测试用户账户"}
            };

            int successCount = 0;
            StringBuilder messages = new StringBuilder();

            for (String[] userData : testUsers) {
                String username = userData[0];
                String email = userData[1];
                String password = userData[2];
                String role = userData[3];
                String bio = userData[4];

                // 检查用户是否已存在
                if (userMapper.existsByUsername(username) > 0) {
                    messages.append(username).append(" 已存在，跳过\n");
                    continue;
                }

                // 创建用户
                try {
                    String encodedPassword = PasswordUtil.encode(password);
                    userMapper.insertTestUser(username, email, encodedPassword, role, bio, "active");
                    successCount++;
                    messages.append(username).append(" 创建成功\n");
                } catch (Exception e) {
                    messages.append(username).append(" 创建失败: ").append(e.getMessage()).append("\n");
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "测试用户创建完成");
            result.put("successCount", successCount);
            result.put("details", messages.toString());

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "创建失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 直接更新用户密码为简单密码（用于毕业设计演示）
     */
    @PostMapping("/update-simple-passwords")
    public Map<String, Object> updateSimplePasswords() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 简单密码：123
            String simplePassword = "123";
            String encodedPassword = PasswordUtil.encode(simplePassword);

            // 更新所有主要测试用户的密码
            String[] usernames = {"admin", "manager", "developer", "designer", "user"};
            int updateCount = 0;
            StringBuilder messages = new StringBuilder();

            for (String username : usernames) {
                int updated = userMapper.updatePasswordByUsername(username, encodedPassword);
                if (updated > 0) {
                    updateCount++;
                    messages.append(username).append(" 密码更新成功\n");
                } else {
                    messages.append(username).append(" 用户不存在或更新失败\n");
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "简单密码更新完成");
            result.put("password", simplePassword);
            result.put("encodedPassword", encodedPassword);
            result.put("updateCount", updateCount);
            result.put("details", messages.toString());

            // 验证密码
            result.put("passwordVerify", PasswordUtil.matches(simplePassword, encodedPassword));

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 直接执行SQL查询检查密码（调试用）
     */
    @GetMapping("/raw-password-check")
    public Map<String, Object> rawPasswordCheck() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 直接查询admin用户的密码
            var admin = userMapper.findByUsername("admin");
            if (admin != null) {
                result.put("status", "SUCCESS");
                result.put("adminId", admin.getId());
                result.put("adminUsername", admin.getUsername());
                result.put("adminPasswordHash", admin.getPasswordHash());
                result.put("adminPasswordHashLength", admin.getPasswordHash() != null ? admin.getPasswordHash().length() : 0);
                result.put("adminPasswordHashIsNull", admin.getPasswordHash() == null);
                result.put("adminPasswordHashIsEmpty", admin.getPasswordHash() != null && admin.getPasswordHash().isEmpty());

                // 尝试直接更新这个用户的密码
                int updateResult = userMapper.updatePasswordByUsername("admin", "123");
                result.put("updateResult", updateResult);

                // 再次查询验证
                var adminAfterUpdate = userMapper.findByUsername("admin");
                result.put("adminPasswordHashAfterUpdate", adminAfterUpdate.getPasswordHash());
                result.put("adminPasswordHashLengthAfterUpdate", adminAfterUpdate.getPasswordHash() != null ? adminAfterUpdate.getPasswordHash().length() : 0);

            } else {
                result.put("status", "ERROR");
                result.put("message", "admin用户不存在");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "查询失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 直接查询数据库表结构（调试用）
     */
    @GetMapping("/check-table-structure")
    public Map<String, Object> checkTableStructure() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 使用原生SQL查询表结构
            var tableInfo = userMapper.getTableStructure();
            result.put("status", "SUCCESS");
            result.put("tableStructure", tableInfo);

            // 直接查询admin用户的原始数据
            var rawData = userMapper.getRawUserData("admin");
            result.put("rawUserData", rawData);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "查询失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 检查 User 实体类字段映射
     */
    @GetMapping("/check-user-mapping")
    public Map<String, Object> checkUserMapping() {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userMapper.findByUsername("admin");

            result.put("status", "SUCCESS");
            result.put("userFound", user != null);

            if (user != null) {
                result.put("id", user.getId());
                result.put("username", user.getUsername());
                result.put("email", user.getEmail());
                result.put("passwordHash", user.getPasswordHash());
                result.put("passwordHashLength", user.getPasswordHash() != null ? user.getPasswordHash().length() : 0);
                result.put("passwordHashIsNull", user.getPasswordHash() == null);
                result.put("passwordHashValue", user.getPasswordHash());
                result.put("role", user.getRole());
                result.put("status", user.getStatus());
                result.put("createdAt", user.getCreatedAt());
                result.put("updatedAt", user.getUpdatedAt());
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "检查用户映射失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 检查所有用户的邮箱情况
     */
    @GetMapping("/check-emails")
    public Map<String, Object> checkEmails() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> emails = userMapper.getAllUserEmails();
            result.put("status", "SUCCESS");
            result.put("userEmails", emails);
            result.put("totalUsers", emails.size());

            // 统计邮箱状态
            long emptyEmails = emails.stream().filter(e -> "EMPTY".equals(e.get("email_status"))).count();
            long nullEmails = emails.stream().filter(e -> "NULL".equals(e.get("email_status"))).count();
            long hasValueEmails = emails.stream().filter(e -> "HAS_VALUE".equals(e.get("email_status"))).count();

            result.put("emptyEmailCount", emptyEmails);
            result.put("nullEmailCount", nullEmails);
            result.put("hasValueEmailCount", hasValueEmails);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 删除空邮箱的测试用户
     */
    @DeleteMapping("/delete-empty-email-users")
    public Map<String, Object> deleteEmptyEmailUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 查找空邮箱用户
            List<Map<String, Object>> emails = userMapper.getAllUserEmails();
            long emptyEmailUsers = emails.stream()
                .filter(e -> "EMPTY".equals(e.get("email_status")))
                .count();

            if (emptyEmailUsers > 0) {
                // 删除空邮箱用户（通过SQL）
                int deletedCount = userMapper.deleteEmptyEmailUsers();
                result.put("status", "SUCCESS");
                result.put("deletedCount", deletedCount);
                result.put("message", "已删除 " + deletedCount + " 个空邮箱用户");
            } else {
                result.put("status", "SUCCESS");
                result.put("deletedCount", 0);
                result.put("message", "没有找到空邮箱用户");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
