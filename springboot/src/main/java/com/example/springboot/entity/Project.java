package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目实体类
 * 对应数据库 projects 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    /**
     * 项目ID
     */
    private Integer id;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目类型：game, app, web, other
     */
    private String type;
    
    /**
     * 项目状态：planning, development, testing, completed, archived
     */
    private String status;
    
    /**
     * 项目负责人ID
     */
    private Integer ownerId;
    
    /**
     * 团队成员ID列表(JSON格式)
     */
    private String teamMembers;
    
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * 项目预算
     */
    private BigDecimal budget;
    
    /**
     * 项目进度(0-100)
     */
    private Integer progress;
    
    /**
     * 项目标签(JSON格式)
     */
    private String tags;
    
    /**
     * 代码仓库地址
     */
    private String repositoryUrl;
    
    /**
     * 演示地址
     */
    private String demoUrl;
    
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
