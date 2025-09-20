package com.example.springboot.controller;

import com.example.springboot.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    
    /**
     * 获取系统设置
     */
    @GetMapping("/settings")
    public ApiResponse<Map<String, Object>> getSystemSettings() {
        try {
            Map<String, Object> settings = Map.of(
                "siteName", "GameVault",
                "siteDescription", "游戏资产管理系统",
                "maxFileSize", "100MB",
                "allowedFileTypes", "jpg,jpeg,png,gif,bmp,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar,7z,mp3,mp4,avi,mov,fbx,obj,3ds,max,blend",
                "enableAudit", true,
                "enableNotification", true
            );
            return ApiResponse.success("获取系统设置成功", settings);
        } catch (Exception e) {
            log.error("获取系统设置失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取系统设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新系统设置
     */
    @PutMapping("/settings")
    public ApiResponse<Void> updateSystemSettings(@RequestBody Map<String, Object> settings) {
        try {
            log.info("更新系统设置: {}", settings);
            return ApiResponse.success("系统设置更新成功");
        } catch (Exception e) {
            log.error("更新系统设置失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新系统设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> status = Map.of(
                "version", "1.0.0",
                "uptime", "2天3小时45分钟",
                "memoryUsage", "512MB / 2GB",
                "diskUsage", "15GB / 100GB",
                "cpuUsage", "25%",
                "activeUsers", 12,
                "totalRequests", 15678L
            );
            return ApiResponse.success("获取系统状态成功", status);
        } catch (Exception e) {
            log.error("获取系统状态失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取系统状态失败: " + e.getMessage());
        }
    }
}
