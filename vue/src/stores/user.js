import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || 'user')
  const isAdmin = computed(() => userRole.value === 'admin')
  const isDeveloper = computed(() => userRole.value === 'developer')

  // 初始化用户信息
  const initUserInfo = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        clearUserInfo()
      }
    }
  }

  // 保存用户信息到localStorage
  const saveUserInfo = (user, accessToken, refreshToken) => {
    token.value = accessToken
    userInfo.value = user
    
    localStorage.setItem('token', accessToken)
    localStorage.setItem('userInfo', JSON.stringify(user))
    localStorage.setItem('userRole', user.role)
    
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken)
    }
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = null
    
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userRole')
    localStorage.removeItem('refreshToken')
  }

  // 用户登录
  const login = async (loginData) => {
    try {
      loading.value = true
      const response = await userApi.login(loginData)

      if (response.data) {
        // 后端返回的数据结构: { token, refreshToken, user, expiresIn }
        const { user, token, refreshToken } = response.data
        saveUserInfo(user, token, refreshToken)
        ElMessage.success(`欢迎回来，${user.fullName || user.username}！`)
        return { success: true, user }
      } else {
        throw new Error('登录响应数据格式错误')
      }
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败，请重试')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  // 用户注册
  const register = async (registerData) => {
    try {
      loading.value = true
      const response = await userApi.register(registerData)
      
      if (response.data) {
        ElMessage.success('注册成功！请登录')
        return { success: true, data: response.data }
      } else {
        throw new Error('注册响应数据格式错误')
      }
    } catch (error) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败，请重试')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    if (!token.value) return null

    try {
      const response = await userApi.getCurrentUser()
      if (response.data) {
        userInfo.value = response.data
        localStorage.setItem('userInfo', JSON.stringify(response.data))
        localStorage.setItem('userRole', response.data.role)
        return response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果token无效，清除用户信息
      if (error.response?.status === 401) {
        clearUserInfo()
      }
      return null
    }
  }

  // 更新用户信息
  const updateUser = async (userData) => {
    try {
      loading.value = true
      const response = await userApi.updateUser(userData)
      
      if (response.data) {
        userInfo.value = response.data
        localStorage.setItem('userInfo', JSON.stringify(response.data))
        ElMessage.success('用户信息更新成功')
        return { success: true, user: response.data }
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      ElMessage.error(error.message || '更新失败，请重试')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  // 修改密码
  const changePassword = async (passwordData) => {
    try {
      loading.value = true
      await userApi.changePassword(passwordData)
      ElMessage.success('密码修改成功')
      return { success: true }
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改密码失败，请重试')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  // 用户登出
  const logout = () => {
    clearUserInfo()
    ElMessage.success('已安全退出')
  }

  // 刷新token
  const refreshToken = async () => {
    const refreshTokenValue = localStorage.getItem('refreshToken')
    if (!refreshTokenValue) return false

    try {
      const response = await userApi.refreshToken(refreshTokenValue)
      if (response.data) {
        // 后端返回的数据结构: { token, refreshToken, user, expiresIn }
        const { user, token, refreshToken: newRefreshToken } = response.data
        saveUserInfo(user, token, newRefreshToken)
        return true
      }
    } catch (error) {
      console.error('刷新token失败:', error)
      clearUserInfo()
      return false
    }
  }

  // 初始化
  initUserInfo()

  return {
    // 状态
    token,
    userInfo,
    loading,
    
    // 计算属性
    isLoggedIn,
    userRole,
    isAdmin,
    isDeveloper,
    
    // 方法
    login,
    register,
    logout,
    fetchCurrentUser,
    updateUser,
    changePassword,
    refreshToken,
    clearUserInfo
  }
})
