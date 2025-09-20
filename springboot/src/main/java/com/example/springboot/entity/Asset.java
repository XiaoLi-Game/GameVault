package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资产实体类
 * 对应数据库 assets 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    
    /**
     * 资产ID
     */
    private Integer id;
    
    /**
     * 资产名称
     */
    private String name;
    
    /**
     * 资产描述
     */
    private String description;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 文件哈希值(用于去重)
     */
    private String fileHash;
    
    /**
     * 缩略图路径
     */
    private String thumbnailPath;
    
    /**
     * 所属项目ID
     */
    private Integer projectId;
    
    /**
     * 上传者ID
     */
    private Integer uploaderId;
    
    /**
     * 资产分类：texture, model, audio, video, script, document, other
     */
    private String category;
    
    /**
     * 资产标签(JSON格式)
     */
    private String tags;
    
    /**
     * 资产元数据(JSON格式)
     */
    private String metadata;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 审核状态：pending, approved, rejected, archived
     */
    private String status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
