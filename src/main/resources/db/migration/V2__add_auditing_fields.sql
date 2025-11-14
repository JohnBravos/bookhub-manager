-- ===============================================
-- Add auditing fields to USERS
-- ===============================================
ALTER TABLE users
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);


-- ===============================================
-- Add auditing fields to AUTHORS
-- ===============================================
ALTER TABLE authors
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);


-- ===============================================
-- Add auditing fields to BOOKS
-- ===============================================
ALTER TABLE books
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);


-- ===============================================
-- Add auditing fields to RESERVATIONS
-- ===============================================
ALTER TABLE reservations
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);


-- ===============================================
-- Add auditing fields to LOANS
-- ===============================================
ALTER TABLE loans
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN last_modified_by VARCHAR(255);
