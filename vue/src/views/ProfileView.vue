<template>
  <div class="profile-view">
    <div class="page-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :xs="24" :lg="8">
        <el-card class="profile-card">
          <div class="profile-info">
            <div class="avatar-section">
              <el-avatar :size="80" :src="userInfo.avatar">
                {{ userInfo.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <el-button type="text" @click="handleAvatarUpload">更换头像</el-button>
            </div>
            
            <div class="user-details">
              <h3>{{ userInfo.username }}</h3>
              <p class="user-email">{{ userInfo.email }}</p>
              <el-tag :type="getRoleTagType(userInfo.role)">{{ getRoleName(userInfo.role) }}</el-tag>
            </div>
            
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ stats.projectCount }}</div>
                <div class="stat-label">参与项目</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ stats.assetCount }}</div>
                <div class="stat-label">上传资产</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ stats.loginDays }}</div>
                <div class="stat-label">登录天数</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 详细设置 -->
      <el-col :xs="24" :lg="16">
        <el-card class="settings-card">
          <template #header>
            <span>账户设置</span>
          </template>
          
          <el-tabs v-model="activeTab" type="border-card">
            <!-- 基本信息 -->
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="profileForm" label-width="100px" style="max-width: 600px">
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" />
                </el-form-item>
                
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                
                <el-form-item label="个人简介">
                  <el-input v-model="profileForm.bio" type="textarea" :rows="4" />
                </el-form-item>
                
                <el-form-item label="所在地">
                  <el-input v-model="profileForm.location" />
                </el-form-item>
                
                <el-form-item label="个人网站">
                  <el-input v-model="profileForm.website" />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile">保存修改</el-button>
                  <el-button @click="resetProfileForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 安全设置 -->
            <el-tab-pane label="安全设置" name="security">
              <div class="security-section">
                <h4>修改密码</h4>
                <el-form :model="passwordForm" label-width="100px" style="max-width: 600px">
                  <el-form-item label="当前密码">
                    <el-input v-model="passwordForm.currentPassword" type="password" show-password />
                  </el-form-item>
                  
                  <el-form-item label="新密码">
                    <el-input v-model="passwordForm.newPassword" type="password" show-password />
                  </el-form-item>
                  
                  <el-form-item label="确认密码">
                    <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                  </el-form-item>
                  
                  <el-form-item>
                    <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                  </el-form-item>
                </el-form>
                
                <el-divider />
                
                <h4>两步验证</h4>
                <div class="two-factor-section">
                  <div class="setting-item">
                    <div class="setting-info">
                      <div class="setting-title">启用两步验证</div>
                      <div class="setting-desc">为您的账户添加额外的安全保护</div>
                    </div>
                    <el-switch
                      v-model="securitySettings.twoFactor"
                      @change="handleTwoFactorChange"
                    />
                  </div>
                </div>
              </div>
            </el-tab-pane>



            <!-- 偏好设置 -->
            <el-tab-pane label="偏好设置" name="preferences">
              <el-form label-width="100px" style="max-width: 600px">
                <el-form-item label="主题">
                  <el-radio-group v-model="preferences.theme">
                    <el-radio label="light">浅色主题</el-radio>
                    <el-radio label="dark">深色主题</el-radio>
                    <el-radio label="auto">跟随系统</el-radio>
                  </el-radio-group>
                </el-form-item>
                
                <el-form-item label="语言">
                  <el-select v-model="preferences.language" style="width: 200px">
                    <el-option label="简体中文" value="zh-CN" />
                    <el-option label="English" value="en-US" />
                    <el-option label="日本語" value="ja-JP" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="时区">
                  <el-select v-model="preferences.timezone" style="width: 200px">
                    <el-option label="北京时间 (UTC+8)" value="Asia/Shanghai" />
                    <el-option label="东京时间 (UTC+9)" value="Asia/Tokyo" />
                    <el-option label="纽约时间 (UTC-5)" value="America/New_York" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="每页显示">
                  <el-select v-model="preferences.pageSize" style="width: 200px">
                    <el-option label="10 条" :value="10" />
                    <el-option label="20 条" :value="20" />
                    <el-option label="50 条" :value="50" />
                    <el-option label="100 条" :value="100" />
                  </el-select>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleSavePreferences">保存设置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const activeTab = ref('basic')

