package com.midea.cloud.srm.biz.pj.base.organization.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.base.organization.service.ISccPjOrganizationRoleService;
import com.midea.cloud.srm.biz.pj.base.organization.service.ISccPjOrganizationRoleUserService;
import com.midea.cloud.srm.biz.pj.hrorganization.mapper.SccPjOrganizationMapper;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.base.organization.dto.BpmSccPjOrganizationRoleDto;
import com.midea.cloud.srm.model.pj.base.organization.dto.SccPjOrganizationRoleDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRole;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author huangbf3
 * 组织角色控制层
 */
@RestController
@RequestMapping("/organizationRole")
public class SccPjOrganizationRoleController {

    @Resource
    private ISccPjOrganizationRoleService organizationRoleService;

    @Resource
    private ISccPjOrganizationRoleUserService organizationRoleUserService;

    @Resource
    private SccPjOrganizationMapper sccPjOrganizationMapper;

    @ApiOperation(value = "分页查询")
    @PostMapping("/listPage")
    public PageInfo<SccPjOrganizationRole> listPage(@RequestBody SccPjOrganizationRole sccPjOrganizationRole) {
        PageUtil.startPage(sccPjOrganizationRole.getPageNum(),sccPjOrganizationRole.getPageSize());
        List<SccPjOrganizationRole> list = organizationRoleService.lambdaQuery()
                .like(StringUtils.isNotBlank(sccPjOrganizationRole.getGroupName()),SccPjOrganizationRole::getGroupName,sccPjOrganizationRole.getGroupName())
                .like(StringUtils.isNotBlank(sccPjOrganizationRole.getParentRoleCode()),SccPjOrganizationRole::getParentRoleCode,sccPjOrganizationRole.getParentRoleCode())
                .like(StringUtils.isNotBlank(sccPjOrganizationRole.getParentRoleName()),SccPjOrganizationRole::getParentRoleName,sccPjOrganizationRole.getParentRoleName())
                .like(StringUtils.isNotBlank(sccPjOrganizationRole.getRoleCode()),SccPjOrganizationRole::getRoleCode,sccPjOrganizationRole.getRoleCode())
                .like(StringUtils.isNotBlank(sccPjOrganizationRole.getRoleName()),SccPjOrganizationRole::getRoleName,sccPjOrganizationRole.getRoleName())
                .eq(StringUtils.isNotBlank(sccPjOrganizationRole.getUseFlag()),SccPjOrganizationRole::getUseFlag,sccPjOrganizationRole.getUseFlag())
                .list();
        return new PageInfo<>(list);
    }

    @ApiOperation(value = "获取明细")
    @GetMapping("/getDetail")
    public SccPjOrganizationRoleDto getDetail(@RequestParam("rowId") Long rowId) {
        SccPjOrganizationRole organizationRole = organizationRoleService.getById(rowId);

        List<SccPjOrganizationRoleUser> organizationRoleUsers = organizationRoleUserService.lambdaQuery().eq(SccPjOrganizationRoleUser::getOrganizationRoleId,rowId).list();

        return new SccPjOrganizationRoleDto().setOrganizationRole(organizationRole).setOrganizationRoleUsers(organizationRoleUsers);
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "批量删除")
    @PostMapping("/deleteBatch")
    public void deleteBatch(@RequestBody List<Long> rowIds) {
        Assert.isTrue(rowIds!=null&&rowIds.size()>0,"ID列表不能为空");

        organizationRoleUserService.lambdaUpdate().in(SccPjOrganizationRoleUser::getOrganizationRoleId,rowIds).remove();
        organizationRoleService.lambdaUpdate().in(SccPjOrganizationRole::getRowId,rowIds).remove();
    }

    @ApiOperation(value = "批量启用")
    @PostMapping("/useBatch")
    @Transactional(rollbackFor = Exception.class)
    public void useBatch(@RequestBody List<Long> rowIds) {
        Assert.isTrue(rowIds!=null&&rowIds.size()>0,"ID列表不能为空");

        organizationRoleService.lambdaUpdate().set(SccPjOrganizationRole::getUseFlag, YesOrNo.Y.name()).in(SccPjOrganizationRole::getRowId,rowIds).update();
    }

    @ApiOperation(value = "批量禁用")
    @PostMapping("/nouseBatch")
    @Transactional(rollbackFor = Exception.class)
    public void nouseBatch(@RequestBody List<Long> rowIds) {
        Assert.isTrue(rowIds!=null&&rowIds.size()>0,"ID列表不能为空");

        organizationRoleService.lambdaUpdate().set(SccPjOrganizationRole::getUseFlag, YesOrNo.N.name()).in(SccPjOrganizationRole::getRowId,rowIds).update();
    }

