<template>
  <div class="project-list">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h1>项目管理</h1>
        <p>管理您的游戏开发项目</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="$router.push('/projects/create')">
          创建项目
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入项目名称或描述，按回车搜索"
            :prefix-icon="Search"
            clearable
            style="width: 280px"
            @keyup.enter="handleSearch"
            @input="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="进行中" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="已暂停" value="paused" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
            @change="handleSearch"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 项目列表 -->
    <div class="project-grid">
      <div v-for="project in projectList" :key="project.id" class="project-card">
        <el-card shadow="hover" @click="handleProjectClick(project)">
          <div class="project-header">
            <div class="project-avatar">
              <img v-if="project.avatar" :src="project.avatar" :alt="project.name" />
              <el-icon v-else size="32" color="#1890ff">
                <Folder />
              </el-icon>
            </div>
            <div class="project-info">
              <h3 class="project-name">{{ project.name }}</h3>
              <p class="project-desc">{{ project.description }}</p>
            </div>
            <el-dropdown @command="handleCommand" @click.stop>
              <el-button type="text" :icon="MoreFilled" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="`edit-${project.id}`">编辑</el-dropdown-item>
                  <el-dropdown-item :command="`members-${project.id}`">成员管理</el-dropdown-item>
                  <el-dropdown-item :command="`settings-${project.id}`">项目设置</el-dropdown-item>
                  <el-dropdown-item divided :command="`delete-${project.id}`">删除项目</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <div class="project-stats">
            <div class="stat-item">
              <el-icon color="#1890ff"><Files /></el-icon>
              <span>{{ project.assetCount }} 个资产</span>
            </div>
            <div class="stat-item">
              <el-icon color="#52c41a"><User /></el-icon>
              <span>{{ project.memberCount }} 个成员</span>
            </div>
            <div class="stat-item">
              <el-icon color="#faad14"><Clock /></el-icon>
              <span>{{ project.lastActivity }}</span>
            </div>
          </div>
          
          <div class="project-footer">
            <div class="project-status">
              <el-tag :type="getStatusTagType(project.status)">
                {{ getStatusName(project.status) }}
              </el-tag>
            </div>
            <div class="project-progress">
              <span class="progress-text">{{ project.progress }}%</span>
              <el-progress 
                :percentage="project.progress" 
                :show-text="false" 
                :stroke-width="4"
                :color="getProgressColor(project.progress)"
              />
            </div>
          </div>
          
          <div class="project-members">
            <div class="member-avatars">
              <el-avatar
                v-for="member in (project.members || []).slice(0, 3)"
                :key="member.id"
                :size="24"
                :src="member.avatar"
              >
                {{ member.name.charAt(0) }}
              </el-avatar>
              <span v-if="(project.members || []).length > 3" class="more-members">
                +{{ (project.members || []).length - 3 }}
              </span>
            </div>
            <div class="project-owner">
              <span>负责人: {{ project.owner }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[12, 24, 48]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Folder,
  Files,
  User,
  Clock,
  MoreFilled
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  dateRange: null
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0
})

// 项目列表
const projectList = ref([])

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    active: 'success',
    completed: 'info',
    paused: 'warning'
  }
  return statusMap[status] || ''
}

// 获取状态名称
const getStatusName = (status) => {
  const nameMap = {
    active: '进行中',
    completed: '已完成',
    paused: '已暂停'
  }
  return nameMap[status] || status
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress < 30) return '#ff4d4f'
  if (progress < 70) return '#faad14'
  return '#52c41a'
}

// 事件处理
const handleSearch = () => {
  pagination.currentPage = 1
  loadProjectList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    dateRange: null
  })
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadProjectList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadProjectList()
}

const handleProjectClick = (project) => {
  router.push(`/projects/${project.id}`)
}

