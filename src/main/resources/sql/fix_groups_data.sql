-- ========================================
-- 修复Groups表数据的SQL脚本
-- 直接创建groups表并插入基础数据
-- ========================================

-- 1. 首先检查并创建groups表（如果不存在）
CREATE TABLE IF NOT EXISTS groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '组别名称',
    sports_meet_id BIGINT NOT NULL COMMENT '运动会ID',
    gender VARCHAR(10) NOT NULL COMMENT '性别：MALE-男子，FEMALE-女子',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    max_leaders_per_team INT DEFAULT 2 COMMENT '每队领队教练最大名额',
    max_athletes_per_team INT DEFAULT 50 COMMENT '每队运动员最大名额',
    max_events_per_athlete INT DEFAULT 3 COMMENT '每人最大报名项目数',
    max_participants_per_event INT DEFAULT 0 COMMENT '每项目最大参赛人数(0=无限制)',
    max_relays_per_team INT DEFAULT 1 COMMENT '每队每接力项目最大队伍数',
    allow_mixed_events BOOLEAN DEFAULT FALSE COMMENT '是否允许混合项目',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-激活，INACTIVE-停用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_groups_sports_meet (sports_meet_id),
    INDEX idx_groups_gender (gender),
    INDEX idx_groups_grade (grade),
    INDEX idx_groups_status (status)
) COMMENT '竞赛组别表';

-- 2. 清理现有数据（如果存在）
DELETE FROM groups WHERE sports_meet_id = 1;

-- 3. 插入基础组别数据
INSERT INTO groups (id, name, sports_meet_id, gender, grade, max_leaders_per_team, max_athletes_per_team, max_events_per_athlete, max_participants_per_event, max_relays_per_team, allow_mixed_events, status) VALUES
(1, '高一男子组', 1, 'MALE', '高一', 2, 20, 3, 4, 1, FALSE, 'ACTIVE'),
(2, '高一女子组', 1, 'FEMALE', '高一', 2, 20, 3, 4, 1, FALSE, 'ACTIVE'),
(3, '高二男子组', 1, 'MALE', '高二', 2, 20, 3, 4, 1, FALSE, 'ACTIVE'),
(4, '高二女子组', 1, 'FEMALE', '高二', 2, 20, 3, 4, 1, FALSE, 'ACTIVE')
(5, '高三男子组', 1, 'MALE', '高三', 2, 20, 3, 4, 1, FALSE, 'ACTIVE'),
(6, '高三女子组', 1, 'FEMALE', '高三', 2, 20, 3, 4, 1, FALSE, 'ACTIVE');

-- 4. 验证插入结果
SELECT 'Groups表修复完成' as message, COUNT(*) as inserted_count FROM groups WHERE sports_meet_id = 1;
