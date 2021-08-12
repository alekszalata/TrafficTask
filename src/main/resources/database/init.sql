CREATE SCHEMA IF NOT EXISTS traffic_limits;
CREATE TABLE IF NOT EXISTS traffic_limits.limits_per_hour (limit_name VARCHAR(255), limit_value BIGINT, effective_date DATE);
INSERT INTO traffic_limits.limits_per_hour (limit_name, limit_value ,effective_date) values ('min', 1024, '2021-08-13') ON CONFLICT DO NOTHING;
INSERT INTO traffic_limits.limits_per_hour (limit_name, limit_value ,effective_date) values ('max', 1073741824., '2021-08-13') ON CONFLICT DO NOTHING;