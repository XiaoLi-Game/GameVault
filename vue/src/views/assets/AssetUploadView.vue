<template>
  <div class="asset-upload">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h1>上传资产</h1>
        <p>上传您的游戏资产文件到资产库</p>
      </div>
      <div class="header-right">
        <el-button @click="$router.push('/assets')">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：上传区域 -->
      <el-col :xs="24" :lg="16">
        <el-card class="upload-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>文件上传</span>
              <el-button v-if="uploadedFiles.length > 0" type="danger" size="small" @click="clearAll">
                清空所有
              </el-button>
            </div>
          </template>

          <!-- 拖拽上传区域 -->
          <div class="upload-area">
            <el-upload
              ref="uploadRef"
              class="upload-selector"
              :multiple="true"
              :auto-upload="false"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              :before-upload="beforeUpload"
              :file-list="fileList"
              accept=".fbx,.obj,.3ds,.blend,.png,.jpg,.jpeg,.tga,.dds,.mp3,.wav,.ogg,.cs,.js,.py,.anim,.unity"
            >
              <el-button type="primary" :icon="Plus">选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持 3D模型、贴图、音频、脚本、动画等文件格式，单个文件不超过 100MB
                </div>
              </template>
            </el-upload>
          </div>

          <!-- 上传进度 -->
          <div v-if="uploading" class="upload-progress">
            <el-progress
              :percentage="uploadProgress"
              :status="uploadStatus"
              :stroke-width="8"
            />
            <p class="progress-text">{{ progressText }}</p>
          </div>

          <!-- 文件列表 -->
          <div v-if="uploadedFiles.length > 0" class="file-list">
            <h3>待上传文件 ({{ uploadedFiles.length }})</h3>
            <div class="file-items">
              <div v-for="(file, index) in uploadedFiles" :key="index" class="file-item">
                <div class="file-preview">
                  <img v-if="isImageFile(file)" :src="file.preview" alt="预览" />
                  <el-icon v-else size="32" class="file-icon">
                    <component :is="getFileIcon(file.type)" />
                  </el-icon>
                </div>
                <div class="file-info">
                  <div class="file-name">{{ file.name }}</div>
                  <div class="file-meta">
                    <span class="file-size">{{ formatFileSize(file.size) }}</span>
                    <el-tag :type="getTypeTagType(file.type)" size="small">
                      {{ getTypeLabel(file.type) }}
                    </el-tag>
                  </div>
                </div>
                <div class="file-actions">
                  <el-button size="small" @click="editFileInfo(index)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button size="small" type="danger" @click="removeFile(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：资产信息表单 -->
      <el-col :xs="24" :lg="8">
        <el-card class="info-card" shadow="never">
          <template #header>
            <span>资产信息</span>
          </template>

          <el-form :model="assetForm" :rules="formRules" ref="formRef" label-width="80px">
            <el-form-item label="所属项目" prop="projectId">
              <el-select v-model="assetForm.projectId" placeholder="选择项目" style="width: 100%">
                <el-option
                  v-for="project in projects"
                  :key="project.id"
                  :label="project.name"
                  :value="project.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="资产类型" prop="category">
              <el-select v-model="assetForm.category" placeholder="选择类型" style="width: 100%">
                <el-option label="3D模型" value="model" />
                <el-option label="贴图材质" value="texture" />
                <el-option label="音频文件" value="audio" />
                <el-option label="动画文件" value="animation" />
                <el-option label="脚本代码" value="script" />
                <el-option label="其他文件" value="other" />
              </el-select>
            </el-form-item>

            <el-form-item label="标签" prop="tags">
              <el-select
                v-model="assetForm.tags"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="添加标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in commonTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="描述" prop="description">
              <el-input
                v-model="assetForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入资产描述..."
              />
            </el-form-item>

            <el-form-item label="版本号" prop="version">
              <el-input v-model="assetForm.version" placeholder="如: 1.0.0" />
            </el-form-item>

            <el-form-item label="许可证" prop="license">
              <el-select v-model="assetForm.license" placeholder="选择许可证" style="width: 100%">
                <el-option label="MIT License" value="MIT" />
                <el-option label="Apache 2.0" value="Apache-2.0" />
                <el-option label="GPL v3" value="GPL-3.0" />
                <el-option label="Creative Commons" value="CC" />
                <el-option label="商业许可" value="Commercial" />
                <el-option label="自定义" value="Custom" />
              </el-select>
            </el-form-item>

            <el-form-item label="公开性">
              <el-radio-group v-model="assetForm.visibility">
                <el-radio label="public">公开</el-radio>
                <el-radio label="private">私有</el-radio>
                <el-radio label="team">团队可见</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 上传按钮 -->
        <div class="upload-actions">
          <el-button
            type="primary"
            size="large"
            :loading="uploading"
            :disabled="uploadedFiles.length === 0"
            @click="startUpload"
            style="width: 100%"
          >
            <el-icon v-if="!uploading"><Upload /></el-icon>
            {{ uploading ? '上传中...' : `上传 ${uploadedFiles.length} 个文件` }}
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 编辑文件信息对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑文件信息" width="500px">
      <el-form :model="editingFile" label-width="80px" v-if="editingFile">
        <el-form-item label="文件名">
          <el-input v-model="editingFile.name" />
        </el-form-item>
        <el-form-item label="显示名称">
          <el-input v-model="editingFile.displayName" placeholder="可选，用于显示的名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editingFile.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFileInfo">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assetApi } from '@/api/asset'
