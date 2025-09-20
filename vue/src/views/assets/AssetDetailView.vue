<template>
  <div class="asset-detail">
    <div class="page-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="$router.go(-1)">返回</el-button>
        <div class="asset-title">
          <h1>{{ asset.name }}</h1>
          <el-tag :type="getStatusTagType(asset.status)">{{ getStatusName(asset.status) }}</el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Download" @click="downloadAsset">下载</el-button>
        <el-button :icon="Edit" @click="editAsset">编辑</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 资产预览 -->
      <el-col :xs="24" :lg="16">
        <el-card class="preview-card">
          <template #header>
            <span>资产预览</span>
          </template>
          
          <div class="asset-preview">
            <div v-if="asset.type === 'texture'" class="image-preview">
              <img :src="asset.preview" :alt="asset.name" />
            </div>
            <div v-else class="file-preview">
              <el-icon size="120" color="#8c8c8c">
                <component :is="getAssetIcon(asset.type)" />
              </el-icon>
              <p>{{ getTypeName(asset.type) }}文件</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 资产信息 -->
      <el-col :xs="24" :lg="8">
        <el-card class="info-card">
          <template #header>
            <span>基本信息</span>
          </template>
          
          <div class="info-list">
            <div class="info-item">
              <span class="label">文件名:</span>
              <span class="value">{{ asset.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">文件大小:</span>
              <span class="value">{{ asset.size }}</span>
            </div>
            <div class="info-item">
              <span class="label">文件类型:</span>
              <span class="value">{{ getTypeName(asset.type) }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属项目:</span>
              <span class="value">{{ asset.project }}</span>
            </div>
            <div class="info-item">
              <span class="label">上传者:</span>
              <span class="value">{{ asset.uploader }}</span>
            </div>
            <div class="info-item">
              <span class="label">上传时间:</span>
              <span class="value">{{ asset.uploadTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">版本:</span>
              <span class="value">{{ asset.version }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="tags-card">
          <template #header>
            <span>标签</span>
          </template>
          
          <div class="tags-list">
            <el-tag v-for="tag in asset.tags" :key="tag" type="info">
              {{ tag }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 描述和更新日志 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="16">
        <el-card class="description-card">
          <template #header>
            <span>描述</span>
          </template>
          
          <div class="description-content">
            <p>{{ asset.description || '暂无描述' }}</p>
          </div>
        </el-card>

        <el-card class="changelog-card" style="margin-top: 20px">
          <template #header>
            <span>更新日志</span>
          </template>
          
          <div class="changelog-content">
            <p>{{ asset.changelog || '暂无更新日志' }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="stats-card">
          <template #header>
            <span>统计信息</span>
          </template>
          
          <div class="stats-list">
            <div class="stat-item">
              <span class="stat-label">下载次数</span>
              <span class="stat-value">{{ asset.downloadCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">查看次数</span>
              <span class="stat-value">{{ asset.viewCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">收藏次数</span>
              <span class="stat-value">{{ asset.favoriteCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assetApi } from '@/api/asset'
import {
  ArrowLeft,
  Download,
  Edit,
  Files,
  Picture,
  Headset,
  VideoCamera,
  Document
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const asset = ref({})

// 获取资产图标
const getAssetIcon = (type) => {
  const iconMap = {
    model: Files,
    texture: Picture,
    audio: Headset,
    animation: VideoCamera,
    script: Document
  }
  return iconMap[type] || Files
}

// 获取类型名称
const getTypeName = (type) => {
  const nameMap = {
    model: '3D模型',
    texture: '贴图',
    audio: '音频',
    animation: '动画',
    script: '脚本'
  }
  return nameMap[type] || type
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || ''
}

// 获取状态名称
const getStatusName = (status) => {
  const nameMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return nameMap[status] || status
}

// 加载资产详情
const loadAssetDetail = async () => {
  try {
    const assetId = route.params.id

    // 调用真实API
    const response = await assetApi.getAssetDetail(assetId)
    if (response.data) {
      asset.value = response.data
    } else {
      throw new Error('资产数据为空')
    }
  } catch (error) {
    console.error('加载资产详情失败:', error)

    // 检查是否是资产不存在的错误
    if (error.message && error.message.includes('资产不存在')) {
      ElMessage.error(`资产 ID ${route.params.id} 不存在，正在返回资产列表`)
      // 延迟跳转到资产列表页面
      setTimeout(() => {
        router.push('/assets')
      }, 2000)
      return
    }

    ElMessage.error('加载资产详情失败，请稍后重试')

    // 如果是其他错误，也跳转回资产列表
    setTimeout(() => {
      router.push('/assets')
    }, 2000)
  }
}

// 下载资产
const downloadAsset = () => {
  ElMessage.success(`正在下载: ${asset.value.name || '资产文件'}`)

  // 模拟下载过程
  setTimeout(() => {
    ElMessage.info(`文件大小: ${formatFileSize(asset.value.fileSize)} | 类型: ${getTypeName(asset.value.category)}`)
  }, 1000)
}

// 编辑资产
const editAsset = () => {
  ElMessage.info(`编辑资产: ${asset.value.name || '资产文件'}`)
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

onMounted(() => {
  loadAssetDetail()
})
</script>

<style scoped>
.asset-detail {
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

.asset-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.asset-title h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.preview-card {
  margin-bottom: 20px;
}

.asset-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: #fafafa;
  border-radius: 8px;
}

.image-preview img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.file-preview {
  text-align: center;
}

.file-preview p {
  margin-top: 16px;
  color: #8c8c8c;
  font-size: 16px;
}

.info-card {
  margin-bottom: 20px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #8c8c8c;
  font-size: 14px;
}

.info-item .value {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.description-content,
.changelog-content {
  line-height: 1.6;
  color: #595959;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 6px;
}

.stat-label {
  color: #8c8c8c;
  font-size: 14px;
}

.stat-value {
  color: var(--el-text-color-primary);
  font-weight: 600;
  font-size: 18px;
}

@media (max-width: 768px) {
  .asset-detail {
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
  
  .asset-title {
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
}
</style>
