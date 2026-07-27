delete
from scc_rbac_permission
where PERMISSION_ID in (
                        443005276194816,
                        443005276194817
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (443005276194816, -1, 6946481694310400, '二开-保存准入流程配置', 'INTERFACE', '/pj/sup/entryConfig/batchSaveOrUpdate-6946481694310400', '/pj/sup/entryConfig/batchSaveOrUpdate', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-05 11:47:16', '10.254.103.115', '超级管理员', null, null, '2023-10-05 11:47:16', '10.254.103.115', '超级管理员', null, 0),
        (443005276194817, -1, 6946481694310400, '二开-查询', 'INTERFACE', '/pj/sup/entryConfig/listPageByParam-6946481694310400', '/pj/sup/entryConfig/listPageByParam', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-05 11:47:16', '10.254.103.115', '超级管理员', null, null, '2023-10-05 11:47:16', '10.254.103.115', '超级管理员', null, 0);