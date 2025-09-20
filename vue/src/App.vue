<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  overflow-x: hidden;
}

#app {
  height: 100vh;
  overflow: hidden;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus 样式覆盖 */
.el-card {
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  background-color: var(--card-bg, var(--el-bg-color));
  transition: background-color 0.3s, border-color 0.3s;
}

.el-card__header {
  border-bottom: 1px solid var(--el-border-color);
  padding: 16px 20px;
}

.el-card__body {
  padding: 20px;
}

.el-table {
  font-size: 14px;
  background-color: var(--card-bg, var(--el-bg-color));
}

.el-table th {
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.el-button {
  font-weight: 400;
}

.el-menu {
  border-right: none;
}

.el-menu-item, .el-sub-menu__title {
  height: 48px;
  line-height: 48px;
}

/* 深色主题特殊样式 */
.dark {
  color-scheme: dark;

  /* 深色模式CSS变量 */
  --el-color-primary: #409eff;
  --el-color-primary-light-3: #79bbff;
  --el-color-primary-light-5: #a0cfff;
  --el-color-primary-light-7: #c6e2ff;
  --el-color-primary-light-8: #d9ecff;
  --el-color-primary-light-9: #ecf5ff;
  --el-color-primary-dark-2: #337ecc;

  /* 文字颜色 - 提高对比度 */
  --el-text-color-primary: #e5eaf3;
  --el-text-color-regular: #cfd3dc;
  --el-text-color-secondary: #a3a6ad;
  --el-text-color-placeholder: #8d9095;
  --el-text-color-disabled: #6c6e72;

  /* 背景颜色 */
  --el-bg-color: #141414;
  --el-bg-color-page: #0a0a0a;
  --el-bg-color-overlay: #1d1e1f;

  /* 填充颜色 */
  --el-fill-color: #2d2d2d;
  --el-fill-color-light: #262727;
  --el-fill-color-lighter: #1d1d1d;
  --el-fill-color-extra-light: #191919;
  --el-fill-color-dark: #39393a;
  --el-fill-color-darker: #424243;
  --el-fill-color-blank: transparent;

  /* 边框颜色 */
  --el-border-color: #4c4d4f;
  --el-border-color-light: #414243;
  --el-border-color-lighter: #363637;
  --el-border-color-extra-light: #2b2b2c;
  --el-border-color-dark: #58585b;
  --el-border-color-darker: #636466;

  /* 自定义变量 */
  --card-bg: #1e1e1e;
  --hover-bg: #2a2a2a;
}

.dark .el-card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  border-color: var(--el-border-color);
  background-color: var(--card-bg);
  color: var(--el-text-color-primary);
}

/* 深色模式下的全局文字样式 */
.dark {
  color: var(--el-text-color-primary);
}

.dark * {
  color: inherit;
}

.dark h1, .dark h2, .dark h3, .dark h4, .dark h5, .dark h6 {
  color: var(--el-text-color-primary);
}

.dark p, .dark span, .dark div {
  color: var(--el-text-color-primary);
}

.dark .el-text {
  color: var(--el-text-color-primary) !important;
}

.dark .login-layout {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: var(--el-text-color-primary);
}

.dark .login-layout * {
  color: var(--el-text-color-primary);
}

.dark .login-layout .el-card {
  background-color: rgba(30, 30, 30, 0.95);
  border-color: var(--el-border-color);
  backdrop-filter: blur(10px);
}

.dark .login-layout .el-card .el-card__header {
  border-bottom-color: var(--el-border-color);
}

.dark .login-layout .el-form-item__label {
  color: var(--el-text-color-primary) !important;
}

/* 深色模式下的表格样式优化 */
.dark .el-table {
  background-color: var(--card-bg);
  color: var(--el-text-color-primary);
}

.dark .el-table th.el-table__cell {
  background-color: var(--el-fill-color-dark);
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color);
}

.dark .el-table td.el-table__cell {
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.dark .el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background-color: var(--el-fill-color-light);
}

.dark .el-table__body tr:hover > td.el-table__cell {
  background-color: var(--hover-bg) !important;
}

/* 深色模式下的输入框样式优化 */
.dark .el-input__wrapper {
  background-color: var(--el-fill-color-light);
  border-color: var(--el-border-color);
}

.dark .el-input__wrapper:hover {
  border-color: var(--el-border-color-dark);
}

.dark .el-input__wrapper.is-focus {
  border-color: var(--el-color-primary);
}

/* 深色模式下的按钮样式优化 */
.dark .el-button--default {
  background-color: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  color: var(--el-text-color-primary);
}

.dark .el-button--default:hover {
  background-color: var(--el-fill-color);
  border-color: var(--el-border-color-dark);
}

/* 深色模式下的plain按钮样式优化 */
.dark .el-button--primary.is-plain {
  background-color: rgba(64, 158, 255, 0.1) !important;
  border-color: rgba(64, 158, 255, 0.4) !important;
  color: #79bbff !important;
}

.dark .el-button--primary.is-plain:hover {
  background-color: rgba(64, 158, 255, 0.2) !important;
  border-color: rgba(64, 158, 255, 0.6) !important;
  color: #a0cfff !important;
}

