<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>仪表盘</h1>
      <p>欢迎回来，{{ userInfo.username }}！</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total-assets">
              <el-icon size="32"><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalAssets }}</div>
              <div class="stat-label">总资产数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total-projects">
              <el-icon size="32"><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalProjects }}</div>
              <div class="stat-label">项目数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending-review">
              <el-icon size="32"><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingReview }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon storage-used">
              <el-icon size="32"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.storageUsed }}</div>
              <div class="stat-label">存储使用</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表和列表区域 -->
    <el-row :gutter="16" class="content-row">
      <!-- 最近活动 -->
      <el-col :xs="24" :lg="12">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="text" size="small" @click="viewAllActivities">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <el-icon :color="activity.iconColor">
                  <component :is="getActivityIcon(activity.icon)" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 项目概览 -->
      <el-col :xs="24" :lg="12">
        <el-card class="project-card">
          <template #header>
            <div class="card-header">
              <span>项目概览</span>
              <el-button type="text" size="small" @click="$router.push('/projects')">
                查看全部
              </el-button>
            </div>
          </template>
          <div class="project-list">
            <div v-for="project in recentProjects" :key="project.id" class="project-item">
              <div class="project-info">
                <div class="project-name">{{ project.name }}</div>
                <div class="project-desc">{{ project.description }}</div>
              </div>
              <div class="project-stats">
                <el-tag size="small">{{ project.assetCount }} 个资产</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-row :gutter="16" class="quick-actions">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快速操作</span>
          </template>
          <div class="action-buttons">
            <el-button type="primary" :icon="Upload" @click="$router.push('/assets/upload')">
              上传资产
            </el-button>
            <el-button type="success" :icon="FolderAdd" @click="$router.push('/projects/create')">
              创建项目
            </el-button>
            <el-button type="warning" :icon="DocumentChecked" @click="$router.push('/audit')">
              审核管理
            </el-button>
            <el-button type="info" :icon="Files" @click="$router.push('/assets')">
              资产管理
            </el-button>
            <el-button type="default" :icon="Folder" @click="$router.push('/projects')">
              项目管理
            </el-button>
            <el-button type="danger" :icon="User" @click="$router.push('/users')">
              用户管理
            </el-button>
            <el-button type="primary" plain :icon="Lock" @click="$router.push('/permissions')">
              权限管理
            </el-button>

          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新资产表格 -->
    <el-row :gutter="16" class="table-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新资产</span>
              <el-button type="text" @click="$router.push('/assets')">查看全部</el-button>
            </div>
          </template>
          <el-table
            :data="recentAssets"
            stripe
            style="width: 100%"
            @row-click="handleRowClick"
            class="dashboard-asset-table"
          >
            <el-table-column prop="name" label="资产名称" min-width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getTypeTagType(scope.row.type)">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="大小" width="80" />
            <el-table-column prop="project" label="所属项目" min-width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="uploadTime" label="上传时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { h } from 'vue'
import { useUserStore } from '@/stores/user'
import { assetApi } from '@/api/asset'
import {
  Files,
  Folder,
  DocumentChecked,
  Coin,
  Upload,
  FolderAdd,
  Plus,
  User,
  Document,
  Delete
} from '@element-plus/icons-vue'

