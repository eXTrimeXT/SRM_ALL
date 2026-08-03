package com.midea.cloud.srm.base.material.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.meicloud.meida.model.dto.Condition;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.base.material.mapper.ExtMaterialItemMapper;
import com.midea.cloud.srm.base.material.service.ExtIMaterialItemService;
import com.midea.cloud.srm.base.material.service.MaterialItemSaveCheckSPIService;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.base.purchase.service.ICategoryPurchaseTypeService;
import com.midea.cloud.srm.base.purchase.service.IPurchaseCategoryService;
import com.midea.cloud.srm.model.base.entity.ExtMaterialItem;
import com.midea.cloud.srm.model.base.entity.ExtMaterialItemDto;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.mideacloud.common.dto.response.BaseResponse;
import com.mideacloud.common.dto.response.PageResponse;
import com.mideacloud.material.api.MtPartApi;
import com.mideacloud.material.dto.request.MtPartPageQueryDTO;
import com.mideacloud.material.dto.request.MtpartUpdateRequestDTO;
import com.mideacloud.material.dto.response.MtPartResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/12 11:02
 *  修改内容:
 * </pre>
 */
@Service
@Slf4j
public class ExtIMaterialItemServiceImpl extends BaseServiceImpl<ExtMaterialItemMapper, ExtMaterialItem> implements ExtIMaterialItemService {
    @Autowired
    private MaterialItemSaveCheckSPIService materialItemSaveCheckSPIService;

    @Autowired
    private IOrganizationService iOrganizationService;


    @Autowired
    ICategoryPurchaseTypeService iCategoryPurchaseTypeService;

    @Resource
    private IPurchaseCategoryService iPurchaseCategoryService;

    @Resource
    private MtPartApi mtPartApi;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void ceeaSaveOrUpdate(ExtMaterialItemDto materialItem) {
        Assert.notNull(materialItem, "获取数据失败");
        //kuangzm 增加新增保存方法字段验证
        Assert.notNull(materialItem.getMaterialItem(), "物料信息不能未空");
        ExtMaterialItem mi = materialItem.getMaterialItem();
        MaterialItem materialItem1 = new MaterialItem();
        BeanUtil.copyProperties(mi, materialItem1);
        materialItemSaveCheckSPIService.checkIfExists(materialItem1);

        //kuangzm 增加新增保存方法
        if (null != mi.getMaterialId()) {
            this.updateById(mi);
        } else {
            this.save(mi);
        }
    }

    @Override
    public List<String> queryItemCodes(MaterialItemQueryDto materialItemQueryDto) {
        List<Condition> reqCondition = this.getConditionByMaterialItemQueryDto(materialItemQueryDto);
        return listItemCodeByRpc(reqCondition);
    }



    private List<String> listItemCodeByRpc(List<Condition> conditionList) {
        List<String> resultList = new ArrayList<>();
        long total = 9223372036854775807L;
        int pageSize = 1000;
        for(int pageNum = 1; ((long) (pageNum - 1) * pageSize) < total; ++pageNum) {
            MtPartPageQueryDTO mtPartPageQueryDTO = new MtPartPageQueryDTO();
            mtPartPageQueryDTO.setConditions(conditionList);
            mtPartPageQueryDTO.setPageNum(pageNum);
            mtPartPageQueryDTO.setPageSize(pageSize);
            long startTime = System.currentTimeMillis();
            BaseResponse<PageResponse<MtPartResponseDTO>> pageResponseBaseResponse = this.mtPartApi.search(mtPartPageQueryDTO);
            long endTime = System.currentTimeMillis();
            if (!"0".equals(pageResponseBaseResponse.getCode())) {
                log.error("调用物料中心获取物料信息失败。参数：{}，返回信息:{}， 时延:{}", mtPartPageQueryDTO, pageResponseBaseResponse, endTime - startTime);
                throw new BaseException(LocaleHandler.getLocaleMsg("调用物料中心获取物料信息失败!"));
            }

            log.info("调用物料中心获取物料信息成功，参数：{}，返回信息:{}， 时延:{}", mtPartPageQueryDTO, pageResponseBaseResponse, endTime - startTime);
            if (CollectionUtil.isNotEmpty(((PageResponse)pageResponseBaseResponse.getData()).getList())) {
                resultList.addAll(this.convert(((PageResponse)pageResponseBaseResponse.getData()).getList()));
            }

            total = ((PageResponse)pageResponseBaseResponse.getData()).getTotal();
        }

        return resultList;
    }


