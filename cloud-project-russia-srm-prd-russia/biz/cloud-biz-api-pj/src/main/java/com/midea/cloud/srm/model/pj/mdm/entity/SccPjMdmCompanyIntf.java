package com.midea.cloud.srm.model.pj.mdm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_pj_mdm_company_intf")
public class SccPjMdmCompanyIntf extends BaseEntity {
    /**主键ID */
    @TableId
    private Long mdmCompanyIntfId;
    /**申请编号，OA流程使用 */
    private String appNum;
    /**序号，OA流程使用 */
    private String serialNum;
    /**供应商编码 */
    private String orgCode;
    /**供应商全称，唯一 */
    private String fullName;
    /**供应商简称 */
    private String shortName;
    /**社会信用代码/税号/身份证号，唯一 */
    private String taxCode;
    /**供应商来源，枚举字段 COMMON：一般供应商，示例：COMMON */
    private String supSource;
    /**标题类型，枚举字段：COMPANY：公司，MEN：先生，MADAM：女士，示例：COMPANY */
    private String titleType;
    /**供应商供货类型，枚举字段：PART：汽车零部件，OFFICE_SUPPLY：办公用品，示例：PART */
    private String supplyType;
    /**国家编码，字典（SAP标准）【对接时需确认】 */
    private String countryCode;
    /**地区编码，字典（SAP标准）【对接时需确认】 */
    private String area;
    /**城市 */
    private String city;
    /**地址 */
    private String address;
    /**区域编码，【服务商提供】 */
    private String region;
    /**主营业务，描述控制在500个字符以内 */
    private String supType;
    /**账户组，字典（SAP标准）【对接时需确认】 */
    private String accountGroup;
    /**贸易伙伴，账户组为 Z003时必填 */
    private String tradePartner;
    /**法人代表 */
    private String legalRepresent;
    /**营业执照有效期开始时间，格式：yyyy-MM-dd */
    private String startDate;
    /**营业执照有效期截止时间，格式：yyyy-MM-dd */
    private String endDate;
    /**银行名称 */
    private String bankName;
    /**银行账号 */
    private String bankAccount;
    /**状态，枚举字段 NORMAL：正式，示例：NORMAL */
    private String status;
    /**数据源项目代码，【服务商提供】 */
    private String operSource;
    /**变更生效时间，格式：yyyy-MM-dd 要求：>=当前日期 */
    private String acceptDateScheduled;
    /**申请人，格式：姓名(工号) */
    private String applicant;
    /**申请人电话 */
    private String applicantPhone;
    /**申请日期，格式：yyyy-MM-dd HH:mm:ss */
    private String appDate;
    /**公司代码 */
    private String companyCode;
    /**处理序号 */
    private String processSerialNum;
    /**处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试 */
    private String processStatus;
    /**处理信息 */
    private String processMessage;
    /**处理时间 */
    private Date processDate;
    /**处理批次号 */
    private Long processGroupId;
}
