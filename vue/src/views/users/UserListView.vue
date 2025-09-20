<template>
  <div class="user-list">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h1>用户列表</h1>
        <p>查看和管理系统用户</p>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" @click="handleAddUser">
          添加用户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入用户名或邮箱"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
            @input="handleSearch"
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="选择角色" clearable style="width: 120px" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="管理员" value="admin" />
            <el-option label="开发者" value="developer" />
            <el-option label="审核员" value="reviewer" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <div class="table-title">
          <span>用户列表 ({{ pagination.total }})</span>
          <span v-if="selectedUsers.length > 0" class="selected-info">
            已选择 {{ selectedUsers.length }} 个用户
          </span>
        </div>
        <div class="table-actions">
          <template v-if="selectedUsers.length > 0">
            <el-button type="warning" @click="handleBatchToggleStatus">批量切换状态</el-button>
            <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
          </template>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar">
                {{ row.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-details">
                <div class="username">{{ row.username }}</div>
                <div class="email">{{ row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="projectCount" label="参与项目" width="100" />

        <el-table-column prop="assetCount" label="上传资产" width="100" />

        <el-table-column prop="lastLogin" label="最后登录" width="160" />

        <el-table-column prop="createdAt" label="注册时间" width="160" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <div class="button-row">
                <el-button link size="small" @click="handleView(row)">查看</el-button>
                <el-button link size="small" @click="handleEdit(row)">编辑</el-button>
              </div>
              <div class="button-row">
                <el-button link size="small" @click="handleResetPassword(row)">重置密码</el-button>
                <el-button
                  link
                  size="small"
                  :type="row.status === 'active' ? 'danger' : 'success'"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.status === 'active' ? '禁用' : '启用' }}
                </el-button>
              </div>
              <div class="button-row">
                <el-button link size="small" type="danger" @click="handleDelete(row)">删除</el-button>
                <div class="button-placeholder"></div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item v-if="!editMode" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="开发者" value="developer" />
            <el-option label="审核员" value="reviewer" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editMode ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const editMode = ref(false)
const selectedUsers = ref([])
const userFormRef = ref()

// 筛选表单
const searchForm = reactive({
  keyword: '',
  role: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  email: '',
  password: '',
  role: '',
  status: 'active'
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 用户列表
const userList = ref([])

// 计算属性
const dialogTitle = computed(() => editMode.value ? '编辑用户' : '添加用户')

// 获取角色标签类型
const getRoleTagType = (role) => {
  const roleMap = {
    admin: 'danger',
    developer: 'primary',
    reviewer: 'warning',
    user: 'info'
  }
  return roleMap[role] || ''
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

// 获取状态标签类型
const getStatusTagType = (status) => {
  return status === 'active' ? 'success' : 'danger'
}

// 获取状态名称
const getStatusName = (status) => {
  return status === 'active' ? '正常' : '禁用'
}

// 事件处理
const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    role: '',
    status: ''
  })
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadUserList()
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleView = (user) => {
  router.push(`/users/${user.id}`)
}

const handleAddUser = () => {
  editMode.value = false
  resetUserForm()
  dialogVisible.value = true
}

const handleEdit = (user) => {
  editMode.value = true
  Object.assign(userForm, {
    id: user.id,
    username: user.username,
    email: user.email,
    role: user.role,
    status: user.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    const valid = await userFormRef.value.validate()
    if (!valid) return

    submitting.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 获取现有用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')

    if (editMode.value) {
      // 更新用户
      const userIndex = storedUsers.findIndex(user => user.id === userForm.id)
      if (userIndex !== -1) {
        storedUsers[userIndex] = {
          ...storedUsers[userIndex],
          username: userForm.username,
          email: userForm.email,
          role: userForm.role,
          status: userForm.status,
          updatedAt: new Date().toISOString()
        }
      }
    } else {
      // 创建新用户
      const newUser = {
        id: Date.now(), // 简单的ID生成
        username: userForm.username,
        email: userForm.email,
        role: userForm.role,
        status: userForm.status,
        avatar: '',
        projectCount: 0,
        assetCount: 0,
        lastLogin: '从未登录',
        createdAt: new Date().toLocaleString('zh-CN'),
        updatedAt: new Date().toISOString()
      }
      storedUsers.push(newUser)
    }

    // 保存到localStorage
    localStorage.setItem('users', JSON.stringify(storedUsers))

    ElMessage.success(editMode.value ? '用户更新成功' : '用户创建成功')
    dialogVisible.value = false
    loadUserList()

  } catch (error) {
    ElMessage.error('操作失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  resetUserForm()
}

const resetUserForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    email: '',
    password: '',
    role: '',
    status: 'active'
  })
  if (userFormRef.value) {
    userFormRef.value.clearValidate()
  }
}

const handleResetPassword = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${user.username}" 的密码吗？`,
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('密码重置成功，新密码已发送到用户邮箱')
  } catch {
    // 用户取消
  }
}

const handleToggleStatus = async (user) => {
  const action = user.status === 'active' ? '禁用' : '启用'

  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.username}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 获取现有用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
    const userIndex = storedUsers.findIndex(u => u.id === user.id)

    if (userIndex !== -1) {
      storedUsers[userIndex].status = user.status === 'active' ? 'disabled' : 'active'
      storedUsers[userIndex].updatedAt = new Date().toISOString()
      localStorage.setItem('users', JSON.stringify(storedUsers))

      // 同时更新当前显示的用户列表
      const currentUserIndex = userList.value.findIndex(u => u.id === user.id)
      if (currentUserIndex !== -1) {
        userList.value[currentUserIndex].status = storedUsers[userIndex].status
      }
    }

    ElMessage.success(`用户${action}成功`)
  } catch {
    // 用户取消
  }
}

