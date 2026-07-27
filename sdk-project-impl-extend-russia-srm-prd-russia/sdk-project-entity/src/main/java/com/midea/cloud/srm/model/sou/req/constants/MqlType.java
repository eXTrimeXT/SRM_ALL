package com.midea.cloud.srm.model.sou.req.constants;

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
 *  修改日期: 2023/10/7 16:40
 *  修改内容:
 * </pre>
 */
public class MqlType {
    /** 采购申请头 */
    public static final String PURCHASE_REQUIREMENT_HEAD = "PurchaseRequirementHead";
    /** 采购申请行 */
    public static final String PURCHASE_REQUIREMENT_LINE = "PurchaseRequirementLine";

    /**
     * EMD附件同步记录表
     */
    public static final String SCC_PR_REQ_EDMATTACH_SYNC = "SccPrReqEdmattachSync";

    /** 申请单扩展表 */
    public static final String EXT_PR_SOU_REQUIREMENT_HEAD = "ExtPrSouRequirementHead";
    /** 寻源单意向金开票 */
    public static final String SOU_DEPOSIT_INVOICE_BUYER = "SouIntDepositInvoiceBuyer";
    /** 寻源需求邀请供应商头表 */
    public static final String SOU_INVITE_HEAD_BUYER = "SouInviteHeadBuyer";
    /** 邀请供应商行表 */
    public static final String SOU_INVITE_ITEM_BUYER = "SouInviteItemBuyer";
    public static final String SOU_INVITE_HISTORY_BUYER = "SouInviteHistoryBuyer";
    /** 寻源需求头表 */
    public static final String SOU_REQ_HEAD_BUYER = "SouReqHeadBuyer";
    /** 招标资料递交 */
    public static final String SUBMIT_BUYER = "SubmitBuyer";
    /** 寻源报名 */
    public static final String SOU_REQ_APPLY_BUYER = "SouReqApplyBuyer";
    /** 寻源报名(供应商) */
    public static final String SOU_REQ_APPLY = "SouReqApply";
    /** 关联供应商表 */
    public static final String SOU_RELATION_SUP_BUYER = "RelationSupBuyer";
    /** 开票信息 */
    public static final String SOU_INVOICE_INFO = "SouInvoiceInfo";
    /** 供应商-主数据 */
    public static final String SUPPLIER = "CompanyInfo";
    /** 供应商爬虫缓存表 */
    public static final String VENDOR_BIGDATA = "VendorBigdata";
    /** 供应商爬虫人员信息缓存表 */
    public static final String VENDOR_BIGLINK = "VendorBiglink";
    /** 供应商-联系人 */
    public static final String CONTACTINFO = "ContactInfo";

    /**
     * 招标计划工作成员表
     */
    public static final String EXT_PR_SOU_REQUIREMENT_GROUP = "ExtPrSouRequirementGroup";

    /**
     * 标前反馈单供应商反馈附件
     **/
    public static final String PRE_BID_BACK_ATTACH = "PreBidBackAttach";
    /**
     * 寻云单修改历史表附件
     */
    public static final String SOU_INFO_HISTORY_ATTACH = "SouInfoHistoryAttach";

    /**
     * 招标价格库
     */
    public static final String BID_PRICE = "BidPrice";
    /**
     * 专家库
     */
    public static final String EXT_SOU_EXPERT = "ExtSouExpert";

    /**
     * 招标计划附件
     */
    public static final String EXT_PR_SOU_REQUIREMENT_ATTACH = "ExtPrSouRequirementAttach";
    /**
     * 质疑
     */
    public static final String QUESTION = "Question";

    /**
     * 审批流程记录
     */
    public static final String FLOW_INSTANCE_RECORD = "FlowInstanceRecord";

    /**
     * 招标主表
     */
    public static final String NPM_SOU_PROJECT = "NpmSouProject";

    /**
     * 招标申请单号关联
     */
    public static final String NPM_SOU_DEMAND = "NpmSouDemand";

    /**
     * 考察申请
     */
    public static final String INSPECT = "Inspect";

