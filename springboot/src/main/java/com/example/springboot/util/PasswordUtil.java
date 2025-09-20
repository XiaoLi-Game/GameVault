package com.example.springboot.util;

import org.springframework.stereotype.Component;

/**
 * 超级简化版密码工具类 - 直接使用明文密码
 * 毕业设计专用，不需要复杂的密码加密
 */
@Component
public class PasswordUtil {

    /**
     * 直接返回原始密码（不加密）
     */
    public static String encode(String rawPassword) {
        return rawPassword == null ? "" : rawPassword;
    }

    /**
     * 直接比较明文密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return rawPassword.equals(encodedPassword);
    }
}
