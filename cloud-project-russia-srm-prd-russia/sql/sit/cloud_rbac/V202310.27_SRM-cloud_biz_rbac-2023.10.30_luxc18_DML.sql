delete
from scc_rbac_permission
where PERMISSION_ID in (
                        447484590827520,
                        447484590827521,
                        447459790921728
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (447484590827520, -1, 7188454360481792, '二开-等级规则保存', 'INTERFACE', '/pj/perfLevel/savePerfLevel-7188454360481792', '/pj/perfLevel/savePerfLevel', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-30 19:20:01', '10.254.103.115', '超级管理员', null, null, '2023-10-30 19:20:01', '10.254.103.115', '超级管理员', null, 0),
        (447484590827521, -1, 7188454360481792, '二开-等级规则更新', 'INTERFACE', '/pj/perfLevel/updatePerfLevel-7188454360481792', '/pj/perfLevel/updatePerfLevel', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-30 19:20:01', '10.254.103.115', '超级管理员', null, null, '2023-10-30 19:20:01', '10.254.103.115', '超级管理员', null, 0),
        (447459790921728, -1, 7188456199684096, '二开-获取有效地绩效模型', 'INTERFACE', '/pj/template/getValidTemplateHeader-7188456199684096', '/pj/template/getValidTemplateHeader', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-30 15:58:12', '10.254.103.115', '超级管理员', null, null, '2023-10-30 15:58:12', '10.254.103.115', '超级管理员', null, 0);