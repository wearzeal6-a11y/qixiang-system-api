-- 为 teams 表添加 org_code 字段
-- 用于明确参赛单位所属的机构代码，解决数据归属混乱问题

ALTER TABLE teams ADD COLUMN org_code VARCHAR(50) NOT NULL DEFAULT 'TEST001';

-- 添加索引以提高查询性能
CREATE INDEX idx_teams_org_code ON teams(org_code);

-- 添加注释
COMMENT ON COLUMN teams.org_code IS '参赛单位所属的机构代码，用于数据归属和权限控制';

-- 更新现有数据，为参赛单位设置正确的机构归属
-- 这里暂时将所有参赛单位设置为 TEST001 机构，后续可以根据实际业务需求调整
UPDATE teams SET org_code = 'TEST001' WHERE org_code = 'TEST001';

-- 如果需要为不同的参赛单位设置不同的机构归属，可以执行以下语句：
-- UPDATE teams SET org_code = 'TEAM001' WHERE team_code IN ('TEAM001');
-- UPDATE teams SET org_code = 'TEAM002' WHERE team_code IN ('TEAM002');
-- UPDATE teams SET org_code = 'TEAM003' WHERE team_code IN ('TEAM003');
