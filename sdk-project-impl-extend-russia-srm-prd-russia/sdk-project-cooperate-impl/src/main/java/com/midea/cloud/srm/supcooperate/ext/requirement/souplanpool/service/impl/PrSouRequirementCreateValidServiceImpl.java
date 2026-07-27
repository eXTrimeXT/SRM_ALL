package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.InviteTendersExtClient;
import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementCreateValidService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class PrSouRequirementCreateValidServiceImpl implements PrSouRequirementCreateValidService {
    @Autowired
    private QlService qlService;

    @Autowired
    private InviteTendersExtClient inviteTendersExtClient;


    @Override
    public void requirementCreateValid(List<Long> requirementHeadIdList, String souType) {
        SouBidRequirementPoolDto query = new SouBidRequirementPoolDto();
        query.setApplicantNoList(new ArrayList<>());
        QlQueryWrapper wrapper = QlWrappers.query("PrSouRequirementPoolForBuyer").in("requirementHeadId", requirementHeadIdList);
        List<Record> requirements = qlService.queryByWrapper(wrapper, Record.class);
        query.setApplicantNoList(requirements.stream().map(r->(String)r.get("requirementHeadNum")).distinct().collect(Collectors.toList()));

        SouBidRequirementPoolDto resPool = inviteTendersExtClient.getRequirementPoolInfo(query);

        //拟定标书
        if(SouTypeEnum.bid.name().equals(souType)) {
            check(resPool, query, 0, 1);
            return;
        }

        //供应商推荐
        if(SouTypeEnum.recomm.name().equals(souType)) {
            check(resPool, query, 0, 2);
            return;
        }

        //寻源需求
        String req = "req";
        if(req.equals(souType)) {
            check(resPool, query, 1, 2);
            return;
        }
    }

    protected void check(SouBidRequirementPoolDto resPool, SouBidRequirementPoolDto query, Integer... types) {
        Arrays.stream(types).forEach(type -> {
            check(type, resPool, query);
        });
    }

    protected void check(Integer type, SouBidRequirementPoolDto resPool, SouBidRequirementPoolDto query) {
        String checkStr = null;
        switch (type) {
            case 0:
                checkStr = checkSouRequirement(resPool, query.getApplicantNoList());
                break;
            case 1:
                checkStr = checkRecommvendor(resPool, query.getApplicantNoList());
                break;
            case 2:
                checkStr = checkBid(resPool, query.getApplicantNoList());
                break;
            default:;
        }
        if(StringUtils.isNotBlank(checkStr)) {
            throw new BaseException(checkStr);
        }

    }

    protected String checkBid(SouBidRequirementPoolDto requirementPoolDto, List<String> applicantNoList) {
        StringBuffer error = new StringBuffer();
        if(CollectionUtils.isNotEmpty(requirementPoolDto.getProjectList())) {
            Map<Long, List<ExtSouDemand>> demandMap = requirementPoolDto.getSouDemandList().stream().collect(Collectors.groupingBy(r->r.getProjectId()));
            List<String> soureqList = demandMap.getOrDefault(requirementPoolDto.getProjectList().get(0).getProjectId(), new ArrayList<>()).stream().map(d->d.getApplicantNo()).distinct().collect(Collectors.toList());

            List<String> baseList = applicantNoList.stream().distinct().collect(Collectors.toList());
            List<String> checkList = soureqList.stream().distinct().collect(Collectors.toList());

            //取交集
            baseList.retainAll(checkList);

            //勾选行 减去 交集 多出属于异常，多勾选
            List<String> selectList = applicantNoList.stream().distinct().collect(Collectors.toList());
            selectList.removeAll(baseList);

            //需求集 剪去 交集 多出属于异常，未勾选
            checkList.removeAll(baseList);

            if(CollectionUtils.isNotEmpty(selectList)) {
                error.append(StringUtils.join("以下选择的申请单行未存在招标书单-", requirementPoolDto.getRecommvendorProjectList().get(0).getSouNo(), "中：", selectList.stream().collect(Collectors.joining("、"))));
            }
            if(CollectionUtils.isNotEmpty(checkList)) {
                error.append(StringUtils.join("存在以下申请单号未进行勾选且存在招标书单-", requirementPoolDto.getRecommvendorProjectList().get(0).getSouNo(), "：", checkList.stream().collect(Collectors.joining("、"))));
            }

        }
        return error.toString();
    }

    protected String checkRecommvendor(SouBidRequirementPoolDto requirementPoolDto, List<String> applicantNoList) {
        StringBuffer error = new StringBuffer();
        if(CollectionUtils.isNotEmpty(requirementPoolDto.getRecommvendorProjectList())) {
            Map<Long, List<ExtSouDemand>> demandMap = requirementPoolDto.getSouDemandList().stream().collect(Collectors.groupingBy(r->r.getProjectId()));
            List<String> soureqList = demandMap.getOrDefault(requirementPoolDto.getRecommvendorProjectList().get(0).getProjectId(), new ArrayList<>()).stream().map(d->d.getApplicantNo()).distinct().collect(Collectors.toList());

            List<String> baseList = applicantNoList.stream().distinct().collect(Collectors.toList());
            List<String> checkList = soureqList.stream().distinct().collect(Collectors.toList());

            //取交集
            baseList.retainAll(checkList);

            //勾选行 减去 交集 多出属于异常，多勾选
            List<String> selectList = applicantNoList.stream().distinct().collect(Collectors.toList());
            selectList.removeAll(baseList);

            //需求集 剪去 交集 多出属于异常，未勾选
            checkList.removeAll(baseList);

            if(CollectionUtils.isNotEmpty(selectList)) {
                error.append(StringUtils.join("以下选择的申请单行未存在供应商推荐单-", requirementPoolDto.getRecommvendorProjectList().get(0).getSouNo(), "中：", selectList.stream().collect(Collectors.joining("、"))));
            }
            if(CollectionUtils.isNotEmpty(checkList)) {
                error.append(StringUtils.join("存在以下申请单号未进行勾选且存在供应商推荐单-", requirementPoolDto.getRecommvendorProjectList().get(0).getSouNo(), "：", checkList.stream().collect(Collectors.joining("、"))));
            }

        }
        return error.toString();
    }

    protected String checkSouRequirement(SouBidRequirementPoolDto requirementPoolDto, List<String> applicantNoList) {
        StringBuffer error = new StringBuffer();
        if(CollectionUtils.isNotEmpty(requirementPoolDto.getSouReqHeadList())) {
            List<String> soureqList = Arrays.asList(requirementPoolDto.getSouReqHeadList().get(0).getRequirementHeadNoList().split(",")).stream().distinct().collect(Collectors.toList());

            List<String> baseList = applicantNoList.stream().distinct().collect(Collectors.toList());
            List<String> checkList = soureqList.stream().distinct().collect(Collectors.toList());

            //取交集
            baseList.retainAll(checkList);

            //勾选行 减去 交集 多出属于异常，多勾选
            List<String> selectList = applicantNoList.stream().distinct().collect(Collectors.toList());
            selectList.removeAll(baseList);

            //需求集 剪去 交集 多出属于异常，未勾选
            checkList.removeAll(baseList);

            if(CollectionUtils.isNotEmpty(selectList)) {
                error.append(StringUtils.join("以下选择的申请单行未存在寻源需求单-", requirementPoolDto.getSouReqHeadList().get(0).getReqHeadNo(), "中：", selectList.stream().collect(Collectors.joining("、"))));
            }
            if(CollectionUtils.isNotEmpty(checkList)) {
                error.append(StringUtils.join("存在以下申请单号未进行勾选且存在于寻源需求单-", requirementPoolDto.getSouReqHeadList().get(0).getReqHeadNo(), "：", checkList.stream().collect(Collectors.joining("、"))));
            }

        }
        return error.toString();
    }
}
