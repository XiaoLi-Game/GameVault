<template>
  <div class="project-create">
    <div class="page-header">
      <div class="header-left">
        <h1>创建项目</h1>
        <p>创建一个新的游戏开发项目</p>
      </div>
      <div class="header-right">
        <el-button @click="$router.go(-1)">取消</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card class="form-card">
          <template #header>
            <span>基本信息</span>
          </template>
          
          <el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="100px">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
            </el-form-item>
            
            <el-form-item label="项目描述" prop="description">
              <el-input
                v-model="projectForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入项目描述"
              />
            </el-form-item>
            
            <el-form-item label="项目类型" prop="type">
              <el-select v-model="projectForm.type" placeholder="选择项目类型" style="width: 100%">
                <el-option label="2D游戏" value="2d" />
                <el-option label="3D游戏" value="3d" />
                <el-option label="VR游戏" value="vr" />
                <el-option label="移动游戏" value="mobile" />
                <el-option label="网页游戏" value="web" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="游戏引擎" prop="engine">
              <el-select v-model="projectForm.engine" placeholder="选择游戏引擎" style="width: 100%">
                <el-option label="Unity" value="unity" />
                <el-option label="Unreal Engine" value="unreal" />
                <el-option label="Godot" value="godot" />
                <el-option label="Cocos2d" value="cocos2d" />
                <el-option label="自定义引擎" value="custom" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="目标平台" prop="platforms">
              <el-checkbox-group v-model="projectForm.platforms">
                <el-checkbox label="windows">Windows</el-checkbox>
                <el-checkbox label="macos">macOS</el-checkbox>
                <el-checkbox label="linux">Linux</el-checkbox>
                <el-checkbox label="ios">iOS</el-checkbox>
                <el-checkbox label="android">Android</el-checkbox>
                <el-checkbox label="web">Web</el-checkbox>
                <el-checkbox label="console">游戏主机</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            
            <el-form-item label="预计周期" prop="duration">
              <el-select v-model="projectForm.duration" placeholder="选择预计开发周期" style="width: 100%">
                <el-option label="1个月以内" value="1month" />
                <el-option label="1-3个月" value="3months" />
                <el-option label="3-6个月" value="6months" />
                <el-option label="6-12个月" value="1year" />
                <el-option label="1年以上" value="long" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="团队规模" prop="teamSize">
              <el-input-number
                v-model="projectForm.teamSize"
                :min="1"
                :max="100"
                placeholder="预计团队人数"
                style="width: 100%"
              />
            </el-form-item>
            
            <el-form-item label="项目标签" prop="tags">
              <el-select
                v-model="projectForm.tags"
                multiple
                filterable
                allow-create
                placeholder="添加项目标签"
                style="width: 100%"
              >
                <el-option v-for="tag in commonTags" :key="tag" :label="tag" :value="tag" />
              </el-select>
            </el-form-item>

            <el-form-item label="项目头像">
              <div class="avatar-upload">
                <el-upload
                  class="avatar-uploader"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :on-success="handleAvatarSuccess"
                  action="#"
                  :auto-upload="false"
                  :on-change="handleAvatarChange"
                >
                  <img v-if="projectForm.avatar" :src="projectForm.avatar" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="avatar-tip">
                  <p>建议尺寸：200x200像素</p>
                  <p>支持格式：JPG、PNG</p>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="settings-card">
          <template #header>
            <span>项目设置</span>
          </template>
          
          <el-form :model="projectSettings" label-width="100px">
            <el-form-item label="可见性">
              <el-radio-group v-model="projectSettings.visibility">
                <el-radio label="public">公开</el-radio>
                <el-radio label="private">私有</el-radio>
              </el-radio-group>
              <div class="form-tip">
                <p v-if="projectSettings.visibility === 'public'">
                  公开项目对所有用户可见，任何人都可以查看项目信息和资产
                </p>
                <p v-else>
                  私有项目仅对项目成员可见，需要邀请才能加入
                </p>
              </div>
            </el-form-item>
            
            <el-form-item label="资产审核">
              <el-switch v-model="projectSettings.assetReview" />
              <div class="form-tip">
                <p>开启后，上传的资产需要管理员审核后才能使用</p>
              </div>
            </el-form-item>
            
            <el-form-item label="版本控制">
              <el-switch v-model="projectSettings.versionControl" />
              <div class="form-tip">
                <p>开启后，系统会自动管理资产的版本历史</p>
              </div>
            </el-form-item>
            
            <el-form-item label="存储限制">
              <el-select v-model="projectSettings.storageLimit" style="width: 100%">
                <el-option label="1GB" value="1gb" />
                <el-option label="5GB" value="5gb" />
                <el-option label="10GB" value="10gb" />
                <el-option label="50GB" value="50gb" />
                <el-option label="无限制" value="unlimited" />
              </el-select>
            </el-form-item>

            <el-form-item label="安全设置">
              <div class="security-settings">
                <div class="security-item">
                  <el-switch v-model="projectSettings.security.enableEncryption" />
                  <span class="security-label">启用资产加密</span>
                </div>
                <div class="security-item">
                  <el-switch v-model="projectSettings.security.requireAuth" />
                  <span class="security-label">访问需要认证</span>
                </div>
                <div class="security-item">
                  <el-switch v-model="projectSettings.security.auditLog" />
                  <span class="security-label">记录操作日志</span>
                </div>
                <div class="security-item">
                  <el-switch v-model="projectSettings.security.ipWhitelist" />
                  <span class="security-label">IP白名单限制</span>
                </div>
              </div>
              <div class="form-tip">
                <p>安全设置可以保护您的项目资产和数据安全</p>
              </div>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="template-card" style="margin-top: 20px">
          <template #header>
            <span>项目模板</span>
          </template>

          <div class="template-list">
            <div
              v-for="template in projectTemplates"
              :key="template.id"
              class="template-item"
              :class="{ active: selectedTemplate === template.id }"
              @click="selectedTemplate = template.id"
            >
              <div class="template-icon">
                <el-icon size="24" :color="template.color">
                  <component :is="template.icon" />
                </el-icon>
              </div>
              <div class="template-info">
                <div class="template-name">{{ template.name }}</div>
                <div class="template-desc">{{ template.description }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="preview-card" style="margin-top: 20px" v-if="projectForm.name">
          <template #header>
            <span>项目预览</span>
          </template>

          <div class="project-preview">
            <div class="preview-header">
              <div class="preview-avatar">
                <img v-if="projectForm.avatar" :src="projectForm.avatar" alt="项目头像" />
                <el-icon v-else size="32" color="#1890ff">
                  <Folder />
                </el-icon>
              </div>
              <div class="preview-info">
                <h3>{{ projectForm.name || '项目名称' }}</h3>
                <p>{{ projectForm.description || '项目描述' }}</p>
              </div>
            </div>

            <div class="preview-details">
              <div class="detail-item" v-if="projectForm.type">
                <span class="label">类型：</span>
                <span class="value">{{ getTypeLabel(projectForm.type) }}</span>
              </div>
              <div class="detail-item" v-if="projectForm.engine">
                <span class="label">引擎：</span>
                <span class="value">{{ getEngineLabel(projectForm.engine) }}</span>
              </div>
              <div class="detail-item" v-if="projectForm.platforms.length">
                <span class="label">平台：</span>
                <span class="value">{{ projectForm.platforms.length }} 个平台</span>
              </div>
              <div class="detail-item" v-if="projectForm.tags.length">
                <span class="label">标签：</span>
                <div class="tags">
                  <el-tag v-for="tag in projectForm.tags.slice(0, 3)" :key="tag" size="small">
                    {{ tag }}
                  </el-tag>
                  <span v-if="projectForm.tags.length > 3" class="more-tags">
                    +{{ projectForm.tags.length - 3 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="form-actions">
      <el-button size="large" @click="$router.go(-1)">取消</el-button>
      <el-button type="primary" size="large" :loading="creating" @click="handleCreate">
        创建项目
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Folder,
  Monitor,
  Headset,
  VideoCamera,
  Plus
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const projectFormRef = ref()
const creating = ref(false)
const selectedTemplate = ref('blank')

// 项目表单
const projectForm = reactive({
  name: '',
  description: '',
  type: '',
  engine: '',
  platforms: [],
  duration: '',
  teamSize: 1,
  tags: [],
  avatar: ''
})

// 项目设置
const projectSettings = reactive({
  visibility: 'private',
  assetReview: true,
  versionControl: true,
  storageLimit: '5gb',
  security: {
    enableEncryption: false,
    requireAuth: true,
    auditLog: true,
    ipWhitelist: false
  }
})

// 表单验证规则
const projectRules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 50, message: '项目名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入项目描述', trigger: 'blur' },
    { min: 10, max: 500, message: '项目描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择项目类型', trigger: 'change' }
  ],
  engine: [
    { required: true, message: '请选择游戏引擎', trigger: 'change' }
  ],
  platforms: [
    { type: 'array', required: true, message: '请选择至少一个目标平台', trigger: 'change' }
  ]
}

