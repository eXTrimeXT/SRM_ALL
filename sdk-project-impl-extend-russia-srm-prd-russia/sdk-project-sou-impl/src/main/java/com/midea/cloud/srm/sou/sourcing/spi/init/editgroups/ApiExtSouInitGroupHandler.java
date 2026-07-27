
package com.midea.cloud.srm.sou.sourcing.spi.init.editgroups;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidExpertLevelEnum;
import com.midea.cloud.srm.model.sou.enums.SouExpertLevelEnum;
import com.midea.cloud.srm.model.sou.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouExpertRandomExtractDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouGroupEditDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editexperts.ExtSouRandomExtractExpertPO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
public class ApiExtSouInitGroupHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @ApiOperation("指定专家前的额外处理")
    public void doHandlerBeforeAddGroup(ApiExtSouGroupEditDto param, String souType) {
    }

    public ApiExtSouGroupEditPO doHandlerValidAndConvertAddGroup(ApiExtSouGroupEditDto param, String souType) {

        //校验数据
        this.doHandlerValid(param, souType);

        //转换数据
        ApiExtSouGroupEditPO po = this.doHandlerConvert(param, souType);
        return po;
    }

    /**
     * 校验数据
     * @param param
     * @param souType
     */
    protected void doHandlerValid(ApiExtSouGroupEditDto param, String souType) {
        if(CollectionUtils.isEmpty(param.getGroupList())) {
            throw new BaseException("指定专家人员列表为空");
        }
        AssertUtils.notNull(param.getProjectId(), "寻源单据ID不能为空");

        LambdaQueryWrapper<ExtSouGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouGroup::getProjectId, param.getProjectId());
        queryWrapper.eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue());
        List<ExtSouGroup> groupList = groupService.list(queryWrapper);
        Map<Long, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(g -> g.getUserId(), Function.identity(), (k1, k2)->k2));

        Set<String> errorList = new HashSet<>();
        param.getGroupList().stream().forEach(g -> {
            if(!ObjectUtils.allNotNull(g.getUserId(), g.getUserName(), g.getFullName())) {
                throw new BaseException("专家信息有误");
            }
            if(groupMap.containsKey(g.getUserId())) {
                errorList.add(g.getUserName());
            }
        });

        if(CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException("以下专家在系统中已经存在：" + errorList.stream().collect(Collectors.joining("、")));
        }
    }

    /**
     * 转换数据
     * @param param
     * @param souType
     * @return
     */
    protected ApiExtSouGroupEditPO doHandlerConvert(ApiExtSouGroupEditDto param, String souType) {
        ApiExtSouGroupEditPO po = new ApiExtSouGroupEditPO();
        List<ExtSouGroup> groupList = param.getGroupList();
        groupList.stream().forEach(g -> {
            g.setGroupId(IdGenrator.generate());
            g.setProjectId(param.getProjectId());
            g.setExtGroupFlag(YesOrNo.NO.getValue());
            g.setExtEvaFlag(YesOrNo.YES.getValue());
            if(StringUtils.isBlank(g.getGroupRole())) {
                g.setGroupRole(ExtSouGroupRoleEnum.MEMBER.getCode());
            }
            if(StringUtils.isBlank(g.getOperateAuth())) {
                g.setOperateAuth(SouScoreDimensionCodeEnum.SOU_TECH.getCode());
            }
            if(StringUtils.isBlank(g.getExtExpertLevel())) {
                g.setExtExpertLevel(SouBidExpertLevelEnum.NORMAL.getCode());
            }

        });

        po.setGroupList(groupList);
        return po;
    }

    @ApiOperation("随机抽取专家后的额外处理")
    public void doHandlerAfterAddGroup(ApiExtSouGroupEditDto param, String souType, ApiExtSouGroupEditPO po) {
    }

}
