package com.example.springboot.service;

import com.example.springboot.entity.Asset;
import com.example.springboot.dto.AssetDTO;
import com.example.springboot.mapper.AssetMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产业务逻辑层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {
    
    private final AssetMapper assetMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * 创建资产
     */
    @Transactional
    public AssetDTO.AssetResponse createAsset(AssetDTO.CreateRequest request, Integer uploaderId) {
        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setFilePath(request.getFilePath());
        asset.setFileSize(request.getFileSize());
        asset.setFileType(request.getFileType());
        asset.setMimeType(request.getMimeType());
        asset.setFileHash(request.getFileHash());
        asset.setThumbnailPath(request.getThumbnailPath());
        asset.setProjectId(request.getProjectId());
        asset.setUploaderId(uploaderId);
        asset.setCategory(request.getCategory());
        asset.setDownloadCount(0);
        asset.setViewCount(0);
        asset.setStatus("pending"); // 默认待审核状态
        
        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                asset.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                log.warn("标签序列化失败: {}", e.getMessage());
                asset.setTags("[]");
            }
        } else {
            asset.setTags("[]");
        }
        
        // 处理元数据
        if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
            asset.setMetadata(request.getMetadata());
        } else {
            asset.setMetadata("{}");
        }
        
        int result = assetMapper.insert(asset);
        if (result > 0) {
            log.info("资产创建成功: {} (ID: {})", asset.getName(), asset.getId());
            return getAssetById(asset.getId());
        } else {
            throw new RuntimeException("资产创建失败");
        }
    }
    
    /**
     * 根据ID获取资产详情
     */
    public AssetDTO.AssetResponse getAssetById(Integer id) {
        AssetDTO.AssetResponse asset = assetMapper.findDetailById(id);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        // 增加查看次数
        assetMapper.incrementViewCount(id);
        
        // 处理标签
        if (asset.getTags() == null) {
            asset.setTags(List.of());
        } else {
            try {
                List<String> tags = objectMapper.readValue(asset.getTags().toString(), List.class);
                asset.setTags(tags);
            } catch (Exception e) {
                log.warn("标签反序列化失败: {}", e.getMessage());
                asset.setTags(List.of());
            }
        }
        
        return asset;
    }
    
    /**
     * 分页查询资产列表
     */
    public Map<String, Object> getAssetList(AssetDTO.ListRequest request) {
        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询数据
        List<AssetDTO.AssetResponse> assets = assetMapper.findByPage(request, offset);
        Long total = assetMapper.countByCondition(request);
        
        // 处理每个资产的标签
        assets.forEach(asset -> {
            if (asset.getTags() != null) {
                try {
                    List<String> tags = objectMapper.readValue(asset.getTags().toString(), List.class);
                    asset.setTags(tags);
                } catch (Exception e) {
                    log.warn("标签反序列化失败: {}", e.getMessage());
                    asset.setTags(List.of());
                }
            } else {
                asset.setTags(List.of());
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", assets);
        result.put("total", total);
        result.put("page", request.getPage());
        result.put("size", request.getSize());
        result.put("pages", (total + request.getSize() - 1) / request.getSize());
        
        return result;
    }
    
    /**
     * 更新资产
     */
    @Transactional
    public AssetDTO.AssetResponse updateAsset(Integer id, AssetDTO.UpdateRequest request) {
        Asset existingAsset = assetMapper.findById(id);
        if (existingAsset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(request.getName());
        asset.setDescription(request.getDescription());
        asset.setCategory(request.getCategory());
        asset.setProjectId(request.getProjectId());
        
        // 处理标签
        if (request.getTags() != null) {
            try {
                asset.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                log.warn("标签序列化失败: {}", e.getMessage());
            }
        }
        
        // 处理元数据
        if (request.getMetadata() != null) {
            asset.setMetadata(request.getMetadata());
        }
        
        int result = assetMapper.update(asset);
        if (result > 0) {
            log.info("资产更新成功: {} (ID: {})", request.getName(), id);
            return getAssetById(id);
        } else {
            throw new RuntimeException("资产更新失败");
        }
    }
    
    /**
     * 删除资产
     */
    @Transactional
    public void deleteAsset(Integer id) {
        Asset asset = assetMapper.findById(id);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        int result = assetMapper.deleteById(id);
        if (result > 0) {
            log.info("资产删除成功: {} (ID: {})", asset.getName(), id);
        } else {
            throw new RuntimeException("资产删除失败");
        }
    }
    
    /**
     * 下载资产（增加下载次数）
     */
    public Asset downloadAsset(Integer id) {
        Asset asset = assetMapper.findById(id);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        // 增加下载次数
        assetMapper.incrementDownloadCount(id);
        
        return asset;
    }
    
    /**
     * 获取资产统计信息
     */
    public AssetDTO.AssetStats getAssetStats() {
        return assetMapper.getStats();
    }
    
    /**
     * 更新资产状态
     */
    @Transactional
    public void updateAssetStatus(Integer id, String status) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setStatus(status);
        
        int result = assetMapper.update(asset);
        if (result > 0) {
            log.info("资产状态更新成功: ID={}, status={}", id, status);
        } else {
            throw new RuntimeException("资产状态更新失败");
        }
    }
}
