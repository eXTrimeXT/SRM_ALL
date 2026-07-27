package com.midea.cloud.srm.sou.recommvendor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.sou.recommvendor.service.ExtSouRecommVendorService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouDemandMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouRecommVendorMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiExtSouInitJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.recomm.ApiExtSouRecommVendorEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.recomm.ExtSouRecommVendorEditPO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Api("寻源核心-供应商接口实现类")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExtSouRecommVendorServiceImpl implements ExtSouRecommVendorService {
    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouRecommVendorService souRecommVendorService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Autowired
    private IExtSouFileService souFileService;

    @Resource
    private ExtSouProjectMapper projectMapper;

    @Resource
    private ExtSouRecommVendorMapper recommVendorMapper;
    @Autowired
    private ExtSouDemandMapper demandMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long editRecommVendor(ApiExtSouRecommVendorInfoDTO param, boolean isCopy, String souType) {
        // 校验操作条件/权限
        if (param.getProject().getProjectId() != null) {
            SouActiveBeanUtils.getActiveBean(souType, ApiExtSouInitJudgeHandler.class).judgeEditProjectAuth(param.getProject().getProjectId(), souType);
        }

        // 入参校验+转换处理
        ExtSouRecommVendorEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiExtSouRecommVendorEditHandler.class).formatValidateAndConvert(param, isCopy, souType);

        //保存项目信息
        projectService.saveOrUpdate(po.getProject());

        //保存标的物信息
        souRecommVendorService.saveOrUpdate(po.getSouRecommendedVendor());

        //保存推荐供应商
        vendorService.saveOrUpdateBatch(po.getSouVendor());

        //保存附件
        souFileService.saveOrUpdateBatch(po.getVendorFileList());

        //保存合并申请单号
        demandMapper.insertBatch(po.getSouDemands());

        return po.getProject().getProjectId();
    }

    @Override
    public ApiExtSouRecommVendorInfoDTO getRecommVendorInfo(Long projectId) {
        AssertUtils.notNull(projectId, "请求参数不允许为空");
        ApiExtSouRecommVendorInfoDTO souRecommVendorInfoDTO = new ApiExtSouRecommVendorInfoDTO();

        //查询头信息
        ExtSouProjectDto extSouProjectDto = listExtSouProjectDto(projectId);
        if (Objects.isNull(extSouProjectDto)) {
            return null;
        }
        souRecommVendorInfoDTO.setProject(extSouProjectDto);

        //查询标的物信息
        souRecommVendorInfoDTO.setSouRecommendedVendor(listExtSouRecommVendorDto(projectId));

        //查询推荐供应商
        souRecommVendorInfoDTO.setSouVendor(listExtSouVendor(projectId));

        //查询供应商文件
        listSouFile(souRecommVendorInfoDTO, projectId);

        return souRecommVendorInfoDTO;
    }

    /**
     * 根据供应供应商单号查询已审批的推荐供应商
     *
     * @param recommVendorNo, 推荐单，存在多个情况
     * @return
     */
    @Override
    public ApiExtSouRecommVendorInfoDTO getRecommVendorInfoByNo(String recommVendorNo) {
        AssertUtils.notNull(recommVendorNo, "请求参数不允许为空");
        ApiExtSouRecommVendorInfoDTO souRecommVendorInfoDTO = new ApiExtSouRecommVendorInfoDTO();

        //查询头信息
        List<ExtSouProjectDto> extSouProjectDto = listExtSouProjectByNo(recommVendorNo);
        if (CollectionUtils.isEmpty(extSouProjectDto)) {
            return null;
        }
        souRecommVendorInfoDTO.setProject(extSouProjectDto.get(0));
        //查询推荐供应商
        List<ExtSouVendor> souVendor = listExtSouVendorBatch(extSouProjectDto.stream().map(e->e.getProjectId()).distinct().collect(Collectors.toList()));

        if (CollectionUtils.isNotEmpty(souVendor)) {
            souVendor = souVendor.stream().peek(v -> {
                //清空ID
                v.setSouVendorId(null);
                //清空关联ID
                v.setProjectId(null);
            }).collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouVendor::getVendorId))), ArrayList::new));
        }

        souRecommVendorInfoDTO.setSouVendor(souVendor);
        return souRecommVendorInfoDTO;
    }

    @Override
    public List<ExtSouVendor> getRecommVendorInfoByProjectId(Long projectId) {
        //查询推荐供应商
        return listExtSouVendor(projectId);
    }

    void queryRecommvendorWithiAdd(List<Long> projectIdList, List<ExtSouVendor> souVendor) {
        LambdaQueryWrapper<ExtSouRecommendedVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouRecommendedVendor::getOriginalProjectId, projectIdList);
        List<ExtSouRecommendedVendor> recommendedVendorList = recommVendorMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(recommendedVendorList)) {
            return;
        }
        LambdaQueryWrapper<ExtSouVendor> vendorQuery = new LambdaQueryWrapper<>();
        vendorQuery.in(ExtSouVendor::getProjectId, recommendedVendorList.stream().map(ExtSouRecommendedVendor::getProjectId).distinct().collect(Collectors.toList()));
        List<ExtSouVendor> vendorList = vendorService.list(vendorQuery);
        if (CollectionUtils.isNotEmpty(vendorList)) {
            souVendor.addAll(vendorList);
        }
        this.queryRecommvendorWithiAdd(recommendedVendorList.stream().map(ExtSouRecommendedVendor::getProjectId).distinct().collect(Collectors.toList()), vendorList);
    }

    private void listSouFile(ApiExtSouRecommVendorInfoDTO projectInfoDTO, Long projectId) {
        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtSouFile::getSouFileId);
        List<ExtSouFile> bidAttachmentList = souFileService.list(queryWrapper);
        projectInfoDTO.setVendorFileList(bidAttachmentList);
    }

    private List<ExtSouVendor> listExtSouVendor(Long projectId) {
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouVendor::getProjectId, projectId);
        List<ExtSouVendor> groupList = vendorService.list(queryWrapper);
        return groupList;
    }

    private List<ExtSouVendor> listExtSouVendorBatch(List<Long> projectIdList) {
        LambdaQueryWrapper<ExtSouVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouVendor::getProjectId, projectIdList);
        List<ExtSouVendor> groupList = vendorService.list(queryWrapper);
        return groupList;
    }

    private ExtSouProjectDto listExtSouProjectDto(Long projectId) {
        ExtSouProjectDto extSouProjectDto = new ExtSouProjectDto();
        ExtSouProject extSouProject = projectMapper.selectById(projectId);
        if (Objects.isNull(extSouProject)) {
            return null;
        }
        BeanCopyUtil.copyProperties(extSouProjectDto, extSouProject);
        //合并需求
        extSouProjectDto.setApplicantNo(getApplicantNo(projectId));
        return extSouProjectDto;
    }

    private List<ExtSouProjectDto> listExtSouProjectByNo(String recommVendorNo) {
        LambdaQueryWrapper<ExtSouProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtSouProject::getSouNo, Arrays.asList(recommVendorNo.split(",")));
        queryWrapper.eq(ExtSouProject::getProjectStatus, SouRecommvendorStatusEnum.APPROVED.getCode());
        List<ExtSouProject> extSouProject = projectMapper.selectList(queryWrapper);
        List<ExtSouProjectDto> extSouProjectDto = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(extSouProject)) {
            extSouProject.stream().forEach(p -> {
                ExtSouProjectDto dto = new ExtSouProjectDto();
                BeanCopyUtil.copyProperties(dto, p);
                extSouProjectDto.add(dto);
            });
        }
        return extSouProjectDto;
    }


    private ExtSouRecommendedVendor listExtSouRecommVendorDto(Long projectId) {

        LambdaQueryWrapper<ExtSouRecommendedVendor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouRecommendedVendor::getProjectId, projectId);
        List<ExtSouRecommendedVendor> recommVendorList = souRecommVendorService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(recommVendorList)) {
            return recommVendorList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String getApplicantNo(Long projectId) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        queryWrapper.select(ExtSouDemand::getApplicantNo);
        queryWrapper.orderByAsc(ExtSouDemand::getDemandId);
        List<ExtSouDemand> demandList = demandMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(demandList)) {
            return demandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.joining(";"));
        }
        return null;
    }

    @Override
    public Long getApplicantByAppNo(String appNo) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getApplicantNo, appNo);
        queryWrapper.eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO);
        queryWrapper.select(ExtSouDemand::getProjectId);
        queryWrapper.orderByAsc(ExtSouDemand::getDemandId);
        List<ExtSouDemand> demandList = demandMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(demandList)) {
            return demandList.get(0).getProjectId();
        }
        return null;
    }

}
