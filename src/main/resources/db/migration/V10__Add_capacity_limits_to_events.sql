-- ============================================================================
-- 麒翔田径编排系统 - 添加项目容量限制
-- 创建时间: 2025-10-21
-- 描述: 为events表添加容量限制相关字段，支持项目报名人数限制
-- ============================================================================

-- 1. 添加容量限制字段
ALTER TABLE events ADD COLUMN IF NOT EXISTS max_participants INT DEFAULT 50 COMMENT '最大参赛人数限制';
ALTER TABLE events ADD COLUMN IF NOT EXISTS current_participants INT DEFAULT 0 COMMENT '当前已报名人数';
ALTER TABLE events ADD COLUMN IF NOT EXISTS is_capacity_limited BOOLEAN DEFAULT FALSE COMMENT '是否启用容量限制';

-- 2. 为现有项目设置合理的容量限制
-- 个人项目通常限制较少，接力项目需要固定人数，团体项目根据实际情况设置

-- 个人径赛项目（设置较大容量）
UPDATE events SET 
    max_participants = 100,
    is_capacity_limited = TRUE
WHERE event_type IN ('TRACK', 'INDIVIDUAL') 
AND (name LIKE '%米' OR name LIKE '%跑' OR name LIKE '%跨栏');

-- 个人田赛项目（中等容量）
UPDATE events SET 
    max_participants = 60,
    is_capacity_limited = TRUE
WHERE event_type IN ('FIELD', 'INDIVIDUAL') 
AND (name LIKE '%跳%' OR name LIKE '%投%' OR name LIKE '%掷%' OR name LIKE '%推%');

-- 接力项目（固定队伍数量）
UPDATE events SET 
    max_participants = 30,
    is_capacity_limited = TRUE
WHERE event_type = 'RELAY';

-- 团体项目（根据项目特性设置）
UPDATE events SET 
    max_participants = 20,
    is_capacity_limited = TRUE
WHERE event_type = 'TEAM';

-- 3. 初始化当前报名人数（基于现有报名数据）
UPDATE events e SET current_participants = (
    SELECT COUNT(DISTINCT r.athlete_id) 
    FROM registrations r 
    WHERE r.event_id = e.id AND r.status = 'CONFIRMED'
);

-- 4. 添加索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_events_capacity ON events(max_participants, current_participants);
CREATE INDEX IF NOT EXISTS idx_events_limited ON events(is_capacity_limited);

-- 5. 添加检查约束确保数据完整性
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_max_participants 
CHECK (max_participants > 0);

ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_current_participants 
CHECK (current_participants >= 0);

ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_capacity_logic 
CHECK (NOT is_capacity_limited OR current_participants <= max_participants);

-- 6. 验证容量限制设置结果
SELECT 
    id,
    name,
    event_type,
    gender,
    max_participants,
    current_participants,
    is_capacity_limited,
    CASE 
        WHEN is_capacity_limited AND current_participants >= max_participants THEN '已满员'
        WHEN is_capacity_limited THEN CONCAT('剩余名额: ', (max_participants - current_participants))
        ELSE '无限制'
    END as status
FROM events 
ORDER BY event_type, name;

-- 7. 统计容量限制设置情况
SELECT 
    event_type,
    COUNT(*) as total_events,
    SUM(CASE WHEN is_capacity_limited THEN 1 ELSE 0 END) as limited_events,
    SUM(CASE WHEN is_capacity_limited AND current_participants >= max_participants THEN 1 ELSE 0 END) as full_events,
    AVG(max_participants) as avg_capacity
FROM events 
GROUP BY event_type
ORDER BY event_type;

-- 8. 检查是否有数据异常
SELECT * FROM events 
WHERE current_participants > max_participants 
AND is_capacity_limited = TRUE;
