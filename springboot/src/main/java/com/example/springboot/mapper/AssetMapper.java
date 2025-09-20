package com.example.springboot.mapper;

import com.example.springboot.entity.Asset;
import com.example.springboot.dto.AssetDTO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产数据访问层
 */
@Mapper
public interface AssetMapper {
    
    /**
     * 根据ID查找资产
     */
    @Select("SELECT * FROM assets WHERE id = #{id}")
    Asset findById(Integer id);
    
    /**
     * 查找资产详情（包含关联信息）
     */
    @Select("""
        SELECT a.*, 
               p.name as project_name,
               u.username as uploader_name
        FROM assets a
        LEFT JOIN projects p ON a.project_id = p.id
        LEFT JOIN users u ON a.uploader_id = u.id
        WHERE a.id = #{id}
    """)
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "fileSize", column = "file_size"),
        @Result(property = "fileType", column = "file_type"),
        @Result(property = "mimeType", column = "mime_type"),
        @Result(property = "fileHash", column = "file_hash"),
        @Result(property = "thumbnailPath", column = "thumbnail_path"),
        @Result(property = "projectId", column = "project_id"),
        @Result(property = "uploaderId", column = "uploader_id"),
        @Result(property = "downloadCount", column = "download_count"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "projectName", column = "project_name"),
        @Result(property = "uploaderName", column = "uploader_name")
    })
    AssetDTO.AssetResponse findDetailById(Integer id);
    
    /**
     * 查找所有资产
     */
    @Select("SELECT * FROM assets ORDER BY created_at DESC")
    List<Asset> findAll();
    
    /**
     * 分页查询资产列表（包含关联信息）
     */
    @Select("""
        <script>
        SELECT a.*,
               p.name as project_name,
               u.username as uploader_name
        FROM assets a
        LEFT JOIN projects p ON a.project_id = p.id
        LEFT JOIN users u ON a.uploader_id = u.id
        <where>
            <if test="request.keyword != null and request.keyword != ''">
                AND (a.name LIKE CONCAT('%', #{request.keyword}, '%')
                     OR a.description LIKE CONCAT('%', #{request.keyword}, '%'))
            </if>
            <if test="request.category != null and request.category != ''">
                AND a.category = #{request.category}
            </if>
            <if test="request.status != null and request.status != ''">
                AND a.status = #{request.status}
            </if>
            <if test="request.projectId != null">
                AND a.project_id = #{request.projectId}
            </if>
            <if test="request.uploaderId != null">
                AND a.uploader_id = #{request.uploaderId}
            </if>
        </where>
        ORDER BY
        <choose>
            <when test="request.sortBy == 'name'">a.name</when>
            <when test="request.sortBy == 'fileSize'">a.file_size</when>
            <when test="request.sortBy == 'downloadCount'">a.download_count</when>
            <when test="request.sortBy == 'viewCount'">a.view_count</when>
            <otherwise>a.created_at</otherwise>
        </choose>
        <choose>
            <when test="request.sortOrder == 'asc'">ASC</when>
            <otherwise>DESC</otherwise>
        </choose>
        LIMIT #{offset}, #{request.size}
        </script>
    """)
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "fileSize", column = "file_size"),
        @Result(property = "fileType", column = "file_type"),
        @Result(property = "mimeType", column = "mime_type"),
        @Result(property = "fileHash", column = "file_hash"),
        @Result(property = "thumbnailPath", column = "thumbnail_path"),
        @Result(property = "projectId", column = "project_id"),
        @Result(property = "uploaderId", column = "uploader_id"),
        @Result(property = "downloadCount", column = "download_count"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "projectName", column = "project_name"),
        @Result(property = "uploaderName", column = "uploader_name")
    })
    List<AssetDTO.AssetResponse> findByPage(@Param("request") AssetDTO.ListRequest request, @Param("offset") int offset);
    
    /**
     * 统计资产总数
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM assets a
        <where>
            <if test="request.keyword != null and request.keyword != ''">
                AND (a.name LIKE CONCAT('%', #{request.keyword}, '%')
                     OR a.description LIKE CONCAT('%', #{request.keyword}, '%'))
            </if>
            <if test="request.category != null and request.category != ''">
                AND a.category = #{request.category}
            </if>
            <if test="request.status != null and request.status != ''">
                AND a.status = #{request.status}
            </if>
            <if test="request.projectId != null">
                AND a.project_id = #{request.projectId}
            </if>
            <if test="request.uploaderId != null">
                AND a.uploader_id = #{request.uploaderId}
            </if>
        </where>
        </script>
    """)
    Long countByCondition(@Param("request") AssetDTO.ListRequest request);
    
    /**
     * 插入资产
     */
    @Insert("""
        INSERT INTO assets (name, description, file_path, file_size, file_type, mime_type, 
                           file_hash, thumbnail_path, project_id, uploader_id, category, 
                           tags, metadata, status, created_at, updated_at)
        VALUES (#{name}, #{description}, #{filePath}, #{fileSize}, #{fileType}, #{mimeType},
                #{fileHash}, #{thumbnailPath}, #{projectId}, #{uploaderId}, #{category},
                #{tags}, #{metadata}, #{status}, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Asset asset);
    
    /**
     * 更新资产
     */
    @Update("""
        <script>
        UPDATE assets
        <set>
            <if test="name != null and name != ''">name = #{name},</if>
            <if test="description != null">description = #{description},</if>
            <if test="category != null and category != ''">category = #{category},</if>
            <if test="tags != null">tags = #{tags},</if>
            <if test="metadata != null">metadata = #{metadata},</if>
            <if test="projectId != null">project_id = #{projectId},</if>
            <if test="status != null and status != ''">status = #{status},</if>
            updated_at = NOW()
        </set>
        WHERE id = #{id}
        </script>
    """)
    int update(Asset asset);
    
    /**
     * 删除资产
     */
    @Delete("DELETE FROM assets WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 增加下载次数
     */
    @Update("UPDATE assets SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(Integer id);
    
    /**
     * 增加查看次数
     */
    @Update("UPDATE assets SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Integer id);
    
    /**
     * 获取资产统计信息
     */
    @Select("""
        SELECT 
            COUNT(*) as total_assets,
            COALESCE(SUM(file_size), 0) as total_size,
            COUNT(CASE WHEN status = 'pending' THEN 1 END) as pending_assets,
            COUNT(CASE WHEN status = 'approved' THEN 1 END) as approved_assets,
            COUNT(CASE WHEN status = 'rejected' THEN 1 END) as rejected_assets,
            COALESCE(SUM(download_count), 0) as total_downloads,
            COALESCE(SUM(view_count), 0) as total_views
        FROM assets
    """)
    @Results({
        @Result(property = "totalAssets", column = "total_assets"),
        @Result(property = "totalSize", column = "total_size"),
        @Result(property = "pendingAssets", column = "pending_assets"),
        @Result(property = "approvedAssets", column = "approved_assets"),
        @Result(property = "rejectedAssets", column = "rejected_assets"),
        @Result(property = "totalDownloads", column = "total_downloads"),
        @Result(property = "totalViews", column = "total_views")
    })
    AssetDTO.AssetStats getStats();
}
