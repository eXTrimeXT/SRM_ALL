delete
from scc_base_dict
where DICT_ID in (
                  449536379217920,
                  449536683042816,
                  7301856075841536
    );

delete
from scc_base_dict_item
where DICT_ID in (
                  449536379217920,
                  449536683042816,
                  7301856075841536
    );

insert into scc_base_dict (DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME, DICT_ROLE, DICT_ROLE_NAME, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (7301856075841536, 'PERF_PROJECT_STATUS', '绩效评分项目-项目状态', '绩效评分项目-项目状态', 'zh_CN', '中文', '', '', '2020-06-08', null, 1, 'admin', '2020-06-08 10:39:07', '127.0.0.1', null, null, null, '2020-06-08 10:39:07', null, null, null, 0);

insert into scc_base_dict (DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME, DICT_ROLE, DICT_ROLE_NAME, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449536379217920, 'PROJECT_SCORE_MAN_STATUS', '项目化绩效评分状态', '', 'zh_CN', '中文', '', '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:37:31', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:37:31', '10.254.103.115', '长城管理员', null, 0),
        (449536683042816, 'PROJECT_SCORE_ITEM_STATUS', '项目化评分项目字典', '', 'zh_CN', '中文', '', '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:39:59', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:39:59', '10.254.103.115', '长城管理员', null, 0);

insert into scc_base_dict_item (DICT_ITEM_ID, DICT_ID, DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME, ITEM_DESCRIPTION, DICT_ITEM_NO, DICT_ITEM_MARK, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449220359464960, 7301856075841536, 'PART_SCORE_CALCULATED', '部分计算得分', 'zh_CN', '中文', '', 7, '', '2023-11-08', null, 8123195817787648, 'super', '2023-11-09 14:45:44', '10.254.103.115', '超级管理员', 344486404481093, 'CCAdmin', '2023-11-11 09:28:55', '10.254.103.115', '长城管理员', null, 0),
        (7301876806844416, 7301856075841536, 'SCORE_DRAFT', '拟定', 'zh_CN', '中文', '项目已创建，但尚未通知评分', null, '', '2020-06-08', null, 1, 'admin', '2020-06-08 10:44:23', '127.0.0.1', null, 8123195817787648, 'super', '2022-06-01 09:47:13', '10.73.39.24', '超级管理员', null, 0),
        (7301878762438656, 7301856075841536, 'SCORE_NOTIFIED', '已通知评分', 'zh_CN', '中文', '已通知评分人进行评分操作，尚未到达评分截止时间', null, '', '2020-06-08', null, 1, 'admin', '2020-06-08 10:44:53', '127.0.0.1', null, null, null, '2020-06-08 10:44:53', null, null, null, 0),
        (7301880705449984, 7301856075841536, 'SCORE_CALCULATED', '已计算得分', 'zh_CN', '中文', '已通过计算得到绩效得分结果操作', null, '', '2020-06-08', null, 1, 'admin', '2020-06-08 10:45:23', '127.0.0.1', null, null, null, '2020-06-08 10:45:23', null, null, null, 0),
        (7301882230276096, 7301856075841536, 'RESULT_NO_PUBLISHED', '结果未发布', 'zh_CN', '中文', '已提交审批流程', null, '', '2020-06-08', '2023-11-08', 1, 'admin', '2020-06-08 10:45:46', '127.0.0.1', null, 8123195817787648, 'super', '2023-11-09 14:44:46', '10.254.103.115', '超级管理员', null, 0),
        (7301883815788544, 7301856075841536, 'RESULT_PUBLISHED', '结果已发布', 'zh_CN', '中文', '审批流程已通过，并发布了项目绩效结果', null, '', '2020-06-08', '2023-11-08', 1, 'admin', '2020-06-08 10:46:10', '127.0.0.1', null, 8123195817787648, 'super', '2023-11-09 14:44:56', '10.254.103.115', '超级管理员', null, 0),
        (7301885310795776, 7301856075841536, 'OBSOLETE', '已废弃', 'zh_CN', '中文', '废弃项目', null, '', '2020-06-08', null, 1, 'admin', '2020-06-08 10:46:33', '127.0.0.1', null, null, null, '2020-06-08 10:46:33', null, null, null, 0);

insert into scc_base_dict_item (DICT_ITEM_ID, DICT_ID, DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME, ITEM_DESCRIPTION, DICT_ITEM_NO, DICT_ITEM_MARK, ACTIVE_DATE, INACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449536414740480, 449536379217920, 'DRAFT', '未提交', 'zh_CN', '中文', '', 1, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:37:48', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:37:48', '10.254.103.115', '长城管理员', null, 0),
        (449536468723712, 449536379217920, 'SUBMITTED', '已提交', 'zh_CN', '中文', '', 2, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:38:15', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:38:15', '10.254.103.115', '长城管理员', null, 0),
        (449536501067776, 449536379217920, 'FLOW_REJECT', '审批驳回', 'zh_CN', '中文', '', 3, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:38:30', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:38:30', '10.254.103.115', '长城管理员', null, 0),
        (449536530808832, 449536379217920, 'CHECK_REJECT', '复核驳回', 'zh_CN', '中文', '', 4, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:38:45', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:38:45', '10.254.103.115', '长城管理员', null, 0),
        (449536561352704, 449536379217920, 'ABANDONED', '已废弃', 'zh_CN', '中文', '', 5, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:39:00', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:39:00', '10.254.103.115', '长城管理员', null, 0),
        (449536590428160, 449536379217920, 'APPROVED', '审批通过', 'zh_CN', '中文', '', 6, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:39:14', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:39:14', '10.254.103.115', '长城管理员', null, 0),
        (449536768440320, 449536683042816, 'DRAFT', '拟定', 'zh_CN', '中文', '', 1, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:40:41', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:40:41', '10.254.103.115', '长城管理员', null, 0),
        (449536810522624, 449536683042816, 'SCORE_NOTIFIED', '已通知评分', 'zh_CN', '中文', '', 2, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:41:01', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:41:01', '10.254.103.115', '长城管理员', null, 0),
        (449536837976064, 449536683042816, 'WITHOUT_CHECK', '待复核', 'zh_CN', '中文', '', 3, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:41:15', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:41:15', '10.254.103.115', '长城管理员', null, 0),
        (449536875765760, 449536683042816, 'CALCULATED_SCORE', '已计算得分', 'zh_CN', '中文', '', 4, '', '2023-11-10', null, 344486404481093, 'CCAdmin', '2023-11-11 09:41:33', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:41:33', '10.254.103.115', '长城管理员', null, 0);

