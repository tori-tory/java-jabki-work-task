CREATE TABLE IF NOT EXISTS work_task.status_change(
    status_from VARCHAR NOT NULL,
    status_to VARCHAR NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (status_from, status_to)
);

CREATE TABLE IF NOT EXISTS work_task.task_event(
    id SERIAL PRIMARY KEY,
    task_id bigint NOT NULL,
    editor_id bigint NOT NULL,
    created_at TIMESTAMP without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    log_message TEXT NOT NULL,

    CONSTRAINT fk_task
          FOREIGN KEY(task_id)
          REFERENCES work_task."task"(id) ON DELETE CASCADE
);

INSERT into work_task.status_change(status_from, status_to)
VALUES ('TO_DO', 'IN_PROGRESS'); --Задача ещё не начала выполняться, и её можно начать выполнять
INSERT into work_task.status_change(status_from, status_to)
VALUES ('TO_DO', 'DELETE'); --Если задача не имеет важности или была ошибочно создана, её можно пометить на удаление
INSERT into work_task.status_change(status_from, status_to)
VALUES ('IN_PROGRESS', 'DONE'); --Задача должна быть в процессе выполнения и успешно завершена. Прежде чем перейти в статус DONE, задача должна быть в IN_PROGRESS.
INSERT into work_task.status_change(status_from, status_to)
VALUES ('IN_PROGRESS', 'DELETE'); --Задачу можно удалить (например, отменить выполнение). Но нужно убедиться, что задача не была завершена
INSERT into work_task.status_change(status_from, status_to)
VALUES ('DONE', 'DELETE'); --Задача может быть удалена только если она уже завершена, и на неё не существует активных зависимостей

INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('TO_DO', 'DONE', false); --Задача не может быть сразу завершена. Она должна пройти через IN_PROGRESS
INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('IN_PROGRESS', 'TO_DO', false); --Задача не может вернуться в исходное состояние после того, как она начала выполняться
INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('DONE', 'IN_PROGRESS', false); --Задача не может вернуться в состояние работы, если она уже была завершена. Её можно только удалить или оставить в статусе завершённой

INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('DELETE', 'TO_DO', false);
INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('DELETE', 'IN_PROGRESS', false);
INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('DELETE', 'DONE', false);

INSERT into work_task.status_change(status_from, status_to, is_active)
VALUES ('DONE', 'TO_DO', false);