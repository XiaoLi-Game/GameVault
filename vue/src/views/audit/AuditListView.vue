<template>
  <div class="audit-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h1>审核管理</h1>
        <p>管理和审核用户上传的资产</p>
      </div>
      <div class="header-right">
      </div>
    </div>



    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="搜索">
          <el-input
            v-model="filterForm.keyword"
            placeholder="输入资产名称或上传者，按回车搜索"
            :prefix-icon="Search"
            clearable
            style="width: 280px"
            @keyup.enter="handleFilter"
            @input="handleFilter"
          />
        </el-form-item>

        <el-form-item label="类型">
          <el-select v-model="filterForm.type" placeholder="选择资产类型" clearable style="width: 140px">
            <el-option label="全部" value="" />
            <el-option label="3D模型" value="model" />
            <el-option label="贴图" value="texture" />
            <el-option label="音频" value="audio" />
            <el-option label="动画" value="animation" />
            <el-option label="脚本" value="script" />
          </el-select>
        </el-form-item>

        <el-form-item label="项目">
          <el-select v-model="filterForm.project" placeholder="选择项目" clearable style="width: 140px">
            <el-option label="全部" value="" />
            <el-option label="Adventure Game" value="Adventure Game" />
            <el-option label="Shooting Game" value="Shooting Game" />
            <el-option label="RPG Game" value="RPG Game" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审核列表 -->
    <el-card class="table-card">
      <el-table
        :data="filteredAuditList"
        v-loading="loading"
      >
        <el-table-column prop="name" label="资产名称" min-width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">{{ getTypeName(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="100" />
        <el-table-column prop="project" label="项目" width="150" />
        <el-table-column prop="uploader.name" label="上传者" width="120" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'pending'" 
              size="small" 
              type="primary" 
              @click="handleAudit(scope.row)"
            >
              审核
            </el-button>
            <el-button 
              v-else 
              size="small" 
              @click="handleViewDetail(scope.row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="资产审核" width="600px">
      <div v-if="currentAuditItem">
        <div class="audit-info">
          <h3>{{ currentAuditItem.name }}</h3>
          <p><strong>类型：</strong>{{ getTypeName(currentAuditItem.type) }}</p>
          <p><strong>大小：</strong>{{ currentAuditItem.size }}</p>
          <p><strong>项目：</strong>{{ currentAuditItem.project }}</p>
          <p><strong>上传者：</strong>{{ currentAuditItem.uploader.name }}</p>
          <p><strong>描述：</strong>{{ currentAuditItem.description }}</p>
        </div>
        
        <el-form :model="auditForm" label-width="80px">
          <el-form-item label="审核结果" required>
            <el-radio-group v-model="auditForm.result">
              <el-radio value="approved">通过</el-radio>
              <el-radio value="rejected">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input 
              v-model="auditForm.comment" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入审核意见..."
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAudit" :loading="auditing">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const auditDialogVisible = ref(false)
const auditing = ref(false)
const currentAuditItem = ref(null)

// 筛选表单
const filterForm = reactive({
  keyword: '',
  type: '',
  project: '',
  status: ''
})

// 审核表单
const auditForm = reactive({
  result: 'approved',
  comment: ''
})

// 审核列表数据
const auditList = ref([
  {
    id: 1001,
    name: 'character_warrior.fbx',
    type: 'model',
    size: '15.0MB',
    project: 'Adventure Game',
    uploader: { name: '张三', avatar: '' },
    uploadTime: '2024-01-15 14:30',
    status: 'pending',
    description: 'Medieval style warrior character model'
  },
  {
    id: 1002,
    name: 'forest_texture_pack.zip',
    type: 'texture',
    size: '50.0MB',
    project: 'Adventure Game',
    uploader: { name: '李四', avatar: '' },
    uploadTime: '2024-01-14 13:20',
    status: 'approved',
    description: 'High quality forest environment texture pack'
  },
  {
    id: 1003,
    name: 'battle_music.mp3',
    type: 'audio',
    size: '8.0MB',
    project: 'Shooting Game',
    uploader: { name: '王五', avatar: '' },
    uploadTime: '2024-01-13 09:15',
    status: 'pending',
    description: 'Intense battle background music'
  },
  {
    id: 1004,
    name: 'player_controller.cs',
    type: 'script',
    size: '12.0KB',
    project: 'RPG Game',
    uploader: { name: '赵六', avatar: '' },
    uploadTime: '2024-01-12 16:45',
    status: 'approved',
    description: 'Unity player controller script'
  },
  {
    id: 1005,
    name: 'sword_animation.anim',
    type: 'animation',
    size: '2.0MB',
    project: 'Adventure Game',
    uploader: { name: '钱七', avatar: '' },
    uploadTime: '2024-01-11 11:30',
    status: 'rejected',
    description: 'Sword weapon attack animation'
  }
])



// 筛选后的审核列表
const filteredAuditList = computed(() => {
  let list = auditList.value

  // 关键词搜索
  if (filterForm.keyword) {
    const keyword = filterForm.keyword.toLowerCase()
    list = list.filter(item =>
      item.name.toLowerCase().includes(keyword) ||
      item.uploader.name.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword)
    )
  }

  // 类型筛选
  if (filterForm.type) {
    list = list.filter(item => item.type === filterForm.type)
  }

  // 项目筛选
  if (filterForm.project) {
    list = list.filter(item => item.project === filterForm.project)
  }

  // 状态筛选
  if (filterForm.status) {
    list = list.filter(item => item.status === filterForm.status)
  }

  return list
})

// 工具函数
const getTypeName = (type) => {
  const typeMap = {
    model: '模型',
    texture: '贴图',
    audio: '音频',
    script: '脚本',
    animation: '动画'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    model: 'primary',
    texture: 'success',
    audio: 'warning',
    script: 'info',
    animation: 'danger'
  }
  return typeMap[type] || ''
}

const getStatusName = (status) => {
  const statusMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const statusMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || ''
}

// 事件处理


const handleFilter = () => {
  // 筛选逻辑已在computed中实现
  // 实时搜索，无需额外操作
}

const handleReset = () => {
  filterForm.keyword = ''
  filterForm.type = ''
  filterForm.project = ''
  filterForm.status = ''
  ElMessage.success('筛选已重置')
}

const handleAudit = (item) => {
  currentAuditItem.value = item
  auditForm.result = 'approved'
  auditForm.comment = ''
  auditDialogVisible.value = true
}

const handleViewDetail = (item) => {
  ElMessage.info(`查看资产详情: ${item.name}`)
}

const handleSubmitAudit = async () => {
  try {
    auditing.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新本地数据
    const index = auditList.value.findIndex(item => item.id === currentAuditItem.value.id)
    if (index !== -1) {
      auditList.value[index].status = auditForm.result
    }
    
    ElMessage.success(`审核${auditForm.result === 'approved' ? '通过' : '拒绝'}成功`)
    auditDialogVisible.value = false
    
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditing.value = false
  }
}



onMounted(() => {
  console.log('审核管理页面已加载')
})
</script>

<style scoped>
.audit-list {
  padding: 24px;
  background: var(--el-bg-color-page);
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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



.search-card {
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.audit-info {
  background: var(--el-bg-color-page);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.audit-info h3 {
  margin: 0 0 12px 0;
  color: var(--el-text-color-primary);
}

.audit-info p {
  margin: 8px 0;
  color: var(--el-text-color-regular);
}
</style>
