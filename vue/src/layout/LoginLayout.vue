<template>
  <div class="login-layout">
    <!-- 主题切换按钮 -->
    <div class="theme-toggle-wrapper">
      <ThemeToggle />
    </div>

    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <div class="brand-logo">
            <el-icon size="48" color="#1890ff">
              <Box />
            </el-icon>
          </div>
          <h1 class="brand-title">GameVault</h1>
          <p class="brand-subtitle">游戏资产管理系统</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon color="#52c41a"><Check /></el-icon>
              <span>高效的资产管理</span>
            </div>
            <div class="feature-item">
              <el-icon color="#52c41a"><Check /></el-icon>
              <span>完善的版本控制</span>
            </div>
            <div class="feature-item">
              <el-icon color="#52c41a"><Check /></el-icon>
              <span>团队协作支持</span>
            </div>
            <div class="feature-item">
              <el-icon color="#52c41a"><Check /></el-icon>
              <span>智能审核流程</span>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="form-container">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Box, Check } from '@element-plus/icons-vue'
import ThemeToggle from '../components/ThemeToggle.vue'
</script>

<style scoped>
.login-layout {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  margin: 0;
  padding: 0;
  overflow: hidden;
  position: relative;
}

.theme-toggle-wrapper {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex: 1;
}

.login-left {
  flex: 1.2;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  padding: 80px 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="2" fill="rgba(255,255,255,0.1)"/></svg>') repeat;
  animation: float 20s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0) translateY(0); }
  100% { transform: translateX(-50px) translateY(-50px); }
}

.brand-section {
  text-align: center;
  max-width: 400px;
  position: relative;
  z-index: 1;
}

.brand-logo {
  margin-bottom: 20px;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin: 0 0 50px 0;
  font-weight: 300;
}

.feature-list {
  text-align: left;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 400;
}

.feature-item .el-icon {
  margin-right: 16px;
  font-size: 18px;
}

.login-right {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color-overlay, rgba(255, 255, 255, 0.95));
  backdrop-filter: blur(10px);
  transition: background-color 0.3s;
}

.form-container {
  width: 100%;
  max-width: 400px;
  background: var(--card-bg, white);
  padding: 40px 35px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s;
}

@media (max-width: 1024px) {
  .login-left {
    flex: 1;
    padding: 60px 40px;
  }

  .login-right {
    flex: 1;
    padding: 60px 40px;
  }

  .brand-title {
    font-size: 40px;
  }

  .form-container {
    max-width: 380px;
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .login-layout {
    position: relative;
    width: 100vw;
    height: 100vh;
  }

  .login-container {
    flex-direction: column;
    width: 100%;
    height: 100%;
  }

  .login-left {
    flex: 0 0 40%;
    padding: 30px 20px;
    min-height: 40vh;
  }

  .login-right {
    flex: 1;
    padding: 30px 20px;
    background: rgba(255, 255, 255, 0.95);
  }

  .brand-title {
    font-size: 32px;
  }

  .brand-subtitle {
    font-size: 16px;
  }

  .form-container {
    max-width: 100%;
    padding: 30px 20px;
    box-shadow: none;
    background: transparent;
  }

  .login-left::before {
    display: none;
  }
}
</style>