// 自定义游戏手柄图标组件 - 经典游戏手柄设计
const GameIcon = {
  name: 'GameIcon',
  render() {
    return h('svg', {
      xmlns: 'http://www.w3.org/2000/svg',
      viewBox: '0 0 24 24',
      fill: 'currentColor'
    }, [
      // 主体手柄形状
      h('path', {
        d: 'M17.5 8C19.43 8 21 9.57 21 11.5V16.5C21 18.43 19.43 20 17.5 20H16C15.45 20 15 19.55 15 19V13H9V19C9 19.55 8.55 20 8 20H6.5C4.57 20 3 18.43 3 16.5V11.5C3 9.57 4.57 8 6.5 8H8.5L10.5 6C11.33 5.17 12.67 5.17 13.5 6L15.5 8H17.5Z'
      }),
      // 左侧十字方向键
      h('rect', {
        x: '5.5',
        y: '11',
        width: '1',
        height: '3',
        rx: '0.5',
        fill: 'white'
      }),
      h('rect', {
        x: '4.5',
        y: '12',
        width: '3',
        height: '1',
        rx: '0.5',
        fill: 'white'
      }),
      // 右侧按钮
      h('circle', {
        cx: '17',
        cy: '11.5',
        r: '0.8',
        fill: 'white'
      }),
      h('circle', {
        cx: '18.5',
        cy: '13',
        r: '0.8',
        fill: 'white'
      }),
      h('circle', {
        cx: '17',
        cy: '14.5',
        r: '0.8',
        fill: 'white'
      }),
      h('circle', {
        cx: '15.5',
        cy: '13',
        r: '0.8',
        fill: 'white'
      })
    ])
  }
}

// 用户状态管理
const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = reactive({
  username: '用户',
  avatar: '',
  fullName: '',
  role: 'user'
})

// 统计数据
const stats = reactive({
  totalAssets: 1248,
  totalProjects: 23,
  pendingReview: 8,
  storageUsed: '2.3GB'
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    title: '创建了新项目 "RPG游戏资产包"',
    time: '1小时前',
    icon: 'GameIcon',
    iconColor: '#52c41a'
  },
  {
    id: 2,
    title: '审核通过资产 "场景贴图_森林.png"',
    time: '3小时前',
    icon: 'DocumentChecked',
    iconColor: '#faad14'
  },
  {
    id: 3,
    title: '删除了资产 "旧版本_音效.wav"',
    time: '1天前',
    icon: 'Delete',
    iconColor: '#ff4d4f'
  }
])

// 最近项目
const recentProjects = ref([
  {
    id: 1,
    name: 'RPG冒险游戏',
    description: '包含角色、场景、道具等完整资产',
    assetCount: 156
  },
  {
    id: 2,
    name: '射击游戏资产包',
    description: '现代战争题材的游戏资产',
    assetCount: 89
  },
  {
    id: 3,
    name: '卡通风格UI',
    description: '适用于休闲游戏的UI资产',
    assetCount: 67
  }
])

// 最新资产数据
const recentAssets = ref([
  {
    id: 1,
    name: 'character_warrior.fbx',
    type: '3D模型',
    size: '2.3MB',
    project: '角色扮演游戏',
    status: '已审核',
    uploadTime: '2024-01-15 14:30'
  },
  {
    id: 2,
    name: 'background_music.mp3',
    type: '音频',
    size: '4.1MB',
    project: '休闲益智游戏',
    status: '待审核',
    uploadTime: '2024-01-15 13:45'
  },
  {
    id: 3,
    name: 'ui_button_set.png',
    type: '图片',
    size: '512KB',
    project: '动作冒险游戏',
    status: '已审核',
    uploadTime: '2024-01-15 12:20'
  },
  {
    id: 4,
    name: 'explosion_effect.vfx',
    type: '特效',
    size: '1.8MB',
    project: '动作冒险游戏',
    status: '审核中',
    uploadTime: '2024-01-15 11:15'
  },
  {
    id: 5,
    name: 'level_script.js',
    type: '脚本',
    size: '45KB',
    project: '角色扮演游戏',
    status: '已审核',
    uploadTime: '2024-01-15 10:30'
  }
])

// 表格相关方法
const getTypeTagType = (type) => {
  const typeMap = {
    '3D模型': 'primary',
    '音频': 'success',
    '图片': 'warning',
    '特效': 'danger',
    '脚本': 'info'
  }
  return typeMap[type] || ''
}

const getStatusTagType = (status) => {
  const statusMap = {
    '已审核': 'success',
    '待审核': 'warning',
    '审核中': 'primary',
    '已拒绝': 'danger'
  }
  return statusMap[status] || ''
}

// 处理表格行点击事件
const handleRowClick = (row) => {
  // 点击行跳转到资产详情页面
  router.push(`/assets/${row.id}`)
}

