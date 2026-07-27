insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_SCORE_STATUS', '专家库 - 专家评审状态', '专家库 - 专家评审状态', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：未评价
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SCORE_STATUS' and LANGUAGE = 'zh_CN'),
    'DRAFT',      '未评价',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：寻源组长已评价
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SCORE_STATUS' and LANGUAGE = 'zh_CN'),
    'SOU_LEADER',      '寻源组长已评价',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：寻源负责人已评价
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SCORE_STATUS' and LANGUAGE = 'zh_CN'),
    'SOU_MANAGER',      '寻源负责人已评价',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 4 ：已生成评价结果
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SCORE_STATUS' and LANGUAGE = 'zh_CN'),
    'FINISH',      '已生成评价结果',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');