package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Integer id);
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据用户名或邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    User findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int existsByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int existsByEmail(@Param("email") String email);
    
    /**
     * 插入新用户
     */
    @Insert("INSERT INTO users (username, email, password_hash, role, bio, login_count, created_at, updated_at, status) " +
            "VALUES (#{username}, #{email}, #{passwordHash}, #{role}, #{bio}, #{loginCount}, #{createdAt}, #{updatedAt}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET username = #{username}, email = #{email}, phone = #{phone}, " +
            "bio = #{bio}, avatar = #{avatar}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateUser(User user);
    
    /**
     * 更新用户密码
     */
    @Update("UPDATE users SET password_hash = #{passwordHash}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("passwordHash") String passwordHash, 
                      @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新登录信息
     */
    @Update("UPDATE users SET last_login_at = #{lastLoginAt}, login_count = #{loginCount}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int updateLoginInfo(@Param("id") Integer id, @Param("lastLoginAt") LocalDateTime lastLoginAt,
                       @Param("loginCount") Integer loginCount, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status, 
                    @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
    
    /**
     * 查询所有用户（分页）
     */
    @Select("SELECT * FROM users ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据角色查询用户
     */
    @Select("SELECT * FROM users WHERE role = #{role} ORDER BY created_at DESC")
    List<User> findByRole(@Param("role") String role);
    
    /**
     * 根据状态查询用户
     */
    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY created_at DESC")
    List<User> findByStatus(@Param("status") String status);
    
    /**
     * 搜索用户（根据用户名或邮箱）
     */
    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%') ORDER BY created_at DESC")
    List<User> searchUsers(@Param("keyword") String keyword);
    
    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    int countAll();
    
    /**
     * 根据状态统计用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    int countByStatus(@Param("status") String status);

    /**
     * 根据用户名更新密码哈希（用于测试修复）
     */
    @Update("UPDATE users SET password_hash = #{passwordHash}, updated_at = NOW() WHERE username = #{username}")
    int updatePasswordByUsername(@Param("username") String username, @Param("passwordHash") String passwordHash);

    /**
     * 插入测试用户（用于测试修复）
     */
    @Insert("INSERT INTO users (username, email, password_hash, role, bio, status, login_count, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{passwordHash}, #{role}, #{bio}, #{status}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTestUser(@Param("username") String username, @Param("email") String email,
                      @Param("passwordHash") String passwordHash, @Param("role") String role,
                      @Param("bio") String bio, @Param("status") String status);

    /**
     * 获取表结构信息（调试用）
     */
    @Select("DESCRIBE users")
    List<Map<String, Object>> getTableStructure();

    /**
     * 获取用户原始数据（调试用）
     */
    @Select("SELECT id, username, email, password_hash, role, status, created_at, updated_at FROM users WHERE username = #{username}")
    Map<String, Object> getRawUserData(@Param("username") String username);

    /**
     * 获取所有用户的邮箱信息（调试用）
     */
    @Select("SELECT id, username, email, CASE WHEN email = '' THEN 'EMPTY' WHEN email IS NULL THEN 'NULL' ELSE 'HAS_VALUE' END as email_status FROM users ORDER BY id")
    List<Map<String, Object>> getAllUserEmails();

    /**
     * 删除空邮箱的用户（调试用）
     */
    @Delete("DELETE FROM users WHERE email = '' OR email IS NULL")
    int deleteEmptyEmailUsers();
}
