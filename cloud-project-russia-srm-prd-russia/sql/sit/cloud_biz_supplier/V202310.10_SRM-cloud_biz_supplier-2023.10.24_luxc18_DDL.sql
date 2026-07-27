alter table scc_sup_company_info
    alter column FOCUS_FLAG             set default 'N',
    alter column POSITION_LIMIT_FLAG    set default 'N',
    alter column CATEGORY_LIMIT_FLAG    set default 'N',
    alter column TIME_LIMIT_FLAG        set default 'N',
    alter column CONTRACT_VERIFICATION  set default 'N',
    alter column BIDDING_FLAG           set default 'N',
    alter column KEY_SUPERVISION_FLAG   set default 'N';