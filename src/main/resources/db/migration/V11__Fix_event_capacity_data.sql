-- 修复项目容量限制数据
-- 执行日期: 2025-10-21

-- 步骤1: 为所有项目设置容量限制
UPDATE events 
SET is_capacity_limited = true,
    max_participants = CASE 
        WHEN name LIKE '%100米%' OR name LIKE '%200米%' THEN 16
        WHEN name LIKE '%400米%' THEN 12
        WHEN name LIKE '%跳远%' OR name LIKE '%跳高%' OR name LIKE '%铅球%' THEN 20
        WHEN name LIKE '%4x100米接力%' THEN 8
        ELSE 16
    END
WHERE is_capacity_limited IS NULL OR max_participants IS NULL;

-- 步骤2: 计算当前报名人数
UPDATE events e 
SET current_participants = (
    SELECT COUNT(*) 
    FROM registrations r 
    WHERE r.event_id = e.id AND r.status = 'CONFIRMED'
)
WHERE current_participants IS NULL;

-- 步骤3: 确保所有项目都有正确的容量设置
UPDATE events 
SET is_capacity_limited = true 
WHERE is_capacity_limited IS NULL;

UPDATE events 
SET max_participants = 16 
WHERE max_participants IS NULL;

UPDATE events 
SET current_participants = 0 
WHERE current_participants IS NULL;
