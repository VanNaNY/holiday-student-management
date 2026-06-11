# holiday-student-management

假期学生管理系统 (Holiday Student Management System)

校园假期「离校 / 留校 / 返校」管理系统：学生在线登记离校/返校行程、申请留校并走多级审批、按地理围栏+时间段签到；辅导员/副书记审批、统计、催办；学工处通过 PC 后台管理批次、学生、教职工与全局统计。

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

## 角色与权限

`STUDENT`(学生) / `COUNSELOR`(辅导员) / `SECRETARY`(副书记) / `ADMIN`(后台)。一名教职工可兼多角色，移动端支持「角色切换」（切换后重新签发携带 `activeRole` 的 JWT）。

管辖范围（Scope）：辅导员限本人管辖班级、副书记限本人所属院部、管理员为全部。统计、审批、签到、催办等均按当前激活角色的范围过滤。

## 功能清单

### 学生（移动端）
- 假期批次首页：当前生效批次、各登记开放时间窗
- 离校登记：目的地、计划离/到校时间、紧急联系人、多段行程（≤10）、状态流转（未登记/已登记/途中/已到达）
- 留校申请：A/B 校区、事由、紧急联系人、集中住宿地址、责任人、附件（1~5 张图）、发起多级审批、撤回重提
- 返校登记：计划行程、多段行程、截止前可改（免审批）
- 留校签到：浏览器定位 + 地理围栏（Haversine 距离）+ 签到时段校验
- 返校报到确认：定位 + 围栏 + 时段
- 更多功能：离校/返校/留校申请/签到记录、登记重置

### 辅导员 / 副书记（移动端）
- 假期审批：待审批列表（搜索、批量通过/驳回）、留校审批详情（学生档案+附件+审批流时间线）
- 审批记录：按批次/状态分页查询
- 批次统计：离校/留校/返校各状态人数、数据导出 Excel
- 留校签到汇总：按日 0/N → 签到详情（已签/未签分页、拨号催办）
- 假期未登记：搜索、拨号催办
- 帮助重置登记：单个/批量重置离校/返校/留校
- 更多：角色切换、留校责任人添加、集中住宿地址添加、修改学生离校时间

### PC 后台（admin）
- 控制台：当前批次、后端连通性、快捷入口
- 批次管理：批次 CRUD、开放时间窗、状态流转
- 学生管理：分页/增改删、Excel 模板下载与批量导入、按院系班级筛选
- 教职工管理：辅导员/副书记增改删、角色绑定、班级辅导员指派
- 统计与导出：批次三类状态概览、导出 xlsx
- 审批记录查询：按批次/状态/关键词分页、详情抽屉（含附件与审批流）
- 签到记录查询：按日汇总 + 某日已签/未签明细
- 系统配置：留校责任人、集中住宿地址维护

## 后端 API 一览（前缀 `/api`）

| 模块 | 主要接口 |
|------|----------|
| auth | `POST /auth/login` · `GET /auth/me` · `POST /auth/switch-role` |
| batch | `GET /batch` · `GET /batch/current` · `POST/PUT/DELETE /batch` · `PUT /batch/{id}/status` |
| org | `GET/POST /org/colleges` · `GET/POST /org/classes` · `PUT /org/classes/{id}`（指派辅导员） |
| student | `GET /student` · `POST /student` · `DELETE /student/{id}` · `POST /student/import` · `GET /student/import/template` |
| staff | `GET /staff` · `POST /staff` · `DELETE /staff/{id}` |
| leave / ret | 离校/返校登记 提交/修改/记录 + 行程 |
| stay | `POST /stay` · `POST /stay/{id}/withdraw` · `GET /stay/{id}` · `GET /stay/records` |
| approval | `GET /approval/pending` · `GET /approval/records` · `GET /approval/{id}` · `POST /approval/{id}/approve\|reject` · `POST /approval/batch` |
| checkin | `GET /checkin/rule` · `POST /checkin/stay\|return` · `GET /checkin/summary` · `GET /checkin/detail` |
| stat | `GET /stat/overview` · `GET /stat/export`（xlsx） |
| manage | `GET /manage/unregistered` · `POST /manage/reset-leave\|reset-return\|reset-stay` · `PUT /manage/leave-time` · `GET/POST /manage/managers` · `GET/POST /manage/central-dorms` |
| file | `POST /files`（图片上传，≤30M，png/jpg） |

