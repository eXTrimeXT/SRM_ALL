package com.midea.cloud.srm.biz.pj.utils;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/6/28 9:59
 *  修改内容:
 * </pre>
 */
public class MqlType {
    public static final String SUPPLIER_INVENTORY = "SupplierInventory";
    public static final String SUPPLIER_INVENTORY_LOG = "SupplierInventoryLog";
    public static final String PURCHASE_CATA_LOG = "PurchaseCatalog";
    public static final String CAPACITY_REPORT = "CapacityReport";
    public static final String SPC_RULE_STANDARD_BUYER = "SpcRuleStandardBuyer";
    public static final String SPC_RULE_PARAM = "spcRuleParam";
    public static final String SPC_MONITOR_LIST_BUYER = "spcMonitorListBuyer";
    /**
     * 供应商-主数据
     */
    public static final String SUPPLIER = "CompanyInfo";
    /**
     * 供应商-查询黑名单
     */
    public static final String BLACKSUPPLIER = "BlackCompany";
    /**
     * 黑名单头
     */
    public static final String BLACK = "Black";
    /**
     * 供应商-组织品类信息
     */
    public static final String ORGCATEGORY = "OrgCategory";
    /**
     * 供应商-银行信息
     */
    public static final String BANKINFO = "BankInfo";
    /**
     * 供应商-财务信息
     */
    public static final String FINANCEINFO = "FinanceInfo";
    /**
     * 供应商-地点信息
     */
    public static final String SITEINFO = "SiteInfo";
    /**
     * 供应商-联系人
     */
    public static final String CONTACTINFO = "ContactInfo";
    /**
     * 供应商-管理体系信息认证附件
     */
    public static final String MANAGEMENTATTACH = "ManagementAttach";
    /**
     * 供应商主数据-异常信息
     */
    public static final String NPM_COMPANY_EXCEPTION_INFO = "npmCompanyExceptionInfo";
    /**
     * 管理体系信息
     */
    public static final String MANAGEMENTINFO = "ManagementInfo";
    /**
     * 准入附件记录
     */
    public static final String FILERECORD = "FileRecord";
    /**
     * 组织-准入日志表
     */
    public static final String ORGJOURNAL = "OrgJournal";
    /**
     * 品类日志
     */
    public static final String CATEJOURNALSITEFORM = "CateJournalSiteForm";
    /**
     * 能力考察
     */
    public static final String SITEFORM = "SiteForm";
    /**
     * 组织-品类关系
     */
    public static final String ORGCATEJOURNAL = "OrgCateJournal";
    /**
     * 开发申请
     */
    public static final String DEVELOPAPPLICATION = "developApplication";
    /**
     * 品类专家委员会评审结果
     */
    public static final String CATEGORYREVIEWERCOMMENT = "categoryReviewerComment";
    /**
     * 合作终止
     */
    public static final String ORGCATFORM = "OrgCatForm";
    /**
     * 合作终止 - 明细
     */
    public static final String ORGCATFORMCATEGORYDETAIL = "OrgCatFormCategoryDetail";
    /**
     * 样件验证
     */
    public static final String QUASAMPLE = "QuaSample";

    /**
     * 准入流程配置*
     */
    public static final String ENTRY_CONFIG = "sup_entry_config_ide";
    /**
     * 准入流程配置-节点表
     */
    public static final String ENTRY_CONFIG_NODE = "pjEntryConfigNode";

    /**
     * 待办记录表
     */
    public static final String SCC_PJ_TODOWITHBPM = "SccPjTodowithbpm";

    public static final String SCC_PJ_API_PAYMENT_RESULT_INTF = "SccPjApiPaymentResultIntf";

    public static final String SCC_PJ_API_PAYMENT_LINE_INTF = "SccPjApiPaymentLineIntf";

    public static final String SCC_PJ_API_PAYMENT_HEAD_INTF = "SccPjApiPaymentHeadIntf";

    public static final String SCC_PJ_API_INVOICE_HEAD_INTF = "SccPjApiInvoiceHeadIntf";

    public static final String SCC_PJ_API_INVOICE_BASE_INTF = "SccPjApiInvoiceBaseIntf";

    public static final String SCC_PJ_API_INVOICE_CON_INTF = "SccPjApiInvoiceConIntf";

    public static final String SCC_PJ_API_INVOICE_SETTLE_INTF = "SccPjApiInvoiceSettleIntf";

    public static final String SCC_PJ_API_SETTLE_INV_INTF = "SccPjApiSettleInvIntf";

    public static final String SCC_PJ_API_SETTLE_INFO_INTF = "SccPjApiSettleInfoIntf";

    public static final String SCC_PJ_API_INVOICE_COST_INTF = "SccPjApiInvoiceCostIntf";

    public static final String SCC_PJ_API_INVOICE_COLL_INTF = "SccPjApiInvoiceCollIntf";

    public static final String SCC_PJ_API_INVOICE_ATTACH_INTF = "SccPjApiInvoiceAttachIntf";

    public static final String SCC_PJ_API_ACOUNT_HEAD_INTF = "SccPjApiAcountHeadIntf";
    public static final String SCC_PJ_API_ACOUNT_BASE_INTF = "SccPjApiAcountBaseIntf";
    public static final String SCC_PJ_API_ACOUNT_SETTLE_INTF = "SccPjApiAcountSettleIntf";
    public static final String SCC_PJ_API_ACOUNT_SETINFO_INTF = "SccPjApiAcountSetinfoIntf";
    public static final String SCC_PJ_API_ACOUNT_COST_INTF = "SccPjApiAcountCostIntf";
    public static final String SCC_PJ_API_ACOUNT_ATTACH_INTF = "SccPjApiAcountAttachIntf";
    public static final String SCC_PJ_API_ACOUNT_RESP_INTF = "SccPjApiAcountRespIntf";

    public static final String SCC_PJ_CATEGORY_SYNC_TEMP = "SccPjCategorySyncTemp";
    public static final String SCC_RBAC_USERTHIRD = "SccRbacUserThird";
    /**
     * 供应商失信名单同步-重点关注
     */
    public static final String SUPPLIER_FOCUS = "SccBaseFocusCompany";

    /**
     *word文件映射
     */
    public static final String WORD_FILE_LINK = "WordFileLink";
}
