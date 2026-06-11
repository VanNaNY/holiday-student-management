-- =============================================================
-- 假期学生管理系统 - 数据库结构（MySQL 8 / 兼容 H2 MODE=MySQL）
-- 字符集 utf8mb4。所有时间字段统一 DATETIME。
-- 命名：表名小写下划线；状态/类型用 VARCHAR 枚举，注释列出取值。
-- =============================================================

-- ---------- 账号与权限 ----------
CREATE TABLE IF NOT EXISTS sys_user (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
  login_name   VARCHAR(64)  NOT NULL COMMENT '学号/工号，登录名',
  password     VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密后的密码',
  name         VARCHAR(64)  NOT NULL COMMENT '姓名',
  phone        VARCHAR(20)           COMMENT '手机号',
  avatar       VARCHAR(255)          COMMENT '头像 URL',
  enabled      TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_user_login UNIQUE (login_name)
) COMMENT '登录账号';

CREATE TABLE IF NOT EXISTS sys_role (
  id        BIGINT      PRIMARY KEY AUTO_INCREMENT,
  code      VARCHAR(32) NOT NULL COMMENT '角色码：STUDENT/COUNSELOR/SECRETARY/ADMIN',
  name      VARCHAR(32) NOT NULL COMMENT '角色名',
  CONSTRAINT uk_role_code UNIQUE (code)
) COMMENT '角色';

CREATE TABLE IF NOT EXISTS user_role (
  id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id  BIGINT NOT NULL,
  role_id  BIGINT NOT NULL,
  CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
) COMMENT '用户-角色绑定（支持一人多角色，用于角色切换）';

-- ---------- 组织架构 ----------
CREATE TABLE IF NOT EXISTS org_college (
  id    BIGINT      PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(64) NOT NULL COMMENT '学院/院部名称'
) COMMENT '学院/院部';

CREATE TABLE IF NOT EXISTS org_class (
  id           BIGINT      PRIMARY KEY AUTO_INCREMENT,
  college_id   BIGINT      NOT NULL,
  grade        VARCHAR(16)          COMMENT '年级，如 2023',
  major        VARCHAR(64)          COMMENT '专业',
  name         VARCHAR(64) NOT NULL COMMENT '班级名',
  counselor_id BIGINT               COMMENT '辅导员 staff.id'
) COMMENT '班级';

-- ---------- 人员档案 ----------
CREATE TABLE IF NOT EXISTS student (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
  user_id      BIGINT       NOT NULL,
  college_id   BIGINT       NOT NULL,
  class_id     BIGINT,
  dorm_address VARCHAR(128)          COMMENT '校内住宿地址',
  CONSTRAINT uk_student_user UNIQUE (user_id)
) COMMENT '学生档案';

CREATE TABLE IF NOT EXISTS staff (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT      NOT NULL,
  college_id  BIGINT      NOT NULL,
  title       VARCHAR(32)          COMMENT '职务：辅导员/副书记 等',
  CONSTRAINT uk_staff_user UNIQUE (user_id)
) COMMENT '教职工档案（辅导员/副书记）';

