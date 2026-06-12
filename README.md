# 🍜 Sky Takeout - 苍穹外卖后端系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.3-brightgreen?style=for-the-badge&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Latest-red?style=for-the-badge&logo=redis)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**一个基于 Spring Boot 的外卖订餐系统后端实现，支持管理端和用户端双端架构**

[功能特性](#-功能特性) • [界面展示](#-界面展示) • [技术栈](#-技术栈) • [快速开始](#-快速开始) • [项目结构](#-项目结构) • [API文档](#-api文档)

</div>

---

## 📖 项目简介

Sky Takeout（苍穹外卖）是一个完整的外卖订餐系统后端项目，采用 **Spring Boot + MyBatis** 技术栈构建。项目实现了从用户浏览菜品、下单支付到商家接单、配送的完整业务流程。

### 核心特点

- ✅ **双端分离架构**：管理端（Admin）和用户端（User）独立接口设计
- ✅ **JWT 认证**：基于 JWT Token 的无状态身份验证
- ✅ **Redis 缓存**：验证码、购物车、营业状态等高频数据缓存
- ✅ **自动填充**：AOP 切面实现公共字段（创建时间、修改人等）自动填充
- ✅ **文件上传**：集成阿里云 OSS 对象存储服务
- ✅ **微信支付**：支持微信支付的订单结算功能
- ✅ **实时推送**：WebSocket 实现来单提醒和客户催单
- ✅ **数据统计**：Excel 导出营业额、订单统计等报表

---

## 📱 界面展示

### 用户端（小程序）

#### 点餐流程

<div align="center">
<table>
<tr>
<td align="center"><b>餐厅主页</b></td>
<td align="center"><b>菜品列表</b></td>
<td align="center"><b>提交订单</b></td>
</tr>
<tr>
<td align="center"><img src="docs/images/client-home.jpg" width="250" alt="餐厅主页"/></td>
<td align="center"><img src="docs/images/client-menu.jpg" width="250" alt="菜品列表"/></td>
<td align="center"><img src="docs/images/client-order.jpg" width="250" alt="提交订单"/></td>
</tr>
</table>
</div>

**功能说明：**
- **餐厅主页**：展示餐厅信息、距离、配送费、预计送达时间
- **菜品列表**：分类浏览菜品，支持选择规格加入购物车
- **提交订单**：确认收货地址、商品明细、打包费、配送费，选择支付方式

#### 订单管理

<div align="center">
<img src="docs/images/client-orders.jpg" width="250" alt="订单历史"/>
</div>

**功能说明：**
- 查看历史订单记录
- 订单状态显示（待付款、已完成、已取消等）
- 支持再来一单、去支付等操作

---

### 管理端（商家后台）

#### 订单管理

<div align="center">
<img src="docs/images/admin-orders.jpg" width="800" alt="订单管理"/>
</div>

**功能说明：**
- 查看所有订单列表（全部订单、待接单、待派送、派送中、已完成、已取消）
- 订单详情弹窗：查看用户信息、菜品明细、费用明细、备注等
- 订单操作：接单、拒单、取消、派送、完成

#### 菜品管理

<div align="center">
<img src="docs/images/admin-dishes.jpg" width="800" alt="菜品管理"/>
</div>

**功能说明：**
- 菜品列表展示：名称、图片、分类、售价、售卖状态、最后操作时间
- 支持按菜品名称、分类、售卖状态筛选查询
- 菜品操作：新建、修改、删除、启售、停售
- 批量删除功能

---

## 🚀 功能特性

### 管理端功能

| 模块 | 功能描述 |
|------|---------|
|  员工管理 | 员工登录、新增、查询、启用/禁用、编辑、删除 |
| 📂 分类管理 | 菜品/套餐分类的新增、查询、修改、删除、启用/禁用 |
|  菜品管理 | 菜品的 CRUD、起售/停售、图片上传、口味配置 |
| 🎁 套餐管理 | 套餐的 CRUD、起售/停售、关联菜品管理 |
| 📋 订单管理 | 订单搜索、详情查看、接单、拒单、取消、派送、完成 |
|  数据统计 | 营业额统计、用户统计、订单统计、销量排名 TOP10 |
| 🏪 店铺管理 | 营业状态设置 |

### 用户端功能

| 模块 | 功能描述 |
|------|---------|
| 🔐 用户登录 | 微信授权登录，获取 OpenID |
| 🍽️ 菜品浏览 | 根据分类查询菜品和套餐列表 |
|  购物车 | 添加商品、查看购物车、清空购物车 |
| 📦 地址管理 | 收货地址的增删改查、设置默认地址 |
|  订单提交 | 选择地址、支付方式、备注信息、下单 |
| 💰 订单支付 | 模拟微信支付流程 |
|  历史订单 | 订单状态筛选、订单详情、再来一单 |
| ⚡ 催单功能 | WebSocket 实时推送催单消息 |

---

## 🛠️ 技术栈

### 核心框架

- **Java**: 17
- **Spring Boot**: 2.7.3
- **MyBatis**: 3.5.7 (配合 XML 映射)
- **Maven**: 项目构建工具

### 数据存储

- **MySQL**: 8.0.30 - 关系型数据库
- **Druid**: 1.2.1 - 数据库连接池（支持监控和 SQL 防注入）
- **Redis**: Latest - 缓存服务（验证码、购物车、营业状态）
- **PageHelper**: 1.3.0 - MyBatis 分页插件

### 安全认证

- **JWT (jjwt)**: 0.9.1 - Token 生成与验证
- **拦截器**: 双端 JWT 拦截器（管理端 `/admin/**`，用户端 `/user/**`）

### 第三方服务

- **阿里云 OSS**: 3.10.2 - 对象存储（图片上传）
- **微信支付 SDK**: 0.4.8 - 微信支付集成
- **HttpClient**: 4.5.13 - HTTP 客户端（调用第三方接口）

### 工具库

- **Lombok**: 1.18.20 - 简化实体类代码
- **Knife4j**: 3.0.2 - API 文档生成（Swagger 增强版）
- **AspectJ**: 1.9.4 - AOP 切面编程（自动填充公共字段）
- **Apache POI**: 3.16 - Excel 导入导出（数据统计报表）
- **Fastjson**: 1.2.76 - JSON 处理
- **WebSocket**: Spring Boot 内置 - 实时消息推送

### 日志框架

- **SLF4J** + **Logback**: 统一日志管理

---

## 📁 项目结构

```
sky-take-out/                    # Maven 父工程
├── sky-common/                  # 公共模块
│   ├── constant/                # 常量定义（MessageConstant, StatusConstant 等）
│   ├── context/                 # 上下文（BaseContext 存储当前用户 ID）
│   ├── enumeration/             # 枚举类（OperationType 操作类型）
│   ├── exception/               # 自定义异常类
│   ├── json/                    # Jackson 配置（LocalDateTime 序列化）
│   ├── properties/              # 配置属性类（JwtProperties, AliOssProperties 等）
│   ├── result/                  # 统一返回结果（Result<T>, PageResult）
│   └── utils/                   # 工具类
│       ├── JwtUtil.java         # JWT 工具类
│       ├── AliOssUtil.java      # 阿里云 OSS 工具类
│       ├── HttpClientUtil.java  # HTTP 客户端工具类
│       └── WeChatPayUtil.java   # 微信支付工具类
│
├── sky-pojo/                    # 实体类模块
│   ├── dto/                     # 数据传输对象（接收前端参数）
│   ├── entity/                  # 数据库实体类（对应表结构）
│   └── vo/                      # 视图对象（返回给前端）
│
└── sky-server/                  # 服务端主模块
    ├── annotation/              # 自定义注解
    │   └── AutoFill.java        # 自动填充注解
    ├── aspect/                  # AOP 切面
    │   └── AutoFillAspect.java  # 自动填充切面（INSERT/UPDATE 时填充公共字段）
    ├── config/                  # 配置类
    │   ├── WebMvcConfiguration.java     # MVC 配置、拦截器、Knife4j
    │   ├── RedisConfiguration.java      # Redis Template 配置
    │   └── OssConfiguration.java        # 阿里云 OSS 配置
    ├── controller/              # 控制器层
    │   ├── admin/               # 管理端接口（Employee, Category, Dish, Setmeal, Order, Shop, Report）
    │   └── user/                # 用户端接口（Shop, Dish, ShoppingCart, AddressBook, Order, User）
    ├── handler/                 # 全局异常处理器
    │   └── GlobalExceptionHandler.java
    ├── interceptor/             # 拦截器
    │   ├── JwtTokenAdminInterceptor.java  # 管理端 JWT 拦截器
    │   └── JwtTokenUserInterceptor.java   # 用户端 JWT 拦截器
    ├── mapper/                  # Mapper 接口 + XML
    ├── service/                 # Service 层
    │   └── impl/                # Service 实现类
    └── resources/
        ├── mapper/              # MyBatis XML 映射文件
        ├── application.yml      # 主配置文件
        └── application-dev.yml  # 开发环境配置（已加入 .gitignore）
```

---

## 🚦 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 安装步骤

#### 1️⃣ 克隆项目

```bash
git clone https://github.com/cyrrr143/diy-1.0.git
cd diy-1.0/sky-take-out
```

#### 2️⃣ 数据库准备

执行 SQL 脚本创建数据库和表：

```bash
# 在 MySQL 中执行
mysql -u root -p < create_user_table.sql
mysql -u root -p < create_shopping_cart_table.sql
# 其他表结构请参考项目中的 SQL 文件或自行创建
```

#### 3️⃣ 配置环境变量

项目使用环境变量管理敏感配置，请在 IDEA 中配置 Run Configuration → Environment variables：

```env
ALIYUN_OSS_ENDPOINT=oss-cn-beijing.aliyuncs.com
ALIYUN_ACCESS_KEY_ID=你的AccessKey ID
ALIYUN_ACCESS_KEY_SECRET=你的AccessKey Secret
ALIYUN_OSS_BUCKET_NAME=你的Bucket名称
```

或者修改 `application-dev.yml` 中的配置（注意不要提交到 Git）。

#### 4️ 启动 Redis

```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
```

#### 5️ 启动应用

```bash
# 方式 1: Maven 命令
mvn spring-boot:run -pl sky-server

# 方式 2: IDEA 直接运行 SkyApplication.java
```

应用启动后访问：
- **API 文档**: http://localhost:8080/doc.html
- **健康检查**: http://localhost:8080/actuator/health

---

## 📚 API 文档

项目集成了 **Knife4j**（Swagger 增强版），启动应用后访问：

 **http://localhost:8080/doc.html**

可以在线查看所有接口的详细信息，并直接测试接口。

### 主要接口概览

#### 管理端接口 (`/admin/**`)

| 模块 | 接口示例 |
|------|---------|
| 员工管理 | `POST /admin/employee/login`, `GET /admin/employee/page` |
| 分类管理 | `POST /admin/category`, `PUT /admin/category` |
| 菜品管理 | `POST /admin/dish`, `GET /admin/dish/page` |
| 套餐管理 | `POST /admin/setmeal`, `PUT /admin/setmeal/status/1` |
| 订单管理 | `GET /admin/order/conditionSearch`, `PUT /admin/order/cancel` |
| 数据统计 | `GET /admin/report/turnoverStatistics` |

#### 用户端接口 (`/user/**`)

| 模块 | 接口示例 |
|------|---------|
| 用户登录 | `POST /user/user/login` |
| 菜品浏览 | `GET /user/dish/list?categoryId=1` |
| 购物车 | `POST /user/shoppingCart/add`, `DELETE /user/shoppingCart/clean` |
| 地址管理 | `POST /user/addressBook`, `GET /user/addressBook/list` |
| 订单提交 | `POST /user/order/submit` |
| 订单支付 | `PUT /user/order/payment` |
| 历史订单 | `GET /user/order/historyOrders?page=1&pageSize=10` |

---

## 🔑 核心设计亮点

### 1️⃣ 双端 JWT 拦截器架构

```java
// WebMvcConfiguration.java
registry.addInterceptor(jwtTokenAdminInterceptor)
        .addPathPatterns("/admin/**")
        .excludePathPatterns("/admin/employee/login");

registry.addInterceptor(jwtTokenUserInterceptor)
        .addPathPatterns("/user/**")
        .excludePathPatterns("/user/user/login");
```

- 管理端和用户端使用不同的 JWT Secret Key
- Token 中包含 `userId` claim，通过 `BaseContext.getCurrentId()` 获取当前用户

### 2️⃣ AOP 自动填充公共字段

```java
@AutoFill(OperationType.INSERT)
void insert(Employee employee);

@AutoFill(OperationType.UPDATE)
void update(Employee employee);
```

通过自定义注解 `@AutoFill` + AspectJ 切面，自动填充：
- **INSERT**: `createTime`, `createUser`, `updateTime`, `updateUser`
- **UPDATE**: `updateTime`, `updateUser`

### 3️ Redis 缓存策略

| 缓存内容 | Key 格式 | 过期时间 |
|---------|---------|---------|
| 验证码 | `code:{phone}` | 5 分钟 |
| 购物车 | `shoppingCart:{userId}` | 永不过期（清空时删除） |
| 营业状态 | `SHOP_STATUS` | 永不过期（手动更新） |

### 4️⃣ 统一异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AccountNotFoundException.class)
    public Result accountNotFound(AccountNotFoundException ex) {
        return Result.error(ex.getMessage());
    }
    
    // 其他异常处理...
}
```

所有业务异常继承自 `BaseException`，通过全局异常处理器统一返回 `Result` 格式。

---

## 📝 注意事项

### ⚠️ 敏感配置

`application-dev.yml` 包含数据库密码、阿里云 AccessKey 等敏感信息，已加入 `.gitignore`，**请勿提交到 Git**。

首次运行前请：
1. 复制 `application-dev.yml.template`（如果有）或手动创建
2. 配置数据库连接、Redis、阿里云 OSS 等信息
3. 或在 IDEA Run Configuration 中配置环境变量

###  安全建议

- 生产环境请使用 HTTPS
- JWT Secret Key 应使用强密码并定期更换
- 阿里云 AccessKey 应设置最小权限原则
- 数据库密码不要使用默认值

---

##  贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

##  许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 📧 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 📮 Email: your-email@example.com
- 💬 Issues: [GitHub Issues](https://github.com/cyrrr143/diy-1.0/issues)

---

<div align="center">

**如果这个项目对你有帮助，欢迎 Star ⭐ 支持一下！**

Made with ❤️ by [cyrrr143](https://github.com/cyrrr143)

</div>
