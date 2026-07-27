insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_INQ_VENDOR_SOURCE_FROM', '长城询比价邀请供应商数据来源', '长城询比价邀请供应商数据来源', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：智能推荐
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_INQ_VENDOR_SOURCE_FROM' and LANGUAGE = 'zh_CN'),
    'AUTO_RECOMMEND',      '智能推荐',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：手动新增
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_INQ_VENDOR_SOURCE_FROM' and LANGUAGE = 'zh_CN'),
    'HAND_MAKE',      '手动新增',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');