-- ---------- 假期批次与规则 ----------
CREATE TABLE IF NOT EXISTS holiday_batch (
  id                 BIGINT      PRIMARY KEY AUTO_INCREMENT,
  name               VARCHAR(64) NOT NULL COMMENT '批次名，如 2026 暑假',
  holiday_start      DATE        COMMENT '假期开始',
  holiday_end        DATE        COMMENT '假期结束',
  leave_open_start   DATETIME    COMMENT '离校登记开放-始',
  leave_open_end     DATETIME    COMMENT '离校登记开放-止',
  stay_open_start    DATETIME    COMMENT '留校申请开放-始',
  stay_open_end      DATETIME    COMMENT '留校申请开放-止',
  return_open_start  DATETIME    COMMENT '返校登记开放-始',
  return_open_end    DATETIME    COMMENT '返校登记开放-止',
  status             VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/ACTIVE/CLOSED',
  create_time        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '假期批次（含各登记开放时间窗）';

CREATE TABLE IF NOT EXISTS checkin_rule (
  id           BIGINT      PRIMARY KEY AUTO_INCREMENT,
  batch_id     BIGINT      NOT NULL,
  college_id   BIGINT               COMMENT '为空表示全校通用',
  type         VARCHAR(16) NOT NULL COMMENT 'STAY 留校签到 / RETURN 返校报到',
  time_start   DATETIME    NOT NULL COMMENT '签到时段-始',
  time_end     DATETIME    NOT NULL COMMENT '签到时段-止',
  fence_lat    DECIMAL(10,6)        COMMENT '围栏中心纬度',
  fence_lng    DECIMAL(10,6)        COMMENT '围栏中心经度',
  fence_radius INT                  COMMENT '围栏半径(米)'
) COMMENT '签到规则（时间段 + 地理围栏）';

-- ---------- 离校登记 ----------
CREATE TABLE IF NOT EXISTS leave_registration (
  id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
  student_id      BIGINT       NOT NULL,
  batch_id        BIGINT       NOT NULL,
  plan_leave_time DATETIME              COMMENT '计划离校时间',
  plan_arrive_time DATETIME             COMMENT '计划到达时间',
  dest_province   VARCHAR(32),
  dest_city       VARCHAR(32),
  dest_district   VARCHAR(32),
  dest_address    VARCHAR(255)          COMMENT '目的地详细地址',
  emergency_name  VARCHAR(32)           COMMENT '紧急联系人',
  emergency_phone VARCHAR(20)           COMMENT '紧急联系人电话',
  remark          VARCHAR(255),
  status          VARCHAR(16)  NOT NULL DEFAULT 'NOT_REG'
                  COMMENT 'NOT_REG未登记/REGISTERED已登记/ON_THE_WAY离校途中/ARRIVED已到目的地',
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_leave_stu_batch UNIQUE (student_id, batch_id)
) COMMENT '离校登记';

-- ---------- 返校登记（免审批） ----------
CREATE TABLE IF NOT EXISTS return_registration (
  id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
  student_id      BIGINT       NOT NULL,
  batch_id        BIGINT       NOT NULL,
  plan_depart_time DATETIME             COMMENT '计划出发时间',
  plan_arrive_time DATETIME             COMMENT '计划返校到达时间',
  depart_province VARCHAR(32),
  depart_city     VARCHAR(32),
  depart_district VARCHAR(32),
  depart_address  VARCHAR(255)          COMMENT '出发地详细地址',
  status          VARCHAR(16)  NOT NULL DEFAULT 'NOT_REG'
                  COMMENT 'NOT_REG未登记/REGISTERED已登记/ON_THE_WAY返校途中/ARRIVED已返校',
  editable_before DATETIME              COMMENT '可修改截止时间',
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_return_stu_batch UNIQUE (student_id, batch_id)
) COMMENT '返校登记';

-- ---------- 多段行程（离校/返校共用） ----------
CREATE TABLE IF NOT EXISTS trip (
  id             BIGINT      PRIMARY KEY AUTO_INCREMENT,
  ref_id         BIGINT      NOT NULL COMMENT '关联登记 id',
  ref_type       VARCHAR(16) NOT NULL COMMENT 'LEAVE 离校 / RETURN 返校',
  seq            INT         NOT NULL COMMENT '行程序号 1..10',
  transport      VARCHAR(16)          COMMENT '交通方式：火车/飞机/汽车/自驾 等',
  transport_info VARCHAR(64)          COMMENT '车次/航班号等',
  from_station   VARCHAR(128)         COMMENT '出发站点',
  dest_province  VARCHAR(32),
  dest_city      VARCHAR(32),
  dest_district  VARCHAR(32),
  dest_station   VARCHAR(128)         COMMENT '到达站点',
  depart_time    DATETIME,
  arrive_time    DATETIME
) COMMENT '多段行程（每条登记最多 10 段）';

-- ---------- 留校申请与审批 ----------
CREATE TABLE IF NOT EXISTS stay_application (
  id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
  student_id      BIGINT       NOT NULL,
  batch_id        BIGINT       NOT NULL,
  plan_start      DATE                  COMMENT '留校开始日期',
  plan_end        DATE                  COMMENT '留校结束日期',
  campus          VARCHAR(8)            COMMENT '校区 A/B',
  reason          VARCHAR(500)          COMMENT '留校事由',
  emergency_name  VARCHAR(32),
  emergency_phone VARCHAR(20),
  origin_dorm     VARCHAR(128)          COMMENT '原宿舍',
  central_dorm_id BIGINT                COMMENT '集中住宿地址 id',
  manager_id      BIGINT                COMMENT '留校责任人 id',
  approval_status VARCHAR(16)  NOT NULL DEFAULT 'PENDING'
                  COMMENT 'PENDING待审批/APPROVED通过/REJECTED驳回/WITHDRAWN已撤回',
  current_node    VARCHAR(16)  NOT NULL DEFAULT 'COUNSELOR'
                  COMMENT '当前审批节点：COUNSELOR辅导员/SECRETARY副书记/DONE',
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '留校申请';

CREATE TABLE IF NOT EXISTS stay_attachment (
  id             BIGINT       PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT       NOT NULL,
  file_url       VARCHAR(255) NOT NULL COMMENT '附件图片 URL'
) COMMENT '留校申请附件（1~5 张图）';

CREATE TABLE IF NOT EXISTS approval_record (
  id             BIGINT       PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT       NOT NULL,
  seq            INT          NOT NULL COMMENT '步骤序号',
  approver_id    BIGINT                COMMENT '操作人 user.id',
  approver_role  VARCHAR(16)           COMMENT 'STUDENT/COUNSELOR/SECRETARY',
  action         VARCHAR(16)  NOT NULL COMMENT 'SUBMIT发起/APPROVE通过/REJECT驳回/WITHDRAW撤回',
  comment        VARCHAR(255),
  create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '审批流水（学生发起→辅导员→副书记）';

-- ---------- 签到记录 ----------
CREATE TABLE IF NOT EXISTS stay_checkin (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
  student_id   BIGINT       NOT NULL,
  batch_id     BIGINT       NOT NULL,
  rule_id      BIGINT,
  checkin_date DATE         NOT NULL COMMENT '签到日期',
  checkin_time DATETIME              COMMENT '实际签到时间',
  lat          DECIMAL(10,6),
  lng          DECIMAL(10,6),
  address      VARCHAR(255),
  status       VARCHAR(16)  NOT NULL DEFAULT 'NOT_YET' COMMENT 'DONE已签到/NOT_YET未签到',
  CONSTRAINT uk_stay_checkin UNIQUE (student_id, batch_id, checkin_date)
) COMMENT '留校签到记录（按天）';

CREATE TABLE IF NOT EXISTS return_checkin (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  student_id  BIGINT       NOT NULL,
  batch_id    BIGINT       NOT NULL,
  checkin_time DATETIME             COMMENT '返校报到时间',
  lat         DECIMAL(10,6),
  lng         DECIMAL(10,6),
  address     VARCHAR(255),
  status      VARCHAR(16)  NOT NULL DEFAULT 'NOT_YET' COMMENT 'DONE已报到/NOT_YET未报到',
  CONSTRAINT uk_return_checkin UNIQUE (student_id, batch_id)
) COMMENT '返校报到确认';

-- ---------- 留校配套基础数据 ----------
CREATE TABLE IF NOT EXISTS stay_manager (
  id         BIGINT      PRIMARY KEY AUTO_INCREMENT,
  college_id BIGINT,
  name       VARCHAR(64) NOT NULL COMMENT '责任人姓名',
  phone      VARCHAR(20)          COMMENT '联系电话',
  on_campus  TINYINT     NOT NULL DEFAULT 1 COMMENT '是否在校 1是 0否'
) COMMENT '留校责任人';

CREATE TABLE IF NOT EXISTS central_dorm (
  id       BIGINT      PRIMARY KEY AUTO_INCREMENT,
  campus   VARCHAR(8)           COMMENT '校区 A/B',
  building VARCHAR(64)          COMMENT '楼栋',
  address  VARCHAR(255) NOT NULL COMMENT '集中住宿地址'
) COMMENT '留校集中住宿地址';