// 常用标签
const commonTags = ref([
  'RPG', 'FPS', '策略', '休闲', '动作', '冒险', '解谜', '竞技',
  '单人', '多人', '合作', 'PVP', 'PVE',
  '像素风', '卡通', '写实', '科幻', '奇幻', '现代'
])

// 项目模板
const projectTemplates = ref([
  {
    id: 'blank',
    name: '空白项目',
    description: '从零开始创建项目',
    icon: Folder,
    color: '#8c8c8c'
  },
  {
    id: '2d-platformer',
    name: '2D平台游戏',
    description: '包含基础的2D平台游戏资产',
    icon: Monitor,
    color: '#1890ff'
  },
  {
    id: '3d-fps',
    name: '3D射击游戏',
    description: '包含FPS游戏的基础资产',
    icon: VideoCamera,
    color: '#52c41a'
  },
  {
    id: 'mobile-casual',
    name: '休闲手游',
    description: '适合移动端的休闲游戏模板',
    icon: Headset,
    color: '#faad14'
  }
])

// 头像上传处理
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarChange = (file) => {
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      projectForm.avatar = e.target.result
    }
    reader.readAsDataURL(file.raw)
  }
}

const handleAvatarSuccess = (response, file) => {
  projectForm.avatar = URL.createObjectURL(file.raw)
}