// 获取活动图标
const getActivityIcon = (iconName) => {
  const iconMap = {
    'GameIcon': GameIcon,
    'DocumentChecked': DocumentChecked,
    'Delete': Delete,
    'FolderAdd': FolderAdd
  }
  return iconMap[iconName] || Files
}

// 工具函数
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusDisplayName = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已审核',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

const viewAllActivities = () => {
  // 创建一个活动页面或者显示更多活动的对话框
  ElMessageBox.alert(
    `
    <div style="max-height: 400px; overflow-y: auto; margin: 0; padding: 0;">
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #52c41a; background: #f6ffed; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">📂</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #52c41a; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">创建了新项目 "RPG游戏资产包"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">1小时前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #faad14; background: #fffbe6; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">🖼️</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #faad14; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">审核通过资产 "场景贴图_森林.png"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">3小时前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #f56c6c; background: #fef0f0; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">🗑️</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #f56c6c; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">删除了资产 "旧版本_音效.wav"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">1天前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #409eff; background: #f0f9ff; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">👥</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #409eff; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">邀请用户加入项目 "移动端游戏"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">2天前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #52c41a; background: #f6ffed; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">✅</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #52c41a; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">完成资产审核 "角色动画_跑步.fbx"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">3天前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #722ed1; background: #f9f0ff; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">🔧</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #722ed1; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">更新了项目设置 "游戏音效库"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">5天前</div>
        </div>
      </div>
      <div style="margin-bottom: 12px; padding: 16px; border-left: 4px solid #13c2c2; background: #e6fffb; border-radius: 6px; min-height: 64px; display: flex; align-items: center;">
        <div style="width: 24px; margin-right: 12px; font-size: 16px; line-height: 1; display: flex; align-items: center; justify-content: flex-start; text-align: left;">📊</div>
        <div style="flex: 1;">
          <div style="font-weight: 500; color: #13c2c2; margin-bottom: 4px; font-size: 14px; line-height: 1.4;">生成了项目报告 "月度资产统计"</div>
          <div style="font-size: 12px; color: #8c8c8c; line-height: 1.2;">1周前</div>
        </div>
      </div>
    </div>
    `,
    '全部活动记录',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭',
      customClass: 'activity-dialog'
    }
  )
}

