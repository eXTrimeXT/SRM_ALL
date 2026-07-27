delete
from scc_rbac_permission
where PERMISSION_ID in (
    448347194470400
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (448347194470400, -1, 7188440820285440, '二开-导入功能', 'INTERFACE', '/pj/perf/score-man-scoring-v1/importScoreManScoringV1Excel-7188440820285440', '/pj/perf/score-man-scoring-v1/importScoreManScoringV1Excel', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 16:19:54', '10.254.103.115', '长城管理员', null, null, '2023-11-04 16:19:54', '10.254.103.115', '长城管理员', null, 0);

delete
from scc_rbac_function
where FUNCTION_ID in (
    449534389411840
    );

insert into scc_rbac_function (FUNCTION_ID, FUNCTION_CODE, FUNCTION_NAME, FUNCTION_ADDRESS, FUNCTION_DESC, FUNCTION_ICON, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449534389411840, 'XMperformanceScoreItems', '项目化绩效评分项目', '/performanceManagement/XMperformanceScoreItems', '', '', '2023-11-11', null, 8123195817787648, 'super', '2023-11-11 09:21:19', '10.254.103.15', '超级管理员', 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', '', 0);

delete
from scc_rbac_permission
where FUNCTION_ID in (
    449534389411840
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449534645164032, 7188476012593152, 449534389411840, null, 'MENU', 'XMperformanceScoreItems', null, null, '', null, null, 'N', 'N', 'N', '2023-11-11', null, 8123195817787648, 'super', '2023-11-11 09:23:24', '10.254.103.15', '超级管理员', 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, 0),
        (449535164456960, -1, 449534389411840, '查询列表', 'INTERFACE', '/pj/projectScoreItems/listPage-449534389411840', '/pj/projectScoreItems/listPage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, 0),
        (449535164456961, -1, 449534389411840, '根据id查询详情', 'INTERFACE', '/pj/projectScoreItems/getDetailById-449534389411840', '/pj/projectScoreItems/getDetailById', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, 0),
        (449535164456962, -1, 449534389411840, '保存/更新', 'INTERFACE', '/pj/projectScoreItems/saveOrUpdate-449534389411840', '/pj/projectScoreItems/saveOrUpdate', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, 0),
        (449535164456963, -1, 449534389411840, '通知评分人', 'INTERFACE', '/pj/projectScoreItems/notifyScorers-449534389411840', '/pj/projectScoreItems/notifyScorers', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, null, '2023-11-11 09:27:38', '10.254.103.115', '长城管理员', null, 0);






