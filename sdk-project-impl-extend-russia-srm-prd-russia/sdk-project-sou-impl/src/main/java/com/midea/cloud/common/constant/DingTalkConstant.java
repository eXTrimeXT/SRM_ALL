package com.midea.cloud.common.constant;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public class DingTalkConstant {

    /**
     * 中落标通知审批通过创建合同、钉钉消息模板编码
     */
    public static final String BID_NOTICE_CONTRACT_CREATE = "BID_NOTICE_CONTRACT_CREATE";

    /** 寻源需求供应商报名关联关系钉钉通知 */
    public static final String SOU_REQ_VENDOR_SIGNUP = "SOU_REQ_VENDOR_SIGNUP";

    /** 高层定标厂家多次为同一厂家 */
    public static final String HIGHT_LEVEL_BID_SAME_TIMES = "HIGHT_LEVEL_BID_SAME_TIMES";

    /** 后一次报价比上一次价格上涨 */
    public static final String SOU_BID_PRICE_UP = "SOU_BID_PRICE_UP";

    /** 时间紧急特殊招标， 后续实际执行时间（发标到中标结果）超出申请时间 */
    public static final String SPECIAL_SOU_EMERGENCY = "SPECIAL_SOU_EMERGENCY";

    /**
     * 招标项目[${souNo}][${souName}]特殊招标申请中唯一单位为${niqueVendor}，实际定标单位为${actualVendor}
     */
    public static final String CA_SPECIAL_REMINDERS = "CA_SPECIAL_REMINDERS";

    /** 递交申请资料预警、发标、收标、评标、上报、定标、中落标通知 */
    public static final String SOU_BID_PROCESS_REMIND = "SOU_BID_PROCESS_REMIND";

    /**  您有新的通知“[${souNo}][${souName}][${vendorName}]”付款${payResult}，请知悉并做相应处理 */
    public static final String PAY_SUCESS_BID = "PAY_SUCESS_BID";

    /**  您有新的通知“[${reqHeadNum}][${projectName}][${vendorName}]”付款${payResult}，请知悉并做相应处理 */
    public static final String PAY_SUCESS_REQ = "PAY_SUCESS_REQ";

    /**  您有新的工作通知：“${souNo}#${souName}#${vendorName}#${description}”该供应商需做拉黑处理，请及时处理； */
    public static final String PAY_BLACK_AS_Y = "PAY_BLACK_AS_Y";

}
