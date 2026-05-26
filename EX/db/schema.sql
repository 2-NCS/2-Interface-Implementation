-- ============================================================
-- [시험] EX — 사원 입사등록 → 그룹웨어 계정발급 인터페이스
-- NCS 2001020212_23v6 인터페이스 구현 (능력평가 본 시험)
-- MySQL 8.x 기준
-- ============================================================

DROP USER IF EXISTS 'ifuserx'@'%';
CREATE USER 'ifuserx'@'%' IDENTIFIED BY 'ifx1234!';

-- ========== 송신측 HrmDB (인사관리시스템) ==========
DROP DATABASE IF EXISTS HrmDB;
CREATE DATABASE HrmDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON HrmDB.* TO 'ifuserx'@'%';
USE HrmDB;

-- 사원 마스터
CREATE TABLE employee (
    emp_id      VARCHAR(12)  NOT NULL,
    emp_name    VARCHAR(50)  NOT NULL,
    dept_cd     CHAR(4)      NOT NULL,
    position    VARCHAR(20)  NOT NULL,
    hire_dt     CHAR(8)      NOT NULL,
    email       VARCHAR(100),
    reg_dt      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (emp_id)
);

-- 인터페이스 송신 테이블 (Outbox)
CREATE TABLE if_outbox (
    outbox_id   BIGINT       NOT NULL AUTO_INCREMENT,
    if_id       VARCHAR(20)  NOT NULL,
    tx_no       VARCHAR(20)  NOT NULL,    -- 멱등키 = emp_id
    payload     TEXT         NOT NULL,
    status      CHAR(1)      NOT NULL DEFAULT 'N',   -- N:미처리 S:성공 F:실패
    retry_cnt   INT          NOT NULL DEFAULT 0,
    reg_dt      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    proc_dt     DATETIME,
    err_msg     VARCHAR(500),
    PRIMARY KEY (outbox_id),
    INDEX idx_status (status, retry_cnt),
    INDEX idx_tx_no (tx_no)
);

-- ========== 수신측 GroupwareDB (그룹웨어) ==========
DROP DATABASE IF EXISTS GroupwareDB;
CREATE DATABASE GroupwareDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON GroupwareDB.* TO 'ifuserx'@'%';
USE GroupwareDB;

-- 부서 마스터 (수신측에 별도 동기화되어 있는 마스터)
CREATE TABLE dept (
    dept_cd     CHAR(4)      NOT NULL,
    dept_nm     VARCHAR(100) NOT NULL,
    PRIMARY KEY (dept_cd)
);

-- 계정 마스터
CREATE TABLE account (
    account_id  VARCHAR(12)  NOT NULL,       -- = emp_id
    pwd_hash    CHAR(64)     NOT NULL,       -- SHA-256 hex
    dept_cd     CHAR(4)      NOT NULL,
    status      CHAR(1)      NOT NULL DEFAULT 'A',   -- A:활성 I:비활성
    create_dt   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id)
);

-- 계정 이력
CREATE TABLE account_history (
    hist_id     BIGINT       NOT NULL AUTO_INCREMENT,
    account_id  VARCHAR(12)  NOT NULL,
    action_cd   CHAR(2)      NOT NULL,       -- 01:생성 02:정지 03:삭제 04:비번변경
    tx_no       VARCHAR(20)  NOT NULL,
    reg_dt      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hist_id),
    INDEX idx_account (account_id, reg_dt)
);

-- 인터페이스 수신 테이블 (Inbox) — 멱등키 보장
CREATE TABLE if_inbox (
    inbox_id    BIGINT       NOT NULL AUTO_INCREMENT,
    if_id       VARCHAR(20)  NOT NULL,
    tx_no       VARCHAR(20)  NOT NULL UNIQUE,    -- ★ 중복 수신 차단
    payload     TEXT         NOT NULL,
    status      CHAR(1)      NOT NULL,
    proc_ms     INT,
    recv_dt     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    err_msg     VARCHAR(500),
    PRIMARY KEY (inbox_id)
);

-- 샘플 부서 마스터
INSERT INTO dept (dept_cd, dept_nm) VALUES
    ('D001', '경영지원본부'),
    ('D002', '개발본부'),
    ('D003', '영업본부'),
    ('D004', '마케팅본부'),
    ('D005', '기획실');

FLUSH PRIVILEGES;

SELECT '--- HrmDB ---' AS ''; SHOW TABLES IN HrmDB;
SELECT '--- GroupwareDB ---' AS ''; SHOW TABLES IN GroupwareDB;
SELECT '--- 초기 부서 ---' AS ''; SELECT * FROM dept;
