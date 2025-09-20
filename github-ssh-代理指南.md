# GitHub SSH 代理推送指南

本指南适用于 Windows 用户，帮助你通过 SSH key 和代理安全地将项目推送到 GitHub。

---

## 1. 生成 SSH 密钥
在 PowerShell 执行：
```powershell
ssh-keygen -t ed25519 -C "你的邮箱"
```
一路回车，生成密钥于 `C:\Users\你的用户名\.ssh\id_ed25519`。

## 2. 获取公钥内容
```powershell
cat C:\Users\你的用户名\.ssh\id_ed25519.pub
```
复制输出内容（以 `ssh-ed25519` 开头），粘贴到 GitHub [SSH keys 页面](https://github.com/settings/keys) 并保存。

## 3. 配置 Git 代理（如需科学上网）
```powershell
git config --global http.proxy socks5://127.0.0.1:7890
git config --global https.proxy socks5://127.0.0.1:7890
```
如用 http 代理，将 socks5 改为 http。

## 4. 测试 SSH 连接
```powershell
ssh -T git@github.com
```
如显示 "Hi <用户名>! You've successfully authenticated..."，说明配置成功。

## 5. 初始化并推送项目
```powershell
git init
git add .
git commit -m "feat: 首次提交项目全部代码"
git remote add origin git@github.com:<你的用户名>/<仓库名>.git
git pull origin main --rebase
git push -u origin main
```

## 6. 常见问题
- **Permission denied (publickey)**：未添加公钥或未选中正确私钥。
- **Key is invalid**：粘贴的是指纹或私钥，需粘贴 `.pub` 文件内容。
- **推送被拒绝**：先 `git pull origin main --rebase`，再推送。

## 7. 进阶建议
- 推荐分阶段提交，保持 commit 历史清晰。
- 可在 `.gitignore`、`README.md`、分支策略文档中规范团队协作。
- 推荐使用 Conventional Commits、集成 GitHub Actions。

---
如遇特殊报错或流程问题，可随时联系项目维护者或查阅本指南。
# Windows 下 GitHub SSH 代理连接指南

## 1. 检查代理服务
- 确认你的代理服务（如 Clash、V2Ray、Shadowsocks）已启动。
- 代理监听地址和端口：`192.168.5.26:7890`（如有变动请自行修改）。

## 2. 配置 SSH
编辑 `C:\Users\1\.ssh\config` 文件，添加如下内容：

```ssh
Host github.com
    HostName ssh.github.com
    User git
    Port 443
    IdentityFile ~/.ssh/id_ed25519
    ProxyCommand "C:\Program Files\Git\mingw64\bin\connect.exe" -s 192.168.5.26:7890 %h %p
```

> 注意：`IdentityFile` 路径可根据你的私钥文件名调整。

## 3. 添加公钥到 GitHub
1. 打开 `id_ed25519.pub` 文件，复制全部内容。
2. 登录 GitHub，进入 `Settings` → `SSH and GPG keys`。
3. 点击 `New SSH key`，粘贴公钥内容，保存。

## 4. 测试连接
在 PowerShell 执行：
```powershell
ssh -T git@github.com
```
- 首次连接会提示主机指纹，输入 `yes` 并回车。
- 若出现 `Permission denied (publickey)`，请检查公钥是否已添加到 GitHub。

## 5. 常见问题
- **FATAL: Failed to determine SOCKS server**：代理服务未启动或端口错误。
- **Permission denied (publickey)**：公钥未添加或配置错误。

## 6. 参考命令
- 查看本地监听端口：
  ```powershell
  netstat -ano | findstr LISTENING
  ```
- 检查代理端口：
  ```powershell
  netstat -ano | findstr 7890
  ```

---
如遇问题可根据上述步骤逐项排查。
