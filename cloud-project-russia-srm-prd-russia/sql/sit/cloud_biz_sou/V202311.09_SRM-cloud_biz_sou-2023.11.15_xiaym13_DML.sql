alter table scc_npm_sou_req_apply
    add WITHDRAW_REASON varchar(1000) null comment '撤回原因' after APPLY_HANDLE_REASON;

alter table scc_npm_sou_invoice_info
    alter column STATUS set default 'DRAFT';
