delete
from srm_cbpm_template_header
where TEMPLATE_HEAD_ID = 449923691659264;

insert into srm_cbpm_template_header (TEMPLATE_HEAD_ID, MODEL_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, PENDING_APPROVE_URL, FEIGN_CLIENT, BUSSINESS_CLASS, FLOW_PLATFORM_CLASS, START_DATE_ACTIVE, END_DATE_ACTIVE, LANGUAGE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, DELETE_FLAG, ENABLE_FLAG, ATTRIBUTE_CATEGORY, ATTRIBUTE1, INTEGRATION_MODE, BUSINESS_NAME, TABLE_URL)
values  (449923691659264, 'projectScoreMan', null, 'projectScoreMan', null, '/', 'com.midea.cloud.srm.feign.perf.PerformanceClient', 'com.midea.cloud.srm.perf.projectscoreman.service.impl.ProjectScoreManFlowServiceImpl', null, null, null, '', 8123195817787648, 'super', '2023-11-13 14:09:28', '10.254.103.115', '超级管理员', 8123195817787648, 'super', '2023-11-13 14:09:48', '10.254.103.115', '超级管理员', null, 0, 0, 'N', null, null, 'Push', '项目化-绩效项目评分', '/');