onMounted(async () => {
  // 获取用户信息
  if (userStore.userInfo) {
    Object.assign(userInfo, userStore.userInfo)
  } else {
    // 如果store中没有用户信息，尝试从API获取
    const user = await userStore.fetchCurrentUser()
    if (user) {
      Object.assign(userInfo, user)
    }
  }

  // 加载仪表板数据
  loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    console.log('加载仪表板数据...')

    // 获取真实的资产数据用于仪表板显示
    const response = await assetApi.getAssets({ page: 1, size: 5 })
    if (response.data && response.data.list) {
      // 更新最新资产数据为真实数据
      recentAssets.value = response.data.list.map(asset => ({
        id: asset.id,
        name: asset.name,
        type: asset.category || '未知',
        size: formatFileSize(asset.fileSize),
        project: '项目名称', // 可以后续从项目API获取
        status: getStatusDisplayName(asset.status),
        uploadTime: formatDate(asset.createdAt)
      }))
    }
  } catch (error) {
    console.error('加载数据失败:', error)

    // 如果API失败，使用模拟数据
    recentAssets.value = [
      {
        id: 1001,
        name: 'character_warrior.fbx',
        type: '3D模型',
        size: '15.0 MB',
        project: '冒险游戏',
        status: '已审核',
        uploadTime: '2024-01-15 14:30'
      },
      {
        id: 1002,
        name: 'forest_texture_pack.zip',
        type: '贴图',
        size: '50.0 MB',
        project: '冒险游戏',
        status: '已审核',
        uploadTime: '2024-01-14 14:20'
      },
      {
        id: 1003,
        name: 'battle_music.mp3',
        type: '音频',
        size: '8.0 MB',
        project: '射击游戏',
        status: '待审核',
        uploadTime: '2024-01-13 09:15'
      },
      {
        id: 1004,
        name: 'player_controller.cs',
        type: '脚本',
        size: '12.0 KB',
        project: '角色扮演',
        status: '已审核',
        uploadTime: '2024-01-12 16:45'
      },
      {
        id: 1005,
        name: 'sword_animation.anim',
        type: '动画',
        size: '2.0 MB',
        project: '冒险游戏',
        status: '已审核',
        uploadTime: '2024-01-11 11:30'
      }
    ]

    console.log('✅ 仪表板使用模拟数据')
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  overflow-x: hidden;
  margin: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary, #262626);
  margin: 0 0 8px 0;
  transition: color 0.3s ease;
}

.page-header p {
  color: var(--el-text-color-regular, #8c8c8c);
  margin: 0;
  transition: color 0.3s ease;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-primary);
}

/* 深色主题下的悬停效果 */
:global(.dark) .stat-card:hover {
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-icon.total-assets {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-icon.total-projects {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-icon.pending-review {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-icon.storage-used {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-info {
  transition: all 0.3s ease;
}

.stat-card:hover .stat-info {
  transform: translateX(4px);
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary, #262626);
  line-height: 1;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-number {
  color: var(--el-color-primary);
  transform: scale(1.05);
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-regular, #8c8c8c);
  margin-top: 4px;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-label {
  color: var(--el-text-color-primary);
}

.content-row {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}



.activity-list {
  max-height: 300px;
  overflow: hidden;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin: 0 -16px;
  border-bottom: 1px solid var(--el-border-color-lighter, #f0f0f0);
  transition: all 0.3s ease;
}

.activity-item:hover {
  background-color: var(--el-fill-color-lighter, #fafafa);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 14px;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  transition: color 0.3s ease;
  line-height: 1.4;
}

.activity-time {
  font-size: 12px;
  color: var(--el-text-color-regular);
  transition: color 0.3s ease;
}

.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  margin: 0 -16px;
  border-bottom: 1px solid var(--el-border-color-lighter, #f0f0f0);
  transition: all 0.3s ease;
}

.project-item:hover {
  background-color: var(--el-fill-color-lighter, #fafafa);
}

.project-item:last-child {
  border-bottom: none;
}

.project-info {
  flex: 1;
  min-width: 0;
}

.project-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  transition: color 0.3s ease;
  line-height: 1.4;
}

.project-desc {
  font-size: 12px;
  color: var(--el-text-color-regular);
  transition: color 0.3s ease;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
  align-items: center;
}

.action-buttons .el-button {
  height: 40px;
  font-size: 14px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 表格中的操作按钮样式 */
.table-action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
}

/* 操作按钮基础样式 */
.action-btn {
  border: 1px solid var(--el-border-color) !important;
  background-color: var(--el-bg-color) !important;
  color: var(--el-text-color-primary) !important;
  font-size: 12px !important;
  padding: 4px 8px !important;
  min-width: 40px !important;
  height: 28px !important;
  line-height: 1 !important;
  border-radius: 4px !important;
  transition: all 0.3s ease;
  text-align: center;
  white-space: nowrap;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 查看按钮 */
.view-btn {
  border-color: #409eff !important;
  color: #409eff !important;
}

.view-btn:hover {
  background-color: #ecf5ff !important;
  border-color: #409eff !important;
  color: #409eff !important;
}

/* 编辑按钮 */
.edit-btn {
  border-color: #67c23a !important;
  color: #67c23a !important;
}

.edit-btn:hover {
  background-color: #f0f9ff !important;
  border-color: #67c23a !important;
  color: #67c23a !important;
}

/* 删除按钮 */
.delete-btn {
  border-color: #f56c6c !important;
  color: #f56c6c !important;
}

.delete-btn:hover {
  background-color: #fef0f0 !important;
  border-color: #f56c6c !important;
  color: #f56c6c !important;
}

/* 深色主题适配 */
:global(.dark) .action-btn {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
}

:global(.dark) .view-btn:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:global(.dark) .edit-btn:hover {
  background-color: rgba(103, 194, 58, 0.1) !important;
}

:global(.dark) .delete-btn:hover {
  background-color: rgba(245, 108, 108, 0.1) !important;
}

/* 操作按钮样式 - 带边框 */
.action-btn {
  border: 1px solid var(--el-border-color) !important;
  background-color: var(--el-bg-color) !important;
  color: var(--el-text-color-primary) !important;
  font-size: 12px;
  padding: 4px 8px !important;
  min-width: 44px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  border-color: var(--el-color-primary) !important;
  color: var(--el-color-primary) !important;
}

.view-btn {
  border-color: #409eff !important;
  color: #409eff !important;
}

.view-btn:hover {
  background-color: #ecf5ff !important;
  border-color: #409eff !important;
}

.edit-btn {
  border-color: #67c23a !important;
  color: #67c23a !important;
}

.edit-btn:hover {
  background-color: #f0f9ff !important;
  border-color: #67c23a !important;
}

.delete-btn {
  border-color: #f56c6c !important;
  color: #f56c6c !important;
}

.delete-btn:hover {
  background-color: #fef0f0 !important;
  border-color: #f56c6c !important;
}

/* 深色主题适配 */
:global(.dark) .action-btn {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
}

:global(.dark) .view-btn:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:global(.dark) .edit-btn:hover {
  background-color: rgba(103, 194, 58, 0.1) !important;
}

:global(.dark) .delete-btn:hover {
  background-color: rgba(245, 108, 108, 0.1) !important;
}

.table-section {
  margin-bottom: 24px;
}

.table-section .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 全局响应式样式 */
:global(.el-row) {
  width: 100% !important;
  max-width: 100% !important;
  margin: 0 !important;
  box-sizing: border-box !important;
}

:global(.el-col) {
  max-width: 100% !important;
  box-sizing: border-box !important;
  padding-left: 8px !important;
  padding-right: 8px !important;
}

:global(.el-card) {
  width: 100% !important;
  max-width: 100% !important;
  box-sizing: border-box !important;
  margin: 0 !important;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .action-buttons .el-button {
    height: 36px;
    font-size: 13px;
  }

  .table-action-buttons {
    gap: 4px;
    justify-content: center;
  }

  .table-action-buttons .action-btn {
    min-width: 36px !important;
    padding: 4px 6px !important;
    font-size: 11px !important;
  }
}

/* 活动对话框样式优化 */
:global(.activity-dialog) {
  width: 480px !important;
  max-width: 90vw !important;
}

:global(.activity-dialog .el-message-box) {
  width: 480px !important;
  max-width: 90vw !important;
}

:global(.activity-dialog .el-message-box__content) {
  padding: 12px 16px !important;
  margin: 0 !important;
}

:global(.activity-dialog .el-message-box__message) {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
}

:global(.activity-dialog .el-message-box__message p) {
  margin: 0 !important;
  padding: 0 !important;
}

/* 深色主题适配 */
html.dark .activity-item:hover {
  background-color: var(--el-fill-color-darker, #2d2d2d);
}

html.dark .project-item:hover {
  background-color: var(--el-fill-color-darker, #2d2d2d);
}

/* 自定义滚动条样式 */
:global(.activity-dialog .el-message-box__message div:first-child) {
  scrollbar-width: thin;
  scrollbar-color: #c1c1c1 transparent;
  margin: 0 !important;
  padding: 0 !important;
}

:global(.activity-dialog .el-message-box__message div:first-child::-webkit-scrollbar) {
  width: 6px;
}

:global(.activity-dialog .el-message-box__message div:first-child::-webkit-scrollbar-track) {
  background: transparent;
}

:global(.activity-dialog .el-message-box__message div:first-child::-webkit-scrollbar-thumb) {
  background-color: #c1c1c1;
  border-radius: 3px;
}

:global(.activity-dialog .el-message-box__message div:first-child::-webkit-scrollbar-thumb:hover) {
  background-color: #a8a8a8;
}

/* 仪表板资产表格样式 */
.dashboard-asset-table :deep(.el-table__row) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.dashboard-asset-table :deep(.el-table__row:hover) {
  background-color: var(--el-table-row-hover-bg-color) !important;
}
</style>
