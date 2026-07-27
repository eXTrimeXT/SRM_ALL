package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm 推荐供应商报名联系人
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentRecomm extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentRecomm start...");
        //推荐供应商报名联系人缓存Map，key-value，key为供应商ID，value为供应商推荐单关联的供应商实体类对象
        Map<Long, RecommvendorDto> vendorRecommMap = new HashMap<>(50);
        //推荐供应商单据实体类列表缓存对象
        List<RecommvendorProjectDto> projectDtoList = null;
        //前端传合并申请单号，多个申请单号用逗号分割
        if(StringUtils.isNotBlank(riskRequest.getApplicantNo())) {
            //将合并申请单号按逗号分割成列表，列表元素为单个申请单号
            List<String> applicantNoList = Arrays.asList(riskRequest.getApplicantNo().split(SrmConstant.SIG_1));
            //查询申请单号关联的推荐单
            List<ExtSouDemand> souDemandList = RiskComponent.getInstance().getQlService().queryByWrapper(QlWrappers.query(RecommType.RecommvendorDemand.name()).in(ExtSouDemand::getApplicantNo, applicantNoList), ExtSouDemand.class);
            //判断是否存在申请单号关联表数据
            if(CollectionUtils.isNotEmpty(souDemandList)) {
                //存在关联申请单号，取项目ID查询项目信息，存在推荐供应商单据信息的关联申请单号才是有意义的关联申请单号
                projectDtoList = RiskComponent.getInstance().getQlService().queryByWrapper(QlWrappers.query(RecommType.RecommvendorProject.name()).in(RecommvendorProjectDto::getProjectId, souDemandList.stream().map(h -> h.getProjectId()).collect(Collectors.toList())).eq(RecommvendorProjectDto::getSouType, SouTypeEnum.recomm.name()), RecommvendorProjectDto.class);
            }
        }
        //判断是否根据申请单号关联查出推荐供应商单据列表
        if(CollectionUtils.isNotEmpty(projectDtoList)) {
            //存在推荐供应商单据信息时，根据推荐供应商单据ID查询所有的供应商报名信息
            List<RecommvendorDto> recommvendorDtoList = RiskComponent.getInstance().getQlService().queryByWrapper(QlWrappers.query(RecommType.Recommvendor.name()).in(RecommvendorDto::getProjectId, projectDtoList.stream().map(r -> r.getProjectId()).collect(Collectors.toList())), RecommvendorDto.class);
            //将推荐供应商单据的供应商报名信息放到缓存Map对象，最后整合数据时用，key-value，key为供应商ID，value为供应商推荐单关联的供应商实体类对象
            vendorRecommMap = recommvendorDtoList.stream().collect(Collectors.toMap(c->c.getVendorId(), Function.identity(), (k1, k2)->k2));
        }
        log.info("riskService VendorRiskPreTreatmentRecomm end...");
        return new RiskResponse(vendorRecommMap);
    }
}
