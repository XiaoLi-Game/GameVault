<template>
  <div class="not-found">
    <div class="not-found-container">
      <!-- 404 图标和标题 -->
      <div class="error-visual">
        <div class="error-number">404</div>
        <div class="error-icon">
          <el-icon><DocumentDelete /></el-icon>
        </div>
      </div>

      <!-- 错误信息 -->
      <div class="error-content">
        <h1 class="error-title">页面未找到</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移动。<br>
          请检查URL是否正确，或返回首页继续浏览。
        </p>

        <!-- 可能的原因 -->
        <div class="error-reasons">
          <h3>可能的原因：</h3>
          <ul>
            <li>页面链接已过期或被删除</li>
            <li>URL地址输入错误</li>
            <li>您没有访问此页面的权限</li>
            <li>服务器临时维护中</li>
          </ul>
        </div>

        <!-- 操作按钮 -->
        <div class="error-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回上页
          </el-button>
          <el-button size="large" @click="refreshPage">
            <el-icon><Refresh /></el-icon>
            刷新页面
          </el-button>
        </div>

        <!-- 快速导航 -->
        <div class="quick-nav">
          <h3>快速导航：</h3>
          <div class="nav-links">
            <router-link to="/dashboard" class="nav-link">
              <el-icon><Odometer /></el-icon>
              <span>仪表板</span>
            </router-link>
            <router-link to="/assets" class="nav-link">
              <el-icon><Files /></el-icon>
              <span>资产管理</span>
            </router-link>
            <router-link to="/projects" class="nav-link">
              <el-icon><Folder /></el-icon>
              <span>项目管理</span>
            </router-link>
            <router-link to="/profile" class="nav-link">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {
  DocumentDelete,
  HomeFilled,
  ArrowLeft,
  Refresh,
  Odometer,
  Files,
  Folder,
  User
} from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}

const goBack = () => {
  router.go(-1)
}

const refreshPage = () => {
  window.location.reload()
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.not-found-container {
  max-width: 1200px;
  width: 100%;
  background: var(--el-bg-color, white);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 60px;
  padding: 60px;
  align-items: center;
  transition: background-color 0.3s ease;
}

.error-visual {
  text-align: center;
  position: relative;
}

.error-number {
  font-size: 180px;
  font-weight: 900;
  color: #f0f2f5;
  line-height: 1;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 80px;
  color: #ff6b6b;
}

.error-content {
  padding-left: 20px;
}

.error-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--el-text-color-primary, #2c3e50);
  margin: 0 0 16px 0;
  line-height: 1.2;
  transition: color 0.3s ease;
}

.error-description {
  font-size: 18px;
  color: var(--el-text-color-regular, #7f8c8d);
  line-height: 1.6;
  margin: 0 0 32px 0;
  transition: color 0.3s ease;
}

.error-reasons {
  margin-bottom: 40px;
}

.error-reasons h3 {
  font-size: 20px;
  color: var(--el-text-color-primary, #34495e);
  margin: 0 0 16px 0;
  font-weight: 600;
  transition: color 0.3s ease;
}

.error-reasons ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.error-reasons li {
  padding: 8px 0;
  color: var(--el-text-color-regular, #7f8c8d);
  position: relative;
  padding-left: 20px;
  transition: color 0.3s ease;
}

.error-reasons li::before {
  content: "•";
  color: #3498db;
  font-weight: bold;
  position: absolute;
  left: 0;
}

.error-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.quick-nav h3 {
  font-size: 20px;
  color: var(--el-text-color-primary, #34495e);
  margin: 0 0 20px 0;
  font-weight: 600;
  transition: color 0.3s ease;
}

.nav-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--el-fill-color-light, #f8f9fa);
  border-radius: 8px;
  text-decoration: none;
  color: var(--el-text-color-primary, #495057);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.nav-link:hover {
  background: var(--el-fill-color, #e9ecef);
  color: var(--el-color-primary, #3498db);
  border-color: var(--el-color-primary, #3498db);
  transform: translateY(-2px);
}

.nav-link .el-icon {
  font-size: 20px;
}

.nav-link span {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .not-found-container {
    grid-template-columns: 1fr;
    gap: 40px;
    padding: 40px;
    text-align: center;
  }

  .error-content {
    padding-left: 0;
  }

  .error-number {
    font-size: 120px;
  }

  .error-icon {
    font-size: 60px;
  }
}

@media (max-width: 768px) {
  .not-found {
    padding: 16px;
  }

  .not-found-container {
    padding: 32px 24px;
  }

  .error-title {
    font-size: 28px;
  }

  .error-description {
    font-size: 16px;
  }

  .error-actions {
    flex-direction: column;
  }

  .nav-links {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .error-number {
    font-size: 80px;
  }

  .error-icon {
    font-size: 40px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-description {
    font-size: 14px;
  }
}

/* 深色模式适配 */
:global(.dark) .not-found {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

:global(.dark) .not-found-container {
  background: var(--card-bg);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
}

:global(.dark) .error-number {
  color: #2a2a2a;
  text-shadow: 2px 2px 4px rgba(255, 255, 255, 0.1);
}

:global(.dark) .error-icon {
  color: #ff6b6b;
}

:global(.dark) .error-reasons li::before {
  color: var(--el-color-primary);
}

:global(.dark) .nav-link {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

:global(.dark) .nav-link:hover {
  background: var(--el-fill-color);
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}
</style>
