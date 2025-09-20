<template>
  <div class="user-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
        <div class="header-info">
          <h1>用户详情</h1>
          <p>查看和管理用户信息</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Edit" @click="handleEdit">编辑用户</el-button>
        <el-dropdown @command="handleCommand">
          <el-button type="default">
            更多操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="reset-password">重置密码</el-dropdown-item>
              <el-dropdown-item command="toggle-status">
                {{ userInfo.status === 'active' ? '禁用用户' : '启用用户' }}
              </el-dropdown-item>
              <el-dropdown-item divided command="delete" class="danger">删除用户</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <!-- 用户基本信息 -->
      <el-col :xs="24" :lg="8">
        <el-card class="user-info-card">
          <div class="user-profile">
            <el-avatar :size="80" :src="userInfo.avatar">
              {{ userInfo.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-basic">
              <h3>{{ userInfo.username }}</h3>
              <p class="user-email">{{ userInfo.email }}</p>
              <div class="user-tags">
                <el-tag :type="getRoleTagType(userInfo.role)">{{ getRoleName(userInfo.role) }}</el-tag>
                <el-tag :type="getStatusTagType(userInfo.status)">{{ getStatusName(userInfo.status) }}</el-tag>
              </div>
            </div>
          </div>
          
          <el-divider />
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.projectCount || 0 }}</div>
              <div class="stat-label">参与项目</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.assetCount || 0 }}</div>
              <div class="stat-label">上传资产</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.loginDays || 0 }}</div>
              <div class="stat-label">登录天数</div>
            </div>
          </div>
        </el-card>

        <!-- 用户权限 -->
        <el-card class="permissions-card">
          <template #header>
            <div class="card-header">
              <span>用户权限</span>
              <el-button type="text" @click="handleEditPermissions">编辑</el-button>
            </div>
          </template>
          
          <div class="permissions-list">
            <div v-for="permission in userPermissions" :key="permission.key" class="permission-item">
              <div class="permission-info">
                <span class="permission-name">{{ permission.name }}</span>
                <span class="permission-desc">{{ permission.description }}</span>
              </div>
              <el-switch 
                v-model="permission.enabled" 
                @change="handlePermissionChange(permission)"
                :disabled="!canEditPermissions"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 详细信息和活动记录 -->
      <el-col :xs="24" :lg="16">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-card>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
                <el-descriptions-item label="角色">
                  <el-tag :type="getRoleTagType(userInfo.role)">{{ getRoleName(userInfo.role) }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="getStatusTagType(userInfo.status)">{{ getStatusName(userInfo.status) }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">{{ userInfo.createdAt }}</el-descriptions-item>
                <el-descriptions-item label="最后登录">{{ userInfo.lastLogin }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ userInfo.updatedAt || '未更新' }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-tab-pane>

          <!-- 活动记录 -->
          <el-tab-pane label="活动记录" name="activity">
            <el-card>
              <div class="activity-list">
                <div v-for="activity in userActivities" :key="activity.id" class="activity-item">
                  <div class="activity-icon">
                    <el-icon :color="getActivityColor(activity.type)">
                      <component :is="getActivityIcon(activity.type)" />
                    </el-icon>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">{{ activity.title }}</div>
                    <div class="activity-desc">{{ activity.description }}</div>
                    <div class="activity-time">{{ activity.time }}</div>
                  </div>
                </div>
              </div>
            </el-card>
          </el-tab-pane>

          <!-- 参与项目 -->
          <el-tab-pane label="参与项目" name="projects">
            <el-card>
              <div class="projects-list">
                <div v-for="project in userProjects" :key="project.id" class="project-item">
                  <div class="project-info">
                    <h4>{{ project.name }}</h4>
                    <p>{{ project.description }}</p>
                    <div class="project-meta">
                      <el-tag size="small">{{ project.role }}</el-tag>
                      <span class="join-time">加入时间: {{ project.joinTime }}</span>
                    </div>
                  </div>
                  <div class="project-actions">
                    <el-button type="text" @click="viewProject(project)">查看</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <!-- 编辑权限对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="编辑用户权限" width="500px">
      <div class="permission-editor">
        <div v-for="permission in userPermissions" :key="permission.key" class="permission-row">
          <div class="permission-info">
            <span class="permission-name">{{ permission.name }}</span>
            <span class="permission-desc">{{ permission.description }}</span>
          </div>
          <el-switch v-model="permission.enabled" />
        </div>
      </div>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Edit,
  ArrowDown,
  User,
  Document,
  Setting,
  Warning,
  Check,
  Clock
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const activeTab = ref('basic')
const permissionDialogVisible = ref(false)

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  email: '',
  role: '',
  status: '',
  avatar: '',
  projectCount: 0,
  assetCount: 0,
  loginDays: 0,
  lastLogin: '',
  createdAt: '',
  updatedAt: ''
})

// 用户权限
const userPermissions = ref([
  {
    key: 'project_create',
    name: '创建项目',
    description: '允许用户创建新项目',
    enabled: false
  },
  {
    key: 'asset_upload',
    name: '上传资产',
    description: '允许用户上传游戏资产',
    enabled: true
  },
  {
    key: 'asset_review',
    name: '审核资产',
    description: '允许用户审核其他用户上传的资产',
    enabled: false
  },
  {
    key: 'user_manage',
    name: '用户管理',
    description: '允许用户管理其他用户',
    enabled: false
  }
])

// 用户活动记录
const userActivities = ref([])

