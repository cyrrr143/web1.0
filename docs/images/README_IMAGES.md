#  界面截图上传指南

本目录用于存放项目界面截图，这些截图将在 README.md 中展示。

## 📁 文件命名规范

请将你的截图按照以下命名规则保存到 `docs/images/` 目录：

### 用户端（小程序）截图

| 文件名 | 说明 | 对应页面 |
|--------|------|---------|
| `client-home.jpg` (img.png) | 餐厅主页 | 第一张图 - 点餐首页 |
| `client-menu.jpg` (缺失) | 菜品列表 | 第二张图 - 选择菜品 |
| `client-order.jpg` (img_1.png) | 提交订单 | 第三张图 - 订单确认页 |
| `client-orders.jpg` (img_2.png) | 订单历史 | 第四张图 - 查看订单记录 |

### 管理端（商家后台）截图

| 文件名 | 说明 | 对应页面 |
|--------|------|---------|
| `admin-orders.jpg` (img_3.png) | 订单管理 | 第五张图 - 订单列表和详情弹窗 |
| `admin-dishes.jpg` (img_4.png) | 菜品管理 | 第六张图 - 菜品列表页面 |

##  上传步骤

### 方法一：手动复制

1. 将你的截图重命名为上述文件名
2. 复制到 `sky-take-out/docs/images/` 目录
3. 提交到 Git：

```bash
cd sky-take-out
git add docs/images/
git commit -m "Add interface screenshots"
git push origin master
```

### 方法二：使用 Git 命令

```bash
# 假设你的截图在桌面
cd sky-take-out

# 复制并重命名用户端截图
copy C:\Users\YourName\Desktop\screenshot1.png docs/images/client-home.jpg
copy C:\Users\YourName\Desktop\screenshot2.png docs/images/client-menu.jpg
copy C:\Users\YourName\Desktop\screenshot3.png docs/images/client-order.jpg
copy C:\Users\YourName\Desktop\screenshot4.png docs/images/client-orders.jpg

# 复制并重命名管理端截图
copy C:\Users\YourName\Desktop\screenshot5.png docs/images/admin-orders.jpg
copy C:\Users\YourName\Desktop\screenshot6.png docs/images/admin-dishes.jpg

# 提交到 Git
git add docs/images/
git commit -m "Add interface screenshots"
git push origin master
```

## 🖼️ 图片要求

- **格式**：JPG 或 PNG（推荐 JPG，体积更小）
- **尺寸**：
  - 小程序截图：宽度约 375px（iPhone 标准宽度）
  - 管理端截图：宽度约 1920px（完整页面截图）
- **大小**：每张图片建议小于 500KB
- **清晰度**：保证文字清晰可读

## 🔍 预览效果

上传图片后，访问 GitHub 仓库即可看到效果：

https://github.com/cyrrr143/diy-1.0

README.md 中的"界面展示"部分会自动显示这些图片。

## ❗ 注意事项

1. **不要删除已上传的图片**：README.md 引用了这些图片路径
2. **保持一致性**：如果替换图片，请保持相同的文件名
3. **敏感信息打码**：确保截图中不包含真实的手机号、地址等隐私信息
4. **Git LFS（可选）**：如果图片较多或较大，可以考虑使用 Git LFS 管理

## 💡 小贴士

- 可以使用截图工具（如 Snipaste、微信截图）直接截取
- 建议使用浏览器开发者工具模拟手机尺寸进行小程序截图
- 管理端截图建议使用全屏模式，保证内容完整

---

如有问题，欢迎提交 Issue！
