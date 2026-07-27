delete
from scc_base_quicksearch_config
where QUICKSEARCH_CONFIG_ID = 455234623211520;

delete
from scc_base_quicksearch_attr
where QUICKSEARCH_ATTR_ID in (
                              455430911856640,
                              455430911856641,
                              455430911856642,
                              455430911856643,
                              455430911856644,
                              455430911856645
    );

insert into scc_base_quicksearch_config (QUICKSEARCH_CONFIG_ID, NAME, QUERY_DATASOURCE, QUERY_MODULE, QUERY_LANGUAGE_TYPE, QUERY_MATCH_OPERATOR, QUERY_TABLE, QUERY_MAX_SIZE, QUERY_MODE, CODE_ATTR, VALUE_ATTR, QUERY_LANGUAGE, QUERY_LANGUAGE_CUSTOM, DIALOG_ENABLED, DIALOG_QUERY_TYPE, DIALOG_QUERY_TABLE, DIALOG_QUERY_MAX_SIZE, DIALOG_QUERY_LANGUAGE, DIALOG_QUERY_LANGUAGE_CUSTOM, DESCRIPTION, SELECT_MODE, REFLECT_INPUT_CLASS_NAME, REFLECT_CLASS_NAME, REFLECT_INPUT_METHOD_NAME, REFLECT_METHOD_NAME, REFLECT_INPUT_PROPERTY_NAME, REFLECT_PROPERTY_NAME, SELECT_INPUT_MODE, HTTP_URL, INPUT_HTTP_URL, COMPANY_VIEW_FLAG, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION)
values  (455234623211520, 'ceea_storage_return', null, 'SUPCOOPERATE_READ', null, 'allMatch', 'ceea_storage_return', 15, null, null, 'WAREHOUSING_RETURN_DETAIL_ID', 'select distinct t.VENDOR_ID,t.VENDOR_CODE,t.VENDOR_NAME
from ceea_storage_return t
where 1 = 1', null, null, null, null, null, 'select distinct t.VENDOR_ID,t.VENDOR_CODE,t.VENDOR_NAME
from ceea_storage_return t
where 1 = 1', null, 'cc-订单化绩效项目-供应商快查', 'URL', '', '', '', '', '', '', 'URL', 'http://srm-java-cooperate-srm-midea-module:8835/api-sup-ce/sc-anon/pjQuickSearch/listPageWarehousingReturnDetail', 'http://srm-java-cooperate-srm-midea-module:8835/api-sup-ce/sc-anon/pjQuickSearch/listPageWarehousingReturnDetail', 'N', 439281408108544, 'GW00244106', '2023-12-13 14:29:56', '10.254.103.115', '李祎哲', 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 0);

insert into scc_base_quicksearch_attr (QUICKSEARCH_ATTR_ID, QUICKSEARCH_ID, ALIAS, ATTR, TITLE, DATA_TYPE, JAVA_TYPE, CODE_LIST, QUERY_ITEM_ENABLED, DISPLAY_ITEM_ENABLED, SEQ, COLUMN_WIDTH, IS_DIALOG_ATTR, ATTR_ORDER, FILEDTYPE, FILEDOPTION, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, CREATED_FULL_NAME, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY_IP, LAST_UPDATED_FULL_NAME, TENANT_ID, VERSION, QUERY_MATCH_OPERATOR, COMPONENT_TYPE, COMPONENT_PROPERTY)
values  (455430911856640, 455234623211520, 't', 'VENDOR_ID', '供应商ID', 'BIGINT', '', '', 'N', 'N', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, '', '', ''),
        (455430911856641, 455234623211520, 't', 'VENDOR_CODE', '供应商编码', 'VARCHAR', '', '', 'Y', 'Y', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, 'allMatch', '', ''),
        (455430911856642, 455234623211520, 't', 'VENDOR_NAME', '供应商名称', 'VARCHAR', '', '', 'Y', 'Y', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, 'allMatch', '', ''),
        (455430911856643, 455234623211520, 't', 'ORGANIZATION_ID', 'ORGANIZATION_ID', '', '', '', 'N', 'N', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, '', '', ''),
        (455430911856644, 455234623211520, 't', 'PER_START_MONTH', 'PER_START_MONTH', '', '', '', 'N', 'N', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, '', '', ''),
        (455430911856645, 455234623211520, 't', 'PER_END_MONTH', 'PER_END_MONTH', '', '', '', 'N', 'N', null, '', '', null, null, null, 439281408108544, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', null, 'GW00244106', '2023-12-14 17:07:21', '10.254.103.115', '李祎哲', '', 0, '', '', '');

