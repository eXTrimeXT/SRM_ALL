alter table scc_pr_requirement_line
    change column `EXT_HISTORY_VENDOR_FLAG` `EXT_HISTORY_VENDOR_FLAG` varchar(20) DEFAULT 'N' COMMENT '是否引出历史供应商，Y/N';
