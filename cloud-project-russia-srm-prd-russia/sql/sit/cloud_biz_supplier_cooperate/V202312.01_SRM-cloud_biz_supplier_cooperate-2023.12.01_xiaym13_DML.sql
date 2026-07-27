alter table scc_catalog_on_shelves
    modify EXT_ORG_ID_LIST longtext null comment '业务实体ID集合';

alter table scc_catalog_on_shelves
    modify EXT_ORG_CODE_LIST longtext null comment '业务实体编码集合';

alter table scc_catalog_on_shelves
    modify EXT_ORG_NAME_LIST longtext null comment '业务实体名称集合';