    private List<Condition> getConditionByMaterialItemQueryDto(MaterialItemQueryDto materialItemQueryDto) {
        List<Condition> reqConditions = new ArrayList();
        if (materialItemQueryDto.getMaterialIdNotEqual() != null) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.ID, "!=", materialItemQueryDto.getMaterialIdNotEqual(), (String)null, (List)null));
        }

        if (materialItemQueryDto.getMaterialId() != null) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.ID, "=", materialItemQueryDto.getMaterialId(), (String)null, (List)null));
        }

        if (StringUtil.notEmpty(materialItemQueryDto.getMaterialCode())) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.MATERIAL_CODE, "like", materialItemQueryDto.getMaterialCode(), (String)null, (List)null));
        }

        if (StringUtil.notEmpty(materialItemQueryDto.getMaterialName())) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.MATERIAL_NAME, "like", materialItemQueryDto.getMaterialName(), (String)null, (List)null));
        }


        if (StringUtils.isNotEmpty(materialItemQueryDto.getMaterialCodeOrName())) {
            List<Condition> conditions = Stream.of(new Condition(MtpartUpdateRequestDTO.MATERIAL_CODE, "like", materialItemQueryDto.getMaterialCodeOrName(), null, null), this.getOrCondition(), new Condition(MtpartUpdateRequestDTO.MATERIAL_NAME, "like", materialItemQueryDto.getMaterialCodeOrName(), null, null)).collect(Collectors.toList());
            Condition sub = new Condition();
            sub.setConditions(conditions);
            this.addConditionWithAnd(reqConditions, sub);
        }

        if (materialItemQueryDto.getCategoryId() != null) {
            this.addConditionWithAnd(reqConditions, new Condition("clsId", "=", materialItemQueryDto.getCategoryId(), (String)null, (List)null));
        }

        List<Organization> organizationList;
        if (StringUtil.notEmpty(materialItemQueryDto.getCategoryName())) {
            List<PurchaseCategory> purchaseCategoryList = this.iPurchaseCategoryService.listByNameLike(materialItemQueryDto.getCategoryName());
            if (CollectionUtil.isEmpty(purchaseCategoryList)) {
                return null;
            }
            List<Long> cateIds = purchaseCategoryList.stream().map(PurchaseCategory::getCategoryId).collect(Collectors.toList());
            this.addConditionWithAnd(reqConditions, new Condition("clsId", "in", cateIds, null, null));
        }

        if (materialItemQueryDto.getItemStatus() != null) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.STOCK_HAS_ENABLE, "=", materialItemQueryDto.getItemStatus(), (String)null, (List)null));
        }

        Set<String> organizationCodesFinal = new HashSet<>(16);
        if (materialItemQueryDto.getOrganizationId() != null) {
            Organization organization = this.iOrganizationService.get(materialItemQueryDto.getOrganizationId());
            if (organization == null) {
                return null;
            }

            organizationCodesFinal.add(organization.getOrganizationCode());
        }

        if (materialItemQueryDto.getOrganizationId() == null && materialItemQueryDto.getOrgId() != null) {
            Set<String> organizationCodes = Sets.newHashSet(this.getSubOrgListCode(materialItemQueryDto.getOrgId()));
            if (CollectionUtil.isEmpty(organizationCodes)) {
                return null;
            }

            organizationCodesFinal = CollectionUtil.isEmpty(organizationCodesFinal) ? organizationCodes : Sets.intersection(organizationCodesFinal, organizationCodes);
            if (CollectionUtil.isEmpty(organizationCodesFinal)) {
                return null;
            }
        }

        if (CollectionUtil.isNotEmpty(materialItemQueryDto.getOrganizationIds())) {
            organizationList = this.iOrganizationService.listByIds(materialItemQueryDto.getOrganizationIds());
            if (CollectionUtil.isEmpty(organizationList)) {
                return null;
            }

            Set<String> organizationCodes = organizationList.stream().map(Organization::getOrganizationCode).collect(Collectors.toSet());
            organizationCodesFinal = CollectionUtil.isEmpty(organizationCodesFinal) ? organizationCodes : Sets.intersection(organizationCodesFinal, organizationCodes);
            if (CollectionUtil.isEmpty(organizationCodesFinal)) {
                return null;
            }
        }

        if (CollectionUtil.isNotEmpty(organizationCodesFinal)) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.INV_ORG_CODE, "in", organizationCodesFinal, (String)null, (List)null));
        }

        if (materialItemQueryDto.getLastUpdateDateBegin() != null) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.LASTUPDATEDATE, ">=", materialItemQueryDto.getLastUpdateDateBegin().format(this.dateTimeFormatter), (String)null, (List)null));
        }

        if (materialItemQueryDto.getLastUpdateDateEnd() != null) {
            this.addConditionWithAnd(reqConditions, new Condition(MtpartUpdateRequestDTO.LASTUPDATEDATE, "<=", materialItemQueryDto.getLastUpdateDateEnd().format(this.dateTimeFormatter), (String)null, (List)null));
        }

        if (CollectionUtils.isNotEmpty(materialItemQueryDto.getExtendConditions())) {
            materialItemQueryDto.getExtendConditions().forEach((ec) -> {
                this.addConditionWithAnd(reqConditions, new Condition(ec.getField(), ec.getOperator(), ec.getValue(), (String)null, (List)null));
            });
        }

        return reqConditions;
    }

    private List<String> getSubOrgListCode(Long orgid) {
        List<Organization> organizationList = this.iOrganizationService.getSubInvOrganizationsByOrgId(orgid);
        return (CollectionUtil.isEmpty(organizationList) ? Collections.emptyList() : organizationList.stream().map(Organization::getOrganizationCode).collect(Collectors.toList()));
    }

    private void addConditionWithAnd(List<Condition> conditionList, Condition condition2Add) {
        if (CollectionUtil.isNotEmpty(conditionList)) {
            conditionList.add(this.getAndCondition());
        }

        conditionList.add(condition2Add);
    }

    private Condition getAndCondition() {
        return new Condition((String)null, (String)null, (Object)null, " and ", (List)null);
    }

    private List<String> convert(List<MtPartResponseDTO> mtPartResponseDTOList){
        if(CollUtil.isEmpty(mtPartResponseDTOList)){
            return Collections.emptyList();
        } else {
            return mtPartResponseDTOList.stream().map(MtPartResponseDTO::getMaterialCode).collect(Collectors.toList());
        }
    }

    private Condition getOrCondition() {
        return new Condition((String)null, (String)null, (Object)null, " or ", (List)null);
    }

}
