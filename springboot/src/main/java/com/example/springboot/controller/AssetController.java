package com.example.springboot.controller;

import com.example.springboot.common.ApiResponse;
import com.example.springboot.dto.AssetDTO;
import com.example.springboot.entity.Asset;
import com.example.springboot.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 资产管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    
    private final AssetService assetService;
    
    /**
     * 创建资产
     */
    @PostMapping
    public ApiResponse<AssetDTO.AssetResponse> createAsset(
            @Valid @RequestBody AssetDTO.CreateRequest request) {
        try {
            // TODO: 从JWT token中获取当前用户ID，这里暂时使用固定值
            Integer uploaderId = 1;
            
            AssetDTO.AssetResponse asset = assetService.createAsset(request, uploaderId);
            return ApiResponse.success("资产创建成功", asset);
        } catch (Exception e) {
            log.error("创建资产失败: {}", e.getMessage(), e);
            return ApiResponse.error("创建资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取资产详情
     */
    @GetMapping("/{id}")
    public ApiResponse<AssetDTO.AssetResponse> getAssetById(@PathVariable Integer id) {
        try {
            AssetDTO.AssetResponse asset = assetService.getAssetById(id);
            return ApiResponse.success("获取资产详情成功", asset);
        } catch (Exception e) {
            log.error("获取资产详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取资产详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询资产列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getAssetList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) Integer uploaderId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            AssetDTO.ListRequest request = new AssetDTO.ListRequest();
            request.setKeyword(keyword);
            request.setCategory(category);
            request.setStatus(status);
            request.setProjectId(projectId);
            request.setUploaderId(uploaderId);
            request.setSortBy(sortBy);
            request.setSortOrder(sortOrder);
            request.setPage(page);
            request.setSize(size);
            
            Map<String, Object> result = assetService.getAssetList(request);
            return ApiResponse.success("获取资产列表成功", result);
        } catch (Exception e) {
            log.error("获取资产列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取资产列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新资产
     */
    @PutMapping("/{id}")
    public ApiResponse<AssetDTO.AssetResponse> updateAsset(
            @PathVariable Integer id,
            @Valid @RequestBody AssetDTO.UpdateRequest request) {
        try {
            AssetDTO.AssetResponse asset = assetService.updateAsset(id, request);
            return ApiResponse.success("资产更新成功", asset);
        } catch (Exception e) {
            log.error("更新资产失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAsset(@PathVariable Integer id) {
        try {
            assetService.deleteAsset(id);
            return ApiResponse.success("资产删除成功");
        } catch (Exception e) {
            log.error("删除资产失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载资产
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAsset(@PathVariable Integer id) {
        try {
            Asset asset = assetService.downloadAsset(id);
            
            // 构建文件路径（这里假设文件存储在uploads目录下）
            String filePath = "./uploads" + asset.getFilePath();
            File file = new File(filePath);
            
            if (!file.exists()) {
                log.warn("文件不存在: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 设置响应头
            String encodedFileName = URLEncoder.encode(asset.getName(), StandardCharsets.UTF_8);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + encodedFileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, 
                           asset.getMimeType() != null ? asset.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("下载资产失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取资产统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<AssetDTO.AssetStats> getAssetStats() {
        try {
            AssetDTO.AssetStats stats = assetService.getAssetStats();
            return ApiResponse.success("获取资产统计成功", stats);
        } catch (Exception e) {
            log.error("获取资产统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取资产统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新资产状态（审核用）
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateAssetStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            assetService.updateAssetStatus(id, status);
            return ApiResponse.success("资产状态更新成功");
        } catch (Exception e) {
            log.error("更新资产状态失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新资产状态失败: " + e.getMessage());
        }
    }
}
