alter table scc_sou_inq_project add EXT_EXCLUDE_ORG_LIMIT_VENDORS char default 'N' null comment '排除非本业务受限实体供应商' after EXCLUDE_ORG_CATEGORY_STATUS;
alter table scc_sou_inq_project add EXT_VENDOR_PERFORMANCE_RANK int null comment '供应商绩效前几名' after EXT_EXCLUDE_ORG_LIMIT_VENDORS;
alter table scc_sou_inq_project add EXT_IS_RANDOM char default 'N' null comment '是否随机' after EXT_VENDOR_PERFORMANCE_RANK;
alter table scc_sou_inq_project add EXT_DEPARTMENT_ID varchar(50) null comment '部门ID' after EXT_IS_RANDOM;
alter table scc_sou_inq_project add EXT_DEPARTMENT_NAME varchar(150) null comment '部门名称' after EXT_DEPARTMENT_ID;
alter table scc_sou_inq_item add EXT_AREA_ID varchar(50) null comment '区域ID' after REMARK;
alter table scc_sou_inq_item add EXT_AREA_CODE varchar(50) null comment '区域编码' after EXT_AREA_ID;
alter table scc_sou_inq_item add EXT_AREA_NAME varchar(150) null comment '区域名称' after EXT_AREA_CODE;