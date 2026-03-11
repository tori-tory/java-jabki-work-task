CREATE SCHEMA IF NOT EXISTS work_task;

CREATE TABLE IF NOT EXISTS work_task.task
(   id SERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    description VARCHAR,
    status VARCHAR NOT NULL,
    dead_line DATE,
    author_id bigint NOT NULL,
    assignee_id bigint NOT NULL,
    created_at TIMESTAMP without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP without time zone
);