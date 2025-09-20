<template>
  <div class="theme-toggle">
    <div class="custom-switch" :class="{ 'is-dark': isDark }" @click="toggleTheme">
        <div class="switch-track">
          <!-- 背景装饰 -->
          <div class="stars">
            <div class="star" v-for="i in 6" :key="i"></div>
          </div>
          <div class="clouds">
            <div class="cloud" v-for="i in 3" :key="i"></div>
          </div>

          <!-- 滑块 -->
          <div class="switch-thumb">
            <div class="sun-moon-container">
              <!-- 太阳 -->
              <div class="sun">
                <div class="sun-rays">
                  <div class="ray" v-for="i in 8" :key="i"></div>
                </div>
                <div class="sun-core"></div>
              </div>

              <!-- 月亮 -->
              <div class="moon">
                <div class="moon-crater crater-1"></div>
                <div class="moon-crater crater-2"></div>
                <div class="moon-crater crater-3"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'

// 主题状态
const isDark = ref(false)

// 主题切换函数
const toggleTheme = (value) => {
  // 如果是从 switch 组件触发，value 就是新的状态
  if (typeof value === 'boolean') {
    isDark.value = value
  } else {
    // 如果是从其他地方触发，切换状态
    isDark.value = !isDark.value
  }

  applyTheme(isDark.value)

  // 保存主题偏好到本地存储
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 应用主题
const applyTheme = (dark) => {
  const html = document.documentElement
  const body = document.body

  // 临时禁用过渡效果和滚动条，防止主题切换时的闪烁
  const originalTransition = body.style.transition
  const originalOverflow = body.style.overflow

  body.style.transition = 'none'
  body.style.overflow = 'hidden'

  if (dark) {
    html.classList.add('dark')
    html.setAttribute('data-theme', 'dark')
  } else {
    html.classList.remove('dark')
    html.setAttribute('data-theme', 'light')
  }

  // 更新CSS变量
  updateCSSVariables(dark)

  // 强制重绘后恢复过渡效果
  requestAnimationFrame(() => {
    body.style.transition = originalTransition
    body.style.overflow = originalOverflow
  })
}

// 更新CSS变量
const updateCSSVariables = (dark) => {
  const root = document.documentElement
  
  if (dark) {
    // 深色主题变量 - 改进对比度
    root.style.setProperty('--el-bg-color', '#141414')
    root.style.setProperty('--el-bg-color-page', '#0d1117')
    root.style.setProperty('--el-bg-color-overlay', '#1c1c1c')
    root.style.setProperty('--el-text-color-primary', '#f0f6fc')
    root.style.setProperty('--el-text-color-regular', '#e6edf3')
    root.style.setProperty('--el-text-color-secondary', '#8b949e')
    root.style.setProperty('--el-text-color-placeholder', '#6e7681')
    root.style.setProperty('--el-text-color-disabled', '#484f58')
    root.style.setProperty('--el-border-color', '#30363d')
    root.style.setProperty('--el-border-color-light', '#21262d')
    root.style.setProperty('--el-border-color-lighter', '#161b22')
    root.style.setProperty('--el-border-color-extra-light', '#0d1117')
    root.style.setProperty('--el-border-color-dark', '#444c56')
    root.style.setProperty('--el-border-color-darker', '#656d76')
    root.style.setProperty('--el-fill-color', '#21262d')
    root.style.setProperty('--el-fill-color-light', '#161b22')
    root.style.setProperty('--el-fill-color-lighter', '#0d1117')
    root.style.setProperty('--el-fill-color-extra-light', '#010409')
    root.style.setProperty('--el-fill-color-dark', '#30363d')
    root.style.setProperty('--el-fill-color-darker', '#444c56')
    root.style.setProperty('--el-fill-color-blank', 'transparent')

    // 自定义变量
    root.style.setProperty('--sidebar-bg', '#0d1117')
    root.style.setProperty('--header-bg', '#161b22')
    root.style.setProperty('--content-bg', '#0d1117')
    root.style.setProperty('--card-bg', '#161b22')
    root.style.setProperty('--hover-bg', '#21262d')
  } else {
    // 浅色主题变量（恢复默认）
    root.style.setProperty('--el-bg-color', '#ffffff')
    root.style.setProperty('--el-bg-color-page', '#f2f3f5')
    root.style.setProperty('--el-bg-color-overlay', '#ffffff')
    root.style.setProperty('--el-text-color-primary', '#303133')
    root.style.setProperty('--el-text-color-regular', '#606266')
    root.style.setProperty('--el-text-color-secondary', '#909399')
    root.style.setProperty('--el-text-color-placeholder', '#a8abb2')
    root.style.setProperty('--el-text-color-disabled', '#c0c4cc')
    root.style.setProperty('--el-border-color', '#dcdfe6')
    root.style.setProperty('--el-border-color-light', '#e4e7ed')
    root.style.setProperty('--el-border-color-lighter', '#ebeef5')
    root.style.setProperty('--el-border-color-extra-light', '#f2f6fc')
    root.style.setProperty('--el-border-color-dark', '#d4d7de')
    root.style.setProperty('--el-border-color-darker', '#cdd0d6')
    root.style.setProperty('--el-fill-color', '#f0f2f5')
    root.style.setProperty('--el-fill-color-light', '#f5f7fa')
    root.style.setProperty('--el-fill-color-lighter', '#fafafa')
    root.style.setProperty('--el-fill-color-extra-light', '#fafcff')
    root.style.setProperty('--el-fill-color-dark', '#ebedf0')
    root.style.setProperty('--el-fill-color-darker', '#e6e8eb')
    root.style.setProperty('--el-fill-color-blank', 'transparent')
    
    // 自定义变量
    root.style.setProperty('--sidebar-bg', '#ffffff')
    root.style.setProperty('--header-bg', '#ffffff')
    root.style.setProperty('--content-bg', '#f5f5f5')
    root.style.setProperty('--card-bg', '#ffffff')
    root.style.setProperty('--hover-bg', '#f5f5f5')
  }
}

// 初始化主题
const initTheme = () => {
  // 强制默认使用白色主题，忽略本地存储和系统主题
  isDark.value = false
  applyTheme(isDark.value)

  // 清除可能存在的主题设置，确保始终使用白主题
  localStorage.removeItem('theme')
}

// 监听系统主题变化
const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
const handleSystemThemeChange = (e) => {
  // 只有在没有手动设置主题时才跟随系统
  if (!localStorage.getItem('theme')) {
    isDark.value = e.matches
    applyTheme(isDark.value)
  }
}

onMounted(() => {
  initTheme()
  mediaQuery.addEventListener('change', handleSystemThemeChange)
})

// 暴露主题状态给父组件
defineExpose({
  isDark,
  toggleTheme
})
</script>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
}

