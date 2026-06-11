-- =============================================================
-- 假期学生管理系统 - 种子数据（开发/演示用）
-- 说明：密码均为明文占位 "123456" 的 BCrypt 值，登录功能在 Phase 1 接入后可用。
-- BCrypt("123456") = $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- =============================================================

-- 角色
INSERT INTO sys_role (id, code, name) VALUES
  (1, 'STUDENT',   '学生'),
  (2, 'COUNSELOR', '辅导员'),
  (3, 'SECRETARY', '副书记'),
  (4, 'ADMIN',     '管理员');

-- 学院
INSERT INTO org_college (id, name) VALUES
  (1, '计算机科学与技术学院'),
  (2, '电子信息工程学院');

-- 账号（密码均为 123456）
INSERT INTO sys_user (id, login_name, password, name, phone) VALUES
  (1, '2023001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13800000001'),
  (2, '2023002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13800000002'),
  (3, 'T1001',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王辅导', '13900000001'),
  (4, 'T2001',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵书记', '13900000002'),
  (5, 'admin',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13700000000');

-- 用户-角色（王辅导兼辅导员+副书记演示角色切换）
INSERT INTO user_role (user_id, role_id) VALUES
  (1, 1), (2, 1),
  (3, 2), (4, 3), (4, 2),
  (5, 4);

-- 教职工档案
INSERT INTO staff (id, user_id, college_id, title) VALUES
  (1, 3, 1, '辅导员'),
  (2, 4, 1, '副书记');

-- 班级（辅导员为 staff.id=1）
INSERT INTO org_class (id, college_id, grade, major, name, counselor_id) VALUES
  (1, 1, '2023', '计算机科学与技术', '计科2301', 1),
  (2, 1, '2023', '软件工程', '软工2301', 1);

-- 学生档案
INSERT INTO student (id, user_id, college_id, class_id, dorm_address) VALUES
  (1, 1, 1, 1, '紫荆公寓 5#-301'),
  (2, 2, 1, 2, '紫荆公寓 6#-208');

-- 假期批次（当前生效）
INSERT INTO holiday_batch
  (id, name, holiday_start, holiday_end,
   leave_open_start, leave_open_end, stay_open_start, stay_open_end,
   return_open_start, return_open_end, status) VALUES
  (1, '2026 暑假', '2026-07-10', '2026-08-31',
   '2026-06-01 00:00:00', '2026-07-09 23:59:59',
   '2026-06-01 00:00:00', '2026-07-05 23:59:59',
   '2026-06-01 00:00:00', '2026-08-31 23:59:59',
   'ACTIVE');

-- 签到规则（留校签到，每天时段 + 校园围栏，半径 800m，坐标为示例）
INSERT INTO checkin_rule
  (id, batch_id, college_id, type, time_start, time_end, fence_lat, fence_lng, fence_radius) VALUES
  (1, 1, NULL, 'STAY',   '2026-07-10 08:00:00', '2026-07-10 20:00:00', 37.860000, 112.450000, 800),
  (2, 1, NULL, 'RETURN', '2026-08-30 08:00:00', '2026-08-31 23:59:59', 37.860000, 112.450000, 800);

-- 留校集中住宿地址
INSERT INTO central_dorm (id, campus, building, address) VALUES
  (1, 'A', '集中楼1#', 'A 校区集中住宿楼 1#'),
  (2, 'B', '集中楼2#', 'B 校区集中住宿楼 2#');

-- 留校责任人
INSERT INTO stay_manager (id, college_id, name, phone, on_campus) VALUES
  (1, 1, '王辅导', '13900000001', 1);
