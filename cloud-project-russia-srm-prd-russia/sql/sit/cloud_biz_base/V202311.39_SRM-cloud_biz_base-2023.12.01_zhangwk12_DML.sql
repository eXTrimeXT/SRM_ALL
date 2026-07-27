update scc_base_dict_item set DICT_ITEM_NAME = '政府指定'
where DICT_ID in (select DICT_ID from scc_base_dict where DICT_CODE = 'PR_SOU_REQUIREMENT_SPECIAL_REASON')
  and DICT_ITEM_CODE = 'GOVERNMENT';