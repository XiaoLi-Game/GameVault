<template>
  <div class="user-permission">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h1>权限管理</h1>
        <p>管理用户角色分配</p>
      </div>
    </div>

    <div class="content-container">
      <!-- 用户角色分配 -->
      <el-card>
        <template #header>
          <span>用户角色分配</span>
        </template>

        <el-table :data="userList" stripe style="width: 100%">
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="email" label="邮箱" min-width="200" />
          <el-table-column prop="nickname" label="昵称" width="120" />
          <el-table-column label="当前角色" width="120">
            <template #default="{ row }">
              <el-tag :type="getRoleTagType(row.role)">
                {{ getRoleName(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分配角色" width="150">
            <template #default="{ row }">
              <el-select
                v-model="row.role"
                placeholder="选择角色"
                @change="handleRoleChange(row)"
              >
                <el-option
                  v-for="role in availableRoles"
                  :key="role.value"
                  :label="role.label"
                  :value="role.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                {{ row.status === 'active' ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastLogin" label="最后登录" width="150" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 用户列表数据
const userList = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@gamevault.com',
    nickname: '系统管理员',
    role: 'admin',
    status: 'active',
    lastLogin: '2024-01-15 10:30'
  },
  {
    id: 2,
    username: 'developer1',
    email: 'dev1@gamevault.com',
    nickname: '开发者张三',
    role: 'developer',
    status: 'active',
    lastLogin: '2024-01-15 09:15'
  },
  {
    id: 3,
    username: 'reviewer1',
    email: 'reviewer1@gamevault.com',
    nickname: '审核员李四',
    role: 'reviewer',
    status: 'active',
    lastLogin: '2024-01-14 16:45'
  },
  {
    id: 4,
    username: 'user1',
    email: 'user1@gamevault.com',
    nickname: '普通用户王五',
    role: 'user',
    status: 'active',
    lastLogin: '2024-01-13 14:20'
  },
  {
    id: 5,
    username: 'user2',
    email: 'user2@gamevault.com',
    nickname: '普通用户赵六',
    role: 'user',
    status: 'disabled',
    lastLogin: '2024-01-10 11:30'
  }
])

// 可用角色选项
const availableRoles = [
  { label: '管理员', value: 'admin' },
  { label: '开发者', value: 'developer' },
  { label: '审核员', value: 'reviewer' },
  { label: '普通用户', value: 'user' }
]

// 获取角色标签类型
const getRoleTagType = (role) => {
  const tagMap = {
    admin: 'danger',
    developer: 'warning',
    reviewer: 'info',
    user: 'success'
  }
  return tagMap[role] || 'info'
}

// 获取角色名称
const getRoleName = (role) => {
  const nameMap = {
    admin: '管理员',
    developer: '开发者',
    reviewer: '审核员',
    user: '普通用户'
  }
  return nameMap[role] || role
}

// 处理角色变更
const handleRoleChange = (user) => {
  ElMessage.success(`已将用户 ${user.username} 的角色更改为 ${getRoleName(user.role)}`)
}
</script>

<style scoped>
.user-permission {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.header-left h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-left p {
  margin: 0;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.content-container {
  background: var(--el-bg-color);
}
</style>