const handleDelete = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 获取现有用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
    const filteredUsers = storedUsers.filter(u => u.id !== user.id)
    localStorage.setItem('users', JSON.stringify(filteredUsers))

    // 同时从当前显示的用户列表中移除
    const currentUserIndex = userList.value.findIndex(u => u.id === user.id)
    if (currentUserIndex !== -1) {
      userList.value.splice(currentUserIndex, 1)
      pagination.total = pagination.total - 1
    }

    ElMessage.success('用户删除成功')
  } catch {
    // 用户取消
  }
}

const handleBatchToggleStatus = async () => {
  if (selectedUsers.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要批量切换选中的 ${selectedUsers.value.length} 个用户的状态吗？`,
      '确认批量操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 获取现有用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')

    selectedUsers.value.forEach(selectedUser => {
      const userIndex = storedUsers.findIndex(u => u.id === selectedUser.id)
      if (userIndex !== -1) {
        storedUsers[userIndex].status = storedUsers[userIndex].status === 'active' ? 'disabled' : 'active'
        storedUsers[userIndex].updatedAt = new Date().toISOString()
      }
    })

    localStorage.setItem('users', JSON.stringify(storedUsers))

    // 同时更新当前显示的用户列表
    selectedUsers.value.forEach(selectedUser => {
      const currentUserIndex = userList.value.findIndex(u => u.id === selectedUser.id)
      if (currentUserIndex !== -1) {
        const updatedUser = storedUsers.find(u => u.id === selectedUser.id)
        if (updatedUser) {
          userList.value[currentUserIndex].status = updatedUser.status
        }
      }
    })

    ElMessage.success(`批量操作成功，已处理 ${selectedUsers.value.length} 个用户`)
    selectedUsers.value = []
  } catch {
    // 用户取消
  }
}

const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复。`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 获取现有用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
    const selectedIds = selectedUsers.value.map(user => user.id)
    const filteredUsers = storedUsers.filter(user => !selectedIds.includes(user.id))

    localStorage.setItem('users', JSON.stringify(filteredUsers))

    // 同时从当前显示的用户列表中移除
    userList.value = userList.value.filter(user => !selectedIds.includes(user.id))
    pagination.total = pagination.total - selectedUsers.value.length

    ElMessage.success(`批量删除成功，已删除 ${selectedUsers.value.length} 个用户`)
    selectedUsers.value = []
  } catch {
    // 用户取消
  }
}



