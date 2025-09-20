<template>
  <div class="system-settings">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h1>系统设置</h1>
        <p>管理系统配置和偏好设置</p>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 基本设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="settings-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" />
            </el-form-item>
            
            <el-form-item label="系统描述">
              <el-input 
                v-model="basicSettings.systemDescription" 
                type="textarea" 
                :rows="3" 
              />
            </el-form-item>
            
            <el-form-item label="默认语言">
              <el-select v-model="basicSettings.defaultLanguage">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="时区设置">
              <el-select v-model="basicSettings.timezone">
                <el-option label="北京时间 (UTC+8)" value="Asia/Shanghai" />
                <el-option label="UTC时间" value="UTC" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 安全设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="settings-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </div>
          </template>
          
          <el-form :model="securitySettings" label-width="120px">
            <el-form-item label="密码策略">
              <el-switch 
                v-model="securitySettings.strongPassword"
                active-text="强密码"
                inactive-text="普通密码"
              />
            </el-form-item>
            
            <el-form-item label="登录超时">
              <el-input-number 
                v-model="securitySettings.sessionTimeout"
                :min="30"
                :max="1440"
                controls-position="right"
              />
              <span class="form-text">分钟</span>
            </el-form-item>
            
            <el-form-item label="最大登录尝试">
              <el-input-number 
                v-model="securitySettings.maxLoginAttempts"
                :min="3"
                :max="10"
                controls-position="right"
              />
              <span class="form-text">次</span>
            </el-form-item>
            
            <el-form-item label="双因子认证">
              <el-switch 
                v-model="securitySettings.twoFactorAuth"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px;">
      <!-- 存储设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="settings-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><FolderOpened /></el-icon>
              <span>存储设置</span>
            </div>
          </template>
          
          <el-form :model="storageSettings" label-width="120px">
            <el-form-item label="文件存储路径">
              <el-input v-model="storageSettings.uploadPath" />
            </el-form-item>
            
            <el-form-item label="最大文件大小">
              <el-input-number 
                v-model="storageSettings.maxFileSize"
                :min="1"
                :max="1024"
                controls-position="right"
              />
              <span class="form-text">MB</span>
            </el-form-item>
            
            <el-form-item label="允许的文件类型">
              <el-input 
                v-model="storageSettings.allowedFileTypes" 
                placeholder="jpg,png,pdf,zip"
              />
            </el-form-item>
            
            <el-form-item label="自动清理">
              <el-switch 
                v-model="storageSettings.autoCleanup"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 邮件设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="settings-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Message /></el-icon>
              <span>邮件设置</span>
            </div>
          </template>
          
          <el-form :model="emailSettings" label-width="120px">
            <el-form-item label="SMTP服务器">
              <el-input v-model="emailSettings.smtpHost" />
            </el-form-item>
            
            <el-form-item label="SMTP端口">
              <el-input-number 
                v-model="emailSettings.smtpPort"
                :min="1"
                :max="65535"
                controls-position="right"
              />
            </el-form-item>
            
            <el-form-item label="发件人邮箱">
              <el-input v-model="emailSettings.fromEmail" />
            </el-form-item>
            
            <el-form-item label="启用SSL">
              <el-switch 
                v-model="emailSettings.enableSSL"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="saveSettings" :loading="saving">
        保存设置
      </el-button>
      <el-button @click="resetSettings">
        重置设置
      </el-button>
      <el-button @click="exportSettings">
        导出配置
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  Lock,
  FolderOpened,
  Message
} from '@element-plus/icons-vue'

// 响应式数据
const saving = ref(false)

// 基本设置
const basicSettings = reactive({
  systemName: 'GameVault',
  systemDescription: '游戏资产管理系统',
  defaultLanguage: 'zh-CN',
  timezone: 'Asia/Shanghai'
})

// 安全设置
const securitySettings = reactive({
  strongPassword: true,
  sessionTimeout: 120,
  maxLoginAttempts: 5,
  twoFactorAuth: false
})

// 存储设置
const storageSettings = reactive({
  uploadPath: '/uploads',
  maxFileSize: 100,
  allowedFileTypes: 'jpg,png,gif,pdf,zip,rar,fbx,obj,mp3,wav',
  autoCleanup: true
})

// 邮件设置
const emailSettings = reactive({
  smtpHost: 'smtp.example.com',
  smtpPort: 587,
  fromEmail: 'noreply@gamevault.com',
  enableSSL: true
})

// 保存设置
const saveSettings = async () => {
  try {
    saving.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('保存设置失败')
  } finally {
    saving.value = false
  }
}

// 重置设置
const resetSettings = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置所有设置吗？此操作不可恢复。',
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 重置为默认值
    Object.assign(basicSettings, {
      systemName: 'GameVault',
      systemDescription: '游戏资产管理系统',
      defaultLanguage: 'zh-CN',
      timezone: 'Asia/Shanghai'
    })
    
    ElMessage.success('设置已重置')
  } catch {
    // 用户取消
  }
}

// 导出配置
const exportSettings = () => {
  const config = {
    basic: basicSettings,
    security: securitySettings,
    storage: storageSettings,
    email: emailSettings
  }
  
  const blob = new Blob([JSON.stringify(config, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'gamevault-settings.json'
  a.click()
  URL.revokeObjectURL(url)
  
  ElMessage.success('配置已导出')
}
</script>

<style scoped>
.system-settings {
  padding: 24px;
  background: var(--el-bg-color-page);
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.page-header p {
  margin: 0;
  color: var(--el-text-color-regular);
}

.settings-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.form-text {
  margin-left: 8px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.action-buttons {
  margin-top: 24px;
  padding: 24px;
  background: var(--el-bg-color);
  border-radius: 8px;
  text-align: center;
}

.action-buttons .el-button {
  margin: 0 8px;
}
</style>
