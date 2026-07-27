delete
from scc_rbac_permission
where PERMISSION_ID in (
                        452947696783360,
                        452947696783361
    );


insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (452947696783360, -1, 7150254476427264, '二开-电子签章位置设置', 'INTERFACE', '/contractInterface/ext/getUrlById-7150254476427264', '/contractInterface/ext/getUrlById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-30 16:18:53', '10.254.103.115', '超级管理员', null, null, '2023-11-30 16:18:53', '10.254.103.115', '超级管理员', null, 0),
        (452947696783361, -1, 7150254476427264, '二开-电子签章确认', 'INTERFACE', '/contractInterface/ext/confirm-7150254476427264', '/contractInterface/ext/confirm', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-30 16:18:53', '10.254.103.115', '超级管理员', null, null, '2023-11-30 16:18:53', '10.254.103.115', '超级管理员', null, 0);



