ALTER TABLE `scc_commit_task_param`
    ADD COLUMN `FIRST_PREDICT_ACTIVITY_PARAM` TEXT NULL DEFAULT NULL COMMENT '第一次预执行返回的审批节点数据' COLLATE 'utf8mb4_unicode_ci';