import {
  ArrowLeft,
  Plus,
  Upload,
  Edit,
  Delete,
  Files,
  Document,
  VideoCamera,
  Headset,
  Picture
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const uploadRef = ref()
const formRef = ref()
const fileList = ref([])
const uploadedFiles = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const progressText = ref('')

// 编辑对话框
const editDialogVisible = ref(false)
const editingFile = ref(null)
const editingIndex = ref(-1)

// 表单数据
const assetForm = reactive({
  projectId: '',
  category: '',
  tags: [],
  description: '',
  version: '1.0.0',
  license: 'MIT',
  visibility: 'public'
})

// 表单验证规则
const formRules = {
  projectId: [
    { required: true, message: '请选择所属项目', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择资产类型', trigger: 'change' }
  ]
}

// 项目数据
const projects = ref([
  { id: 1, name: '冒险游戏' },
  { id: 2, name: '射击游戏' },
  { id: 3, name: '角色扮演' },
  { id: 4, name: '策略游戏' }
])

// 常用标签
const commonTags = ref([
  '角色', '环境', '道具', '武器', '建筑', 'UI', '特效', '音乐', '音效',
  '脚本', '工具', '材质', '贴图', '模型', '动画', '粒子', '光照'
])

// 文件处理方法
const handleFileChange = (file, fileList) => {
  const newFile = {
    name: file.name,
    displayName: '',
    description: '',
    size: file.size,
    type: getFileType(file.name),
    raw: file.raw,
    preview: null
  }

  // 如果是图片，生成预览
  if (isImageFile(newFile)) {
    const reader = new FileReader()
    reader.onload = (e) => {
      newFile.preview = e.target.result
    }
    reader.readAsDataURL(file.raw)
  }

  uploadedFiles.value.push(newFile)
}

const handleFileRemove = (file, fileList) => {
  const index = uploadedFiles.value.findIndex(f => f.name === file.name)
  if (index > -1) {
    uploadedFiles.value.splice(index, 1)
  }
}

const beforeUpload = (file) => {
  // 文件大小检查 (100MB)
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) {
    ElMessage.error('文件大小不能超过 100MB!')
    return false
  }

  // 文件类型检查
  const allowedTypes = [
    // 3D模型
    'fbx', 'obj', '3ds', 'blend', 'dae', 'gltf', 'glb',
    // 贴图
    'png', 'jpg', 'jpeg', 'tga', 'dds', 'exr', 'hdr',
    // 音频
    'mp3', 'wav', 'ogg', 'aac', 'flac',
    // 脚本
    'cs', 'js', 'py', 'cpp', 'h', 'lua',
    // 动画
    'anim', 'fbx', 'bvh',
    // 其他
    'unity', 'prefab', 'mat', 'shader'
  ]

  const fileExt = file.name.split('.').pop().toLowerCase()
  if (!allowedTypes.includes(fileExt)) {
    ElMessage.error('不支持的文件格式!')
    return false
  }

  return true
}

const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
  // 同时从 el-upload 的文件列表中移除
  fileList.value.splice(index, 1)
}

const clearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有文件吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    uploadedFiles.value = []
    fileList.value = []
    uploadRef.value?.clearFiles()
  } catch {
    // 用户取消
  }
}

const editFileInfo = (index) => {
  editingIndex.value = index
  editingFile.value = { ...uploadedFiles.value[index] }
  editDialogVisible.value = true
}

const saveFileInfo = () => {
  if (editingIndex.value >= 0) {
    uploadedFiles.value[editingIndex.value] = { ...editingFile.value }
  }
  editDialogVisible.value = false
  editingFile.value = null
  editingIndex.value = -1
}

// 上传处理
const startUpload = async () => {
  try {
    // 验证表单
    await formRef.value.validate()

    if (uploadedFiles.value.length === 0) {
      ElMessage.warning('请先选择要上传的文件')
      return
    }

    uploading.value = true
    uploadProgress.value = 0
    uploadStatus.value = ''

    let successCount = 0
    let failCount = 0

    // 逐个上传文件
    for (let i = 0; i < uploadedFiles.value.length; i++) {
      const file = uploadedFiles.value[i]
      progressText.value = `正在上传: ${file.displayName || file.name} (${i + 1}/${uploadedFiles.value.length})`

      try {
        // 准备上传数据
        const assetData = {
          name: file.displayName || file.name,
          description: file.description || assetForm.description,
          filePath: `/uploads/${file.name}`,
          fileSize: file.size,
          fileType: file.type,
          mimeType: file.raw?.type || 'application/octet-stream',
          fileHash: `sha256:${Date.now()}_${Math.random()}`,
          thumbnailPath: file.preview ? `/thumbnails/${file.name}.jpg` : null, // 使用文件路径而不是base64
          projectId: assetForm.projectId,
          category: assetForm.category,
          tags: assetForm.tags,
          metadata: JSON.stringify({
            version: assetForm.version,
            license: assetForm.license,
            visibility: assetForm.visibility,
            originalName: file.name,
            hasPreview: !!file.preview
          })
        }

        // 调用创建资产API
        await assetApi.createAsset(assetData)
        successCount++

        // 更新进度
        uploadProgress.value = Math.floor(((i + 1) / uploadedFiles.value.length) * 100)

      } catch (error) {
        console.error(`上传文件 ${file.name} 失败:`, error)
        failCount++
      }
    }

    // 显示结果
    if (successCount > 0) {
      uploadStatus.value = 'success'
      progressText.value = `上传完成！成功 ${successCount} 个，失败 ${failCount} 个`
      ElMessage.success(`成功上传 ${successCount} 个文件！`)

      // 延迟后跳转到资产列表，并添加刷新标记
      setTimeout(() => {
        router.push({ path: '/assets', query: { refresh: 'true' } })
      }, 2000)
    } else {
      uploadStatus.value = 'exception'
      progressText.value = '上传失败'
      ElMessage.error('所有文件上传失败，请重试')
    }

  } catch (error) {
    uploadStatus.value = 'exception'
    progressText.value = '上传失败'
    ElMessage.error('上传失败，请重试')
  } finally {
    setTimeout(() => {
      uploading.value = false
      uploadProgress.value = 0
    }, 2000)
  }
}

