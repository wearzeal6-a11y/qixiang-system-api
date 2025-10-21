-- ========================================
-- 报名数据汇总API测试数据脚本
-- 用于测试报名数据汇总功能的各种场景
-- ========================================

-- 清理现有测试数据（如果存在）
DELETE FROM registrations WHERE team_id IN (1, 2, 3);
DELETE FROM athletes WHERE team_id IN (1, 2, 3);
DELETE FROM group_event_mappings WHERE group_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM events WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM groups WHERE id IN (1, 2, 3, 4, 5, 6);

-- ========================================
-- 1. 创建测试用的竞赛组别
-- ========================================

-- 插入测试组别（假设运动会ID为1）
INSERT INTO groups (id, name, sports_meet_id, gender, grade, max_leaders_per_team, max_athletes_per_team, max_events_per_athlete, max_participants_per_event, max_relays_per_team, allow_mixed_events, status, created_at) VALUES
(1, '高一男子组', 1, 'MALE', '高一', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW()),
(2, '高一女子组', 1, 'FEMALE', '高一', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW()),
(3, '高二男子组', 1, 'MALE', '高二', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW()),
(4, '高二女子组', 1, 'FEMALE', '高二', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW()),
(5, '高三男子组', 1, 'MALE', '高三', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW()),
(6, '高三女子组', 1, 'FEMALE', '高三', 2, 20, 3, 4, 1, FALSE, 'ACTIVE', NOW());

-- ========================================
-- 2. 创建测试用的比赛项目
-- ========================================

-- 插入测试比赛项目
INSERT INTO events (id, name, event_type, status, created_at) VALUES
(1, '100米', 'INDIVIDUAL', 'ACTIVE', NOW()),
(2, '200米', 'INDIVIDUAL', 'ACTIVE', NOW()),
(3, '400米', 'INDIVIDUAL', 'ACTIVE', NOW()),
(4, '800米', 'INDIVIDUAL', 'ACTIVE', NOW()),
(5, '1500米', 'INDIVIDUAL', 'ACTIVE', NOW()),
(6, '跳高', 'INDIVIDUAL', 'ACTIVE', NOW()),
(7, '跳远', 'INDIVIDUAL', 'ACTIVE', NOW()),
(8, '铅球', 'INDIVIDUAL', 'ACTIVE', NOW()),
(9, '4x100米接力', 'RELAY', 'ACTIVE', NOW()),
(10, '4x400米接力', 'RELAY', 'ACTIVE', NOW());

-- ========================================
-- 3. 创建组别-项目关联关系
-- ========================================

-- 为每个组别分配可参加的项目
-- 高一组别项目
INSERT INTO group_event_mappings (group_id, event_id, is_mandatory, created_at) VALUES
-- 高一男子组
(1, 1, TRUE, NOW()),  -- 100米（必报）
(1, 2, FALSE, NOW()), -- 200米（选报）
(1, 3, FALSE, NOW()), -- 400米（选报）
(1, 6, FALSE, NOW()), -- 跳高（选报）
(1, 7, FALSE, NOW()), -- 跳远（选报）
(1, 9, FALSE, NOW()), -- 4x100米接力（选报）
-- 高一女子组
(2, 1, TRUE, NOW()),  -- 100米（必报）
(2, 2, FALSE, NOW()), -- 200米（选报）
(2, 3, FALSE, NOW()), -- 400米（选报）
(2, 6, FALSE, NOW()), -- 跳高（选报）
(2, 7, FALSE, NOW()), -- 跳远（选报）
(2, 9, FALSE, NOW()), -- 4x100米接力（选报）
-- 高二男子组
(3, 1, TRUE, NOW()),  -- 100米（必报）
(3, 2, FALSE, NOW()), -- 200米（选报）
(3, 4, FALSE, NOW()), -- 800米（选报）
(3, 6, FALSE, NOW()), -- 跳高（选报）
(3, 8, FALSE, NOW()), -- 铅球（选报）
(3, 10, FALSE, NOW()), -- 4x400米接力（选报）
-- 高二女子组
(4, 1, TRUE, NOW()),  -- 100米（必报）
(4, 2, FALSE, NOW()), -- 200米（选报）
(4, 4, FALSE, NOW()), -- 800米（选报）
(4, 6, FALSE, NOW()), -- 跳高（选报）
(4, 8, FALSE, NOW()), -- 铅球（选报）
(4, 10, FALSE, NOW()), -- 4x400米接力（选报）
-- 高三男子组
(5, 1, TRUE, NOW()),  -- 100米（必报）
(5, 3, FALSE, NOW()), -- 400米（选报）
(5, 5, FALSE, NOW()), -- 1500米（选报）
(5, 7, FALSE, NOW()), -- 跳远（选报）
(5, 8, FALSE, NOW()), -- 铅球（选报）
(5, 9, FALSE, NOW()), -- 4x100米接力（选报）
-- 高三女子组
(6, 1, TRUE, NOW()),  -- 100米（必报）
(6, 3, FALSE, NOW()), -- 400米（选报）
(6, 5, FALSE, NOW()), -- 1500米（选报）
(6, 7, FALSE, NOW()), -- 跳远（选报）
(6, 8, FALSE, NOW()), -- 铅球（选报）
(6, 9, FALSE, NOW()); -- 4x100米接力（选报）