// 获取类型标签
const getTypeLabel = (type) => {
  const typeMap = {
    '2d': '2D游戏',
    '3d': '3D游戏',
    'vr': 'VR游戏',
    'mobile': '移动游戏',
    'web': '网页游戏',
    'other': '其他'
  }
  return typeMap[type] || type
}

// 获取引擎标签
const getEngineLabel = (engine) => {
  const engineMap = {
    'unity': 'Unity',
    'unreal': 'Unreal Engine',
    'godot': 'Godot',
    'cocos2d': 'Cocos2d',
    'custom': '自定义引擎',
    'other': '其他'
  }
  return engineMap[engine] || engine
}

// 创建项目
const handleCreate = async () => {
  if (!projectFormRef.value) return

  try {
    const valid = await projectFormRef.value.validate()
    if (!valid) return

    creating.value = true

    // 构建项目数据
    const projectData = {
      ...projectForm,
      ...projectSettings,
      template: selectedTemplate.value,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString(),
      status: 'active',
      progress: 0,
      id: Date.now(), // 临时ID生成方式
      assetCount: 0,
      memberCount: 1,
      lastActivity: '刚刚',
      owner: '当前用户', // 这里应该从用户状态获取
      members: [
        {
          id: 1,
          name: '当前用户',
          avatar: '',
          role: 'owner'
        }
      ]
    }

    console.log('创建项目数据:', projectData)

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))

    // 保存到本地存储（模拟后端存储）
    const existingProjects = JSON.parse(localStorage.getItem('projects') || '[]')
    existingProjects.unshift(projectData)
    localStorage.setItem('projects', JSON.stringify(existingProjects))

    ElMessage.success('项目创建成功！')
    router.push('/projects')

  } catch (error) {
    console.error('创建项目失败:', error)
    ElMessage.error('项目创建失败，请重试')
  } finally {
    creating.value = false
  }
}
</script>

<style scoped>
.project-create {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.form-card {
  margin-bottom: 20px;
}

.form-tip {
  margin-top: 8px;
}

.form-tip p {
  font-size: 12px;
  color: #8c8c8c;
  margin: 0;
  line-height: 1.4;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.template-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.template-item:hover {
  border-color: #1890ff;
  background: #f0f8ff;
}

.template-item.active {
  border-color: #1890ff;
  background: #e6f7ff;
}

.template-icon {
  margin-right: 12px;
}

.template-info {
  flex: 1;
}

/* 深色模式下的模板选择器优化 */
html.dark .template-item {
  border-color: var(--el-border-color) !important;
  background-color: var(--el-fill-color-light) !important;
  color: var(--el-text-color-primary) !important;
}

html.dark .template-item:hover {
  border-color: var(--el-color-primary) !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

html.dark .template-item.active {
  border-color: var(--el-color-primary) !important;
  background-color: rgba(64, 158, 255, 0.2) !important;
}

html.dark .template-name {
  color: var(--el-text-color-primary) !important;
}

html.dark .template-desc {
  color: var(--el-text-color-regular) !important;
}

.template-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.template-desc {
  font-size: 12px;
  color: #8c8c8c;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
}

@media (max-width: 768px) {
  .project-create {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

:deep(.el-checkbox) {
  margin-right: 0;
}

.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #1890ff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c8c8c;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 80px;
  height: 80px;
  object-fit: cover;
  display: block;
}

.avatar-tip {
  flex: 1;
}

.avatar-tip p {
  font-size: 12px;
  color: #8c8c8c;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.project-preview {
  padding: 0;
}

.preview-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.preview-avatar {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  overflow: hidden;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-info {
  flex: 1;
}

.preview-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 4px 0;
}

.preview-info p {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
  line-height: 1.4;
}

.preview-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.detail-item .label {
  color: #8c8c8c;
  margin-right: 8px;
  min-width: 40px;
}

.detail-item .value {
  color: var(--el-text-color-primary);
}

.tags {
  display: flex;
  align-items: center;
  gap: 4px;
}

.security-settings {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.security-label {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.more-tags {
  font-size: 12px;
  color: #8c8c8c;
}
</style>
