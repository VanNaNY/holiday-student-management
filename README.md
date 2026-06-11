# holiday-student-management

假期学生管理系统 (Holiday Student Management System)

校园假期「离校 / 留校 / 返校」管理系统：学生在线登记离校/返校行程、申请留校并走多级审批、按地理围栏+时间段签到；辅导员/副书记审批、统计、催办；学工处通过 PC 后台管理批次、学生与全局统计。

## 项目结构

本仓库采用前后端分离的单仓库（monorepo）结构：

```
holiday-student-management/
├─ backend/    # 后端：Spring Boot 3 + Java 17 + Maven（REST API）
├─ frontend/   # 移动端 H5：Vue 3 + Vite + Vant（学生/辅导员/副书记，按角色路由）
├─ admin/      # PC 管理后台：Vue 3 + Vite + Element Plus
├─ docs/
│  ├─ db/      # schema.sql 建表脚本 + data.sql 种子数据
│  └─ design/  # 需求设计图（功能基线）
└─ README.md   # 项目总说明（本文件）
```

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Spring Boot 3.3 · Java 17 · Maven · MyBatis-Plus · JWT · MySQL(prod)/H2(dev) · springdoc(Swagger) · EasyExcel |
| 移动端 | Vue 3 · Vite · Vant 4 · Vue Router · Pinia · Axios |
| PC 后台 | Vue 3 · Vite · Element Plus · Vue Router · Pinia · Axios |

## 角色

`STUDENT`(学生) / `COUNSELOR`(辅导员) / `SECRETARY`(副书记) / `ADMIN`(后台)。一名教职工可兼多角色，移动端支持「角色切换」。

## 本地运行

### 后端（backend）

需要 JDK 17 与 Maven。默认 `dev` profile 使用 H2 文件数据库，无需安装 MySQL 即可启动。

```bash
cd backend
mvn spring-boot:run
# 验证：
#   健康检查   http://localhost:8080/api/ping
#   Swagger UI http://localhost:8080/api/swagger-ui.html
#   H2 控制台  http://localhost:8080/api/h2-console
```

切换到 MySQL（`prod` profile）：建库后通过环境变量注入连接信息，导入 `docs/db/schema.sql`、`docs/db/data.sql`，再以 `prod` profile 启动。

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 移动端（frontend）

```bash
cd frontend
npm install
npm run dev    # http://localhost:5173 （/api 自动代理到后端 8080）
```

### PC 后台（admin）

```bash
cd admin
npm install
npm run dev    # http://localhost:5174 （/api 自动代理到后端 8080）
```

## 测试账号（种子数据，密码均为 `123456`）

| 登录名 | 姓名 | 角色 |
|--------|------|------|
| 2023001 | 张三 | 学生 |
| 2023002 | 李四 | 学生 |
| T1001 | 王辅导 | 辅导员 |
| T2001 | 赵书记 | 副书记 + 辅导员（演示角色切换）|
| admin | 系统管理员 | 后台管理员 |

> 登录鉴权（JWT）在 Phase 1 接入，接入后上述账号即可登录。

## 开发进度

按阶段交付，每阶段可独立运行验证：

- [x] **Phase 0** 脚手架与工具链：三端工程可运行、DB 脚本、文档
- [ ] **Phase 1** 基础域：鉴权/JWT/角色切换、组织架构/学生、假期批次
- [ ] **Phase 2** 学生登记：离校登记 / 留校申请(含附件) / 返校登记 + 多段行程
- [ ] **Phase 3** 审批工作流：辅导员 → 副书记多级审批、撤回重提
- [ ] **Phase 4** 签到：留校签到 / 返校报到（地理围栏 + 时间段）
- [ ] **Phase 5** 管理与统计：批次统计、导出、催办、帮助重置
- [ ] **Phase 6** PC 后台完善与三端联调

## 协作约定

- 主分支为 `main`，各自开功能分支开发，通过 Pull Request 合并。
- 代码标识符/接口用英文；面向中文用户的注释用中文。