.dark .el-button--info.is-plain {
  background-color: rgba(144, 147, 153, 0.1) !important;
  border-color: rgba(144, 147, 153, 0.4) !important;
  color: #b1b3b8 !important;
}

.dark .el-button--info.is-plain:hover {
  background-color: rgba(144, 147, 153, 0.2) !important;
  border-color: rgba(144, 147, 153, 0.6) !important;
  color: #c8c9cc !important;
}

/* 深色模式下的下拉菜单样式优化 */
.dark .el-dropdown-menu {
  background-color: var(--card-bg);
  border-color: var(--el-border-color);
}

.dark .el-dropdown-menu__item {
  color: var(--el-text-color-primary);
}

.dark .el-dropdown-menu__item:hover {
  background-color: var(--hover-bg);
  color: var(--el-text-color-primary);
}

/* 深色模式下的菜单和导航样式 */
.dark .el-menu {
  background-color: var(--card-bg);
  border-color: var(--el-border-color);
}

.dark .el-menu-item {
  color: var(--el-text-color-primary);
}

.dark .el-menu-item:hover {
  background-color: var(--hover-bg);
  color: var(--el-text-color-primary);
}

.dark .el-menu-item.is-active {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.dark .el-sub-menu__title {
  color: var(--el-text-color-primary);
}

.dark .el-sub-menu__title:hover {
  background-color: var(--hover-bg);
  color: var(--el-text-color-primary);
}

/* Element Plus 标签组件适配 - 浅色模式 */
.el-tag {
  font-weight: 500;
}

.el-tag.el-tag--primary {
  background-color: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.el-tag.el-tag--success {
  background-color: #f0f9ff;
  border-color: #95d475;
  color: #67c23a;
}

.el-tag.el-tag--warning {
  background-color: #fdf6ec;
  border-color: #f5dab1;
  color: #e6a23c;
}

.el-tag.el-tag--danger {
  background-color: #fef0f0;
  border-color: #fbc4c4;
  color: #f56c6c;
}

.el-tag.el-tag--info {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
}

/* 深色模式下的标签样式 */
.dark .el-tag {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
  font-weight: 500;
}

/* 深色模式下的提示框样式 */
.dark .el-alert {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
}

.dark .el-alert .el-alert__title {
  color: var(--el-text-color-primary) !important;
}

.dark .el-alert .el-alert__description {
  color: var(--el-text-color-regular) !important;
}

.dark .el-alert .el-alert__icon {
  color: var(--el-text-color-primary) !important;
}

.dark .el-tag.el-tag--primary {
  background-color: rgba(64, 158, 255, 0.2) !important;
  border-color: rgba(64, 158, 255, 0.4) !important;
  color: #79bbff !important;
}

.dark .el-tag.el-tag--success {
  background-color: rgba(103, 194, 58, 0.2) !important;
  border-color: rgba(103, 194, 58, 0.4) !important;
  color: #95d475 !important;
}

.dark .el-tag.el-tag--warning {
  background-color: rgba(230, 162, 60, 0.2) !important;
  border-color: rgba(230, 162, 60, 0.4) !important;
  color: #eebe77 !important;
}

.dark .el-tag.el-tag--danger {
  background-color: rgba(245, 108, 108, 0.2) !important;
  border-color: rgba(245, 108, 108, 0.4) !important;
  color: #f89898 !important;
}

.dark .el-tag.el-tag--info {
  background-color: rgba(144, 147, 153, 0.2) !important;
  border-color: rgba(144, 147, 153, 0.4) !important;
  color: #b1b3b8 !important;
}

/* 深色模式下的分页器样式优化 */
.dark .el-pagination {
  color: var(--el-text-color-primary);
}

.dark .el-pagination .el-pagination__total {
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .el-pagination__goto {
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .el-pagination__classifier {
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .btn-prev,
.dark .el-pagination .btn-next {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .btn-prev:hover,
.dark .el-pagination .btn-next:hover {
  background-color: var(--el-fill-color) !important;
  border-color: var(--el-border-color-dark) !important;
  color: var(--el-color-primary) !important;
}

.dark .el-pagination .el-pager li {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .el-pager li:hover {
  background-color: var(--el-fill-color) !important;
  color: var(--el-color-primary) !important;
}

.dark .el-pagination .el-pager li.is-active {
  background-color: var(--el-color-primary) !important;
  border-color: var(--el-color-primary) !important;
  color: #ffffff !important;
}

.dark .el-pagination .el-select .el-select__wrapper {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

.dark .el-pagination .el-input__wrapper {
  background-color: var(--el-fill-color-light) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

/* Tooltip 深色模式适配 */
.dark .el-tooltip__popper {
  background-color: var(--el-bg-color-overlay) !important;
  border: 1px solid var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

.dark .el-tooltip__popper .el-popper__arrow::before {
  background-color: var(--el-bg-color-overlay) !important;
  border: 1px solid var(--el-border-color) !important;
}

/* 响应式工具类 */
@media (max-width: 768px) {
  .hidden-xs {
    display: none !important;
  }
}

@media (min-width: 769px) {
  .hidden-sm-and-up {
    display: none !important;
  }
}
</style>