// 工具函数
const getFileType = (filename) => {
  const ext = filename.split('.').pop().toLowerCase()

  const typeMap = {
    // 3D模型
    'fbx': 'model', 'obj': 'model', '3ds': 'model', 'blend': 'model',
    'dae': 'model', 'gltf': 'model', 'glb': 'model',

    // 贴图
    'png': 'texture', 'jpg': 'texture', 'jpeg': 'texture', 'tga': 'texture',
    'dds': 'texture', 'exr': 'texture', 'hdr': 'texture',

    // 音频
    'mp3': 'audio', 'wav': 'audio', 'ogg': 'audio', 'aac': 'audio', 'flac': 'audio',

    // 脚本
    'cs': 'script', 'js': 'script', 'py': 'script', 'cpp': 'script',
    'h': 'script', 'lua': 'script',

    // 动画
    'anim': 'animation', 'bvh': 'animation'
  }

  return typeMap[ext] || 'other'
}

const isImageFile = (file) => {
  return file.type === 'texture' && ['png', 'jpg', 'jpeg'].includes(
    file.name.split('.').pop().toLowerCase()
  )
}

const getFileIcon = (type) => {
  const iconMap = {
    model: Files,
    texture: Picture,
    audio: Headset,
    animation: VideoCamera,
    script: Document,
    other: Files
  }
  return iconMap[type] || Files
}

const getTypeLabel = (type) => {
  const labelMap = {
    model: '3D模型',
    texture: '贴图',
    audio: '音频',
    animation: '动画',
    script: '脚本',
    other: '其他'
  }
  return labelMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    model: 'primary',
    texture: 'success',
    audio: 'warning',
    animation: 'info',
    script: 'danger',
    other: ''
  }
  return typeMap[type] || ''
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.asset-upload {
  padding: 24px;
  background: var(--el-bg-color-page);
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-left p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

/* 卡片样式 */
.upload-card,
.info-card {
  background: var(--card-bg);
  border: 1px solid var(--el-border-color-lighter);
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 上传区域 */
.upload-area {
  margin-bottom: 24px;
}

.upload-selector {
  width: 100%;
  text-align: center;
}

:deep(.el-icon--upload) {
  font-size: 48px;
  color: var(--el-text-color-secondary);
  margin-bottom: 16px;
}

:deep(.el-upload__text) {
  color: var(--el-text-color-regular);
  font-size: 16px;
  margin-bottom: 8px;
}

:deep(.el-upload__text em) {
  color: var(--el-color-primary);
  font-style: normal;
}

:deep(.el-upload__tip) {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 8px;
}

/* 上传进度 */
.upload-progress {
  margin: 24px 0;
  padding: 20px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  text-align: center;
}

.progress-text {
  margin-top: 12px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

/* 文件列表 */
.file-list {
  margin-top: 24px;
}

.file-list h3 {
  margin: 0 0 16px 0;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 500;
}

.file-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.file-item:hover {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-fill-color);
}

.file-preview {
  width: 48px;
  height: 48px;
  margin-right: 16px;
  border-radius: 6px;
  background: var(--el-fill-color);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.file-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon {
  color: var(--el-text-color-secondary);
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.file-size {
  color: var(--el-text-color-secondary);
}

.file-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: var(--el-text-color-regular);
}

:deep(.el-input__wrapper) {
  background: var(--el-fill-color-light);
}

:deep(.el-textarea__inner) {
  background: var(--el-fill-color-light);
}

:deep(.el-select .el-input__wrapper) {
  background: var(--el-fill-color-light);
}

/* 上传按钮 */
.upload-actions {
  margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .asset-upload {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .file-preview {
    margin-right: 0;
  }

  .file-actions {
    margin-left: 0;
    align-self: flex-end;
  }



  :deep(.el-icon--upload) {
    font-size: 36px;
  }
}
</style>
