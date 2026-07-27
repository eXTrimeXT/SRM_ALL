delete
from scc_rbac_permission
where PERMISSION_ID in (
                        443382218930176,
                        443382256732160,
                        443382290960384
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (443382218930176, -1, 6946546715131904, '二开-查询GSCP状态', 'INTERFACE', '/pj/companyInfo/getGscpStatus-6946546715131904', '/pj/companyInfo/getGscpStatus', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-07 14:54:50', '10.254.103.115', '超级管理员', null, null, '2023-10-07 14:54:50', '10.254.103.115', '超级管理员', null, 0),
        (443382256732160, -1, 6946545880662016, '二开-查询GSCP状态', 'INTERFACE', '/pj/companyInfo/getGscpStatus-6946545880662016', '/pj/companyInfo/getGscpStatus', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-07 14:55:08', '10.254.103.115', '超级管理员', null, null, '2023-10-07 14:55:08', '10.254.103.115', '超级管理员', null, 0),
        (443382290960384, -1, 6946467938304000, '二开-查询GSCP状态', 'INTERFACE', '/pj/companyInfo/getGscpStatus-6946467938304000', '/pj/companyInfo/getGscpStatus', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-07 14:55:25', '10.254.103.115', '超级管理员', null, null, '2023-10-07 14:55:25', '10.254.103.115', '超级管理员', null, 0);

