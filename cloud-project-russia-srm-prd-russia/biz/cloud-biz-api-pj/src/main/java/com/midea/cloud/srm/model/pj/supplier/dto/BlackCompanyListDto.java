package com.midea.cloud.srm.model.pj.supplier.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.midea.cloud.srm.model.pj.sapcreatesupview.dto.SapCreateSupViewDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author fubiao
 */
@Data
public class BlackCompanyListDto {
/**    	失信列表 */
    /**    否	string			失信公司名单 */
    @JSONField(name ="companyName")
    private String companyName;
    /**    否	string			失信类型（重点关注、禁止合作） */
    @JSONField(name ="companyType")
    private String companyType;
    /**    否	string				问题类别 */
    @JSONField(name ="question")
    private String question;
    /**    否	string			信息类型（腐败黑名单、合同黑名单、招标黑名单、其他） */
    @JSONField(name ="messageFrom")
    private String messageFrom;
    /**    否	string			生效时间 */
    @JSONField(name ="endTime")
    private String endTime;
    /**    否	string			失信公司社会信用码 */
    @JSONField(name ="taxCode")
    private String taxCode;
    /**    否	string				MDM创建时间 */
    @JSONField(name ="createTime")
    private String createTime;
    /**    否	string			MDM更新时间 */
    @JSONField(name ="updateTime")
    private String updateTime;

    /**    否	string			失信公司名单编码 */
    @JSONField(name ="companyCode")
    private String companyCode;
    /**    否	string			注册资金 */
    @JSONField(name ="registeredCapital")
    private String registeredCapital;
    /**    否	string			法定代表人 */
    @JSONField(name ="legalPerson")
    private String legalPerson;
    /**    否	string			营业地址 */
    @JSONField(name ="companyCountry")
    private String companyCountry;
    /**    否	string			营业地址省州 */
    @JSONField(name ="companyProvince")
    private String companyProvince;

    /**    否	string			营业地址城市 */
    @JSONField(name ="companyCity")
    private String companyCity;

    /**    否	string			企业成立日期 */
    @JSONField(name ="companyCreationDate")
    private LocalDate companyCreationDate;

}
