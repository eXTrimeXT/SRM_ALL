delete
from scc_rbac_permission
where PERMISSION_ID in (
                        443343808634880,
                        443374478764032,
                        443374524448768
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (443343808634880, -1, 6946467938304000, '二开-查询是否黑名单', 'INTERFACE', 'CompanyInfo#queryIfBlackCompany-6946467938304000', '/pj/companyInfo/queryIfBlackCompany', null, '', '', '', '', '', '', null, null, 8123195817787648, 'super', '2023-10-07 09:42:15', '10.254.103.115', '超级管理员', 8123195817787648, 'super', '2023-10-07 09:55:08', '10.254.103.115', '超级管理员', '', 0),
        (443374478764032, -1, 6946545880662016, '二开-查询是否黑名单', 'INTERFACE', '/pj/companyInfo/queryIfBlackCompany-6946545880662016', '/pj/companyInfo/queryIfBlackCompany', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-07 13:51:50', '10.254.103.115', '超级管理员', null, null, '2023-10-07 13:51:50', '10.254.103.115', '超级管理员', null, 0),
        (443374524448768, -1, 6946546715131904, '二开-查询是否黑名单', 'INTERFACE', '/pj/companyInfo/queryIfBlackCompany-6946546715131904', '/pj/companyInfo/queryIfBlackCompany', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-10-07 13:52:13', '10.254.103.115', '超级管理员', null, null, '2023-10-07 13:52:13', '10.254.103.115', '超级管理员', null, 0);



