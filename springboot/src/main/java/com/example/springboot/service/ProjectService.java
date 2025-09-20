package com.example.springboot.service;

import com.example.springboot.entity.Project;
import com.example.springboot.dto.ProjectDTO;
import com.example.springboot.mapper.ProjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目业务逻辑层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * 创建项目
     */
    @Transactional
    public ProjectDTO.ProjectResponse createProject(ProjectDTO.CreateRequest request, Integer ownerId) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setType(request.getType());
        project.setStatus(request.getStatus());
        project.setOwnerId(ownerId);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setBudget(request.getBudget());
        project.setProgress(request.getProgress());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setDemoUrl(request.getDemoUrl());
        
        // 处理团队成员
        if (request.getTeamMembers() != null && !request.getTeamMembers().isEmpty()) {
            try {
                project.setTeamMembers(objectMapper.writeValueAsString(request.getTeamMembers()));
            } catch (JsonProcessingException e) {
                log.warn("团队成员序列化失败: {}", e.getMessage());
                project.setTeamMembers("[]");
            }
        } else {
            project.setTeamMembers("[]");
        }
        
        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                project.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                log.warn("标签序列化失败: {}", e.getMessage());
                project.setTags("[]");
            }
        } else {
            project.setTags("[]");
        }
        
        int result = projectMapper.insert(project);
        if (result > 0) {
            log.info("项目创建成功: {} (ID: {})", project.getName(), project.getId());
            return getProjectById(project.getId());
        } else {
            throw new RuntimeException("项目创建失败");
        }
    }
    
    /**
     * 根据ID获取项目详情
     */
    public ProjectDTO.ProjectResponse getProjectById(Integer id) {
        ProjectDTO.ProjectResponse project = projectMapper.findDetailById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        
        // 处理团队成员
        if (project.getTeamMembers() == null) {
            project.setTeamMembers(List.of());
        } else {
            try {
                List<Integer> teamMembers = objectMapper.readValue(project.getTeamMembers().toString(), List.class);
                project.setTeamMembers(teamMembers);
            } catch (Exception e) {
                log.warn("团队成员反序列化失败: {}", e.getMessage());
                project.setTeamMembers(List.of());
            }
        }
        
        // 处理标签
        if (project.getTags() == null) {
            project.setTags(List.of());
        } else {
            try {
                List<String> tags = objectMapper.readValue(project.getTags().toString(), List.class);
                project.setTags(tags);
            } catch (Exception e) {
                log.warn("标签反序列化失败: {}", e.getMessage());
                project.setTags(List.of());
            }
        }
        
        return project;
    }
    
    /**
     * 分页查询项目列表
     */
    public Map<String, Object> getProjectList(ProjectDTO.ListRequest request) {
        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询数据
        List<ProjectDTO.ProjectResponse> projects = projectMapper.findByPage(request, offset);
        Long total = projectMapper.countByCondition(request);
        
        // 处理每个项目的团队成员和标签
        projects.forEach(project -> {
            // 处理团队成员
            if (project.getTeamMembers() != null) {
                try {
                    List<Integer> teamMembers = objectMapper.readValue(project.getTeamMembers().toString(), List.class);
                    project.setTeamMembers(teamMembers);
                } catch (Exception e) {
                    log.warn("团队成员反序列化失败: {}", e.getMessage());
                    project.setTeamMembers(List.of());
                }
            } else {
                project.setTeamMembers(List.of());
            }
            
            // 处理标签
            if (project.getTags() != null) {
                try {
                    List<String> tags = objectMapper.readValue(project.getTags().toString(), List.class);
                    project.setTags(tags);
                } catch (Exception e) {
                    log.warn("标签反序列化失败: {}", e.getMessage());
                    project.setTags(List.of());
                }
            } else {
                project.setTags(List.of());
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", projects);
        result.put("total", total);
        result.put("page", request.getPage());
        result.put("size", request.getSize());
        result.put("pages", (total + request.getSize() - 1) / request.getSize());
        
        return result;
    }
    
    /**
     * 更新项目
     */
    @Transactional
    public ProjectDTO.ProjectResponse updateProject(Integer id, ProjectDTO.UpdateRequest request) {
        Project existingProject = projectMapper.findById(id);
        if (existingProject == null) {
            throw new RuntimeException("项目不存在");
        }
        
        Project project = new Project();
        project.setId(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setType(request.getType());
        project.setStatus(request.getStatus());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setBudget(request.getBudget());
        project.setProgress(request.getProgress());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setDemoUrl(request.getDemoUrl());
        
        // 处理团队成员
        if (request.getTeamMembers() != null) {
            try {
                project.setTeamMembers(objectMapper.writeValueAsString(request.getTeamMembers()));
            } catch (JsonProcessingException e) {
                log.warn("团队成员序列化失败: {}", e.getMessage());
            }
        }
        
        // 处理标签
        if (request.getTags() != null) {
            try {
                project.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                log.warn("标签序列化失败: {}", e.getMessage());
            }
        }
        
        int result = projectMapper.update(project);
        if (result > 0) {
            log.info("项目更新成功: {} (ID: {})", request.getName(), id);
            return getProjectById(id);
        } else {
            throw new RuntimeException("项目更新失败");
        }
    }
    
    /**
     * 删除项目
     */
    @Transactional
    public void deleteProject(Integer id) {
        Project project = projectMapper.findById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        
        int result = projectMapper.deleteById(id);
        if (result > 0) {
            log.info("项目删除成功: {} (ID: {})", project.getName(), id);
        } else {
            throw new RuntimeException("项目删除失败");
        }
    }
    
    /**
     * 管理项目成员
     */
    @Transactional
    public void manageProjectMember(Integer projectId, ProjectDTO.MemberRequest request) {
        Project project = projectMapper.findById(projectId);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        
        try {
            List<Integer> teamMembers;
            if (project.getTeamMembers() != null && !project.getTeamMembers().isEmpty()) {
                teamMembers = objectMapper.readValue(project.getTeamMembers(), List.class);
            } else {
                teamMembers = List.of();
            }
            
            if ("add".equals(request.getAction())) {
                if (!teamMembers.contains(request.getUserId())) {
                    teamMembers.add(request.getUserId());
                }
            } else if ("remove".equals(request.getAction())) {
                teamMembers.remove(request.getUserId());
            }
            
            project.setTeamMembers(objectMapper.writeValueAsString(teamMembers));
            projectMapper.update(project);
            
            log.info("项目成员管理成功: 项目ID={}, 用户ID={}, 操作={}", projectId, request.getUserId(), request.getAction());
        } catch (Exception e) {
            log.error("项目成员管理失败: {}", e.getMessage(), e);
            throw new RuntimeException("项目成员管理失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取项目统计信息
     */
    public ProjectDTO.ProjectStats getProjectStats() {
        return projectMapper.getStats();
    }
}