    /**
     * 澄清列表
     */
    public static final String ANSWER  = "Answer";

    /**
     * 合同头表
     */
    public static final String CONTRACT_HEAD = "ContractHead";

    /**
     * 用户组织授权表
     */
    public static final String ORGANIZATION_USER = "OrganizationUser";
    /**
     * 购物车
     */
    public static final String SHOP_CART = "ShopCart";

    /**
     * 场景附件表
     */
    public static final String SCC_BASE_SCENE_FILE = "SccBaseSceneFile";

    /**
     * 品类分工表
     */
    public static final String DIVISION_CATEGORY = "DivisionCategory ";

    /**
     * 标前反馈
     */
    public static final String PRE_BID_FEEDBACK_BUYER = "PreBidFeedbackBuyer";

    /**
     * 标前反馈供应商
     */
    public static final String PRE_BID_FEEDBACK_VENDOR_BUYER = "PreBidFeedbackVendorBuyer";

    /**
     * 标前交流通知
     */
    public static final String PRE_BID_NOTICE_BUYER = "PreBidNoticeBuyer";

    /**
     * 角色值集表
     */
    public static final String SCC_RBAC_ROLE = "SccRbacRole";

    /**
     * 用户角色表
     */
    public static final String SCC_RBAC_ROLE_USER = "SccRbacRoleUser";

    /**
     * 异常登记
     */
    public static final String SCC_NPM_SOU_ABNORMAL_REG = "SccNpmSouAbnormalReg";

    /**
     * 异常登记附件
     */
    public static final String SCC_NPM_SOU_ABNORMAL_FILE = "SccNpmSouAbnormalFile";

    /**
     * 财务-公司信息
     */
    public static final String SCC_SOU_FINANCE_COMPANY = "SouFinanceCompany";

    /**
     * 内部商城上下架
     */
    public static final String CATALOG_ON_SHELVES = "CatalogOnShelves";

    /**
     * 项目化绩效评分结果-头表
     */
    public static final String SCC_NPM_PROJECT_SCORE_HEADER = "SccNpmProjectScoreHeader";

    /**
     * 需求提报-推荐单位
     */
    public static final String EXT_PR_SOU_REQUIREMENT_VENDOR = "ExtPrSouRequirementVendor";

    public static final String BID_NOTICE_DETAIL = "BidNoticeDetail";

    public static final String BID_NOTICE = "BidNotice";

    /**
     * 采购订单
     */
    public static final String ORDER = "Order";

    /**
     * 采购订单-供应商端
     */
    public static final String ORDER_VENDOR = "OrderVendor";

    /**
     * 采购申请取消
     */
    public static final String PR_SOU_REQUIREMENT_CANCEL_FOR_BUYER = "PrSouRequirementCancelForBuyer";

    /**
     * 采购申请取消行
     */
    public static final String EXT_PR_SOU_REQUIREMENT_CANCEL_LINE = "ExtPrSouRequirementCancelLine";

    /**
     * 定时任务扫描
     */
    public static final String TIMER_TASK = "TimerTask";

    public static final String PR_SOU_REQUIREMENT_POOL_FOR_BUYER = "PrSouRequirementPoolForBuyer";

    /**
     * 历史价格
     */
    public static final String SCC_SC_HISTORY_PRICE_TEMP = "SccScHistoryPriceTemp";

    /**
     * 订单历史
     */
    public static final String SCC_SC_ORDER_HISTORY = "SccScOrderHistory";

    public static final String ANSWER_VENDOR = "AnswerVendor";

    public static final String SCC_PJ_USER = "SccPjUser";

    public static final String MONITORING = "Monitoring";

    public static final String SCC_SOU_PAYMENT_API_REL = "sccSouPaymentApiRel";

    public static final String SCC_BASE_SYSTEM_CONFIGURE = "SccBaseSystemConfigure";

    public static final String ORG_CATEGORY = "OrgCategory";

    /**
     *围串标识别流水
     */
    public static final String ORDER_FILE_CHECK = "OrderFileCheck";
    /**
     *word文件映射
     */
    public static final String WORD_FILE_LINK = "WordFileLink";

}
