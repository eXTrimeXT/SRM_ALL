delete
from scc_rbac_permission
where PERMISSION_ID in (
        451696720103424,
        451867737536512,
        450125511307264,
        450125857112064,
        450805825198080,
        451134508662784,
        451159893166080,
        451159893166081,
        451159893166082,
        451159893166083,
        451159893166084,
        451308866951168,
        451308866951169,
        451308866951170,
        451308866951171,
        451308866951172,
        451308866951173,
        451308909645824
    );

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (451696720103424, -1, 7856886071296000, '二开-查询头列表接口', 'INTERFACE', '/pj/orgCategory/listPageHeader-7856886071296000', '/pj/orgCategory/listPageHeader', null, '', '', '', '', '', '', null, null, 8123195817787648, 'super', '2023-11-23 14:38:25', '10.254.103.115', '超级管理员', 8123195817787648, 'super', '2023-11-24 13:50:09', '10.254.103.115', '超级管理员', '', 0),
        (451867737536512, -1, 7856886071296000, '二开-根据头查询明细列表', 'INTERFACE', '/pj/orgCategory/listPageDetailByHeader-7856886071296000', '/pj/orgCategory/listPageDetailByHeader', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-24 13:50:09', '10.254.103.115', '超级管理员', null, null, '2023-11-24 13:50:09', '10.254.103.115', '超级管理员', null, 0);

delete
from scc_rbac_function
where FUNCTION_ID in (
                      450125395886080,
                      450125761914880,
                      451308636844032
    );

insert into scc_rbac_function (FUNCTION_ID, FUNCTION_CODE, FUNCTION_NAME, FUNCTION_ADDRESS, FUNCTION_DESC, FUNCTION_ICON, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (450125395886080, 'XMperformanceWarning', '项目化评分结果预警', '/performanceManagement/XMperformanceWarning', '', '', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 17:30:57', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-18 13:48:17', '10.254.103.15', '超级管理员', '', 0),
        (450125761914880, 'yujingdan', '供应商预警单', '/performanceManagement/yujingdan', '', '', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 17:33:55', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', '', 0),
        (451308636844032, 'yujingdanXT', '供应商预警协同', '/performanceManagement/yujingdan', '', '', '2023-11-21', null, 8123195817787648, 'super', '2023-11-21 10:00:11', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', '', 0);

insert into scc_rbac_permission (PERMISSION_ID, PARENT_PERMISSION_ID, FUNCTION_ID, PERMISSION_NAME, PERMISSION_TYPE, PERMISSION_CODE, PERMISSION, SORT, ICON_PATH, ORG_CONTROL_DIM, ENABLE_CATEGORY_DIVISION, ENABLE_ATTACH_MANAGE, ENABLE_BUSI_STATE_CONTROL, ENABLE_WORK_FLOW, START_DATE, END_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (450125511307264, 7188476012593152, 450125395886080, null, 'MENU', 'XMperformanceWarning', null, null, '', null, null, 'N', 'N', 'N', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 17:31:53', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-18 13:48:18', '10.254.103.15', '超级管理员', null, 0),
        (450125857112064, 7188476012593152, 450125761914880, null, 'MENU', 'yujingdan', null, null, '', null, null, 'N', 'N', 'N', '2023-11-14', null, 8123195817787648, 'super', '2023-11-14 17:34:42', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (450805825198080, -1, 450125395886080, '供应商处理接口', 'INTERFACE', '/projectScoreHeader/supplierReply-450125395886080', '/projectScoreHeader/supplierReply', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-18 13:48:18', '10.254.103.15', '超级管理员', null, null, '2023-11-18 13:48:18', '10.254.103.15', '超级管理员', null, 0),
        (451134508662784, -1, 450125761914880, '列表接口', 'INTERFACE', '/projectScoreWarning/listPage-450125761914880', '/projectScoreWarning/listPage', null, '', '', '', '', '', '', null, null, 8123195817787648, 'super', '2023-11-20 10:23:07', '10.254.103.15', '超级管理员', 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', '', 0),
        (451159893166080, -1, 450125761914880, '保存/更新', 'INTERFACE', '/projectScoreWarning/saveOrUpdateDetail-450125761914880', '/projectScoreWarning/saveOrUpdateDetail', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, null, '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (451159893166081, -1, 450125761914880, '发布', 'INTERFACE', '/projectScoreWarning/publish-450125761914880', '/projectScoreWarning/publish', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, null, '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (451159893166082, -1, 450125761914880, '采购商端-获取详情', 'INTERFACE', '/projectScoreWarning/getDetailById-450125761914880', '/projectScoreWarning/getDetailById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, null, '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (451159893166083, -1, 450125761914880, '删除', 'INTERFACE', '/projectScoreWarning/delete-450125761914880', '/projectScoreWarning/delete', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, null, '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (451159893166084, -1, 450125761914880, '供应商端-获取详情', 'INTERFACE', '/projectScoreWarning/sup/getDetailById-450125761914880', '/projectScoreWarning/sup/getDetailById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, null, '2023-11-20 13:49:42', '10.254.103.115', '超级管理员', null, 0),
        (451308866951168, -1, 451308636844032, '列表接口', 'INTERFACE', '/projectScoreWarning/listPage-451308636844032', '/projectScoreWarning/listPage', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308866951169, -1, 451308636844032, '保存/更新', 'INTERFACE', '/projectScoreWarning/saveOrUpdateDetail-451308636844032', '/projectScoreWarning/saveOrUpdateDetail', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308866951170, -1, 451308636844032, '发布', 'INTERFACE', '/projectScoreWarning/publish-451308636844032', '/projectScoreWarning/publish', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308866951171, -1, 451308636844032, '采购商端-获取详情', 'INTERFACE', '/projectScoreWarning/getDetailById-451308636844032', '/projectScoreWarning/getDetailById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308866951172, -1, 451308636844032, '删除', 'INTERFACE', '/projectScoreWarning/delete-451308636844032', '/projectScoreWarning/delete', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308866951173, -1, 451308636844032, '供应商端-获取详情', 'INTERFACE', '/projectScoreWarning/sup/getDetailById-451308636844032', '/projectScoreWarning/sup/getDetailById', null, null, null, null, null, null, null, null, null, 8123195817787648, 'super', '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:03', '10.254.103.15', '超级管理员', null, 0),
        (451308909645824, 7245105233526784, 451308636844032, null, 'MENU', 'yujingdanXT', null, null, '', null, null, 'N', 'N', 'N', '2023-11-21', null, 8123195817787648, 'super', '2023-11-21 10:02:24', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:24', '10.254.103.15', '超级管理员', null, 0);


delete
from scc_rbac_permission_language
where PERMISSION_LANGUAGE_ID in (
                                 450125511325696,
                                 450125857132544,
                                 451308909666304
    );

insert into scc_rbac_permission_language (PERMISSION_LANGUAGE_ID, PERMISSION_ID, LANGUAGE, PERMISSION_NAME, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (450125511325696, 450125511307264, 'zh_CN', '项目化评分结果预警', 8123195817787648, 'super', '2023-11-14 17:31:53', '10.254.103.15', '超级管理员', null, null, '2023-11-14 17:31:53', '10.254.103.15', '超级管理员', null, 0),
        (450125857132544, 450125857112064, 'zh_CN', '供应商预警单', 8123195817787648, 'super', '2023-11-14 17:34:42', '10.254.103.15', '超级管理员', null, null, '2023-11-14 17:34:42', '10.254.103.15', '超级管理员', null, 0),
        (451308909666304, 451308909645824, 'zh_CN', '供应商预警协同', 8123195817787648, 'super', '2023-11-21 10:02:24', '10.254.103.15', '超级管理员', null, null, '2023-11-21 10:02:24', '10.254.103.15', '超级管理员', null, 0);
