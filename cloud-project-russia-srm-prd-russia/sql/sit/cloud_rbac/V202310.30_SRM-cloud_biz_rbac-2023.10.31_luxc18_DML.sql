delete
from scc_rbac_permission
where PERMISSION_ID in (
                        447639258636288,447637687816192
    );

insert into meicloud_usrm_cloud_rbac.scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (447639258636288, -1, 7188456199684096, '二开-保存/更新订单化绩效项目', 'INTERFACE', '/pj/scoreproject/scoreItems/saveOrUpdatePerfScoreItems-7188456199684096', '/pj/scoreproject/scoreItems/saveOrUpdatePerfScoreItems', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-10-31 16:18:43', '10.254.103.115', '长城管理员', null, null, '2023-10-31 16:18:43', '10.254.103.115', '长城管理员', null, 0),
        (447637687816192, -1, 6946472478048256, '二开-删除srm账户接口', 'INTERFACE', '/extUser/deleteUser-6946472478048256', '/extUser/deleteUser', null, null, null, null, null, null, null, null, null, 344486404481093, 'CCAdmin', '2023-10-31 16:05:55', '10.254.103.115', '长城管理员', null, null, '2023-10-31 16:05:55', '10.254.103.115', '长城管理员', null, 0);

