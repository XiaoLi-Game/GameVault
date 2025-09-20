<template>
  <div class="forgot-password-form">
    <div class="form-header">
      <h2>重置密码</h2>
      <p>请输入您的邮箱地址，我们将发送重置链接</p>
    </div>
    
    <el-form
      ref="forgotFormRef"
      :model="forgotForm"
      :rules="forgotRules"
      size="large"
      @submit.prevent="handleForgotPassword"
    >
      <el-form-item prop="email">
        <el-input
          v-model="forgotForm.email"
          placeholder="请输入注册时的邮箱地址"
          :prefix-icon="Message"
          clearable
          @keyup.enter="handleForgotPassword"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          size="large"
          style="width: 100%"
          :loading="loading"
          @click="handleForgotPassword"
        >
          发送重置链接
        </el-button>
      </el-form-item>
    </el-form>
    
    <div class="form-footer">
      <router-link to="/login" class="back-link">
        <el-icon><ArrowLeft /></el-icon>
        返回登录
      </router-link>
    </div>
    
    <!-- 成功提示 -->
    <div v-if="emailSent" class="success-message">
      <el-result
        icon="success"
        title="邮件发送成功"
        sub-title="请检查您的邮箱并点击重置链接"
      >
        <template #extra>
          <el-button type="primary" @click="router.push('/login')">
            返回登录
          </el-button>
          <el-button @click="resendEmail" :loading="loading">
            重新发送
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const forgotFormRef = ref()
const loading = ref(false)
const emailSent = ref(false)

// 表单数据
const forgotForm = reactive({
  email: ''
})

// 表单验证规则
const forgotRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 发送重置邮件
const handleForgotPassword = async () => {
  if (!forgotFormRef.value) return
  
  try {
    const valid = await forgotFormRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    // 模拟发送邮件API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟发送成功
    emailSent.value = true
    ElMessage.success('重置邮件发送成功')
    
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重新发送邮件
const resendEmail = async () => {
  loading.value = true
  
  try {
    // 模拟重新发送邮件
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('邮件已重新发送')
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-form {
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
  font-size: 14px;
  line-height: 1.5;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
}

.back-link {
  color: #1890ff;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  font-size: 14px;
}

.back-link:hover {
  color: #40a9ff;
}

.back-link .el-icon {
  margin-right: 4px;
}

.success-message {
  margin-top: 32px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
}

:deep(.el-result) {
  padding: 20px 0;
}

:deep(.el-result__title) {
  font-size: 18px;
  margin-top: 16px;
}

:deep(.el-result__subtitle) {
  font-size: 14px;
  margin-top: 8px;
}
</style>
