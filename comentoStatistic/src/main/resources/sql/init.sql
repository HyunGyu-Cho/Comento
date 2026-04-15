CREATE DATABASE IF NOT EXISTS statistic;

CREATE TABLE IF NOT EXISTS statistic.login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    login_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS statistic.dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL,
    member_id BIGINT NOT NULL,
    emp_name VARCHAR(50) NOT NULL
);

-- dept 샘플 데이터
INSERT INTO statistic.dept (dept_name, member_id, emp_name) VALUES
('개발팀', 1, '홍길동'),
('개발팀', 2, '김철수'),
('마케팅팀', 3, '이영희');

-- 샘플 데이터 (테스트용)
INSERT INTO statistic.login_log (member_id, login_at) VALUES
(1, '2025-01-05 09:00:00'),
(2, '2025-01-15 10:30:00'),
(1, '2025-01-20 14:00:00'),
(1, '2025-02-01 08:00:00'),
(2, '2025-02-10 11:00:00'),
(3, '2025-02-15 16:00:00'),
(1, '2025-03-01 09:00:00'),
(2, '2025-03-01 10:00:00');
