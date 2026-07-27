package com.midea.cloud.srm.model.pj.hruser.entity;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author huangbf3
 * 长城 - 员工信息
 *
 * 结构参考: https://open.gwm.cn/goodsdetail?group_id=a6a41d00072a4a2e8a15e15c539f0796&tabQueryType=0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SccPjHrUserInfo extends BaseObjectX {

    @ApiModelProperty("员工工号")
    private String personnelNo;

    @ApiModelProperty("是否正式员工(1-正式工/2-非正式工)")
    private String isFormal;

    @ApiModelProperty("是否外籍(0-否/1-是)")
    private String isForeign;

    @ApiModelProperty("国籍(??)")
    private String country;

    @ApiModelProperty("证件姓名")
    private String certificateName;

    @ApiModelProperty("中文名称")
    private String chineseName;

    @ApiModelProperty("性别(1-男/2-女)")
    private String sex;

    @ApiModelProperty("户口所在地")
    private String registeredResidence;

    @ApiModelProperty("居住类型(1-定居/2-租住)")
    private String liveType;

    @ApiModelProperty("政治面貌(1-中国共产党党员/2-共青团员/3-群众)")
    private String politicalAffiliation;

    @ApiModelProperty("民族(??)")
    private String nation;

    @ApiModelProperty("健康状况")
    private String health;

    @ApiModelProperty("简历编号")
    private String resumeNumber;

    @ApiModelProperty("户籍编号")
    private String houseNumber;

    @ApiModelProperty("户籍所在地")
    private String dwellingUnit;

    @ApiModelProperty("工作区域(??)")
    private String workAddress;

    @ApiModelProperty("学历(1-博士/2-硕士/3-本科/4-专科/5-高中/6-中专/7-初中/8-初中以下)")
    private String diploma;

    @ApiModelProperty("教育类型(1-统招/2-自考/3-成教/4-网络/5-夜校/6-公司大专班)")
    private String educationType;

    @ApiModelProperty("招聘来源(1-校园招聘/2-普工招聘/3-有经验招聘/5-复职)")
    private String recruitmentSources;

    @ApiModelProperty("毕业学校")
    private String graduateSchool;

    @ApiModelProperty("专业")
    private String professional;

    @ApiModelProperty("毕业日期")
    private Date graduateTime;

    @ApiModelProperty("英语等级(0-无/1-专业八级/2-专业四级/3-八级/4-六级/5-四级/6-三级/7-雅思/8-托福)")
    private String englishAbility;

    @ApiModelProperty("计算机等级(0-无/1-一级/2-二级/3-三级/4-四级)")
    private String computerAbility;

    @ApiModelProperty("其他证书")
    private String certificate;

    @ApiModelProperty("其他语言")
    private String otherLanguage;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("QQ")
    private String qq;

    @ApiModelProperty("职称")
    private String title;

    @ApiModelProperty("所在组织ID")
    private Long groupId;

    @ApiModelProperty("用工类型(1-正式人员/3-聘用人员/4-返聘人员/5-实习人员/6-劳务派遣/7-外包人员)")
    private String workType;

    @ApiModelProperty("职务(??)")
    private String duty;

    @ApiModelProperty("职系 举例:管理层,产品企划,过程设计,制造技术,售后技术")
    private String grade;

    @ApiModelProperty("职位 举例:组长,班长,秘书,文员,董事长")
    private String position;

    @ApiModelProperty("职级(P1)")
    private String rank;

    @ApiModelProperty("职级名称")
    private String rankName;

    @ApiModelProperty("岗位编码")
    private String positionCode;

    @ApiModelProperty("岗位实践阶段 1试用期 2实习期 3学习期 4定岗期 ,旧版数据库有 0:-1")
    private String postPracticeStage;

    @ApiModelProperty("细分岗位")
    private String subdivisionPost;

    @ApiModelProperty("职务等级 0职员 10主管 20科级 30部级 40经理级 50总裁级")
    private String dutyRank;

    @ApiModelProperty("入厂日期")
    private Date admissionDate;

    @ApiModelProperty("人员状态,1:在职,2:离职")
    private String state;

    @ApiModelProperty("直接领导id")
    private String directLeaderId;

    @ApiModelProperty("考勤审核天数")
    private String approvalPowerNumber;

    @ApiModelProperty("银行卡号")
    private String bankCard;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("个人照片在文件服务器的文件code")
    private String photoFileCode;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("更新人id")
    private String updateUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("人员备注,用于技术中心")
    private String personRemark;

    @ApiModelProperty("英语托业成绩")
    private String englishScore;

    @ApiModelProperty("是否签订挑战者承诺书,0:不涉及,1:是,2:否")
    private String isCommitment;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("员工所属机构(优才系统使用)")
    private String personMechanism;

    @ApiModelProperty("招聘人(优才系统使用)")
    private String recruiter;

    @ApiModelProperty("返校时间(优才系统使用)")
    private String backSchoolTime;

    @ApiModelProperty("英文名称")
    private String englishName;

    @ApiModelProperty("组织ID对应的英文名称")
    private String groupNameEn;

    @ApiModelProperty("职务名称")
    private String dutyName;

    @ApiModelProperty("职位名称")
    private String positionName;

}
