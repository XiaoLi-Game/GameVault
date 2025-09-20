# GameVault 项目结构说明

## 项目概述
GameVault 是一个基于 Vue 3 + Spring Boot 的游戏资产管理系统，采用前后端分离架构。

## 技术栈
- **前端**: Vue 3 + Element Plus + Vue Router + Pinia
- **后端**: Spring Boot + MyBatis + MySQL
- **认证**: JWT Token
- **构建工具**: Vite

## 项目结构

```
vue/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── index.js       # API 配置和实例
│   │   ├── user.js        # 用户相关 API
│   │   └── asset.js       # 资产相关 API
│   │
│   ├── assets/            # 静态资源
│   │   ├── base.css       # 基础样式
│   │   ├── main.css       # 主样式
│   │   └── logo.svg       # Logo
│   │
│   ├── components/        # 公共组件
│   │   └── ThemeToggle.vue # 主题切换组件
│   │
│   ├── layout/            # 布局组件
│   │   ├── LoginLayout.vue # 登录页布局
│   │   └── MainLayout.vue  # 主页面布局
│   │
│   ├── router/            # 路由配置
│   │   └── index.js       # 路由定义
│   │
│   ├── stores/            # 状态管理
│   │   └── user.js        # 用户状态
│   │
│   ├── views/             # 页面组件
│   │   ├── auth/          # 认证相关页面
│   │   │   ├── LoginView.vue
│   │   │   ├── RegisterView.vue
│   │   │   └── ForgotPasswordView.vue
│   │   │
│   │   ├── assets/        # 资产管理页面
│   │   │   ├── AssetListView.vue
│   │   │   └── AssetUploadView.vue
│   │   │
│   │   ├── projects/      # 项目管理页面
│   │   │   ├── ProjectListView.vue
│   │   │   ├── ProjectCreateView.vue
│   │   │   └── ProjectDetailView.vue
│   │   │
│   │   ├── users/         # 用户管理页面
│   │   │   ├── UserListView.vue
│   │   │   └── PermissionsView.vue
│   │   │
│   │   ├── audit/         # 审核管理页面
│   │   │   └── AuditListView.vue
│   │   │
│   │   ├── settings/      # 系统设置页面
│   │   │   └── SystemSettingsView.vue
│   │   │
│   │   ├── DashboardView.vue    # 仪表盘（保留但不在菜单中）
│   │   ├── ProfileView.vue      # 个人中心
│   │   └── NotFoundView.vue     # 404 页面
│   │
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
│
├── package.json           # 项目配置
├── vite.config.js         # Vite 配置
└── README.md              # 项目说明
```

## 功能模块

### 1. 认证模块 (5个功能)
- 用户登录/注册
- 忘记密码
- 用户登出
- 快速登录

### 2. 资产管理模块 (6个功能)
- 统计卡片（合并自仪表盘）
- 资产列表（搜索筛选）
- 资产上传
- 资产详情
- 资产操作（查看/下载/编辑/删除）
- 视图切换

### 3. 项目管理模块 (3个功能)
- 项目列表
- 项目创建
- 项目详情

### 4. 用户管理模块 (4个功能)
- 用户列表（增删改查）
- 用户详情
- 个人中心
- 权限管理

### 5. 审核管理模块 (1个功能)
- 审核列表 + 单个审核操作

### 6. 系统设置模块 (1个功能)
- 系统配置

## 权限系统

### 角色定义
- **管理员(admin)**: 所有功能权限
- **经理(manager)**: 管理功能权限
- **普通用户(user)**: 基础功能权限

### 权限分布
| 功能模块 | 管理员 | 经理 | 用户 |
|----------|--------|------|------|
| 资产管理 | ✅ | ✅ | ✅ |
| 项目管理 | ✅ | ✅ | ✅ |
| 用户管理 | ✅ | ✅ | ❌ |
| 审核管理 | ✅ | ✅ | ❌ |
| 系统设置 | ✅ | ✅ | ❌ |

## 特殊功能

### 演示功能
- 资产删除（刷新恢复）
- 项目删除（刷新恢复）
- 用户友好的演示提示

### 测试功能
- 快速登录（测试账号自动填充）
- 示例数据展示

## 开发说明

### 启动项目
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

### 测试账号
- 管理员: admin / 123
- 经理: manager / 123
- 用户: user / 123

### API 接口
- 基础URL: http://localhost:8080
- 认证方式: JWT Token
- 请求格式: JSON

## 项目特色

1. **模块化设计** - 清晰的功能模块划分
2. **权限管理** - 完整的角色权限控制
3. **响应式设计** - 支持多设备访问
4. **演示友好** - 适合毕业设计展示
5. **技术先进** - 使用最新的前端技术栈

## 注意事项

1. 本项目为毕业设计项目，部分功能为演示性质
2. 删除操作为演示功能，刷新页面可恢复数据
3. 系统设置中的部分配置为静态展示
4. 文件上传功能已简化，使用标准文件选择器