完整接口见 Swagger UI：`http://localhost:8080/api/swagger-ui.html`。

## 本地运行

### 后端（backend）

需要 JDK 17 与 Maven。默认 `dev` profile 使用 H2 内存数据库，启动时自动执行 `classpath:db/schema.sql` + `data.sql`，无需安装 MySQL 即可启动（每次重启重置数据）。

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# 验证：
#   健康检查   http://localhost:8080/api/ping
#   Swagger UI http://localhost:8080/api/swagger-ui.html
#   H2 控制台  http://localhost:8080/api/h2-console  (JDBC: jdbc:h2:mem:holiday, 用户 SA)
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

## 生产部署

### 1. 数据库（MySQL）

```bash
# 建库并导入结构与种子数据
mysql -u root -p -e "CREATE DATABASE holiday DEFAULT CHARSET utf8mb4;"
mysql -u root -p holiday < docs/db/schema.sql
mysql -u root -p holiday < docs/db/data.sql   # 可选：演示账号/批次
```

### 2. 后端（prod profile，连接 MySQL）

通过环境变量注入连接信息后以 `prod` profile 启动；或打成可执行 jar 运行：

```bash
cd backend
mvn clean package -DskipTests
SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/holiday?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai' \
SPRING_DATASOURCE_USERNAME=holiday \
SPRING_DATASOURCE_PASSWORD=*** \
JWT_SECRET='请替换为不少于32字节的随机密钥' \
java -jar target/*.jar --spring.profiles.active=prod
```

上传文件保存在运行目录的 `uploads/`，通过 `/api/files/**` 访问；如需持久化请挂载该目录。

### 3. 前端 / 后台（静态部署）

```bash
cd frontend && npm install && npm run build   # 产物 dist/
cd admin    && npm install && npm run build   # 产物 dist/
```

将 `dist/` 交由 Nginx 托管，并把 `/api` 反向代理到后端服务（示例）：

```nginx
location / { root /var/www/frontend/dist; try_files $uri $uri/ /index.html; }
location /api/ { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
```

移动端与后台为两套独立站点，可分别绑定不同域名/路径。

## 测试账号（种子数据，密码均为 `123456`）

| 登录名 | 姓名 | 角色 |
|--------|------|------|
| 2023001 | 张三 | 学生 |
| 2023002 | 李四 | 学生 |
| T1001 | 王辅导 | 辅导员 |
| T2001 | 赵书记 | 副书记 + 辅导员（演示角色切换）|
| admin | 系统管理员 | 后台管理员 |

> 副书记 T2001 兼辅导员角色，登录默认激活辅导员；在移动端「角色切换」为副书记后方可执行终审。

## 开发进度

按阶段交付，每阶段可独立运行验证：

- [x] **Phase 0** 脚手架与工具链：三端工程可运行、DB 脚本、文档
- [x] **Phase 1** 基础域：鉴权/JWT/角色切换、组织架构/学生、假期批次
- [x] **Phase 2** 学生登记：离校登记 / 留校申请(含附件) / 返校登记 + 多段行程
- [x] **Phase 3** 审批工作流：辅导员 → 副书记多级审批、撤回重提
- [x] **Phase 4** 签到：留校签到 / 返校报到（地理围栏 + 时间段）
- [x] **Phase 5** 管理与统计：批次统计、导出、催办、帮助重置
- [x] **Phase 6** PC 后台完善（教职工管理/统计/审批查询/签到查询/系统配置）与三端联调

## 协作约定

- 主分支为 `main`，各自开功能分支开发，通过 Pull Request 合并。
- 代码标识符/接口用英文；面向中文用户的注释用中文。
