<template>
  <div class="audit-detail">
    <div class="page-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
        <div class="header-info">
          <h1>审核详情</h1>
          <p>查看资产详细信息和审核记录</p>
        </div>
      </div>
      <div class="header-right">
        <el-button v-if="asset.status === 'pending'" type="success" :icon="Check" @click="handleApprove">
          通过审核
        </el-button>
        <el-button v-if="asset.status === 'pending'" type="danger" :icon="Close" @click="handleReject">
          拒绝审核
        </el-button>
        <el-button :icon="Download" @click="handleDownload">下载资产</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：资产信息 -->
      <el-col :span="16">
        <el-card class="asset-card">
          <template #header>
            <div class="card-header">
              <span>资产信息</span>
              <el-tag :type="getStatusTagType(asset.status)">
                {{ getStatusName(asset.status) }}
              </el-tag>
            </div>
          </template>
          
          <div class="asset-preview">
            <div class="preview-placeholder">
              <el-icon size="64" :color="getAssetIconColor(asset.type)">
                <component :is="getAssetIcon(asset.type)" />
              </el-icon>
              <p>{{ asset.name }}</p>
            </div>
          </div>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="资产名称">{{ asset.name }}</el-descriptions-item>
            <el-descriptions-item label="资产类型">{{ getTypeName(asset.type) }}</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ asset.size }}</el-descriptions-item>
            <el-descriptions-item label="所属项目">{{ asset.project }}</el-descriptions-item>
            <el-descriptions-item label="上传者">{{ asset.uploader.name }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ asset.uploadTime }}</el-descriptions-item>
            <el-descriptions-item label="文件格式">{{ getFileFormat(asset.name) }}</el-descriptions-item>
            <el-descriptions-item label="版本号">v1.0</el-descriptions-item>
            <el-descriptions-item label="资产描述" :span="2">
              {{ asset.description || '暂无描述' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 右侧：审核记录 -->
      <el-col :span="8">
        <el-card class="audit-history-card">
          <template #header>
            <span>审核记录</span>
          </template>
          
          <el-timeline>
            <el-timeline-item
              v-for="record in auditHistory"
              :key="record.id"
              :timestamp="record.time"
              :type="getTimelineType(record.action)"
            >
              <div class="timeline-content">
                <div class="action-info">
                  <span class="action">{{ record.action }}</span>
                  <span class="auditor">{{ record.auditor }}</span>
                </div>
                <div v-if="record.comment" class="comment">
                  {{ record.comment }}
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <!-- 相关资产 -->
        <el-card class="related-assets-card" style="margin-top: 20px;">
          <template #header>
            <span>相关资产</span>
          </template>
          
          <div class="related-list">
            <div v-for="item in relatedAssets" :key="item.id" class="related-item">
              <el-icon :color="getAssetIconColor(item.type)">
                <component :is="getAssetIcon(item.type)" />
              </el-icon>
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-meta">{{ getTypeName(item.type) }}</div>
              </div>
              <el-tag size="small" :type="getStatusTagType(item.status)">
                {{ getStatusName(item.status) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核资产" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="auditForm.result">
            <el-radio label="approved">
              <el-icon color="#67c23a"><Check /></el-icon>
              通过
            </el-radio>
            <el-radio label="rejected">
              <el-icon color="#f56c6c"><Close /></el-icon>
              拒绝
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          :label="auditForm.result === 'rejected' ? '拒绝原因' : '审核意见'"
          :required="auditForm.result === 'rejected'"
        >
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="auditForm.result === 'rejected' ? '请输入拒绝原因（必填）' : '请输入审核意见（可选）'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="auditing" 
          @click="handleSubmitAudit"
          :disabled="auditForm.result === 'rejected' && !auditForm.comment.trim()"
        >
          确认{{ auditForm.result === 'approved' ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Check,
  Close,
  Download,
  Picture,
  VideoCamera,
  Document,
  Files
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const auditing = ref(false)
const auditDialogVisible = ref(false)
const asset = ref({})
const auditHistory = ref([])
const relatedAssets = ref([])

// 审核表单
const auditForm = reactive({
  result: 'approved',
  comment: ''
})

// 工具函数
const getAssetIcon = (type) => {
  const iconMap = {
    model: Files,
    texture: Picture,
    audio: VideoCamera, // 使用 VideoCamera 替代 Headphones
    animation: VideoCamera,
    script: Document
  }
  return iconMap[type] || Files
}

const getAssetIconColor = (type) => {
  const colorMap = {
    model: '#409eff',
    texture: '#67c23a',
    audio: '#e6a23c',
    animation: '#f56c6c',
    script: '#909399'
  }
  return colorMap[type] || '#909399'
}

const getTypeName = (type) => {
  const nameMap = {
    model: '3D模型',
    texture: '贴图',
    audio: '音频',
    animation: '动画',
    script: '脚本'
  }
  return nameMap[type] || '未知'
}

const getStatusName = (status) => {
  const nameMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return nameMap[status] || '未知'
}

const getStatusTagType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getTimelineType = (action) => {
  const typeMap = {
    '上传资产': 'primary',
    '提交审核': 'warning',
    '审核通过': 'success',
    '审核拒绝': 'danger'
  }
  return typeMap[action] || 'primary'
}

const getFileFormat = (filename) => {
  const ext = filename.split('.').pop().toUpperCase()
  return ext
}

// 事件处理
const goBack = () => {
  router.go(-1)
}

const handleApprove = () => {
  auditForm.result = 'approved'
  auditForm.comment = ''
  auditDialogVisible.value = true
}

const handleReject = () => {
  auditForm.result = 'rejected'
  auditForm.comment = ''
  auditDialogVisible.value = true
}

const handleDownload = () => {
  ElMessage.info('开始下载资产...')
}

const handleSubmitAudit = async () => {
  auditing.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    
    // 更新资产状态
    asset.value.status = auditForm.result
    asset.value.auditor = '当前用户'
    asset.value.auditTime = new Date().toLocaleString()
    
    // 添加审核记录
    auditHistory.value.unshift({
      id: Date.now(),
      action: auditForm.result === 'approved' ? '审核通过' : '审核拒绝',
      auditor: '当前用户',
      time: new Date().toLocaleString(),
      comment: auditForm.comment
    })
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditing.value = false
  }
}

// 加载数据
const loadAssetDetail = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    asset.value = {
      id: route.params.id,
      name: '角色模型_战士.fbx',
      type: 'model',
      size: '15.2MB',
      project: 'RPG冒险游戏',
      uploader: { name: '张三', avatar: '' },
      uploadTime: '2024-01-15 14:30',
      status: 'pending',
      auditTime: '',
      auditor: '',
      description: '高精度战士角色模型，包含完整的动画骨骼系统，适用于RPG游戏中的主角或重要NPC角色。模型采用PBR材质，支持多种光照环境。'
    }
    
    // 审核历史
    auditHistory.value = [
      {
        id: 1,
        action: '上传资产',
        auditor: '张三',
        time: '2024-01-15 14:30',
        comment: '上传了新的角色模型资产'
      },
      {
        id: 2,
        action: '提交审核',
        auditor: '系统',
        time: '2024-01-15 14:31',
        comment: '资产已自动提交审核'
      }
    ]
    
    // 相关资产
    relatedAssets.value = [
      {
        id: 2,
        name: '战士_贴图.jpg',
        type: 'texture',
        status: 'approved'
      },
      {
        id: 3,
        name: '战士_动画.fbx',
        type: 'animation',
        status: 'pending'
      }
    ]
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

onMounted(() => {
  loadAssetDetail()
})
</script>

<style scoped>
.audit-detail {
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
  align-items: flex-start;
  gap: 16px;
}

.header-info h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.header-info p {
  color: #8c8c8c;
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

.asset-preview {
  text-align: center;
  padding: 40px 20px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.preview-placeholder p {
  margin: 16px 0 0 0;
  color: #666;
  font-size: 16px;
  font-weight: 500;
}

.timeline-content {
  padding-left: 8px;
}

.action-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.action {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.auditor {
  font-size: 12px;
  color: #8c8c8c;
}

.comment {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 6px;
  background: #fafafa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.related-item:hover {
  background: #f0f0f0;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 2px;
}

.item-meta {
  font-size: 12px;
  color: #8c8c8c;
}

/* 深色模式适配 */
:global(.dark) .header-info h1 {
  color: var(--el-text-color-primary);
}

:global(.dark) .header-info p {
  color: var(--el-text-color-regular);
}

:global(.dark) .asset-preview {
  background: var(--el-bg-color-page);
}

:global(.dark) .preview-placeholder p {
  color: var(--el-text-color-regular);
}

:global(.dark) .action {
  color: var(--el-text-color-primary);
}

:global(.dark) .auditor {
  color: var(--el-text-color-regular);
}

:global(.dark) .comment {
  color: var(--el-text-color-regular);
}

:global(.dark) .related-item {
  background: var(--el-bg-color-page);
}

:global(.dark) .related-item:hover {
  background: var(--el-fill-color-light);
}

:global(.dark) .item-name {
  color: var(--el-text-color-primary);
}

:global(.dark) .item-meta {
  color: var(--el-text-color-regular);
}
</style>
