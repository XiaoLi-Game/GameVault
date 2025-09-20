<template>
  <div class="project-detail">
    <!-- 项目内容 -->
    <div v-if="project.id">
      <div class="page-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="$router.go(-1)">返回</el-button>
        <div class="project-title">
          <h1>{{ project.name }}</h1>
          <el-tag :type="getStatusTagType(project.status)">{{ getStatusName(project.status) }}</el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Edit" @click="editProject">编辑项目</el-button>
        <el-button :icon="Setting" @click="openSettings">设置</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 项目信息 -->
      <el-col :xs="24" :lg="16">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>项目概览</span>
              <el-progress :percentage="project.progress" :stroke-width="8" />
            </div>
          </template>
          
          <div class="project-info">
            <div class="info-row">
              <div class="info-item">
                <span class="label">项目描述:</span>
                <p class="description">{{ project.description }}</p>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <span class="label">项目类型:</span>
                <span class="value">{{ getTypeName(project.type) }}</span>
              </div>
              <div class="info-item">
                <span class="label">游戏引擎:</span>
                <span class="value">{{ getEngineName(project.engine) }}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <span class="label">创建时间:</span>
                <span class="value">{{ project.createTime }}</span>
              </div>
              <div class="info-item">
                <span class="label">最后更新:</span>
                <span class="value">{{ project.updateTime }}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <span class="label">目标平台:</span>
                <div class="platforms">
                  <el-tag v-for="platform in project.platforms" :key="platform" size="small">
                    {{ getPlatformName(platform) }}
                  </el-tag>
                </div>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <span class="label">项目标签:</span>
                <div class="tags">
                  <el-tag v-for="tag in project.tags" :key="tag" type="info" size="small">
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 项目统计 -->
        <el-card class="stats-card" style="margin-top: 20px">
          <template #header>
            <span>项目统计</span>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="24" color="#1890ff"><Files /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ project.stats?.totalAssets || 0 }}</div>
                  <div class="stat-label">总资产数</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="24" color="#52c41a"><User /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ project.stats?.memberCount || 0 }}</div>
                  <div class="stat-label">团队成员</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="24" color="#faad14"><FolderOpened /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ project.stats?.storageUsed || '0GB' }}</div>
                  <div class="stat-label">存储使用</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="24" color="#f5222d"><Calendar /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ project.stats?.daysLeft || 0 }}天</div>
                  <div class="stat-label">剩余天数</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 最近活动 -->
        <el-card class="activity-card" style="margin-top: 20px">
          <template #header>
            <span>最近活动</span>
          </template>
          
          <div class="activity-list">
            <div v-for="activity in project.activities" :key="activity.id" class="activity-item">
              <div class="activity-avatar">
                <el-avatar :size="32" :src="activity.user.avatar">
                  {{ activity.user.name.charAt(0) }}
                </el-avatar>
              </div>
              <div class="activity-content">
                <div class="activity-text">
                  <strong>{{ activity.user.name }}</strong> {{ activity.action }}
                  <span class="activity-target">{{ activity.target }}</span>
                </div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :xs="24" :lg="8">
        <!-- 团队成员 -->
        <el-card class="members-card">
          <template #header>
            <div class="card-header">
              <span>团队成员</span>
              <el-button size="small" type="primary" :icon="Plus" @click="inviteMember">邀请成员</el-button>
            </div>
          </template>
          
          <div class="members-list">
            <div v-for="member in project.members" :key="member.id" class="member-item">
              <el-avatar :size="40" :src="member.avatar">
                {{ member.name.charAt(0) }}
              </el-avatar>
              <div class="member-info">
                <div class="member-name">{{ member.name }}</div>
                <div class="member-role">{{ getRoleName(member.role) }}</div>
              </div>
              <div class="member-status">
                <el-tag :type="member.online ? 'success' : 'info'" size="small">
                  {{ member.online ? '在线' : '离线' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>



        <!-- 项目设置 -->
        <el-card class="settings-card" style="margin-top: 20px">
          <template #header>
            <span>项目设置</span>
          </template>
          
          <div class="settings-list">
            <div class="setting-item">
              <span class="setting-label">可见性</span>
              <el-tag :type="project.visibility === 'public' ? 'success' : 'warning'">
                {{ project.visibility === 'public' ? '公开' : '私有' }}
              </el-tag>
            </div>
            <div class="setting-item">
              <span class="setting-label">资产审核</span>
              <el-switch v-model="project.assetReview" @change="toggleAssetReview" />
            </div>
            <div class="setting-item">
              <span class="setting-label">版本控制</span>
              <el-switch v-model="project.versionControl" @change="toggleVersionControl" />
            </div>
            <div class="setting-item">
              <span class="setting-label">存储限制</span>
              <span class="setting-value">{{ project.storageLimit }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Edit,
  Setting,
  Files,
  User,
  FolderOpened,
  Calendar,
  Plus
} from '@element-plus/icons-vue'

const route = useRoute()

// 响应式数据
const project = ref({})

// 获取类型名称
const getTypeName = (type) => {
  const nameMap = {
    '2d': '2D游戏',
    '3d': '3D游戏',
    'vr': 'VR游戏',
    'mobile': '移动游戏',
    'web': '网页游戏'
  }
  return nameMap[type] || type
}

// 获取引擎名称
const getEngineName = (engine) => {
  const nameMap = {
    'unity': 'Unity',
    'unreal': 'Unreal Engine',
    'godot': 'Godot',
    'cocos2d': 'Cocos2d',
    'custom': '自定义引擎'
  }
  return nameMap[engine] || engine
}

// 获取平台名称
const getPlatformName = (platform) => {
  const nameMap = {
    'windows': 'Windows',
    'macos': 'macOS',
    'linux': 'Linux',
    'ios': 'iOS',
    'android': 'Android',
    'web': 'Web',
    'console': '游戏主机'
  }
  return nameMap[platform] || platform
}

// 获取角色名称
const getRoleName = (role) => {
  const nameMap = {
    'owner': '项目所有者',
    'admin': '管理员',
    'developer': '开发者',
    'designer': '设计师',
    'viewer': '查看者'
  }
  return nameMap[role] || role
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    'active': 'success',
    'paused': 'warning',
    'completed': 'info',
    'archived': 'danger'
  }
  return statusMap[status] || ''
}

// 获取状态名称
const getStatusName = (status) => {
  const nameMap = {
    'active': '进行中',
    'paused': '已暂停',
    'completed': '已完成',
    'archived': '已归档'
  }
  return nameMap[status] || status
}

// 加载项目详情
const loadProjectDetail = async () => {
  try {
    const projectId = route.params.id
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    project.value = {
      id: projectId,
      name: 'RPG冒险游戏',
      description: '这是一个中世纪风格的RPG游戏，玩家将扮演一名勇敢的冒险者，在广阔的世界中探索、战斗和成长。游戏包含丰富的剧情、多样的角色和精美的场景。',
      type: '3d',
      engine: 'unity',
      status: 'active',
      progress: 65,
      createTime: '2024-01-01',
      updateTime: '2024-01-15 14:30',
      platforms: ['windows', 'macos', 'android'],
      tags: ['RPG', '冒险', '中世纪', '3D'],
      visibility: 'private',
      assetReview: true,
      versionControl: true,
      storageLimit: '10GB',
      stats: {
        totalAssets: 156,
        memberCount: 8,
        storageUsed: '6.2GB',
        daysLeft: 45
      },
      members: [
        {
          id: 1,
          name: '张三',
          role: 'owner',
          avatar: '',
          online: true
        },
        {
          id: 2,
          name: '李四',
          role: 'admin',
          avatar: '',
          online: false
        },
        {
          id: 3,
          name: '王五',
          role: 'developer',
          avatar: '',
          online: true
        }
      ],
      activities: [
        {
          id: 1,
          user: { name: '张三', avatar: '' },
          action: '上传了新资产',
          target: '角色模型_法师.fbx',
          time: '2小时前'
        },
        {
          id: 2,
          user: { name: '李四', avatar: '' },
          action: '更新了',
          target: '场景贴图包',
          time: '4小时前'
        },
        {
          id: 3,
          user: { name: '王五', avatar: '' },
          action: '创建了新文件夹',
          target: '音效素材',
          time: '1天前'
        }
      ]
    }
    
  } catch (error) {
    ElMessage.error('加载项目详情失败')
  }
}

// 交互处理函数
const editProject = () => {
  ElMessage.info('编辑项目功能')
  // 这里可以跳转到编辑页面或打开编辑对话框
}

const openSettings = () => {
  ElMessage.info('项目设置功能')
  // 这里可以打开设置对话框
}

const inviteMember = () => {
  ElMessage.info('邀请成员功能')
  // 这里可以打开邀请成员对话框
}



const toggleAssetReview = (value) => {
  ElMessage.success(`资产审核已${value ? '开启' : '关闭'}`)
  // 这里可以调用API更新设置
}

const toggleVersionControl = (value) => {
  ElMessage.success(`版本控制已${value ? '开启' : '关闭'}`)
  // 这里可以调用API更新设置
}

onMounted(() => {
  loadProjectDetail()
})
</script>

<style scoped>
.project-detail {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.project-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.project-title h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.project-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  gap: 32px;
}

.info-item {
  flex: 1;
}

.info-item .label {
  display: block;
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 4px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

.info-item .description {
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

.platforms,
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  color: #262626;
  margin-bottom: 4px;
}

.activity-target {
  color: #1890ff;
}

.activity-time {
  font-size: 12px;
  color: #8c8c8c;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.member-role {
  font-size: 12px;
  color: #8c8c8c;
}

.actions-list {
  display: flex;
  flex-direction: column;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.setting-label {
  color: #8c8c8c;
  font-size: 14px;
}

.setting-value {
  color: #262626;
  font-weight: 500;
}

@media (max-width: 768px) {
  .project-detail {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .project-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .header-right {
    width: 100%;
  }
  
  .header-right .el-button {
    flex: 1;
  }
  
  .info-row {
    flex-direction: column;
    gap: 16px;
  }
}

.loading-container {
  padding: 40px;
  text-align: center;
}
</style>