-- ========================================
-- 4. 创建测试用的运动员
-- ========================================

-- 为参赛单位1（测试单位）创建运动员
INSERT INTO athletes (id, name, gender, age, team_id, group_id, created_at) VALUES
-- 高一男子组运动员
(1, '张三', 'MALE', 16, 1, 1, NOW()),
(2, '李四', 'MALE', 16, 1, 1, NOW()),
(3, '王五', 'MALE', 16, 1, 1, NOW()),
(4, '赵六', 'MALE', 16, 1, 1, NOW()),
(5, '钱七', 'MALE', 16, 1, 1, NOW()),
-- 高一女子组运动员
(6, '孙八', 'FEMALE', 16, 1, 2, NOW()),
(7, '周九', 'FEMALE', 16, 1, 2, NOW()),
(8, '吴十', 'FEMALE', 16, 1, 2, NOW()),
(9, '郑十一', 'FEMALE', 16, 1, 2, NOW()),
(10, '王十二', 'FEMALE', 16, 1, 2, NOW()),
-- 高二男子组运动员
(11, '陈十三', 'MALE', 17, 1, 3, NOW()),
(12, '林十四', 'MALE', 17, 1, 3, NOW()),
(13, '黄十五', 'MALE', 17, 1, 3, NOW()),
(14, '何十六', 'MALE', 17, 1, 3, NOW()),
(15, '高十七', 'MALE', 17, 1, 3, NOW()),
-- 高二女子组运动员
(16, '马十八', 'FEMALE', 17, 1, 4, NOW()),
(17, '罗十九', 'FEMALE', 17, 1, 4, NOW()),
(18, '梁二十', 'FEMALE', 17, 1, 4, NOW()),
(19, '谢二一', 'FEMALE', 17, 1, 4, NOW()),
(20, '韩二二', 'FEMALE', 17, 1, 4, NOW());

-- 为参赛单位2创建部分运动员
INSERT INTO athletes (id, name, gender, age, team_id, group_id, created_at) VALUES
(21, '张明', 'MALE', 16, 2, 1, NOW()),
(22, '李华', 'MALE', 16, 2, 1, NOW()),
(23, '王芳', 'FEMALE', 16, 2, 2, NOW()),
(24, '赵敏', 'FEMALE', 16, 2, 2, NOW()),
(25, '陈强', 'MALE', 17, 2, 3, NOW()),
(26, '林丽', 'FEMALE', 17, 2, 4, NOW());

-- ========================================
-- 5. 创建测试用的报名记录
-- ========================================

-- 为参赛单位1创建报名记录
-- 领队教练报名（通过athlete_id为NULL来标识）
INSERT INTO registrations (team_id, group_id, athlete_id, event_id, status, registration_time) VALUES
-- 高一男子组领队教练
(1, 1, NULL, NULL, 'CONFIRMED', NOW()),
-- 高一女子组领队教练
(1, 2, NULL, NULL, 'CONFIRMED', NOW()),
-- 高二男子组领队教练
(1, 3, NULL, NULL, 'CONFIRMED', NOW()),
-- 高二女子组领队教练
(1, 4, NULL, NULL, 'CONFIRMED', NOW());

