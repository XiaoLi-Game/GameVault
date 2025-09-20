<template>
  <div class="asset-list">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h1>资产管理</h1>
        <p>管理您的游戏资产文件</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Upload" @click="$router.push('/assets/upload')">
          上传资产
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">1248</div>
            <div class="stat-label">总资产数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">23</div>
            <div class="stat-label">项目数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">8</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">2.3GB</div>
            <div class="stat-label">存储使用</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入资产名称或标签，按回车搜索"
            :prefix-icon="Search"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
            @input="handleSearch"
          />
        </el-form-item>

        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="选择资产类型" clearable style="width: 140px">
            <el-option label="全部" value="" />
            <el-option label="3D模型" value="model" />
            <el-option label="贴图" value="texture" />
            <el-option label="音频" value="audio" />
            <el-option label="动画" value="animation" />
            <el-option label="脚本" value="script" />
          </el-select>
        </el-form-item>

        <el-form-item label="项目">
          <el-select v-model="searchForm.projectId" placeholder="选择项目" clearable style="width: 140px">
            <el-option label="全部" value="" />
            <el-option v-for="project in projects" :key="project.id" :label="project.name" :value="project.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px">
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

    <!-- 视图切换和排序 -->
    <div class="toolbar">
      <div class="view-toggle">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="table">
            <el-icon><List /></el-icon>
            列表视图
          </el-radio-button>
          <el-radio-button label="grid">
            <el-icon><Grid /></el-icon>
            网格视图
          </el-radio-button>
        </el-radio-group>
      </div>

      <div class="sort-controls">
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px" size="small">
          <el-option label="创建时间" value="createdAt" />
          <el-option label="文件名" value="name" />
          <el-option label="文件大小" value="size" />
          <el-option label="下载次数" value="downloads" />
        </el-select>
        <el-button-group size="small" style="margin-left: 8px">
          <el-button :type="sortOrder === 'desc' ? 'primary' : ''" @click="sortOrder = 'desc'">
            <el-icon><SortDown /></el-icon>
          </el-button>
          <el-button :type="sortOrder === 'asc' ? 'primary' : ''" @click="sortOrder = 'asc'">
            <el-icon><SortUp /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 资产列表内容 -->
    <el-card class="content-card" shadow="never">
      <!-- 表格视图 -->
      <el-table
        v-if="viewMode === 'table'"
        :data="filteredAssets"
        v-loading="loading"
        class="asset-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <div class="asset-preview">
              <img v-if="row.type === 'texture'" :src="row.thumbnail" :alt="row.name" />
              <el-icon v-else size="32" class="file-icon">
                <component :is="getFileIcon(row.type)" />
              </el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="文件名" min-width="200">
          <template #default="{ row }">
            <div class="file-info">
              <div class="file-name">{{ row.name }}</div>
              <div class="file-meta">{{ formatFileSize(row.size) }} • {{ row.format }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="project" label="所属项目" width="150">
          <template #default="{ row }">
            <span class="project-name">{{ row.project?.name || '未分配' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="downloads" label="下载次数" width="100" />
        <el-table-column prop="createdAt" label="上传时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <div class="button-row">
                <el-button link size="small" :icon="View" @click="viewAsset(row)">查看</el-button>
                <el-button link size="small" :icon="Edit" @click="editAsset(row)">编辑</el-button>
              </div>
              <div class="button-row">
                <el-button link size="small" :icon="Download" @click="downloadAsset(row)">下载</el-button>
                <el-button link size="small" :icon="Delete" type="danger" @click="deleteAsset(row)">删除</el-button>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 网格视图 -->
      <div v-else class="asset-grid" v-loading="loading">
        <div v-for="asset in filteredAssets" :key="asset.id" class="asset-card">
          <div class="asset-card-preview">
            <img v-if="asset.type === 'texture'" :src="asset.thumbnail" :alt="asset.name" />
            <div v-else class="file-icon-large">
              <el-icon size="48">
                <component :is="getFileIcon(asset.type)" />
              </el-icon>
            </div>
            <div class="asset-card-overlay">
              <el-button-group size="small">
                <el-button :icon="View" circle @click="viewAsset(asset)" />
                <el-button :icon="Download" circle @click="downloadAsset(asset)" />
                <el-button :icon="Edit" circle @click="editAsset(asset)" />
                <el-button :icon="Delete" type="danger" circle @click="deleteAsset(asset)" />
              </el-button-group>
            </div>
          </div>
          <div class="asset-card-info">
            <div class="asset-name" :title="asset.name">{{ asset.name }}</div>
            <div class="asset-meta">
              <el-tag :type="getTypeTagType(asset.type)" size="small">
                {{ getTypeLabel(asset.type) }}
              </el-tag>
              <span class="file-size">{{ formatFileSize(asset.size) }}</span>
            </div>
            <div class="asset-stats">
              <span class="downloads">
                <el-icon><Download /></el-icon>
                {{ asset.downloads }}
              </span>
              <el-tag :type="getStatusTagType(asset.status)" size="small">
                {{ getStatusLabel(asset.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 48, 96]"
          :total="totalAssets"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑资产对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑资产"
      width="600px"
      :before-close="handleEditDialogClose"
    >
      <el-form
        :model="currentEditAsset"
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="资产名称">
          <el-input v-model="currentEditAsset.name" placeholder="请输入资产名称" />
        </el-form-item>

        <el-form-item label="资产类型">
          <el-select v-model="currentEditAsset.type" placeholder="请选择资产类型" style="width: 100%">
            <el-option label="模型" value="model" />
            <el-option label="纹理" value="texture" />
            <el-option label="音频" value="audio" />
            <el-option label="视频" value="video" />
            <el-option label="脚本" value="script" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属项目">
          <el-select v-model="currentEditAsset.projectId" placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="资产描述">
          <el-input
            v-model="currentEditAsset.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资产描述"
          />
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="currentEditAsset.tags" placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAssetEdit" :loading="loading">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assetApi } from '@/api/asset'
import {
  Search,
  Refresh,
  Upload,
  List,
  Grid,
  SortDown,
  SortUp,
  View,
  Download,
  Edit,
  Delete,
  Files,
  Document,
  VideoCamera,
  Headset,
  Picture
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const viewMode = ref('table')
const currentPage = ref(1)
const pageSize = ref(24)
const sortBy = ref('createdAt')
const sortOrder = ref('desc')

// 搜索表单
const searchForm = ref({
  keyword: '',
  type: '',
  projectId: '',
  status: ''
})

// 选中的资产
const selectedAssets = ref([])

// 编辑对话框相关
const editDialogVisible = ref(false)
const currentEditAsset = ref({})

// 模拟项目数据
const projects = ref([
  { id: 1, name: '冒险游戏' },
  { id: 2, name: '射击游戏' },
  { id: 3, name: '角色扮演' },
  { id: 4, name: '策略游戏' }
])

// 资产数据（优先API，后备模拟数据）
const assets = ref([
  {
    id: 1001,
    name: 'character_warrior.fbx',
    type: 'model',
    format: 'FBX',
    size: 15728640,
    thumbnail: '/api/thumbnails/character_warrior.jpg',
    project: { id: 1, name: '冒险游戏' },
    status: 'approved',
    downloads: 156,
    createdAt: '2024-01-15T10:30:00Z',
    tags: ['角色', '战士', '3D模型']
  },
  {
    id: 1002,
    name: 'forest_texture_pack.zip',
    type: 'texture',
    format: 'ZIP',
    size: 52428800,
    thumbnail: '/api/thumbnails/forest_texture.jpg',
    project: { id: 1, name: '冒险游戏' },
    status: 'approved',
    downloads: 89,
    createdAt: '2024-01-14T14:20:00Z',
    tags: ['贴图', '森林', '环境']
  },
  {
    id: 1003,
    name: 'battle_music.mp3',
    type: 'audio',
    format: 'MP3',
    size: 8388608,
    thumbnail: null,
    project: { id: 2, name: '射击游戏' },
    status: 'pending',
    downloads: 23,
    createdAt: '2024-01-13T09:15:00Z',
    tags: ['音乐', '战斗', '背景音乐']
  },
  {
    id: 1004,
    name: 'player_controller.cs',
    type: 'script',
    format: 'CS',
    size: 12288,
    thumbnail: null,
    project: { id: 3, name: '角色扮演' },
    status: 'approved',
    downloads: 234,
    createdAt: '2024-01-12T16:45:00Z',
    tags: ['脚本', '控制器', 'Unity']
  },
  {
    id: 1005,
    name: 'sword_animation.anim',
    type: 'animation',
    format: 'ANIM',
    size: 2097152,
    thumbnail: null,
    project: { id: 1, name: '冒险游戏' },
    status: 'approved',
    downloads: 67,
    createdAt: '2024-01-11T11:30:00Z',
    tags: ['动画', '武器', '攻击']
  },
  {
    id: 1006,
    name: 'ui_icons.png',
    type: 'texture',
    format: 'PNG',
    size: 1048576,
    thumbnail: '/api/thumbnails/ui_icons.jpg',
    project: { id: 4, name: '策略游戏' },
    status: 'rejected',
    downloads: 12,
    createdAt: '2024-01-10T13:20:00Z',
    tags: ['UI', '图标', '界面']
  }
])

// 计算属性
const totalAssets = computed(() => filteredAssets.value.length)

const filteredAssets = computed(() => {
  let result = assets.value

  // 关键词搜索
  if (searchForm.value.keyword) {
    const keyword = searchForm.value.keyword.toLowerCase()
    result = result.filter(asset =>
      asset.name.toLowerCase().includes(keyword) ||
      asset.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  // 类型筛选
  if (searchForm.value.type) {
    result = result.filter(asset => asset.type === searchForm.value.type)
  }

  // 项目筛选
  if (searchForm.value.projectId) {
    result = result.filter(asset => asset.project?.id === searchForm.value.projectId)
  }

  // 状态筛选
  if (searchForm.value.status) {
    result = result.filter(asset => asset.status === searchForm.value.status)
  }

  // 排序
  result.sort((a, b) => {
    let aValue = a[sortBy.value]
    let bValue = b[sortBy.value]

    if (sortBy.value === 'createdAt') {
      aValue = new Date(aValue)
      bValue = new Date(bValue)
    }

    if (sortOrder.value === 'desc') {
      return bValue > aValue ? 1 : -1
    } else {
      return aValue > bValue ? 1 : -1
    }
  })

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return result.slice(start, end)
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
  loadAssets()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    type: '',
    projectId: '',
    status: ''
  }
  currentPage.value = 1
  loadAssets()
}

const handleSelectionChange = (selection) => {
  selectedAssets.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAssets()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadAssets()
}

const viewAsset = (asset) => {
  // 跳转到资产详情页面
  router.push(`/assets/${asset.id}`)
}

const downloadAsset = (asset) => {
  // 显示下载提示信息
  ElMessage.success(`正在下载: ${asset.name}`)

  // 模拟下载过程，显示文件信息
  setTimeout(() => {
    ElMessage.info(`文件大小: ${formatFileSize(asset.size)} | 格式: ${asset.format}`)
  }, 1000)
}

const editAsset = (asset) => {
  // 设置当前编辑的资产
  currentEditAsset.value = { ...asset }
  editDialogVisible.value = true
}

const deleteAsset = async (asset) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资产 "${asset.name}" 吗？\n\n注意：这是演示功能，资产会临时删除，刷新页面即可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 800))

    // 从前端列表中临时移除
    const index = assets.value.findIndex(a => a.id === asset.id)
    if (index > -1) {
      assets.value.splice(index, 1)
    }

    ElMessage.success('删除成功！刷新页面可恢复数据')
  } catch {
    ElMessage.info('已取消删除')
  }
}

// 编辑对话框处理函数
const handleEditDialogClose = (done) => {
  // 可以在这里添加未保存提醒
  done()
}

const saveAssetEdit = async () => {
  try {
    loading.value = true

    // 调用真实API保存编辑
    await assetApi.updateAsset(currentEditAsset.value.id, currentEditAsset.value)

    // 更新本地数据
    const index = assets.value.findIndex(a => a.id === currentEditAsset.value.id)
    if (index > -1) {
      assets.value[index] = { ...currentEditAsset.value }
    }

    ElMessage.success('资产信息更新成功')
    editDialogVisible.value = false
  } catch (error) {
    console.error('保存资产编辑失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

// 工具函数
const getFileIcon = (type) => {
  const iconMap = {
    model: Files,
    texture: Picture,
    audio: Headset,
    animation: VideoCamera,
    script: Document
  }
  return iconMap[type] || Files
}

const getTypeLabel = (type) => {
  const labelMap = {
    model: '3D模型',
    texture: '贴图',
    audio: '音频',
    animation: '动画',
    script: '脚本'
  }
  return labelMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    model: 'primary',
    texture: 'success',
    audio: 'warning',
    animation: 'info',
    script: 'danger'
  }
  return typeMap[type] || ''
}

const getStatusLabel = (status) => {
  const labelMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return labelMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || ''
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载资产列表数据
const loadAssets = async () => {
  try {
    loading.value = true
    const response = await assetApi.getAssets({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword,
      type: searchForm.value.type,
      status: searchForm.value.status
    })

    if (response.data && response.data.list && response.data.list.length > 0) {
      // 将API数据转换为前端需要的格式
      assets.value = response.data.list.map(asset => ({
        id: asset.id,
        name: asset.name,
        type: asset.category || 'other',
        format: asset.fileType || 'Unknown',
        size: asset.fileSize || 0,
        thumbnail: asset.thumbnailPath || '/api/thumbnails/default.jpg',
        project: { id: asset.projectId, name: '项目名称' },
        status: asset.status || 'pending',
        downloads: asset.downloadCount || 0,
        createdAt: asset.createdAt,
        tags: asset.tags || []
      }))

      console.log(`✅ 加载了 ${assets.value.length} 个真实资产`)
    } else {
      console.log('📝 API返回空数据，保持使用默认示例数据')
    }
  } catch (error) {
    console.error('加载资产列表失败:', error)
    console.log('📝 API调用失败，保持使用默认示例数据')

    // 保持使用默认的示例数据（已在初始化时设置）
    console.log(`📝 保持使用默认示例数据: ${assets.value.length} 个资产`)
  } finally {
    loading.value = false
  }
}

// 监听路由查询参数变化
watch(() => route.query.refresh, (newVal) => {
  if (newVal === 'true') {
    // 清除查询参数并刷新数据
    router.replace({ path: '/assets' })
    ElMessage.success('检测到新上传的资产，正在刷新列表...')
    loadAssets()
  }
})

// 生命周期
onMounted(() => {
  console.log('📋 资产列表页面已挂载，当前资产数量:', assets.value.length)

  // 尝试加载API数据
  loadAssets()

  // 如果有刷新标记，立即刷新
  if (route.query.refresh === 'true') {
    router.replace({ path: '/assets' })
    ElMessage.success('资产上传成功，列表已刷新')
  }

  // 确保有数据显示
  setTimeout(() => {
    console.log('📋 页面加载完成后资产数量:', assets.value.length)
    if (assets.value.length === 0) {
      console.log('⚠️ 检测到资产列表为空，这可能是个问题')
    }
  }, 1000)
})
</script>

<style scoped>
.asset-list {
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

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
  background: var(--card-bg);
}

.search-form {
  margin: 0;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 0;
}

.view-toggle .el-radio-button {
  margin-right: 8px;
}

.sort-controls {
  display: flex;
  align-items: center;
}

/* 内容卡片 */
.content-card {
  background: var(--card-bg);
  border: 1px solid var(--el-border-color-lighter);
}

/* 表格样式 */
.asset-table {
  background: transparent;
}

.asset-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  overflow: hidden;
}

.asset-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon {
  color: var(--el-text-color-secondary);
}

.file-info .file-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.file-info .file-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.project-name {
  color: var(--el-text-color-regular);
}

/* 网格视图 */
.asset-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  padding: 16px 0;
}

.asset-card {
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.asset-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: var(--el-color-primary);
}

.asset-card-preview {
  position: relative;
  height: 160px;
  background: var(--el-fill-color);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.asset-card-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon-large {
  color: var(--el-text-color-secondary);
}

.asset-card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.asset-card:hover .asset-card-overlay {
  opacity: 1;
}

.asset-card-info {
  padding: 16px;
}

.asset-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.asset-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.file-size {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.asset-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.downloads {
  display: flex;
  align-items: center;
  color: var(--el-text-color-secondary);
}

.downloads .el-icon {
  margin-right: 4px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .asset-list {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 16px;
  }

  .asset-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: flex-start;
}

.button-row {
  display: flex;
  gap: 6px;
  justify-content: flex-start;
  align-items: center;
  min-height: 24px;
}

.button-row .el-button {
  min-width: 60px;
  text-align: center;
  padding: 4px 8px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #ffffff;
  color: #606266;
  transition: all 0.3s ease;
}

.button-row .el-button:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
  color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 深色主题适配 */
html.dark .button-row .el-button {
  background-color: #2d2d2d;
  border-color: #4c4d4f;
  color: #e5eaf3;
}

html.dark .button-row .el-button:hover {
  background-color: #3a3a3a;
  border-color: #606266;
  color: #409eff;
}

/* 统计卡片样式 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: var(--el-color-primary);
  margin-bottom: 8px;
}

.stat-label {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

</style>
