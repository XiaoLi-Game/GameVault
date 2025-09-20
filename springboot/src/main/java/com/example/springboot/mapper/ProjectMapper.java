package com.example.springboot.mapper;

import com.example.springboot.entity.Project;
import com.example.springboot.dto.ProjectDTO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目数据访问层
 */
@Mapper
public interface ProjectMapper {
    
    /**
     * 根据ID查找项目
     */
    @Select("SELECT * FROM projects WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "ownerId", column = "owner_id"),
        @Result(property = "teamMembers", column = "team_members"),
        @Result(property = "startDate", column = "start_date"),
        @Result(property = "endDate", column = "end_date"),
        @Result(property = "repositoryUrl", column = "repository_url"),
        @Result(property = "demoUrl", column = "demo_url"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Project findById(Integer id);
    
    /**
     * 查找项目详情（包含关联信息）
     */
    @Select("""
        SELECT p.*,
               u.username as owner_name,
               COUNT(a.id) as asset_count
        FROM projects p
        LEFT JOIN users u ON p.owner_id = u.id
        LEFT JOIN assets a ON p.id = a.project_id
        WHERE p.id = #{id}
        GROUP BY p.id
    """)
    ProjectDTO.ProjectResponse findDetailById(Integer id);
    
    /**
     * 分页查询项目列表（包含关联信息）
     */
    @Select("""
        <script>
        SELECT p.*,
               u.username as owner_name,
               COUNT(a.id) as asset_count
        FROM projects p
        LEFT JOIN users u ON p.owner_id = u.id
        LEFT JOIN assets a ON p.id = a.project_id
        <where>
            <if test="request.keyword != null and request.keyword != ''">
                AND (p.name LIKE CONCAT('%', #{request.keyword}, '%')
                     OR p.description LIKE CONCAT('%', #{request.keyword}, '%'))
            </if>
            <if test="request.type != null and request.type != ''">
                AND p.type = #{request.type}
            </if>
            <if test="request.status != null and request.status != ''">
                AND p.status = #{request.status}
            </if>
            <if test="request.ownerId != null">
                AND p.owner_id = #{request.ownerId}
            </if>
        </where>
        GROUP BY p.id
        ORDER BY
        <choose>
            <when test="request.sortBy == 'name'">p.name</when>
            <when test="request.sortBy == 'progress'">p.progress</when>
            <when test="request.sortBy == 'startDate'">p.start_date</when>
            <when test="request.sortBy == 'endDate'">p.end_date</when>
            <otherwise>p.created_at</otherwise>
        </choose>
        <choose>
            <when test="request.sortOrder == 'asc'">ASC</when>
            <otherwise>DESC</otherwise>
        </choose>
        LIMIT #{offset}, #{request.size}
        </script>
    """)
    List<ProjectDTO.ProjectResponse> findByPage(@Param("request") ProjectDTO.ListRequest request, @Param("offset") int offset);
    
    /**
     * 统计项目总数
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM projects p
        <where>
            <if test="keyword != null and keyword != ''">
                AND (p.name LIKE CONCAT('%', #{keyword}, '%') 
                     OR p.description LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="type != null and type != ''">
                AND p.type = #{type}
            </if>
            <if test="status != null and status != ''">
                AND p.status = #{status}
            </if>
            <if test="ownerId != null">
                AND p.owner_id = #{ownerId}
            </if>
        </where>
        </script>
    """)
    Long countByCondition(ProjectDTO.ListRequest request);
    
    /**
     * 插入项目
     */
    @Insert("""
        INSERT INTO projects (name, description, type, status, owner_id, team_members, 
                             start_date, end_date, budget, progress, tags, repository_url, 
                             demo_url, created_at, updated_at)
        VALUES (#{name}, #{description}, #{type}, #{status}, #{ownerId}, #{teamMembers},
                #{startDate}, #{endDate}, #{budget}, #{progress}, #{tags}, #{repositoryUrl},
                #{demoUrl}, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Project project);
    
    /**
     * 更新项目
     */
    @Update("""
        <script>
        UPDATE projects
        <set>
            <if test="name != null and name != ''">name = #{name},</if>
            <if test="description != null">description = #{description},</if>
            <if test="type != null and type != ''">type = #{type},</if>
            <if test="status != null and status != ''">status = #{status},</if>
            <if test="teamMembers != null">team_members = #{teamMembers},</if>
            <if test="startDate != null">start_date = #{startDate},</if>
            <if test="endDate != null">end_date = #{endDate},</if>
            <if test="budget != null">budget = #{budget},</if>
            <if test="progress != null">progress = #{progress},</if>
            <if test="tags != null">tags = #{tags},</if>
            <if test="repositoryUrl != null">repository_url = #{repositoryUrl},</if>
            <if test="demoUrl != null">demo_url = #{demoUrl},</if>
            updated_at = NOW()
        </set>
        WHERE id = #{id}
        </script>
    """)
    int update(Project project);
    
    /**
     * 删除项目
     */
    @Delete("DELETE FROM projects WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 获取项目统计信息
     */
    @Select("""
        SELECT 
            COUNT(*) as total_projects,
            COUNT(CASE WHEN status = 'planning' THEN 1 END) as planning_projects,
            COUNT(CASE WHEN status = 'development' THEN 1 END) as development_projects,
            COUNT(CASE WHEN status = 'testing' THEN 1 END) as testing_projects,
            COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_projects,
            COUNT(CASE WHEN status = 'archived' THEN 1 END) as archived_projects,
            COALESCE(SUM(budget), 0) as total_budget,
            COALESCE(AVG(progress), 0) as average_progress
        FROM projects
    """)
    @Results({
        @Result(property = "totalProjects", column = "total_projects"),
        @Result(property = "planningProjects", column = "planning_projects"),
        @Result(property = "developmentProjects", column = "development_projects"),
        @Result(property = "testingProjects", column = "testing_projects"),
        @Result(property = "completedProjects", column = "completed_projects"),
        @Result(property = "archivedProjects", column = "archived_projects"),
        @Result(property = "totalBudget", column = "total_budget"),
        @Result(property = "averageProgress", column = "average_progress")
    })
    ProjectDTO.ProjectStats getStats();
}