.custom-switch {
  width: 80px;
  height: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.switch-track {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #87CEEB 0%, #98D8E8 100%);
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  transition: all 0.6s ease;
  box-shadow:
    inset 0 2px 4px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.15);
}

.custom-switch.is-dark .switch-track {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  box-shadow:
    inset 0 2px 4px rgba(0, 0, 0, 0.3),
    0 2px 8px rgba(0, 0, 0, 0.4);
}

/* 星星装饰 */
.stars {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.6s ease;
}

.custom-switch.is-dark .stars {
  opacity: 1;
}

.star {
  position: absolute;
  width: 2px;
  height: 2px;
  background: #fff;
  border-radius: 50%;
  animation: twinkle 2s infinite alternate;
}

.star:nth-child(1) { top: 8px; left: 15px; animation-delay: 0s; }
.star:nth-child(2) { top: 15px; left: 25px; animation-delay: 0.3s; }
.star:nth-child(3) { top: 10px; left: 35px; animation-delay: 0.6s; }
.star:nth-child(4) { top: 20px; left: 45px; animation-delay: 0.9s; }
.star:nth-child(5) { top: 12px; left: 55px; animation-delay: 1.2s; }
.star:nth-child(6) { top: 25px; left: 65px; animation-delay: 1.5s; }

