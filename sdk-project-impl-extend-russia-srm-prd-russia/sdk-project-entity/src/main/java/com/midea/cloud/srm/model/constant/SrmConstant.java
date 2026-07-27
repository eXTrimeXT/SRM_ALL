package com.midea.cloud.srm.model.constant;

import com.midea.cloud.srm.model.sou.enums.TypeEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author 100014337
 */
public class SrmConstant {
    public static final String YES= "Y";
    public static final String NO= "N";
    /**流程模板配置：流程模板编码：寻源需求单*/
    public static final String FLOW_CODE_SOU_REQ_HEAD = "souReqHead";

    /**流程模板配置：流程模板编码：招标资料提交*/
    public static final String FLOW_CODE_BID_DATA_SUBMIT = "bidDataSubmit";

    /**流程模板配置：流程模板编码：考察申请*/
    public static final String FLOW_CODE_INSPECT_APPLY = "INSPECT_APPLY";

    /**流程模板配置：流程模板编码：推荐供应商*/
    public static final String FLOW_CODE_RCOMMVENDOR = "RCOMMVENDOR";


    /**流程模板配置：流程模板编码：定标申请*/
    public static final String FLOW_CODE_SOU_CA = "SOU_CA";

    /**流程模板配置：流程模板编码：废标申请*/
    public static final String FLOW_CODE_SOU_DCA = "SOU_DCA";

    /**流程模板配置：流程模板编码：中/落标申请*/
    public static final String FLOW_CODE_SOU_TN = "SOU_TN";

    /**流程模板配置：流程模板编码：中/落标废弃*/
    public static final String FLOW_CODE_SOU_ATN = "SOU_ATN";

    /**流程模板配置：流程模板编码：考察报告*/
    public static final String FLOW_CODE_INSPECT_REPORT = "INSPECT_REPORT";

    /**流程模板配置：流程模板编码：寻源需求单*/
    public static final String PR_ABANDON_DEAULT_REASON = "采购需求取消";

    public static final String SIG_1= ";";
    public static final String SIG_2= "|";
    public static final String SIG_3= ",";

    public static final String LEFT_BRACE = "<";
    public static final String RIGHT_BRACE = ">";
    public static final String SIG_4 = "@";

    public static final Long LONG_ZERO = 0L;

    public static final Long LONG_ONE = 1L;

    public static final Integer NUM_ZERO = 0;

    public static final Integer NUM_MINUS_ONE = -1;

    public static final Integer NUM_ONE = 1;

    public static final Integer NUM_TWO = 2;

    public static final Integer NUM_THREE = 3;

    public static final Integer NUM_FOUR = 4;

    public static final Integer NUM_FIVE = 5;

    public static final Integer NUM_SIX = 6;

    public static final Integer NUM_SEVEN = 7;

    public static final Integer NUM_EIGHT = 8;

    public static final Integer NUM_FIFTEEN = 15;

    /**招标评分规则 综合评定结论项 */
    public static final String SOU_SCORE_REVIEW_ITEM_CONCLUSION = "结论";

    public static final Long LONG_MINUS_ONE = -1L;

    public static final String UNDER_LINE = "_";

    public static final String SHORT_LINE = "-";

    public static final String SIGN_ABSTRACT = "签署摘要.pdf";

    /**
     * 签署不需要回写的列表
     */
    public static final List<String> SIGN_EXCLUDE_LIST = Arrays.asList(SIGN_ABSTRACT);

    /**
     * 虚拟供应商编码
     */
    public static final String VIRTUAL_VENDOR_CODE = "SRM";

    /**
     * 定标供应商谈判固定列
     */
    public static final String CA_NEGOTIATE_FIRST_COL = "投标供应商";

    /**
     * 定标申请附件
     */
    public static final String SCENE_SOU_CA_ATTACHMENT = "SCENE_SOU_CA_ATTACHMENT";

    /**
     * 品类分工供应商负责人
     */
    public static final String DIVISION_CATEGORY_DUTY_SUPPLIER_LEADER = "Supplier Leader";

    public static final String IP_MONITOR_TECH_SCORE = "评标";

    public static final String GSCP_STATUS_OPEN = "Open";

    /**
     * 集团招标负责人
     */
    public static final String RESPONSIBLE_PERSON_OF_GROUP_BIDDING = "Responsible_person_of_group_bidding";

    public static final char BRACKET_LEFT = '[';

    public static final char BRACKET_RIGHT = ']';

    public static final String BRACKET_LEFT_STR = "\\[";

    public static final String BRACKET_RIGHT_STR = "]";

    public static final String DOUBLE_QUOTATION = "\"";

    public static final String BACKSLASH = "\\\\";

    public static final String GSCP_EXCEPTION = "GSCP接口请求异常（超时）";

    public static final String COLON = ": ";

    public static final String PARENTHESES_LEFT = "(";

    public static final String PARENTHESES_RIGHT = ")";

    public static final String PER_CENT = "%";

    public static final String SUPPLIER_COOPERATE = "协同";

    public static final String REFLECTION_SET = "set";

    public static final String REFLECTION_GET = "get";

    public static final String SCHEDULE_FIELD_ENDWITH = "Proportion";

    public static final String SYS_SRM = "SRM";

    public static final String HR = "HR";

    public static final String DEAULT_CURRENCY_CODE = "RMB";

    public static final String DEAULT_CURRENCY_NAME = "人民币";

    public static final String API_PAYMENT_NATURE_NAME_MARGIN = "付（供应商）保证金";

    public static final String API_PAYMENT_NATURE_NAME_INT_DEPOSIT_REFUND = "采购-其他";

    public static final String API_SETTLE_DEAULT_DEPART_NAME = "招标部";

    /**
     * FW0907/意向金冲销FW9901/保证金扣款FW9902
     */
    public static final String API_SETTLE_SERVICE_CODE_FW9902 = "FW9902";

    /**
     * FW0907/意向金冲销FW9901/保证金扣款FW9902
     */
    public static final String API_SETTLE_SERVICE_CODE_FW9901 = "意向金冲销FW9901";

    public static final String API_SETTLE_TAX_CLASSIFY_CODE = "3049900000000000000";

    public static final String TEN_THOUSAND_STR = "10000";

    public static final String SUCCESS = "SUCCESS";

    public static final String CC_API_PAYMENT_BID = "BID";

    public static final String CC_API_PAYMENT_REQ = "REQ";

    public static final String CC_API_PAYMENT_REQ_ACOUNT = "REQ_ACOUNT";

    public static final String CODE = "code";

    public static final String SUCCESS_CODE = "200";

    public static final String SUCCESS_NAME = "成功";

    public static final String ACC_SUBJECT_CODE_MARGIN = "22410300";

    public static final String ACC_SUBJECT_NAME_MARGIN = "其他应付款-保证金押金";

    /** 履约管理员 */
    public static final String SYSTEM_CONFIG_PERF_REMIND_LIST = "PERF_REMIND_LIST";
}
