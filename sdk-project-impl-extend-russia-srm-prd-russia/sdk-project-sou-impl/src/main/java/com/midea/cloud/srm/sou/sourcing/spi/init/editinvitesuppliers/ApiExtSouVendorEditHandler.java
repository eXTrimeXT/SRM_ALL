package com.midea.cloud.srm.sou.sourcing.spi.init.editinvitesuppliers;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouVendorDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveOperateService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.bid.process.service.BidSouProcessQueryWebService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ExtSouItemEditPO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouVendorEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ISouApproveOperateService approveOperateService;

    @Autowired
    private IExtSouProcessConfigService processConfigService;

    @Autowired
    private BidSouProcessQueryWebService bidSouProcessQueryService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouScoreRuleService ruleService;

    private static final String CACH_PROJECT = "CACH_PROJECT";

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public ExtSouVendorEditPO formatValidateAndConvert(ApiExtSouVendorDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    private ExtSouProjectDto toExtSouProjectDto(ExtSouProject project) {
        try {
            ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
            BeanCopyUtil.copyProperties(extSouProjectDto, project, true);
            return extSouProjectDto;
        } catch (Exception e) {
            log.error("toExtSouProjectDto Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 入参格式化及校验
     * @param param 参数
     * @param souType 参数
     */
    protected void formatAndValidate(ApiExtSouVendorDto param, String souType) {
        if(!param.isTempSave()) {
            ExtSouProject project = projectService.getById(param.getProjectId());
            param.getExtensions().put(CACH_PROJECT, project);

            ExtSouProjectDto extSouProjectDto = toExtSouProjectDto(project);
            extSouProjectDto.setProcessConfig(new ApiExtSouProcessConfigVo());

            //提交校验
            List<ApiSouProcessNodeVO> nodeVOList = bidSouProcessQueryService.listProcessNodes(param.getProjectId());
            extSouProjectDto.setProcessNodeList(nodeVOList);

            processConfigService.fixNpmProcessAndNode(extSouProjectDto);

            nodeVOList = nodeVOList.stream().filter(n -> Enable.Y.equals(n.getEnabled()) && Arrays.asList(SouProcessNodeEnum.requireInfo.name(), SouProcessNodeEnum.scoreRule.name()).contains(n.getProcessNode())).collect(Collectors.toList());
            List<String> errorList = new ArrayList<>(8);
            nodeVOList.stream().forEach(node -> {
                if(SouProcessNodeEnum.requireInfo.name().equals(node.getProcessNode())) {
                    //报价信息要有数据
                    Long countItem = itemService.lambdaQuery().eq(ExtSouItem::getProjectId, param.getProjectId()).count();
                    if(SrmConstant.LONG_ZERO.compareTo(countItem) >= 0) {
                        errorList.add("缺失报价信息");
                    }
                } else if(SouProcessNodeEnum.scoreRule.name().equals(node.getProcessNode())) {
                    //报价信息要有数据
                    Long countRule = ruleService.lambdaQuery().eq(ExtScoreRule::getProjectId, param.getProjectId()).count();
                    if(SrmConstant.LONG_ZERO.compareTo(countRule) >= 0) {
                        errorList.add("缺失评分规则");
                    }
                }
            });
            if(CollectionUtils.isNotEmpty(errorList)) {
                throw new BaseException(StringUtils.joinWith("", errorList.stream().collect(Collectors.joining(SrmConstant.SIG_3)), ": 请先维护数据再进行提交！"));
            }
        }
    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtSouVendorEditPO convert(ApiExtSouVendorDto param, String souType) {
        ExtSouVendorEditPO po = new ExtSouVendorEditPO();
        po.setVendorList(this.doConvertVendor(param, souType));
        return po;
    }

    /**
     * 转换得到寻源信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected List<ExtSouVendor> doConvertVendor(ApiExtSouVendorDto param, String souType) {

        ExtSouProject project = projectService.getById(param.getProjectId());

        List<ExtSouVendor> vendorList = param.getVendorList();
        if(Objects.isNull(vendorList)) {
            vendorList = new ArrayList<>();
        }

        AtomicInteger index = new AtomicInteger(1);
        //查询最大的排序
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, param.getProjectId());
        queryWrapper.orderByDesc(ExtSouVendor::getSortIndex);
        PageUtil.startPage(1, 1);
        List<ExtSouVendor> list = vendorService.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(list)) {
            index.set(list.get(0).getSortIndex());
        }

        vendorList.stream().forEach(item -> {
            if(Objects.isNull(item.getSouVendorId())) {
                item.setSouVendorId(IdGenrator.generate());
            }
            item.setProjectId(param.getProjectId());
            item.setJoinRound(project.getCurrentRound());
            item.setSortIndex(index.getAndAdd(1));
        });
        return vendorList;
    }

    @ApiOperation("报价信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiExtSouVendorDto param, String souType) {
    }

    @ApiOperation("报价信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiExtSouVendorDto param, String souType, ExtSouVendorEditPO po) {
        //提交审批
        if(!param.isTempSave()) {
            approveOperateService.operate(param.getProjectId(), SouApprovalStatusEnum.SUBMITTED, "", "SOU_PROJECT_APPLY");
        }
    }


}