-- 运动员报名记录
INSERT INTO registrations (team_id, group_id, athlete_id, event_id, status, registration_time) VALUES
-- 高一男子组运动员报名
(1, 1, 1, 1, 'CONFIRMED', NOW()),  -- 张三报名100米（必报）
(1, 1, 1, 2, 'CONFIRMED', NOW()),  -- 张三报名200米
(1, 1, 1, 6, 'CONFIRMED', NOW()),  -- 张三报名跳高
(1, 1, 2, 1, 'CONFIRMED', NOW()),  -- 李四报名100米（必报）
(1, 1, 2, 3, 'CONFIRMED', NOW()),  -- 李四报名400米
(1, 1, 2, 7, 'CONFIRMED', NOW()),  -- 李四报名跳远
(1, 1, 3, 1, 'CONFIRMED', NOW()),  -- 王五报名100米（必报）
(1, 1, 3, 2, 'CONFIRMED', NOW()),  -- 王五报名200米
(1, 1, 4, 1, 'CONFIRMED', NOW()),  -- 赵六报名100米（必报）
(1, 1, 4, 6, 'CONFIRMED', NOW()),  -- 赵六报名跳高
(1, 1, 5, 1, 'CONFIRMED', NOW()),  -- 钱七报名100米（必报）
(1, 1, 5, 7, 'CONFIRMED', NOW()),  -- 钱七报名跳远

-- 高一女子组运动员报名
(1, 2, 6, 1, 'CONFIRMED', NOW()),  -- 孙八报名100米（必报）
(1, 2, 6, 2, 'CONFIRMED', NOW()),  -- 孙八报名200米
(1, 2, 7, 1, 'CONFIRMED', NOW()),  -- 周九报名100米（必报）
(1, 2, 7, 3, 'CONFIRMED', NOW()),  -- 周九报名400米
(1, 2, 8, 1, 'CONFIRMED', NOW()),  -- 吴十报名100米（必报）
(1, 2, 8, 6, 'CONFIRMED', NOW()),  -- 吴十报名跳高
(1, 2, 9, 1, 'CONFIRMED', NOW()),  -- 郑十一报名100米（必报）
(1, 2, 9, 7, 'CONFIRMED', NOW()),  -- 郑十一报名跳远
(1, 2, 10, 1, 'CONFIRMED', NOW()), -- 王十二报名100米（必报）
(1, 2, 10, 2, 'CONFIRMED', NOW()), -- 王十二报名200米

-- 高二男子组运动员报名
(1, 3, 11, 1, 'CONFIRMED', NOW()), -- 陈十三报名100米（必报）
(1, 3, 11, 2, 'CONFIRMED', NOW()), -- 陈十三报名200米
(1, 3, 11, 4, 'CONFIRMED', NOW()), -- 陈十三报名800米
(1, 3, 12, 1, 'CONFIRMED', NOW()), -- 林十四报名100米（必报）
(1, 3, 12, 6, 'CONFIRMED', NOW()), -- 林十四报名跳高
(1, 3, 13, 1, 'CONFIRMED', NOW()), -- 黄十五报名100米（必报）
(1, 3, 13, 8, 'CONFIRMED', NOW()), -- 黄十五报名铅球
(1, 3, 14, 1, 'CONFIRMED', NOW()), -- 何十六报名100米（必报）
(1, 3, 14, 2, 'CONFIRMED', NOW()), -- 何十六报名200米
(1, 3, 15, 1, 'CONFIRMED', NOW()), -- 高十七报名100米（必报）
(1, 3, 15, 10, 'CONFIRMED', NOW()), -- 高十七报名4x400米接力

-- 高二女子组运动员报名
(1, 4, 16, 1, 'CONFIRMED', NOW()), -- 马十八报名100米（必报）
(1, 4, 16, 2, 'CONFIRMED', NOW()), -- 马十八报名200米
(1, 4, 16, 4, 'CONFIRMED', NOW()), -- 马十八报名800米
(1, 4, 17, 1, 'CONFIRMED', NOW()), -- 罗十九报名100米（必报）
(1, 4, 17, 6, 'CONFIRMED', NOW()), -- 罗十九报名跳高
(1, 4, 18, 1, 'CONFIRMED', NOW()), -- 梁二十报名100米（必报）
(1, 4, 18, 8, 'CONFIRMED', NOW()), -- 梁二十报名铅球
(1, 4, 19, 1, 'CONFIRMED', NOW()), -- 谢二一报名100米（必报）
(1, 4, 19, 2, 'CONFIRMED', NOW()), -- 谢二一报名200米
(1, 4, 20, 1, 'CONFIRMED', NOW()), -- 韩二二报名100米（必报）
(1, 4, 20, 10, 'CONFIRMED', NOW()); -- 韩二二报名4x400米接力

