-- ============================================================================
-- 麒翔田径编排系统 - 报名系统相关表
-- 创建时间: 2025-10-15
-- 描述: 创建运动员、比赛项目、报名记录等报名系统核心表
-- ============================================================================

-- 1. 创建运动员表
CREATE TABLE athletes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自动增长',
    team_id BIGINT NOT NULL COMMENT '参赛单位ID',
    group_id BIGINT NOT NULL COMMENT '竞赛组别ID',
    name VARCHAR(100) NOT NULL COMMENT '运动员姓名',
    id_number VARCHAR(20) COMMENT '身份证号',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    INDEX idx_athletes_team_id (team_id),
    INDEX idx_athletes_group_id (group_id),
    INDEX idx_athletes_team_group (team_id, group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动员表';

-- 2. 创建比赛项目表（简化，只定义基础项目）
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自动增长',
    name VARCHAR(100) NOT NULL COMMENT '项目名称，如：100米、跳远、铅球、4x100米接力',
    event_type VARCHAR(20) NOT NULL COMMENT '项目类型：INDIVIDUAL-个人项目，RELAY-接力项目，TEAM-团体项目',
    description TEXT COMMENT '项目描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_events_type (event_type),
    INDEX idx_events_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛项目表';

-- 3. 创建报名记录表（核心关系表）
CREATE TABLE registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自动增长',
    team_id BIGINT NOT NULL COMMENT '参赛单位ID',
    athlete_id BIGINT COMMENT '运动员ID（个人项目必填，接力项目可为空）',
    group_id BIGINT NOT NULL COMMENT '竞赛组别ID',
    event_id BIGINT NOT NULL COMMENT '比赛项目ID',
    registration_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    status VARCHAR(20) DEFAULT 'CONFIRMED' COMMENT '报名状态：CONFIRMED-已确认，CANCELLED-已取消，PENDING-待确认',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (athlete_id) REFERENCES athletes(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (team_id, athlete_id, group_id, event_id),
    INDEX idx_registrations_team_id (team_id),
    INDEX idx_registrations_athlete_id (athlete_id),
    INDEX idx_registrations_group_id (group_id),
    INDEX idx_registrations_event_id (event_id),
    INDEX idx_registrations_team_group (team_id, group_id),
    INDEX idx_registrations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名记录表';

-- 4. 创建组别-项目关联表（定义哪些组别可以参加哪些项目）
CREATE TABLE group_event_mappings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自动增长',
    group_id BIGINT NOT NULL COMMENT '竞赛组别ID',
    event_id BIGINT NOT NULL COMMENT '比赛项目ID',
    is_mandatory BOOLEAN DEFAULT FALSE COMMENT '是否为必报项目',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_mapping (group_id, event_id),
    INDEX idx_group_mappings_group_id (group_id),
    INDEX idx_group_mappings_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组别-项目关联表';

-- 5. 插入基础比赛项目数据
INSERT INTO events (name, event_type, description) VALUES
('100米', 'INDIVIDUAL', '田径100米短跑项目'),
('200米', 'INDIVIDUAL', '田径200米短跑项目'),
('400米', 'INDIVIDUAL', '田径400米短跑项目'),
('800米', 'INDIVIDUAL', '田径800米中跑项目'),
('1500米', 'INDIVIDUAL', '田径1500米中跑项目'),
('跳远', 'INDIVIDUAL', '田径跳远项目'),
('跳高', 'INDIVIDUAL', '田径跳高项目'),
('铅球', 'INDIVIDUAL', '田径铅球项目'),
('标枪', 'INDIVIDUAL', '田径标枪项目'),
('4x100米接力', 'RELAY', '田径4x100米接力项目'),
('4x400米接力', 'RELAY', '田径4x400米接力项目');

-- 6. 创建组别-项目关联数据
INSERT INTO group_event_mappings (group_id, event_id, is_mandatory) VALUES
-- 高一男子组
(1, 1, FALSE), (1, 2, FALSE), (1, 3, FALSE), (1, 6, FALSE), (1, 7, FALSE), (1, 8, FALSE), (1, 10, FALSE),
-- 高一女子组  
(2, 1, FALSE), (2, 2, FALSE), (2, 3, FALSE), (2, 6, FALSE), (2, 7, FALSE), (2, 8, FALSE), (2, 10, FALSE),
-- 高二男子组
(3, 1, FALSE), (3, 2, FALSE), (3, 3, FALSE), (3, 6, FALSE), (3, 7, FALSE), (3, 8, FALSE), (3, 10, FALSE),
-- 高二女子组
(4, 1, FALSE), (4, 2, FALSE), (4, 3, FALSE), (4, 6, FALSE), (4, 7, FALSE), (4, 8, FALSE), (4, 10, FALSE),
-- 高三男子组
(5, 1, FALSE), (5, 2, FALSE), (5, 3, FALSE), (5, 6, FALSE), (5, 7, FALSE), (5, 8, FALSE), (5, 10, FALSE),
-- 高三女子组
(6, 1, FALSE), (6, 2, FALSE), (6, 3, FALSE), (6, 6, FALSE), (6, 7, FALSE), (6, 8, FALSE), (6, 10, FALSE);

-- 7. 创建视图，方便查询报名统计
CREATE VIEW registration_summary_view AS
SELECT 
    t.id as team_id,
    t.name as team_name,
    g.id as group_id,
    g.name as group_name,
    e.id as event_id,
    e.name as event_name,
    COUNT(r.id) as registration_count,
    g.max_participants_per_event as event_limit,
    g.max_athletes_per_team as athlete_limit,
    g.max_leaders_per_team as leader_limit
FROM teams t
LEFT JOIN registrations r ON t.id = r.team_id
LEFT JOIN groups g ON r.group_id = g.id
LEFT JOIN events e ON r.event_id = e.id
WHERE t.status = 'ACTIVE'
GROUP BY t.id, t.name, g.id, g.name, e.id, e.name, g.max_participants_per_event, g.max_athletes_per_team, g.max_leaders_per_team;

-- 8. 添加索引优化查询性能
CREATE INDEX idx_registrations_team_group_event ON registrations(team_id, group_id, event_id);
CREATE INDEX idx_athletes_team_group ON athletes(team_id, group_id);