const handleCommand = async (command) => {
  const [action, projectId] = command.split('-')
  
  switch (action) {
    case 'edit':
      ElMessage.info('编辑功能开发中...')
      break
    case 'members':
      ElMessage.info('成员管理功能开发中...')
      break
    case 'settings':
      ElMessage.info('项目设置功能开发中...')
      break
    case 'delete':
      try {
        await ElMessageBox.confirm(
          '确定要删除这个项目吗？\n\n注意：这是演示功能，项目会临时删除，刷新页面即可恢复。',
          '确认删除',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 800))

        // 从前端列表中临时移除
        const projectIndex = projects.value.findIndex(p => p.id === projectId)
        if (projectIndex > -1) {
          projects.value.splice(projectIndex, 1)
        }

        ElMessage.success('删除成功！刷新页面可恢复数据')
      } catch {
        // 用户取消删除
      }
      break
  }
}

// 加载项目列表
const loadProjectList = async () => {
  loading.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    // 从localStorage读取项目数据
    const storedProjects = JSON.parse(localStorage.getItem('projects') || '[]')

    // 合并存储的项目和默认模拟数据
    const defaultProjects = [
      {
        id: 1,
        name: 'RPG冒险游戏',
        description: '一款开放世界的角色扮演游戏，包含丰富的剧情和战斗系统',
        avatar: '',
        status: 'active',
        progress: 75,
        assetCount: 156,
        memberCount: 8,
        lastActivity: '2小时前',
        owner: '张三',
        members: [
          { id: 1, name: '张三', avatar: '' },
          { id: 2, name: '李四', avatar: '' },
          { id: 3, name: '王五', avatar: '' },
          { id: 4, name: '赵六', avatar: '' }
        ]
      },
      {
        id: 2,
        name: '射击游戏资产包',
        description: '现代战争题材的射击游戏资产，包含武器、角色、场景等',
        avatar: '',
        status: 'active',
        progress: 45,
        assetCount: 89,
        memberCount: 5,
        lastActivity: '1天前',
        owner: '李四',
        members: [
          { id: 2, name: '李四', avatar: '' },
          { id: 5, name: '钱七', avatar: '' },
          { id: 6, name: '孙八', avatar: '' }
        ]
      },
      {
        id: 3,
        name: '卡通风格UI',
        description: '适用于休闲游戏的卡通风格用户界面设计',
        avatar: '',
        status: 'completed',
        progress: 100,
        assetCount: 67,
        memberCount: 3,
        lastActivity: '3天前',
        owner: '王五',
        members: [
          { id: 3, name: '王五', avatar: '' },
          { id: 7, name: '周九', avatar: '' }
        ]
      }
    ]

    // 合并存储的项目和默认项目
    let allProjects = [...storedProjects, ...defaultProjects]

    // 应用搜索筛选
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      allProjects = allProjects.filter(project =>
        project.name.toLowerCase().includes(keyword) ||
        project.description.toLowerCase().includes(keyword)
      )
    }

    // 应用状态筛选
    if (searchForm.status) {
      allProjects = allProjects.filter(project => project.status === searchForm.status)
    }

    // 应用日期范围筛选
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      const [startDate, endDate] = searchForm.dateRange
      allProjects = allProjects.filter(project => {
        // 这里可以根据实际的创建时间字段进行筛选
        // 由于模拟数据没有具体的创建时间，这里先保留逻辑框架
        return true
      })
    }

    projectList.value = allProjects
    pagination.total = allProjects.length
    
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProjectList()
})
</script>

<style scoped>
.project-list {
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

.filter-card {
  margin-bottom: 24px;
}

.search-form {
  margin: 0;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.project-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.project-card:hover {
  transform: translateY(-2px);
}

.project-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.project-avatar {
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

.project-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-info {
  flex: 1;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 4px 0;
}

.project-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #595959;
}

.stat-item .el-icon {
  margin-right: 8px;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.project-progress {
  flex: 1;
  margin-left: 16px;
}

.progress-text {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
  display: block;
}

.project-members {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.member-avatars {
  display: flex;
  align-items: center;
  gap: 4px;
}

.more-members {
  font-size: 12px;
  color: #8c8c8c;
  margin-left: 4px;
}

.project-owner {
  font-size: 12px;
  color: #8c8c8c;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .project-list {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .project-grid {
    grid-template-columns: 1fr;
  }
  
  .project-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .project-progress {
    width: 100%;
    margin-left: 0;
  }
}
</style>
