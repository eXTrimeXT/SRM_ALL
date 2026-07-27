package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.enums.ExtSouFileConfigTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtThechEvaHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouScoreRuleService scoreRuleService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("当前是否可以开始技术评标")
    public void judgeEvaTechAuth(long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "寻源单据信息不存在！");

        ApiProjectStatusRangeVo<SouBiddingProStatusEnum>  vo = ApiProjectStatusFactory.projectStatus(souProject.getProjectStatus());
        if(!vo.getNextStatus().contains(SouBiddingProStatusEnum.TECH_BID_EVA)) {
            throw new BaseException("当前状态不允许开始技术评标！");
        }
        //隐藏关键信息时，需要有脱敏文件
        if(YesOrNo.YES.getValue().equals(souProject.getExtHideKeyInfo())) {
           Integer countSecret = Math.toIntExact(orderFileService.lambdaQuery().eq(ExtSouOrderFile::getProjectId, projectId)
                   .eq(ExtSouOrderFile::getFileType, ExtSouFileConfigTypeEnum.TECH_BID_SECRET.getCode())
                   .isNotNull(ExtSouOrderFile::getOrderDocId).count());
           if(Integer.compare(countSecret, 0) <= 0) {
               throw new BaseException("未上传方案脱敏文件，不允许进行该操作！");
           }
        }
    }



    @ApiOperation("校验数据和转换数据")
    public ExtTechEvaEditPO formatvalidAndConvertPo(Long projectId, String souType) {
        //1.校验数据
        this.formatvalid(projectId, souType);
        //2.转换数据
        return this.convert(projectId, souType);
    }

    @ApiOperation("校验数据")
    protected void formatvalid(Long projectId, String souType) {

    }

    @ApiOperation("转换数据")
    protected ExtTechEvaEditPO convert(Long projectId, String souType) {
        ExtTechEvaEditPO po = new ExtTechEvaEditPO();
        po.setTechScoreHeadList(doConvertTechScoreHead(projectId));
        return po;
    }

    protected List<ExtSouTechScoreHead> doConvertTechScoreHead(Long projectId) {
        //校验是否有维护评分规则，无评分规则时不生成评审单据
        LambdaQueryWrapper<ExtScoreRule> ruleQuery = new LambdaQueryWrapper<>();
        ruleQuery.eq(ExtScoreRule::getProjectId, projectId);
        Integer count = Math.toIntExact(scoreRuleService.count(ruleQuery));
        if(Integer.compare(count, 0) <= 0) {
            return new ArrayList<>();
        }

        //评分小组
        List<ExtSouGroup> groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId).eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue()).list();

        //查询技术投标头表
        LambdaQueryWrapper<ExtSouTechScoreHead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouTechScoreHead::getProjectId, projectId);

        List<ExtSouTechScoreHead> techScoreHeadList = techScoreHeadService.list(queryWrapper);

        Map<String, ExtSouTechScoreHead> techScoreHeadMap = techScoreHeadList.stream().collect(Collectors.toMap(t -> StringUtils.joinWith("_", t.getVendorId(), t.getGroupId()), Function.identity(), (k1, k2)->k2));

        List<ExtSouTechScoreHead> saveList = new ArrayList<>();
        groupList.stream().filter(g -> SouScoreDimensionCodeEnum.SOU_TECH.getCode().equals(g.getScoreAuth())).forEach(g -> {
            String key = StringUtils.joinWith("_", -1L, g.getGroupId());
            ExtSouTechScoreHead techScoreHead = techScoreHeadMap.get(key);
            if(Objects.isNull(techScoreHead)) {
                techScoreHead = new ExtSouTechScoreHead();
                techScoreHead.setTechScoreHeadId(IdGenrator.generate());
                techScoreHead.setProjectId(projectId);
                techScoreHead.setVendorId(-1L);
                techScoreHead.setGroupId(g.getGroupId());
                techScoreHead.setIsProxy(Enable.N.name());
            }
            techScoreHead.setOrderId(-1L);
            techScoreHead.setScoreStatus(SouTechScoreStatusEnum.UNFINISHED.name());
            saveList.add(techScoreHead);
        });
        return saveList;
    }

    @ApiOperation("技术评标后置处理")
    public void doHandlerAfterEvaTech(long projectId, String souType, ExtTechEvaEditPO po) {
        //更新实际技术评标时间
//        planService.applyAtualPoint(projectId, new Date(), ExtSouPlan::getTechEvaluationTime);
    }
}
