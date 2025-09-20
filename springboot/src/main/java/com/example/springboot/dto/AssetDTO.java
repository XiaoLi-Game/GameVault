package com.example.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 资产相关的数据传输对象
 */
public class AssetDTO {
    
    /**
     * 资产创建请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "资产名称不能为空")
        @Size(max = 255, message = "资产名称长度不能超过255个字符")
        private String name;
        
        @Size(max = 1000, message = "资产描述长度不能超过1000个字符")
        private String description;
        
        @NotBlank(message = "文件路径不能为空")
        private String filePath;
        
        @NotNull(message = "文件大小不能为空")
        private Long fileSize;
        
        @NotBlank(message = "文件类型不能为空")
        private String fileType;
        
        private String mimeType;
        private String fileHash;
        private String thumbnailPath;
        private Integer projectId;
        
        @NotBlank(message = "资产分类不能为空")
        private String category;
        
        private List<String> tags;
        private String metadata;
    }
    
    /**
     * 资产更新请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Size(max = 255, message = "资产名称长度不能超过255个字符")
        private String name;
        
        @Size(max = 1000, message = "资产描述长度不能超过1000个字符")
        private String description;
        
        private String category;
        private List<String> tags;
        private String metadata;
        private Integer projectId;
    }
    
    /**
     * 资产响应信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetResponse {
        private Integer id;
        private String name;
        private String description;
        private String filePath;
        private Long fileSize;
        private String fileType;
        private String mimeType;
        private String fileHash;
        private String thumbnailPath;
        private Integer projectId;
        private String projectName;
        private Integer uploaderId;
        private String uploaderName;
        private String category;
        private List<String> tags;
        private String metadata;
        private Integer downloadCount;
        private Integer viewCount;
        private String status;
        private String createdAt;
        private String updatedAt;
    }
    
    /**
     * 资产列表查询请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListRequest {
        private String keyword;
        private String category;
        private String status;
        private Integer projectId;
        private Integer uploaderId;
        private String sortBy = "createdAt";
        private String sortOrder = "desc";
        private Integer page = 1;
        private Integer size = 20;
    }
    
    /**
     * 资产统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetStats {
        private Long totalAssets;
        private Long totalSize;
        private Long pendingAssets;
        private Long approvedAssets;
        private Long rejectedAssets;
        private Long totalDownloads;
        private Long totalViews;
    }
}
