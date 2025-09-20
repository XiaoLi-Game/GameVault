<template>
  <div class="login-form">
    <div class="form-header">
      <h2>登录</h2>
      <p>欢迎回来，请登录您的账户</p>
    </div>
    
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      size="large"
      @submit.prevent="handleLogin"
    >
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
          clearable
        />
      </el-form-item>
      
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
          clearable
          @keyup.enter="handleLogin"
        />
      </el-form-item>
      
      <el-form-item>
        <div class="form-options">
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <router-link to="/forgot-password" class="forgot-link">
            忘记密码？
          </router-link>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          size="large"
          style="width: 100%"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>
    
    <div class="form-footer">
      <span>还没有账户？</span>
      <router-link to="/register" class="register-link">
        立即注册
      </router-link>
    </div>

    <!-- 测试账号提示 -->
    <div class="demo-accounts">
      <h4>🎮 测试账号</h4>
      <div class="account-list">
        <div class="account-item" @click="quickLogin('admin', '123')">
          <strong>管理员：</strong>admin / 123
          <span class="quick-login">点击快速登录</span>
        </div>
        <div class="account-item" @click="quickLogin('user', '123')">
          <strong>普通用户：</strong>user / 123
          <span class="quick-login">点击快速登录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则 - 简化版
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度在 3 到 20 个字符', trigger: 'blur' }
  ]
}



// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用真实的登录API
    const result = await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (result.success) {
      // 登录成功，跳转到仪表板
      router.push('/dashboard')
    }
    // 错误消息已在store中处理

  } catch (error) {
    console.error('登录处理错误:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 快速登录
const quickLogin = (username, password) => {
  loginForm.username = username
  loginForm.password = password
  handleLogin()
}
</script>

<style scoped>
.login-form {
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
  margin: 0 0 12px 0;
}

.form-header p {
  color: #8c8c8c;
  margin: 0;
  font-size: 16px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  color: #40a9ff;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: #8c8c8c;
}

.register-link {
  color: #1890ff;
  text-decoration: none;
  margin-left: 4px;
}

.register-link:hover {
  color: #40a9ff;
}

.demo-accounts {
  margin-top: 32px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.demo-accounts h4 {
  margin: 0 0 16px 0;
  color: #1890ff;
  font-size: 16px;
  font-weight: 600;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-item {
  font-size: 14px;
  color: #666;
  font-family: 'Courier New', monospace;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.account-item:hover {
  background: #e6f7ff;
  color: #1890ff;
}

.account-item strong {
  color: #333;
  font-weight: 600;
}

.account-item:hover strong {
  color: #1890ff;
}

.quick-login {
  font-size: 12px;
  color: #1890ff;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.account-item:hover .quick-login {
  opacity: 1;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
}
</style>