-- 为参赛单位2创建部分报名记录
INSERT INTO registrations (team_id, group_id, athlete_id, event_id, status, registration_time) VALUES
-- 领队教练
(2, 1, NULL, NULL, 'CONFIRMED', NOW()),
(2, 2, NULL, NULL, 'CONFIRMED', NOW()),
-- 运动员报名
(2, 1, 21, 1, 'CONFIRMED', NOW()), -- 张明报名100米（必报）
(2, 1, 21, 2, 'CONFIRMED', NOW()), -- 张明报名200米
(2, 1, 22, 1, 'CONFIRMED', NOW()), -- 李华报名100米（必报）
(2, 2, 23, 1, 'CONFIRMED', NOW()), -- 王芳报名100米（必报）
(2, 2, 23, 6, 'CONFIRMED', NOW()), -- 王芳报名跳高
(2, 2, 24, 1, 'CONFIRMED', NOW()), -- 赵敏报名100米（必报）
(2, 3, 25, 1, 'CONFIRMED', NOW()), -- 陈强报名100米（必报）
(2, 4, 26, 1, 'CONFIRMED', NOW'); -- 林丽报名100米（必报）

-- ========================================
-- 6. 创建一些边界条件测试数据
-- ========================================

-- 创建一个超限的报名记录（用于测试超限检测）
-- 假设100米项目限制4人，我们添加第5个人
INSERT INTO registrations (team_id, group_id, athlete_id, event_id, status, registration_time) VALUES
(1, 1, 3, 1, 'CONFIRMED', NOW()); -- 王五额外报名100米（导致超限）

-- ========================================
-- 7. 验证数据插入结果
-- ========================================

-- 显示插入的数据统计
SELECT 'Groups' as table_name, COUNT(*) as record_count FROM groups WHERE id IN (1,2,3,4,5,6)
UNION ALL
SELECT 'Events', COUNT(*) FROM events WHERE id IN (1,2,3,4,5,6,7,8,9,10)
UNION ALL
SELECT 'Group-Event Mappings', COUNT(*) FROM group_event_mappings WHERE group_id IN (1,2,3,4,5,6)
UNION ALL
SELECT 'Athletes', COUNT(*) FROM athletes WHERE team_id IN (1,2)
UNION ALL
SELECT 'Registrations', COUNT(*) FROM registrations WHERE team_id IN (1,2);

-- 显示各组别的报名统计
SELECT 
    g.name as group_name,
    COUNT(DISTINCT CASE WHEN r.athlete_id IS NULL THEN r.id END) as leader_count,
    COUNT(DISTINCT CASE WHEN r.athlete_id IS NOT NULL THEN r.athlete_id END) as athlete_count,
    COUNT(r.id) as total_registrations
FROM groups g
LEFT JOIN registrations r ON g.id = r.group_id
WHERE g.id IN (1,2,3,4,5,6) AND r.team_id = 1
GROUP BY g.id, g.name
ORDER BY g.name;

-- 显示各项目的报名统计
SELECT 
    e.name as event_name,
    e.event_type,
    COUNT(r.id) as registration_count,
    COUNT(DISTINCT r.athlete_id) as athlete_count
FROM events e
LEFT JOIN registrations r ON e.id = r.event_id AND r.team_id = 1 AND r.athlete_id IS NOT NULL
WHERE e.id IN (1,2,3,4,5,6,7,8,9,10)
GROUP BY e.id, e.name, e.event_type
ORDER BY e.name;

-- ========================================
-- 测试数据创建完成
-- ========================================

-- 输出测试数据创建完成信息
SELECT 'Test data creation completed!' as message,
       (SELECT COUNT(*) FROM groups WHERE id IN (1,2,3,4,5,6)) as groups_created,
       (SELECT COUNT(*) FROM events WHERE id IN (1,2,3,4,5,6,7,8,9,10)) as events_created,
       (SELECT COUNT(*) FROM athletes WHERE team_id IN (1,2)) as athletes_created,
       (SELECT COUNT(*) FROM registrations WHERE team_id IN (1,2)) as registrations_created;
