delete
from scc_rbac_permission
where PERMISSION_ID in (
                        446753111662592,
                        446753111662593,
                        446753111662594
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (446753111662592, -1, 6946472478048256, '二开-校验供应商账户数量', 'INTERFACE', '/extUser/checkIfOverThree-6946472478048256', '/extUser/checkIfOverThree', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, null, '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, 0),
        (446753111662593, -1, 6946472478048256, '二开-新增子账号接口', 'INTERFACE', '/extUser/addVendor-6946472478048256', '/extUser/addVendor', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, null, '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, 0),
        (446753111662594, -1, 6946472478048256, '二开-编辑更新子账号接口', 'INTERFACE', '/extUser/modifyVendor-6946472478048256', '/extUser/modifyVendor', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, null, '2023-10-26 16:07:14', '10.254.103.115', '超级管理员', null, 0);
