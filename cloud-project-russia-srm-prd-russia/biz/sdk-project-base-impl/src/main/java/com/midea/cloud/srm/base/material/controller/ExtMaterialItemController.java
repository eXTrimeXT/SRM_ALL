package com.midea.cloud.srm.base.material.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.controller.BaseCheckController;
import com.midea.cloud.srm.base.material.service.ExtIMaterialItemService;
import com.midea.cloud.srm.base.material.service.IMaterialItemService;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.model.base.entity.ExtMaterialItemDto;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.enums.OrganizationTypeCode;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 *  修改日期: 2023/12/12 10:38
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/material/materialItem/ext")
@Api(value = "MaterialItemController", tags = "物料维护")
@Slf4j
public class ExtMaterialItemController extends BaseCheckController {
    @Autowired
    private ExtIMaterialItemService extIMaterialItemService;
    @Autowired
    private IMaterialItemService iMaterialItemService;
    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @PostMapping("/ceeaSaveOrUpdate")
    @ApiOperation(value = "物料保存", notes = "物料保存")
    public void ceeaSaveOrUpdate(@Valid @RequestBody ExtMaterialItemDto materialItem, BindingResult bindingResult) {
        checkParamBeforeHandle(log, bindingResult, materialItem);
        extIMaterialItemService.ceeaSaveOrUpdate(materialItem);
    }

    @PostMapping("/listDialogQuickActiveInvMaterial")
    @ApiOperation(value = "弹窗物料快查-只查询有效的库存组织下的", notes = "弹窗物料快查-只查询有效的库存组织下的")
    public PageInfo<MaterialItem> listDialogQuickActiveInvMaterial(@RequestBody MaterialItemQueryDto materialItemQueryDto) {
        materialItemQueryDto.setItemStatus("Y");

        List<Long> organizationIds = getActiveInv();
        materialItemQueryDto.setOrganizationIds(organizationIds);

        PageInfo<MaterialItem> materialItemPageInfo= iMaterialItemService.listPageByCondition(materialItemQueryDto);
        if (ObjectUtil.isNotEmpty(materialItemPageInfo.getList())) {
            List<String> materialItemCodes=materialItemPageInfo.getList().stream().map(MaterialItem::getMaterialCode).collect(Collectors.toList());
            List<ExternalMaterial> externalMaterials =qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("ExternalMaterial")
                    .in(ExternalMaterial::getMaterialCode, materialItemCodes), ExternalMaterial.class);
            Map<String,ExternalMaterial> externalMaterialMap=new HashMap<>();
            if (ObjectUtil.isNotEmpty(externalMaterials)) {
                externalMaterialMap=externalMaterials.stream().collect(Collectors.toMap(ExternalMaterial::getMaterialCode,l->l));
            }
            for (MaterialItem materialItem : materialItemPageInfo.getList()) {
                ExternalMaterial externalMaterial = externalMaterialMap.get(materialItem.getMaterialCode());
                materialItem.setDescription(externalMaterial == null ? "N" : "Y");
            }
        }
        return materialItemPageInfo;
    }

    private List<Long> getActiveInv() {
        // 有效的库存组织
        List<Organization> organizationList = organizationService.list(Organization::getEnabled, "Y", Organization::getOrganizationTypeCode, OrganizationTypeCode.INV.toString());

        List<Long> organizationIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(organizationList)) {
            organizationIds = organizationList.stream().map(Organization::getOrganizationId).collect(Collectors.toList());
        } else {
            organizationIds.add(-1L);
        }
        return organizationIds;
    }

}
