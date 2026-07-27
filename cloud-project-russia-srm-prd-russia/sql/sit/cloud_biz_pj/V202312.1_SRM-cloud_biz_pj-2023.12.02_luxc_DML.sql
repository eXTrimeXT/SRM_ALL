delete
from scc_pj_sign_template
where TEMPLATE_HEAD_ID = 1;

insert into scc_pj_sign_template (TEMPLATE_HEAD_ID, ORDER_TYPE, ORDER_TYPE_NEME, FEIGN_URL_PATH, BUSSINESS_CLASS, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (1, 'SIGN_CALLBACK', '契约锁回调测试', '/api-pj', 'com.midea.cloud.srm.biz.pj.changchengapi.sign.service.impl.ISccPjSignCallbackServiceImpl', 1, '1', '2023-10-11 14:37:03', '1', null, null, null, null, null, null, null, 0);