<template>
  <div class="register-form">
    <div class="form-header">
      <h2>注册</h2>
      <p>创建您的GameVault账户</p>
    </div>
    
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      size="large"
      @submit.prevent="handleRegister"
    >
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
          clearable
        />
      </el-form-item>
      
      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="请输入邮箱地址（可选）"
          :prefix-icon="Message"
          clearable
        />
      </el-form-item>
      
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
          clearable
        />
      </el-form-item>
      
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
          :prefix-icon="Lock"
          show-password
          clearable
          @keyup.enter="handleRegister"
        />
      </el-form-item>
      
      <el-form-item prop="agree">
        <el-checkbox v-model="registerForm.agree">
          我已阅读并同意
          <a href="#" class="terms-link">《用户协议》</a>
          和
          <a href="#" class="terms-link">《隐私政策》</a>
        </el-checkbox>
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          size="large"
          style="width: 100%"
          :loading="loading"
          @click="handleRegister"
        >
          注册
        </el-button>
      </el-form-item>
    </el-form>
    
    <div class="form-footer">
      <span>已有账户？</span>
      <router-link to="/login" class="login-link">
        立即登录
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref()
const loading = ref(false)

// 表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agree: false
})

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 同意协议验证
const validateAgree = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

// 表单验证规则 - 简化版
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    // 邮箱改为可选
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agree: [
    { validator: validateAgree, trigger: 'change' }
  ]
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用真实的注册API
    const result = await userStore.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      fullName: registerForm.username // 暂时使用用户名作为全名
    })

    if (result.success) {
      // 注册成功，跳转到登录页
      router.push('/auth/login')
    }
    // 错误消息已在store中处理

  } catch (error) {
    console.error('注册处理错误:', error)
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-form {
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
}

.form-header p {
  color: #8c8c8c;
  margin: 0;
}

.terms-link {
  color: #1890ff;
  text-decoration: none;
}

.terms-link:hover {
  color: #40a9ff;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: #8c8c8c;
}

.login-link {
  color: #1890ff;
  text-decoration: none;
  margin-left: 4px;
}

.login-link:hover {
  color: #40a9ff;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
  line-height: 1.5;
}
</style>
