import api from './index'

/**
 * 用户相关API
 */
export const userApi = {
  /**
   * 用户登录
   * @param {Object} loginData - 登录数据
   * @param {string} loginData.username - 用户名
   * @param {string} loginData.password - 密码
   * @returns {Promise} 登录响应
   */
  login(loginData) {
    return api.post('/users/login', {
      usernameOrEmail: loginData.username,
      password: loginData.password
    })
  },

  /**
   * 用户注册
   * @param {Object} registerData - 注册数据
   * @param {string} registerData.username - 用户名
   * @param {string} registerData.email - 邮箱
   * @param {string} registerData.password - 密码
   * @param {string} registerData.fullName - 全名
   * @returns {Promise} 注册响应
   */
  register(registerData) {
    return api.post('/users/register', registerData)
  },

  /**
   * 获取当前用户信息
   * @returns {Promise} 用户信息
   */
  getCurrentUser() {
    return api.get('/users/me')
  },

  /**
   * 更新用户信息
   * @param {Object} userData - 用户数据
   * @returns {Promise} 更新响应
   */
  updateUser(userData) {
    return api.put('/users/me', userData)
  },

  /**
   * 修改密码
   * @param {Object} passwordData - 密码数据
   * @param {string} passwordData.oldPassword - 旧密码
   * @param {string} passwordData.newPassword - 新密码
   * @returns {Promise} 修改响应
   */
  changePassword(passwordData) {
    return api.put('/users/me/password', passwordData)
  },

  /**
   * 刷新token
   * @param {string} refreshToken - 刷新token
   * @returns {Promise} 刷新响应
   */
  refreshToken(refreshToken) {
    return api.post('/users/refresh', refreshToken)
  },

  /**
   * 获取用户详情
   * @param {number} userId - 用户ID
   * @returns {Promise} 用户详情
   */
  getUserById(userId) {
    return api.get(`/users/${userId}`)
  }
}

/**
 * 测试相关API
 */
export const testApi = {
  /**
   * 健康检查
   * @returns {Promise} 健康状态
   */
  health() {
    return api.get('/test/health')
  }
}

export default userApi
