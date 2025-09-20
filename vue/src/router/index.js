import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import MainLayout from '../layout/MainLayout.vue'
import LoginLayout from '../layout/LoginLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 登录相关路由
    {
      path: '/auth',
      component: LoginLayout,
      meta: { requiresAuth: false },
      children: [
        {
          path: 'login',
          name: 'Login',
          component: () => import('../views/auth/LoginView.vue'),
          meta: { title: '登录' }
        },
        {
          path: 'register',
          name: 'Register',
          component: () => import('../views/auth/RegisterView.vue'),
          meta: { title: '注册' }
        },
        {
          path: 'forgot-password',
          name: 'ForgotPassword',
          component: () => import('../views/auth/ForgotPasswordView.vue'),
          meta: { title: '忘记密码' }
        }
      ]
    },
    // 重定向旧的登录路径
    {
      path: '/login',
      redirect: '/auth/login'
    },
    {
      path: '/register',
      redirect: '/auth/register'
    },
    {
      path: '/forgot-password',
      redirect: '/auth/forgot-password'
    },
    // 主应用路由
    {
      path: '/',
      component: MainLayout,
      redirect: '/assets',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/DashboardView.vue'),
          meta: {
            title: '仪表盘',
            icon: 'Odometer',
            requiresAuth: true
          }
        },
        // 资产管理
        {
          path: 'assets',
          name: 'Assets',
          component: () => import('../views/assets/AssetListView.vue'),
          meta: {
            title: '资产管理',
            icon: 'Files',
            requiresAuth: true
          }
        },
        {
          path: 'assets/upload',
          name: 'AssetUpload',
          component: () => import('../views/assets/AssetUploadView.vue'),
          meta: {
            title: '上传资产',
            icon: 'Upload',
            requiresAuth: true
          }
        },
        {
          path: 'assets/:id',
          name: 'AssetDetail',
          component: () => import('../views/assets/AssetDetailView.vue'),
          meta: {
            title: '资产详情',
            hidden: true,
            requiresAuth: true
          },
          props: true // 启用路由参数作为props传递
        },
        // 项目管理
        {
          path: 'projects',
          name: 'Projects',
          component: () => import('../views/projects/ProjectListView.vue'),
          meta: {
            title: '项目管理',
            icon: 'Folder',
            requiresAuth: true
          }
        },
        {
          path: 'projects/create',
          name: 'ProjectCreate',
          component: () => import('../views/projects/ProjectCreateView.vue'),
          meta: {
            title: '创建项目',
            hidden: true,
            requiresAuth: true
          }
        },
        {
          path: 'projects/:id',
          name: 'ProjectDetail',
          component: () => import('../views/projects/ProjectDetailView.vue'),
          meta: {
            title: '项目详情',
            hidden: true,
            requiresAuth: true
          },
          props: true // 启用路由参数作为props传递
        },
        // 用户管理
        {
          path: 'users',
          name: 'Users',
          component: () => import('../views/users/UserListView.vue'),
          meta: {
            title: '用户管理',
            icon: 'User',
            roles: ['admin', 'manager'],
            requiresAuth: true
          }
        },
        {
          path: 'users/:id',
          name: 'UserDetail',
          component: () => import('../views/users/UserDetailView.vue'),
          meta: {
            title: '用户详情',
            roles: ['admin', 'manager'],
            requiresAuth: true
          }
        },
        {
          path: 'permissions',
          name: 'UserPermissions',
          component: () => import('../views/users/UserPermissionView.vue'),
          meta: {
            title: '权限管理',
            roles: ['admin', 'manager'],
            requiresAuth: true
          }
        },
        // 审核管理
        {
          path: 'audit',
          name: 'Audit',
          component: () => import('../views/audit/AuditListView.vue'),
          meta: {
            title: '审核管理',
            icon: 'DocumentChecked',
            roles: ['admin', 'manager'],
            requiresAuth: true
          }
        },
        {
          path: 'audit/:id',
          name: 'AuditDetail',
          component: () => import('../views/audit/AuditDetailView.vue'),
          meta: {
            title: '审核详情',
            hidden: true,
            requiresAuth: true
          },
          props: true
        },

        // 系统设置
        {
          path: 'system/settings',
          name: 'SystemSettings',
          component: () => import('../views/settings/SystemSettingsView.vue'),
          meta: {
            title: '系统设置',
            icon: 'Setting',
            requiresAuth: true,
            roles: ['admin', 'manager']
          }
        },
        // 个人中心
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/ProfileView.vue'),
          meta: {
            title: '个人中心',
            hidden: true,
            requiresAuth: true
          }
        }
      ]
    },
    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFoundView.vue'),
      meta: { title: '页面未找到' }
    }
  ]
})

// 改进的路由守卫 - 符合Vue Router 4最佳实践
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - GameVault`
  }

  const userStore = useUserStore()

  // 检查是否需要身份验证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // 检查角色权限
  const requiredRoles = to.meta.roles
  const hasRequiredRole = !requiredRoles || requiredRoles.includes(userStore.userRole)

  // 如果是认证相关页面
  if (to.path.startsWith('/auth/')) {
    if (userStore.isLoggedIn) {
      // 已登录用户访问登录页面，重定向到仪表盘
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 如果需要身份验证但未登录
  if (requiresAuth && !userStore.isLoggedIn) {
    next('/auth/login')
    return
  }

  // 如果已登录但没有所需角色权限
  if (userStore.isLoggedIn && !hasRequiredRole) {
    console.log('权限检查失败:', {
      path: to.path,
      userRole: userStore.userRole,
      requiredRoles,
      hasRequiredRole
    })

    // 根据用户角色重定向到合适的页面
    if (userStore.userRole === 'user') {
      // 普通用户重定向到仪表盘
      ElMessage.warning('您没有权限访问该页面')
      next('/dashboard')
    } else {
      // 其他情况重定向到404
      next('/404')
    }
    return
  }

  next()
})

export default router
