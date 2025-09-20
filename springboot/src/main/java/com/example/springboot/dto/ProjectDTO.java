package com.example.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 项目相关的数据传输对象
 */
public class ProjectDTO {
    
    /**
     * 项目创建请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "项目名称不能为空")
        @Size(max = 100, message = "项目名称长度不能超过100个字符")
        private String name;
        
        @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
        private String description;
        
        @NotBlank(message = "项目类型不能为空")
        private String type;
        
        private String status = "planning";
        private List<Integer> teamMembers;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budget;
        
        @Min(value = 0, message = "项目进度不能小于0")
        @Max(value = 100, message = "项目进度不能大于100")
        private Integer progress = 0;
        
        private List<String> tags;
        private String repositoryUrl;
        private String demoUrl;
    }
    
    /**
     * 项目更新请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Size(max = 100, message = "项目名称长度不能超过100个字符")
        private String name;
        
        @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
        private String description;
        
        private String type;
        private String status;
        private List<Integer> teamMembers;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budget;
        
        @Min(value = 0, message = "项目进度不能小于0")
        @Max(value = 100, message = "项目进度不能大于100")
        private Integer progress;
        
        private List<String> tags;
        private String repositoryUrl;
        private String demoUrl;
    }
    
    /**
     * 项目响应信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private Integer id;
        private String name;
        private String description;
        private String type;
        private String status;
        private Integer ownerId;
        private String ownerName;
        private List<Integer> teamMembers;
        private List<String> teamMemberNames;
        private String startDate;
        private String endDate;
        private BigDecimal budget;
        private Integer progress;
        private List<String> tags;
        private String repositoryUrl;
        private String demoUrl;
        private String createdAt;
        private String updatedAt;
        
        // 统计信息
        private Long assetCount;
        private Long memberCount;
    }
    
    /**
     * 项目列表查询请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListRequest {
        private String keyword;
        private String type;
        private String status;
        private Integer ownerId;
        private String sortBy = "createdAt";
        private String sortOrder = "desc";
        private Integer page = 1;
        private Integer size = 20;
    }
    
    /**
     * 项目成员管理请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRequest {
        @NotNull(message = "用户ID不能为空")
        private Integer userId;
        
        @NotBlank(message = "操作类型不能为空")
        private String action; // add, remove
    }
    
    /**
     * 项目统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectStats {
        private Long totalProjects;
        private Long planningProjects;
        private Long developmentProjects;
        private Long testingProjects;
        private Long completedProjects;
        private Long archivedProjects;
        private BigDecimal totalBudget;
        private Double averageProgress;
    }
}
