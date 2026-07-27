delete
from scc_rbac_permission
where PERMISSION_ID in (
                        448699002077184,
                        448699034191872,
                        448699087837184
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (448699002077184, -1, 7188452334305280, '二开-绩效查询列表接口', 'INTERFACE', '/pj/scoring/perfOverallScore/listPerfOverallScorePage-7188452334305280', '/pj/scoring/perfOverallScore/listPerfOverallScorePage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-06 16:02:55', '10.254.103.115', '长城管理员', null, null, '2023-11-06 16:02:55', '10.254.103.115', '长城管理员', null, 0),
        (448699034191872, -1, 388013318858752, '二开-绩效查询列表接口', 'INTERFACE', '/pj/scoring/perfOverallScore/listPerfOverallScorePage-388013318858752', '/pj/scoring/perfOverallScore/listPerfOverallScorePage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-06 16:03:11', '10.254.103.115', '长城管理员', null, null, '2023-11-06 16:03:11', '10.254.103.115', '长城管理员', null, 0),
        (448699087837184, -1, 323900035905664, '二开-绩效查询列表接口', 'INTERFACE', '/pj/scoring/perfOverallScore/listPerfOverallScorePage-323900035905664', '/pj/scoring/perfOverallScore/listPerfOverallScorePage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-06 16:03:37', '10.254.103.115', '长城管理员', null, null, '2023-11-06 16:03:37', '10.254.103.115', '长城管理员', null, 0);