// 用户信息
const userInfo = reactive({
  username: 'admin',
  email: 'admin@example.com',
  role: 'admin',
  avatar: ''
})

// 用户统计
const stats = reactive({
  projectCount: 12,
  assetCount: 156,
  loginDays: 89
})

// 个人资料表单
const profileForm = reactive({
  username: 'admin',
  email: 'admin@example.com',
  phone: '13800138000',
  bio: '这是一个简短的个人介绍',
  location: '北京市',
  website: 'https://example.com'
})

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 安全设置
const securitySettings = reactive({
  twoFactor: false
})



// 偏好设置
const preferences = reactive({
  theme: 'light',
  language: 'zh-CN',
  timezone: 'Asia/Shanghai',
  pageSize: 20
})

// 获取角色标签类型
const getRoleTagType = (role) => {
  const tagMap = {
    admin: 'danger',
    manager: 'warning',
    user: 'info'
  }
  return tagMap[role] || 'info'
}

// 获取角色名称
const getRoleName = (role) => {
  const nameMap = {
    admin: '管理员',
    manager: '项目经理',
    user: '普通用户'
  }
  return nameMap[role] || role
}

// 事件处理
const handleAvatarUpload = () => {
  ElMessage.info('头像上传功能开发中...')
}

const handleSaveProfile = () => {
  ElMessage.success('个人资料保存成功')
}

const resetProfileForm = () => {
  Object.assign(profileForm, {
    username: userInfo.username,
    email: userInfo.email,
    phone: '13800138000',
    bio: '这是一个简短的个人介绍',
    location: '北京市',
    website: 'https://example.com'
  })
  ElMessage.info('表单已重置')
}

const handleChangePassword = () => {
  if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning('请填写完整的密码信息')
    return
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('新密码和确认密码不一致')
    return
  }
  
  ElMessage.success('密码修改成功')
  Object.assign(passwordForm, {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

const handleTwoFactorChange = (value) => {
  ElMessage.success(value ? '两步验证已启用' : '两步验证已关闭')
}

const handleSavePreferences = () => {
  ElMessage.success('偏好设置保存成功')
}

onMounted(() => {
  // 从localStorage获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsed = JSON.parse(storedUserInfo)
    Object.assign(userInfo, parsed)
    Object.assign(profileForm, {
      username: parsed.username,
      email: parsed.email
    })
  }
})
</script>

<style scoped>
.profile-view {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.page-header p {
  color: var(--el-text-color-regular);
  margin: 0;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-info {
  text-align: center;
}

.avatar-section {
  margin-bottom: 20px;
}

.avatar-section .el-button {
  margin-top: 8px;
  font-size: 12px;
}

.user-details h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: var(--el-text-color-primary);
}

.user-email {
  color: var(--el-text-color-regular);
  margin: 0 0 12px 0;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: var(--el-text-color-regular);
}

.settings-card {
  margin-bottom: 20px;
}

.security-section h4,
.notification-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.setting-item,
.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.setting-item:last-child,
.notification-item:last-child {
  border-bottom: none;
}

.setting-info,
.notification-info {
  flex: 1;
}

.setting-title,
.notification-title {
  font-size: 14px;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.setting-desc,
.notification-desc {
  font-size: 12px;
  color: var(--el-text-color-regular);
}

.two-factor-section,
.notification-list {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 16px;
}

@media (max-width: 768px) {
  .profile-view {
    padding: 16px;
  }
  
  .user-stats {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
