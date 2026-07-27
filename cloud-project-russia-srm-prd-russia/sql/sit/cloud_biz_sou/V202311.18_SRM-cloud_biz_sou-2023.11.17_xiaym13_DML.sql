alter table scc_npm_pre_bid_feedback_vendor
    modify FEEDBACK_STATUS varchar(50) default 'NO_FEEDBACK' not null comment '反馈状态';