@keyframes twinkle {
  0% { opacity: 0.3; transform: scale(1); }
  100% { opacity: 1; transform: scale(1.2); }
}

/* 云朵装饰 */
.clouds {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 1;
  transition: opacity 0.6s ease;
}

.custom-switch.is-dark .clouds {
  opacity: 0;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  animation: float 3s infinite ease-in-out;
}

.cloud:nth-child(1) {
  width: 12px;
  height: 6px;
  top: 8px;
  left: 15px;
  animation-delay: 0s;
}

.cloud:nth-child(2) {
  width: 8px;
  height: 4px;
  top: 20px;
  left: 35px;
  animation-delay: 1s;
}

.cloud:nth-child(3) {
  width: 10px;
  height: 5px;
  top: 12px;
  left: 55px;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-2px); }
}

/* 滑块 */
.switch-thumb {
  position: absolute;
  top: 4px;
  left: 4px;
  width: 32px;
  height: 32px;
  background: #fff;
  border-radius: 50%;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.320, 1);
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.15),
    0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.custom-switch.is-dark .switch-thumb {
  transform: translateX(40px);
  background: #f8f8f8;
}

/* 太阳月亮容器 */
.sun-moon-container {
  position: relative;
  width: 24px;
  height: 24px;
}

/* 太阳 */
.sun {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 1;
  transform: rotate(0deg) scale(1);
  transition: all 0.6s ease;
}

.custom-switch.is-dark .sun {
  opacity: 0;
  transform: rotate(180deg) scale(0.5);
}

.sun-core {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 12px;
  height: 12px;
  background: #FFD700;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 8px rgba(255, 215, 0, 0.6);
}

.sun-rays {
  position: absolute;
  width: 100%;
  height: 100%;
  animation: rotate 8s linear infinite;
}

.ray {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px;
  height: 6px;
  background: #FFD700;
  border-radius: 1px;
  transform-origin: center;
}

.ray:nth-child(1) { transform: translate(-50%, -50%) rotate(0deg) translateY(-10px); }
.ray:nth-child(2) { transform: translate(-50%, -50%) rotate(45deg) translateY(-10px); }
.ray:nth-child(3) { transform: translate(-50%, -50%) rotate(90deg) translateY(-10px); }
.ray:nth-child(4) { transform: translate(-50%, -50%) rotate(135deg) translateY(-10px); }
.ray:nth-child(5) { transform: translate(-50%, -50%) rotate(180deg) translateY(-10px); }
.ray:nth-child(6) { transform: translate(-50%, -50%) rotate(225deg) translateY(-10px); }
.ray:nth-child(7) { transform: translate(-50%, -50%) rotate(270deg) translateY(-10px); }
.ray:nth-child(8) { transform: translate(-50%, -50%) rotate(315deg) translateY(-10px); }

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 月亮 */
.moon {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  transform: rotate(-180deg) scale(0.5);
  transition: all 0.6s ease;
}

.custom-switch.is-dark .moon {
  opacity: 1;
  transform: rotate(0deg) scale(1);
}

.moon::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 16px;
  height: 16px;
  background: #E6E6FA;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow:
    0 0 8px rgba(230, 230, 250, 0.6),
    inset -2px -2px 4px rgba(0, 0, 0, 0.1);
}

.moon-crater {
  position: absolute;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
}

.crater-1 {
  width: 3px;
  height: 3px;
  top: 6px;
  left: 8px;
}

.crater-2 {
  width: 2px;
  height: 2px;
  top: 12px;
  left: 14px;
}

.crater-3 {
  width: 2px;
  height: 2px;
  top: 16px;
  left: 6px;
}


</style>
