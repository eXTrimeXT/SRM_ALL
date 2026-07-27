delete
from scc_rbac_permission
where PERMISSION_ID in (
                        448304059148288, 448304059148289, 448304059148290, 448304059148291, 448300158853120
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (448304059148288, -1, 448301653835776, '复核列表查询', 'INTERFACE', '/pj/scoreItemsOrderCheck/listPage-448301653835776', '/pj/scoreItemsOrderCheck/listPage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, null, '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, 0),
        (448304059148289, -1, 448301653835776, '复核详情查询', 'INTERFACE', '/pj/scoreItemsOrderCheck/getDetailById-448301653835776', '/pj/scoreItemsOrderCheck/getDetailById', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, null, '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, 0),
        (448304059148290, -1, 448301653835776, '复核驳回', 'INTERFACE', '/pj/scoreItemsOrderCheck/reject-448301653835776', '/pj/scoreItemsOrderCheck/reject', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, null, '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, 0),
        (448304059148291, -1, 448301653835776, '复核计算得分', 'INTERFACE', '/pj/scoreItemsOrderCheck/calcScore-448301653835776', '/pj/scoreItemsOrderCheck/calcScore', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, null, '2023-11-04 10:28:52', '10.254.103.115', '长城管理员', null, 0),
        (448300158853120, -1, 7188450714976256, '二开-绩效模型列表查询', 'INTERFACE', '/pj/template/listPefTemplateHeaderPage-7188450714976256', '/pj/template/listPefTemplateHeaderPage', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-11-04 09:57:08', '10.254.103.115', '长城管理员', null, null, '2023-11-04 09:57:08', '10.254.103.115', '长城管理员', null, 0);