// 加载用户列表
const loadUserList = async () => {
  loading.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    // 从localStorage获取用户数据
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')

    // 默认用户数据（仅在localStorage为空时使用）
    const defaultUsers = [
      {
        id: 1,
        username: 'admin',
        email: 'admin@example.com',
        role: 'admin',
        status: 'active',
        avatar: '',
        projectCount: 5,
        assetCount: 23,
        lastLogin: '2024-01-15 14:30',
        createdAt: '2023-12-01 10:00'
      },
      {
        id: 2,
        username: 'developer1',
        email: 'dev1@example.com',
        role: 'developer',
        status: 'active',
        avatar: '',
        projectCount: 3,
        assetCount: 156,
        lastLogin: '2024-01-15 16:20',
        createdAt: '2023-12-05 14:30'
      },
      {
        id: 3,
        username: 'reviewer1',
        email: 'reviewer@example.com',
        role: 'reviewer',
        status: 'active',
        avatar: '',
        projectCount: 2,
        assetCount: 45,
        lastLogin: '2024-01-14 09:15',
        createdAt: '2023-12-10 16:20'
      },
      {
        id: 4,
        username: 'user1',
        email: 'user1@example.com',
        role: 'user',
        status: 'disabled',
        avatar: '',
        projectCount: 1,
        assetCount: 8,
        lastLogin: '2024-01-10 11:30',
        createdAt: '2023-12-15 14:45'
      }
    ]

    // 如果localStorage为空，初始化默认用户
    let allUsers = storedUsers
    if (storedUsers.length === 0) {
      allUsers = defaultUsers
      localStorage.setItem('users', JSON.stringify(defaultUsers))
    }

    // 应用搜索和筛选
    let filteredUsers = allUsers

    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      filteredUsers = filteredUsers.filter(user =>
        user.username.toLowerCase().includes(keyword) ||
        user.email.toLowerCase().includes(keyword)
      )
    }

    if (searchForm.role) {
      filteredUsers = filteredUsers.filter(user => user.role === searchForm.role)
    }

    if (searchForm.status) {
      filteredUsers = filteredUsers.filter(user => user.status === searchForm.status)
    }

    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize

    userList.value = filteredUsers.slice(startIndex, endIndex)
    pagination.total = filteredUsers.length

  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.page-header p {
  color: var(--el-text-color-regular);
  margin: 0;
}



.filter-card {
  margin-bottom: 16px;
}

.search-form {
  margin: 0;
}

.table-card {
  margin-bottom: 16px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title .selected-info {
  margin-left: 12px;
  color: var(--el-color-primary);
  font-size: 14px;
}

.table-title {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 12px;
}

.username {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.email {
  font-size: 12px;
  color: #8c8c8c;
}

/* 深色模式下的用户信息文本优化 */
html.dark .username {
  color: var(--el-text-color-primary) !important;
}

html.dark .email {
  color: var(--el-text-color-regular) !important;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.danger {
  color: #ff4d4f;
}

.success {
  color: #52c41a;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
  width: 100%;
}

.button-row {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
  align-items: center;
  min-height: 28px;
  width: 100%;
}

.button-row .el-button {
  min-width: 64px;
  text-align: center;
  font-size: 12px;
  padding: 4px 8px;
  border: 1px solid var(--el-border-color);
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
  border-radius: 4px;
  transition: all 0.2s;
}

.button-row .el-button:hover {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.button-row .el-button.is-type-danger {
  color: var(--el-color-danger);
}

.button-row .el-button.is-type-danger:hover {
  background-color: var(--el-color-danger-light-9);
  border-color: var(--el-color-danger);
}

.button-row .el-button.is-type-success {
  color: var(--el-color-success);
}

.button-row .el-button.is-type-success:hover {
  background-color: var(--el-color-success-light-9);
  border-color: var(--el-color-success);
}

@media (max-width: 768px) {
  .user-list {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 16px;
  }
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: flex-start;
}

.button-row {
  display: flex;
  gap: 6px;
  justify-content: flex-start;
  align-items: center;
  min-height: 24px;
}

.button-placeholder {
  min-width: 60px;
  height: 24px;
}

.button-row .el-button {
  min-width: 60px;
  text-align: center;
  padding: 4px 8px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #ffffff;
  color: #606266;
  transition: all 0.3s ease;
}

.button-row .el-button:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
  color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 深色主题适配 */
html.dark .button-row .el-button {
  background-color: #2d2d2d;
  border-color: #4c4d4f;
  color: #e5eaf3;
}

html.dark .button-row .el-button:hover {
  background-color: #3a3a3a;
  border-color: #606266;
  color: #409eff;
}


</style>
