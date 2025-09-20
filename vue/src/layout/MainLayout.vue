<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarCollapsed ? '64px' : '240px'" class="sidebar">
      <div class="sidebar-header">
        <div v-if="!sidebarCollapsed" class="logo">
          <el-icon size="32" color="#1890ff"><GameIcon /></el-icon>
          <span>GameVault</span>
        </div>
        <div v-else class="logo-mini">
          <el-icon size="32" color="#1890ff"><GameIcon /></el-icon>
        </div>
      </div>
      
      <el-menu
        :default-active="$route.path"
        :collapse="sidebarCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >

        <el-sub-menu index="assets">
          <template #title>
            <el-icon><Files /></el-icon>
            <span>资产管理</span>
          </template>
          <el-menu-item index="/assets">资产列表</el-menu-item>
          <el-menu-item index="/assets/upload">上传资产</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="projects">
          <template #title>
            <el-icon><GameIcon /></el-icon>
            <span>项目管理</span>
          </template>
          <el-menu-item index="/projects">项目列表</el-menu-item>
          <el-menu-item index="/projects/create">创建项目</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu v-if="hasPermission(['admin'])" index="users">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/users">用户列表</el-menu-item>
          <el-menu-item index="/permissions">权限管理</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item v-if="hasPermission(['admin', 'manager'])" index="/audit">
          <el-icon><DocumentChecked /></el-icon>
          <template #title>审核管理</template>
        </el-menu-item>

        <!-- 系统设置 -->
        <el-menu-item v-if="hasPermission(['admin', 'manager'])" index="/system/settings">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>


      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            :icon="sidebarCollapsed ? Expand : Fold"
            @click="toggleSidebar"
          />
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 搜索 -->
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索资产、项目..."
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
            />
          </div>

          <!-- 主题切换 -->
          <ThemeToggle />
          
          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">
                {{ userInfo.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>


  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import ThemeToggle from '../components/ThemeToggle.vue'
import {
  Odometer,
  Files,
  Folder,
  User,
  DocumentChecked,
  Document,
  Expand,
  Fold,
  Search,
  ArrowDown,
  Setting
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

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 响应式数据
const sidebarCollapsed = ref(false)
const searchKeyword = ref('')

// 用户信息
const userInfo = reactive({
  username: 'admin',
  avatar: '',
  role: 'admin'
})

// 从localStorage获取用户信息
const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsed = JSON.parse(storedUserInfo)
    Object.assign(userInfo, {
      username: parsed.username || parsed.nickname || 'admin',
      avatar: parsed.avatar || '',
      role: parsed.role || 'admin'
    })
  }
}

// 权限检查
const hasPermission = (requiredRoles) => {
  if (!requiredRoles || requiredRoles.length === 0) return true
  const currentRole = userStore.userRole || userInfo.role || 'user'
  console.log('权限检查:', { currentRole, requiredRoles, hasPermission: requiredRoles.includes(currentRole) })
  return requiredRoles.includes(currentRole)
}

// 初始化用户信息
loadUserInfo()



// 面包屑导航
const breadcrumbs = computed(() => {
  const pathSegments = route.path.split('/').filter(Boolean)
  const breadcrumbMap = {
    dashboard: '仪表盘',
    assets: '资产管理',
    upload: '上传资产',
    projects: '项目管理',
    create: '创建项目',
    users: '用户列表',
    permissions: '权限管理',
    audit: '审核管理',
    settings: '设置',
    logs: '系统日志'
  }
  
  const breadcrumbs = [{ title: '首页', path: '/dashboard' }]
  
  let currentPath = ''
  pathSegments.forEach(segment => {
    currentPath += `/${segment}`
    if (breadcrumbMap[segment]) {
      breadcrumbs.push({
        title: breadcrumbMap[segment],
        path: currentPath
      })
    }
  })
  
  return breadcrumbs
})

// 监听路由变化，自动收起移动端侧边栏
watch(route, () => {
  if (window.innerWidth <= 768) {
    sidebarCollapsed.value = true
  }
})

// 事件处理
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/search?q=${encodeURIComponent(searchKeyword.value)}`)
  }
}



const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '确认退出',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 使用userStore的logout方法
        userStore.logout()

        // 立即跳转到登录页面
        await router.push('/auth/login')

        // 强制刷新页面确保状态清除
        window.location.reload()
      } catch {
        // 用户取消
      }
      break
  }
}



// 响应式处理
const handleResize = () => {
  if (window.innerWidth <= 768) {
    sidebarCollapsed.value = true
  }
}

// 监听窗口大小变化
window.addEventListener('resize', handleResize)
</script>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
  width: 100%;
  max-width: 100vw;
}

.sidebar {
  background: var(--el-bg-color-page);
  transition: width 0.3s ease, background-color 0.3s;
  overflow: hidden;
  flex-shrink: 0;
  border-right: 1px solid var(--el-border-color);
  min-width: 64px;
}

/* 深色模式下的侧边栏背景 */
:global(.dark) .sidebar {
  background: var(--el-bg-color, #141414);
  border-right-color: var(--el-border-color-darker);
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 24px;
  border-bottom: 1px solid var(--el-border-color-darker, #1f1f1f);
}

.logo {
  display: flex;
  align-items: center;
  color: var(--el-text-color-primary, white);
  font-size: 18px;
  font-weight: 600;
}

.logo span {
  margin-left: 8px;
}

.logo-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-primary, white);
  width: 100%;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.logo-mini img {
  width: 32px;
  height: 32px;
}

.sidebar-menu {
  border: none;
  background: var(--el-bg-color-page);
  width: 100%;
  transition: all 0.3s ease;
}

/* 收缩状态下的菜单样式 */
.sidebar-menu.el-menu--collapse {
  width: 64px;
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item) {
  padding: 0 20px !important;
  text-align: center;
  justify-content: center;
}

.sidebar-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  padding: 0 20px !important;
  text-align: center;
  justify-content: center;
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item .el-icon) {
  margin-right: 0 !important;
}

.sidebar-menu.el-menu--collapse :deep(.el-sub-menu__title .el-icon) {
  margin-right: 0 !important;
}

/* 收缩状态下隐藏子菜单箭头 */
.sidebar-menu.el-menu--collapse :deep(.el-sub-menu__icon-arrow) {
  display: none;
}

/* 深色模式下的菜单背景 */
:global(.dark) .sidebar-menu {
  background: var(--el-bg-color, #141414);
}

/* 浅色模式下的菜单项样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: var(--el-text-color-primary);
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: var(--el-color-primary);
}

:deep(.el-menu-item.is-active) {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

/* 深色模式下的菜单项样式 */
:global(.dark) :deep(.el-menu-item),
:global(.dark) :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.85);
}

:global(.dark) :deep(.el-menu-item:hover),
:global(.dark) :deep(.el-sub-menu__title:hover) {
  color: #fff;
}

:global(.dark) :deep(.el-menu-item.is-active) {
  color: #fff;
  background-color: var(--el-color-primary);
}

/* 深色模式下的侧边栏样式优化 */
:global(.dark) .sidebar-header {
  border-bottom-color: var(--el-border-color, #414243);
}

/* 收缩状态下的侧边栏头部对齐 */
.sidebar[style*="width: 64px"] .sidebar-header {
  justify-content: center;
  padding-left: 0;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
  width: 100%;
}

.header {
  background: var(--header-bg, white);
  border-bottom: 1px solid var(--el-border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  transition: background-color 0.3s;
  width: 100%;
  height: 64px;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 面包屑导航对齐调整 */
.header-left :deep(.el-breadcrumb) {
  margin-left: -8px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  width: 240px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-weight: 500;
  color: #262626;
}

.main-content {
  flex: 1;
  background: var(--content-bg, #f5f5f5);
  overflow-y: auto;
  transition: background-color 0.3s;
  min-height: 0;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}



/* 全局防溢出样式 */
:global(*) {
  box-sizing: border-box;
}

:global(html) {
  overflow-x: hidden;
  max-width: 100vw;
}

:global(body) {
  overflow-x: hidden;
  max-width: 100vw;
  margin: 0;
  padding: 0;
}

:global(#app) {
  overflow-x: hidden;
  max-width: 100vw;
}

:global(.el-container) {
  width: 100% !important;
  max-width: 100% !important;
  overflow-x: hidden !important;
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .header-left {
    gap: 8px;
  }
  
  .header-right {
    gap: 8px;
  }
  
  .search-box {
    width: 160px;
  }
  
  .username {
    display: none;
  }
  
  :deep(.el-breadcrumb) {
    display: none;
  }
}
</style>
