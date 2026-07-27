delete from scc_base_dict_item where DICT_ID in (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION');
delete from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION';
delete from scc_base_dict_item where DICT_ID in (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX');
delete from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX';
delete from scc_base_dict_item where DICT_ID in (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS');
delete from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS';

-- 字典：专家库学历
insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_EDUCATION', '专家库学历', '专家库学历', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：博士
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '1',      '博士',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：硕士
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '2',      '硕士',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 3 ：本科
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '3',      '本科',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 4 ：专科
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '4',      '专科',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 5 ：高中
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '5',      '高中',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 6 ：中专
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '6',      '中专',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 7 ：初中
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '7',      '初中',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 8 ：初中以下
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_EDUCATION' and LANGUAGE = 'zh_CN'),
    '8',      '初中以下',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');


-- 字典：专家库性别
insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_SEX', '专家库性别', '专家库性别', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：男
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX' and LANGUAGE = 'zh_CN'),
    '1',      '男',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：女
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_SEX' and LANGUAGE = 'zh_CN'),
    '2',      '女',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');


-- 字典：专家库在职状态
insert into scc_base_dict
(DICT_ID, DICT_CODE, DICT_NAME, DESCRIPTION, LANGUAGE, LANGUAGE_NAME,
 CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP, LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     'EXT_SOU_EXPERT_JOB_STATUS', '专家库在职状态', '专家库在职状态', 'zh_CN', '中文',
     '1',       'admin',    now(),         '127.0.0.1', '1',             'admin',         '127.0.0.1');
-- 1 ：在职
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS' and LANGUAGE = 'zh_CN'),
    '1',      '在职',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');
-- 2 ：离职
insert into scc_base_dict_item
(DICT_ITEM_ID,
 DICT_ID,
 DICT_ITEM_CODE, DICT_ITEM_NAME, ITEM_LANGUAGE, ITEM_LANGUAGE_NAME,
 ACTIVE_DATE, CREATED_ID, CREATED_BY, CREATION_DATE, CREATED_BY_IP,
 LAST_UPDATED_ID, LAST_UPDATED_BY, LAST_UPDATED_BY_IP)
values
    ((select max(ii.DICT_ITEM_ID) + 1 from scc_base_dict_item ii),
     (select DICT_ID from scc_base_dict where DICT_CODE = 'EXT_SOU_EXPERT_JOB_STATUS' and LANGUAGE = 'zh_CN'),
    '2',      '离职',          'zh_CN',       '中文',
            current_date, '1',       'admin',    now(),         '127.0.0.1',
    '1',             'admin',         '127.0.0.1');