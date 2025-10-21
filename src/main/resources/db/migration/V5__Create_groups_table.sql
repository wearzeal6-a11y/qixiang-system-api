-- ============================================================================
-- 麒翔田径编排系统 - 竞赛组别表
-- 创建时间: 2025-10-15
-- 描述: 用于存储运动会竞赛组别信息的数据库表，这是报名规则的核心存储中心
-- ============================================================================

-- 首先创建运动会表（如果不存在）
CREATE TABLE IF NOT EXISTS sports_meets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自动增长',
    name VARCHAR(200) NOT NULL COMMENT '运动会名称',
    description TEXT COMMENT '运动会描述',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    location VARCHAR(200) COMMENT '举办地点',
    status VARCHAR(20) DEFAULT 'PLANNING' COMMENT '状态：PLANNING-筹备中，ONGOING-进行中，COMPLETED-已完成',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动会信息表';

-- 插入默认运动会数据
INSERT IGNORE INTO sports_meets (id, name, description, start_date, end_date, location, status) VALUES
(1, '2025年春季田径运动会', '麒翔中学2025年春季田径运动会', '2025-04-15', '2025-04-17', '麒翔中学体育场', 'PLANNING');

-- 创建竞赛组别表
CREATE TABLE groups (
    -- 主键字段
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID，自动增长',
    
    -- 基本信息
    name VARCHAR(100) NOT NULL COMMENT '组别名称，如：高一男子组、高二女子组、教工组等',
    sports_meet_id BIGINT NOT NULL COMMENT '关联的运动会ID',
    
    -- 分类信息
    gender VARCHAR(10) COMMENT '性别：MALE-男性，FEMALE-女性，MIXED-混合',
    grade VARCHAR(20) COMMENT '年级，如：高一、高二、高三、教工等',
    
    -- 报名规则配置
    max_leaders_per_team INT DEFAULT 2 COMMENT '每队领队教练最大名额',
    max_athletes_per_team INT DEFAULT 20 COMMENT '每队运动员最大名额',
    max_events_per_athlete INT DEFAULT 3 COMMENT '每人最大报名项目数',
    max_participants_per_event INT DEFAULT 4 COMMENT '每项目最大参赛人数(0=无限制)',
    max_relays_per_team INT DEFAULT 1 COMMENT '每队每接力项目最大队伍数',
    allow_mixed_events BOOLEAN DEFAULT FALSE COMMENT '是否允许混合项目',
    
    -- 状态和描述
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '组别状态：ACTIVE-激活，INACTIVE-禁用',
    description VARCHAR(500) COMMENT '组别描述',
    remarks VARCHAR(1000) COMMENT '备注信息',
    
    -- 审计字段
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建者ID',
    
    -- 创建索引
    INDEX idx_groups_sports_meet (sports_meet_id),
    INDEX idx_groups_gender (gender),
    INDEX idx_groups_grade (grade),
    INDEX idx_groups_status (status),
    INDEX idx_groups_name (name),
    INDEX idx_groups_sports_meet_grade (sports_meet_id, grade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛组别表，存储运动会组别信息和报名规则';

-- 插入基础竞赛组别数据
-- 假设运动会ID为1，实际应用中应该从sports_meets表获取
INSERT INTO groups (name, sports_meet_id, gender, grade, description) VALUES
('高一男子组', 1, 'MALE', '高一', '高中一年级男子竞赛组别'),
('高一女子组', 1, 'FEMALE', '高一', '高中一年级女子竞赛组别'),
('高二男子组', 1, 'MALE', '高二', '高中二年级男子竞赛组别'),
('高二女子组', 1, 'FEMALE', '高二', '高中二年级女子竞赛组别'),
('高三男子组', 1, 'MALE', '高三', '高中三年级男子竞赛组别'),
('高三女子组', 1, 'FEMALE', '高三', '高中三年级女子竞赛组别');

-- 添加表注释
ALTER TABLE groups COMMENT = '竞赛组别表，存储运动会组别信息和报名规则配置';