// 用户参与的项目
const userProjects = ref([])

// 计算属性
const canEditPermissions = computed(() => {
  // 这里可以根据当前用户权限判断是否可以编辑
  return true
})

// 工具函数
const getRoleTagType = (role) => {
  const roleMap = {
    admin: 'danger',
    developer: 'primary',
    reviewer: 'warning',
    user: 'info'
  }
  return roleMap[role] || ''
}

const getRoleName = (role) => {
  const nameMap = {
    admin: '管理员',
    developer: '开发者',
    reviewer: '审核员',
    user: '普通用户'
  }
  return nameMap[role] || role
}

const getStatusTagType = (status) => {
  return status === 'active' ? 'success' : 'danger'
}

const getStatusName = (status) => {
  return status === 'active' ? '正常' : '禁用'
}

const getActivityIcon = (type) => {
  const iconMap = {
    login: User,
    upload: Document,
    project: Setting,
    warning: Warning,
    success: Check
  }
  return iconMap[type] || Clock
}

const getActivityColor = (type) => {
  const colorMap = {
    login: '#409eff',
    upload: '#67c23a',
    project: '#e6a23c',
    warning: '#f56c6c',
    success: '#67c23a'
  }
  return colorMap[type] || '#909399'
}

// 事件处理
const handleBack = () => {
  router.push('/users')
}

const handleEdit = () => {
  ElMessage.info('编辑功能开发中...')
}

const handleCommand = (command) => {
  switch (command) {
    case 'reset-password':
      handleResetPassword()
      break
    case 'toggle-status':
      handleToggleStatus()
      break
    case 'delete':
      handleDelete()
      break
  }
}

const handleResetPassword = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${userInfo.username}" 的密码吗？`,
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('密码重置成功，新密码已发送到用户邮箱')
  } catch {
    // 用户取消
  }
}

const handleToggleStatus = async () => {
  const action = userInfo.status === 'active' ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${userInfo.username}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    userInfo.status = userInfo.status === 'active' ? 'disabled' : 'active'
    ElMessage.success(`用户${action}成功`)
  } catch {
    // 用户取消
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${userInfo.username}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('用户删除成功')
    router.push('/users')
  } catch {
    // 用户取消
  }
}

const handleEditPermissions = () => {
  permissionDialogVisible.value = true
}

const handlePermissionChange = (permission) => {
  ElMessage.success(`权限 "${permission.name}" 已${permission.enabled ? '启用' : '禁用'}`)
}

const handleSavePermissions = () => {
  ElMessage.success('权限保存成功')
  permissionDialogVisible.value = false
}

const viewProject = (project) => {
  router.push(`/projects/${project.id}`)
}

// 加载用户详情
const loadUserDetail = async () => {
  loading.value = true
  
  try {
    const userId = route.params.id
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟用户数据
    Object.assign(userInfo, {
      id: userId,
      username: 'developer1',
      email: 'dev1@example.com',
      role: 'developer',
      status: 'active',
      avatar: '',
      projectCount: 3,
      assetCount: 156,
      loginDays: 45,
      lastLogin: '2024-01-15 16:20',
      createdAt: '2023-12-05 14:30',
      updatedAt: '2024-01-10 10:15'
    })
    
    // 模拟活动记录
    userActivities.value = [
      {
        id: 1,
        type: 'upload',
        title: '上传了新资产',
        description: '上传了角色模型 "战士.fbx"',
        time: '2024-01-15 16:20'
      },
      {
        id: 2,
        type: 'project',
        title: '加入了项目',
        description: '加入了项目 "RPG冒险游戏"',
        time: '2024-01-15 14:30'
      },
      {
        id: 3,
        type: 'login',
        title: '登录系统',
        description: '用户登录了系统',
        time: '2024-01-15 09:00'
      }
    ]
    
    // 模拟参与项目
    userProjects.value = [
      {
        id: 1,
        name: 'RPG冒险游戏',
        description: '一个中世纪风格的RPG游戏',
        role: '开发者',
        joinTime: '2023-12-10'
      },
      {
        id: 2,
        name: '休闲益智游戏',
        description: '简单有趣的益智类游戏',
        role: '开发者',
        joinTime: '2024-01-05'
      }
    ]
    
  } catch (error) {
    ElMessage.error('加载用户详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserDetail()
})
</script>

<style scoped>
.user-detail {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-info h1 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 600;
}

.header-info p {
  margin: 0;
  color: var(--el-text-color-regular);
}

.header-right {
  display: flex;
  gap: 12px;
}

.user-info-card {
  margin-bottom: 20px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.user-basic h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
}

.user-email {
  margin: 0 0 12px 0;
  color: var(--el-text-color-regular);
}

.user-tags {
  display: flex;
  gap: 8px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.permissions-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.permissions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
}

.permission-info {
  flex: 1;
}

.permission-name {
  display: block;
  font-weight: 500;
  margin-bottom: 4px;
}

.permission-desc {
  font-size: 12px;
  color: var(--el-text-color-regular);
}

.detail-tabs {
  background: white;
  border-radius: 6px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
}

.activity-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--el-color-primary-light-9);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.activity-desc {
  color: var(--el-text-color-regular);
  margin-bottom: 8px;
}

.activity-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.projects-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
}

.project-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
}

.project-info p {
  margin: 0 0 12px 0;
  color: var(--el-text-color-regular);
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.join-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.permission-editor {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
}

.danger {
  color: var(--el-color-danger) !important;
}
</style>
