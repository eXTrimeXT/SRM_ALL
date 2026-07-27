package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskDataGeneration;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskCompanyInfo;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: for srm 构造、生成数据
 *
 * @author srm
 * @date 2024-05-18
 */
public class VendorRiskDataGenerator extends AbstractRiskDataGeneration {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {

        //创建供应商风险返回对象变量
        RecommvendorRiskDto recommvendorRiskDto = new RecommvendorRiskDto();

        //请求参数，供应商ID列表集合
        List<Long> vendorIdList = riskRequest.getVendorIdList();

        //供应商信息变量定义
        List<Record> companyInfoList = null;

        //判断是否启用阳光诚信接口，由调用方设置
        if(riskRequest.getSunshineCreditFlag()) {
            /** 启用阳光诚信接口 */
            companyInfoList = RiskComponent.getInstance().getExtSupplierClient().querySupplierRiskBlacklist(vendorIdList);
        } else {
            /** 不启用阳光诚信接口，直接查询供应商库表 */
            QlOpenQueryWrapper qlOpenQueryWrapper = QlOpenWrappers.query(MqlType.SUPPLIER).in(CompanyInfo::getCompanyId, vendorIdList);
            //通过RiskComponent组件获取依赖Bean，使用QlOpenClient 跨微服务查询供应商库
            companyInfoList = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, qlOpenQueryWrapper, Record.class);
        }

        //设置供应商列表信息，给定一个空列表
        recommvendorRiskDto.setVendorRiskList(new ArrayList<>());
        //判断是否查到供应商信息
        if(CollectionUtils.isNotEmpty(companyInfoList)) {
            //查询到供应商信息时，将构造供应商风险实体类并添加到列表中
            companyInfoList.stream().forEach(companyInfo -> recommvendorRiskDto.getVendorRiskList().add(buildData(companyInfo)));
        }

        //响应中的数据返回供应商风险实体实例信息
        return new RiskResponse(recommvendorRiskDto);
    }

    /**
     * 构造供应商信息实体类信息实例
     * @param companyInfo
     * @return
     */
    private RecommvendorDto buildData(Record companyInfo) {
        RecommvendorDto recommvendorDto = new RecommvendorDto();
        //供应商ID
        recommvendorDto.setVendorId(companyInfo.get(CompanyInfo::getCompanyId));
        //供应商编码
        recommvendorDto.setVendorCode(companyInfo.get(CompanyInfo::getCompanyCode));
        //供应商名称
        recommvendorDto.setVendorName(companyInfo.get(CompanyInfo::getCompanyName));
        //时间受限
        recommvendorDto.setTimeLimitFlag(companyInfo.get(RiskCompanyInfo::getTimeLimitFlag));
        //重点关注
        recommvendorDto.setExtIsMainPoint(companyInfo.get(RiskCompanyInfo::getFocusFlag));
        //是否集团黑名单
        recommvendorDto.setGroupBlacklistFlag(companyInfo.get(CompanyInfo::getIsBacklist));
        //是否单位受限
        recommvendorDto.setPositionLimitFlag(companyInfo.get(RiskCompanyInfo::getPositionLimitFlag));
        //是否品类受限
        recommvendorDto.setCategoryLimitFlag(companyInfo.get(RiskCompanyInfo::getCategoryLimitFlag));
        //是否重点监督
        recommvendorDto.setKeySupervisionFlag(companyInfo.get(RiskCompanyInfo::getKeySupervisionFlag));
        //联系人是否重复: 报名联系人&供应商主数据
        recommvendorDto.setContackRepeatFlag(YesOrNo.NO.getValue());
        //联系人电话是否重复: 报名联系人&供应商主数据
        recommvendorDto.setTelRepeatFlag(YesOrNo.NO.getValue());
        //联系人邮箱是否重复: 报名联系人&供应商主数据
        recommvendorDto.setEmailRepeatFlag(YesOrNo.NO.getValue());
        // @ApiModelProperty("是否失信")
        recommvendorDto.setExtIsDishonesty(YesOrNo.NO.getValue());
        //    @ApiModelProperty("是否经营异常")
        recommvendorDto.setExtIsBizAnomaly(YesOrNo.NO.getValue());

        // @ApiModelProperty("法人是否重复")
        recommvendorDto.setLegalRepeatFlag(YesOrNo.NO.getValue());
        //    @ApiModelProperty("股东是否重复")
        recommvendorDto.setHolderRepeatFlag(YesOrNo.NO.getValue());
        //    @ApiModelProperty("主要人员是否重复")
        recommvendorDto.setMainPeopleRepeatFlag(YesOrNo.NO.getValue());

        return recommvendorDto;
    }
}
