delete
from scc_rbac_function
where FUNCTION_ID in (
        450081923004416,
        449884855080960
    );

insert into scc_rbac_function (FUNCTION_ID, FUNCTION_CODE, FUNCTION_NAME, FUNCTION_ADDRESS, FUNCTION_DESC, FUNCTION_ICON, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (450081923004416, 'XMorderReview', '项目化绩效复核', '/performanceManagement/XMorderReview', '', '', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 11:37:10', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-14 14:44:23', '10.254.103.115', '超级管理员', '', 0),
        (449884855080960, 'XMpingFen', '项目化绩效评分', '/performanceManagement/XMpingFen', '', '', '2023-11-13', null, 8123195817787648, 'super', '2023-11-13 08:53:25', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', '', 0);

delete
from scc_rbac_permission
where PERMISSION_ID in (
                        449885074485248,
                        449897188282368,
                        449897188282369,
                        449897188282370,
                        450082047156224,
                        450101828362240
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (449885074485248, 7188476012593152, 449884855080960, null, 'MENU', 'XMpingFen', null, null, '', null, null, 'N', 'N', 'N', '2023-11-13', null, 8123195817787648, 'super', '2023-11-13 08:55:12', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, 0),
        (449897188282368, -1, 449884855080960, '查询列表', 'INTERFACE', '/projectScoreMan/listPage-449884855080960', '/projectScoreMan/listPage', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, null, '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, 0),
        (449897188282369, -1, 449884855080960, '查询详情', 'INTERFACE', '/projectScoreMan/getDetailById-449884855080960', '/projectScoreMan/getDetailById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, null, '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, 0),
        (449897188282370, -1, 449884855080960, '保存/更新', 'INTERFACE', '/projectScoreMan/saveOrUpdateDetail-449884855080960', '/projectScoreMan/saveOrUpdateDetail', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, null, '2023-11-13 10:33:47', '10.254.103.115', '超级管理员', null, 0),
        (450082047156224, 7188476012593152, 450081923004416, null, 'MENU', 'XMorderReview', null, null, '', null, null, 'N', 'N', 'N', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 11:38:10', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-14 14:44:23', '10.254.103.115', '超级管理员', null, 0),
        (450101828362240, -1, 450081923004416, '查询列表', 'INTERFACE', '/projectScoreMan/listPageForCheck-450081923004416', '/pj/projectScoreItems/listPageForCheck', null, '', '', '', '', '', '', null, null, 8123195817787648, 'super', '2023-11-14 14:19:09', '10.254.103.115', '超级管理员', 8123195817787648, 'super', '2023-11-14 14:44:23', '10.254.103.115', '超级管理员', '', 0);
