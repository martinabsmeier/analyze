-- DROP DB
-- ---------------------------------------------------------------------------------------------------------------------
DROP DATABASE IF EXISTS analyze;

-- CREATE DB
-- ---------------------------------------------------------------------------------------------------------------------
CREATE DATABASE analyze WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'de_DE.UTF-8'
    LC_CTYPE = 'de_DE.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- CREATE SCHEMA
-- ---------------------------------------------------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION postgres;
SET search_path TO public;
