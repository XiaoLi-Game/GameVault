package com.example.springboot.controller;

import com.example.springboot.common.ApiResponse;
import com.example.springboot.dto.ProjectDTO;
import com.example.springboot.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 项目管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    /**
     * 创建项目
     */
    @PostMapping
    public ApiResponse<ProjectDTO.ProjectResponse> createProject(
            @Valid @RequestBody ProjectDTO.CreateRequest request) {
        try {
            // TODO: 从JWT token中获取当前用户ID，这里暂时使用固定值
            Integer ownerId = 1;
            
            ProjectDTO.ProjectResponse project = projectService.createProject(request, ownerId);
            return ApiResponse.success("项目创建成功", project);
        } catch (Exception e) {
            log.error("创建项目失败: {}", e.getMessage(), e);
            return ApiResponse.error("创建项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProjectDTO.ProjectResponse> getProjectById(@PathVariable Integer id) {
        try {
            ProjectDTO.ProjectResponse project = projectService.getProjectById(id);
            return ApiResponse.success("获取项目详情成功", project);
        } catch (Exception e) {
            log.error("获取项目详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取项目详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询项目列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getProjectList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            ProjectDTO.ListRequest request = new ProjectDTO.ListRequest();
            request.setKeyword(keyword);
            request.setType(type);
            request.setStatus(status);
            request.setOwnerId(ownerId);
            request.setSortBy(sortBy);
            request.setSortOrder(sortOrder);
            request.setPage(page);
            request.setSize(size);
            
            Map<String, Object> result = projectService.getProjectList(request);
            return ApiResponse.success("获取项目列表成功", result);
        } catch (Exception e) {
            log.error("获取项目列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取项目列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public ApiResponse<ProjectDTO.ProjectResponse> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody ProjectDTO.UpdateRequest request) {
        try {
            ProjectDTO.ProjectResponse project = projectService.updateProject(id, request);
            return ApiResponse.success("项目更新成功", project);
        } catch (Exception e) {
            log.error("更新项目失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable Integer id) {
        try {
            projectService.deleteProject(id);
            return ApiResponse.success("项目删除成功");
        } catch (Exception e) {
            log.error("删除项目失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除项目失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理项目成员
     */
    @PostMapping("/{id}/members")
    public ApiResponse<Void> manageProjectMember(
            @PathVariable Integer id,
            @Valid @RequestBody ProjectDTO.MemberRequest request) {
        try {
            projectService.manageProjectMember(id, request);
            return ApiResponse.success("项目成员管理成功");
        } catch (Exception e) {
            log.error("项目成员管理失败: {}", e.getMessage(), e);
            return ApiResponse.error("项目成员管理失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取项目统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<ProjectDTO.ProjectStats> getProjectStats() {
        try {
            ProjectDTO.ProjectStats stats = projectService.getProjectStats();
            return ApiResponse.success("获取项目统计成功", stats);
        } catch (Exception e) {
            log.error("获取项目统计失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取项目统计失败: " + e.getMessage());
        }
    }
}
