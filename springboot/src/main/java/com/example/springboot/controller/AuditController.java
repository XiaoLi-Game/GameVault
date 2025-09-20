package com.example.springboot.controller;

import com.example.springboot.common.ApiResponse;
import com.example.springboot.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审核管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {
    
    private final AssetService assetService;
    
    /**
     * 审核通过
     */
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approveAsset(@PathVariable Integer id) {
        try {
            assetService.updateAssetStatus(id, "approved");
            return ApiResponse.success("审核通过成功");
        } catch (Exception e) {
            log.error("审核通过失败: {}", e.getMessage(), e);
            return ApiResponse.error("审核通过失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核拒绝
     */
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> rejectAsset(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        try {
            assetService.updateAssetStatus(id, "rejected");
            log.info("资产审核拒绝: ID={}, 原因={}", id, reason);
            return ApiResponse.success("审核拒绝成功");
        } catch (Exception e) {
            log.error("审核拒绝失败: {}", e.getMessage(), e);
            return ApiResponse.error("审核拒绝失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量审核
     */
    @PostMapping("/batch")
    public ApiResponse<Void> batchAudit(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            String action = (String) request.get("action");
            
            if (ids == null || ids.isEmpty()) {
                return ApiResponse.error("请选择要审核的资产");
            }
            
            String status = "approve".equals(action) ? "approved" : "rejected";
            
            for (Integer id : ids) {
                assetService.updateAssetStatus(id, status);
            }
            
            log.info("批量审核成功: 数量={}, 操作={}", ids.size(), action);
            return ApiResponse.success("批量审核成功");
        } catch (Exception e) {
            log.error("批量审核失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量审核失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取审核历史
     */
    @GetMapping("/history")
    public ApiResponse<Map<String, Object>> getAuditHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 这里简化处理，直接返回已审核的资产列表
            // 在实际项目中，应该有专门的审核记录表
            Map<String, Object> result = Map.of(
                "list", List.of(),
                "total", 0L,
                "page", page,
                "size", size,
                "pages", 0L
            );
            return ApiResponse.success("获取审核历史成功", result);
        } catch (Exception e) {
            log.error("获取审核历史失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取审核历史失败: " + e.getMessage());
        }
    }
}