    @ApiOperation(value = "保存或更新")
    @PostMapping("/saveOrUpdate")
    @Transactional(rollbackFor = Exception.class)
    public SccPjOrganizationRole saveOrUpdate(@RequestBody SccPjOrganizationRoleDto dto) {
        saveBeforeCheck(dto);

        SccPjOrganizationRole organizationRole = dto.getOrganizationRole();
        List<SccPjOrganizationRoleUser> organizationRoleUsers = dto.getOrganizationRoleUsers();
        Long rowId = IdGenrator.generate();
        if(organizationRole.getRowId()==null){

            organizationRole.setRowId(rowId);
        }else{
            organizationRoleUserService.lambdaUpdate().in(SccPjOrganizationRoleUser::getOrganizationRoleId,organizationRole.getRowId()).remove();
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(organizationRoleUsers)) {
            for(SccPjOrganizationRoleUser item:organizationRoleUsers){
                item.setOrganizationRoleId(organizationRole.getRowId());
                item.setRowId(IdGenrator.generate());
            }
        }
        Long srmOrganizationId = organizationRole.getSrmOrgnizationId();
        LambdaQueryWrapper<SccPjOrganization> qw = new LambdaQueryWrapper<>();
        qw.eq(SccPjOrganization::getOrganizationId, srmOrganizationId);
        List<SccPjOrganization> pjOrgList = sccPjOrganizationMapper.selectList(qw);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(pjOrgList)) {
            organizationRole.setHrOrgnizationId(pjOrgList.get(0).getId());
        }
        organizationRoleService.saveOrUpdate(organizationRole);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(organizationRoleUsers)) {
            organizationRoleUserService.saveBatch(organizationRoleUsers);
        }
        return organizationRole;
    }

    private void saveBeforeCheck(SccPjOrganizationRoleDto dto){
        Assert.notNull(dto,"参数不能为空");
        SccPjOrganizationRole organizationRole = dto.getOrganizationRole();
        List<SccPjOrganizationRoleUser> organizationRoleUsers = dto.getOrganizationRoleUsers();

        Assert.notNull(organizationRole,"组织角色数据不能为空");
        Assert.notNull(organizationRole.getSrmOrgnizationId(),"SRM组织ID不能为空");
        Assert.notNull(organizationRole.getHrOrgnizationId(),"HR组织ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(organizationRole.getGroupName()),"组织/部门不能为空");
        Assert.isTrue(StringUtils.isNotBlank(organizationRole.getRoleCode()),"流程角色编码不能为空");
        Assert.isTrue(StringUtils.isNotBlank(organizationRole.getRoleName()),"流程角色名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(organizationRole.getUseFlag()),"是否启用标志不能为空");
        //Assert.isTrue(organizationRoleUsers!=null&&organizationRoleUsers.size()>0,"组织角色员工数据不能为空");
        if(organizationRoleUsers!=null){
            for(SccPjOrganizationRoleUser item : organizationRoleUsers){
                Assert.isTrue(StringUtils.isNotBlank(item.getUserNickName()),"员工名称不能为空");
                Assert.isTrue(StringUtils.isNotBlank(item.getUserName()),"员工账号不能为空");
                Assert.notNull(item.getHrUserId(),"HR员工ID不能为空");
            }
        }
    }

    @ApiOperation(value = "组织角色bpm推送")
    @RequestMapping("/pushBpm")
    public void organizationRoleBpm(@RequestBody BpmSccPjOrganizationRoleDto bpmPor) {
        Assert.notNull(bpmPor,"参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmPor.getOperation()),"操作标志不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(bpmPor.getIds()),"角色id不能为空");
        organizationRoleService.organizationRoleBpm(bpmPor);
    }

    /**
     *
     * @param zbfw 中标范围
     * @param zbfwcode 单据ID
     * @param caNo 定标审批单号
     */
    @ApiOperation(value = "定标审批_推送中标范围")
    @RequestMapping("/push_ZBFW_Bpm")
    public void pushZbfwToBpm(@RequestParam("zbfw") String zbfw,@RequestParam("zbfwcode") String zbfwcode,@RequestParam("caNo") String caNo) {
        organizationRoleService.pushZbfwToBpm(zbfw,zbfwcode,caNo);
    }


    @ApiOperation(value = "组织角色eas推送")
    @RequestMapping("/pushEas")
    public void organizationRoleEas(@RequestBody BpmSccPjOrganizationRoleDto bpmPor) throws Exception {
        Assert.notNull(bpmPor,"参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(bpmPor.getIds()),"角色id不能为空");
        organizationRoleService.organizationRoleEas(bpmPor);
    }

    @ApiOperation(value = "根据用户账号获取")
    @RequestMapping("/getParentUserByUsername")
    public SccPjOrganizationRoleUser getParentUserByUsername(@RequestParam("username")String username) {
        return organizationRoleService.getParentUserByUsername(username);
    }

    @ApiOperation(value = "流程角色导入模板")
    @RequestMapping("/importExcelTemplate")
    public void importExcelTemplate(HttpServletResponse response) throws IOException {
        organizationRoleService.importExcelTemplate(response);
    }

    /**
     * 流程角色导入
     */
    @ApiOperation(value = "流程角色导入", notes = "流程角色导入")
    @PostMapping(value = "/importExcel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload) throws Exception {
        return organizationRoleService.importExcel(file, fileupload);
    }

}
