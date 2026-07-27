
package com.midea.cloud.srm.sou.sourcing.spi.init.editexperts;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouExpertRandomExtractDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.expert.mapper.ExtSouNpmExpertMapper;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtNpmSouExpertService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitRandomExtractExpertHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private ExtNpmSouExpertService extNpmSouExpertService;

    @Autowired
    private RbacClient rbacClient;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @ApiOperation("随机抽取专家前的额外处理")
    public void doHandlerBeforeRandomExtractExpert(ApiExtSouExpertRandomExtractDto param, String souType) {
    }

    public ExtSouRandomExtractExpertPO doHandlerValidAndConvertRandomExtractExpert(ApiExtSouExpertRandomExtractDto param, String souType) {

        //校验数据
        this.doHandlerValid(param, souType);

        //转换数据
        ExtSouRandomExtractExpertPO po = this.doHandlerConvert(param, souType);
        return po;
    }

    /**
     * 校验数据
     * @param param
     * @param souType
     */
    protected void doHandlerValid(ApiExtSouExpertRandomExtractDto param, String souType) {

    }

    /**
     * 转换数据
     * @param param
     * @param souType
     * @return
     */
    protected ExtSouRandomExtractExpertPO doHandlerConvert(ApiExtSouExpertRandomExtractDto param, String souType) {
        ExtSouRandomExtractExpertPO po = new ExtSouRandomExtractExpertPO();

        ExtSouProject project = projectService.getById(param.getProjectId());
        project.setExtExpertRange(param.getExtExpertRange());
        po.setSouProject(project);

        //转换数据
        this.doConvertExpert(param, souType, project, po);
        //用户信息
        this.doConvertUserInfoForExpert(po);

        return po;
    }

    protected void doConvertUserInfoForExpert(ExtSouRandomExtractExpertPO po) {
        if(CollectionUtils.isEmpty(po.getGroupList())) {
            return;
        }
        List<Long> userIdList = po.getGroupList().stream().map(p->p.getUserId()).distinct().collect(Collectors.toList());
        List<User> userList = rbacClient.getByUserIds(userIdList);
        if(CollectionUtils.isEmpty(userList)) {
            return;
        }
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getUserId, Function.identity(), (k1, k2)->k2));
        po.getGroupList().stream().forEach(g -> {
            if(userMap.containsKey(g.getUserId())) {
                User user = userMap.get(g.getUserId());
                g.setPhone(user.getPhone());
                g.setEmail(user.getEmail());
                g.setPosition(user.getCeeaJobcodeDescr());
            }
        });
    }

    protected void doConvertExpert(ApiExtSouExpertRandomExtractDto param, String souType, ExtSouProject project, ExtSouRandomExtractExpertPO po) {
        List<ExtSouExpertRecord> recordList = new ArrayList<>();

        //查询招标小组
        LambdaQueryWrapper<ExtSouGroup> groupMapper = new LambdaQueryWrapper<>();
        groupMapper.eq(ExtSouGroup::getProjectId, project.getProjectId());
        List<ExtSouGroup> groupList = groupService.list(groupMapper);

        //查询专家信息
        List<ExtSouExpertRecord> expertList = extNpmSouExpertService.queryExpert(param, project, groupList);
        if(CollectionUtils.isEmpty(expertList)) {
            return;
        }

        Map<String, List<ExtSouExpertRecord>> expertMap = expertList.stream().collect(Collectors.groupingBy(s -> s.getExpertLevel()));


        AtomicReference<Integer> maxSortIndex = new AtomicReference<>(0);
        //已有专家
        Map<String, List<ExtSouGroup>> groupMap = groupList.stream().peek(group -> {
            maxSortIndex.set(Math.max(maxSortIndex.get(), ObjectUtils.defaultIfNull(group.getSortIndex(), 0)));
        }).filter(g -> YesOrNo.YES.getValue().equals(g.getExtEvaFlag()) && SouScoreDimensionCodeEnum.SOU_TECH.getCode().equals(g.getScoreAuth()))
                .collect(Collectors.groupingBy(g -> g.getExtExpertLevel()));

        //需要高级专家数
        Integer extAskSeniorExpertNum = ObjectUtils.defaultIfNull(project.getExtAskSeniorExpertNum(), 0);
        //总评标人数
        Integer extBidEvaluatorNum = ObjectUtils.defaultIfNull(project.getExtBidEvaluatorNum(), 0);

        //已有高级专家数
        Integer expertNum = groupMap.getOrDefault(SouBidExpertLevelEnum.SENIOR.getCode(), new ArrayList<>()).size();

        //已有普通专家数
        Integer normaltNum = groupMap.getOrDefault(SouBidExpertLevelEnum.NORMAL.getCode(), new ArrayList<>()).size();

        //还需高级专家数
        AtomicReference<Integer> needExpertNum = new AtomicReference<>(extAskSeniorExpertNum - expertNum);

        //还需普通专家人数 = 总人数 - 已有专家-还需专家数 - 已有普通专家数
        AtomicReference<Integer> needComNum = new AtomicReference<>(extBidEvaluatorNum - expertNum - Math.max(needExpertNum.get(), 0) - normaltNum);

        List<ExtSouGroup> saveGrouList = new ArrayList<>();
        //获取高级专家
        expertMap.getOrDefault(SouBidExpertLevelEnum.SENIOR.getCode(), new ArrayList<>()).forEach(expert -> {
            if(needExpertNum.getAndSet(needExpertNum.get() - 1) > 0) {
                saveGrouList.add(buildExtSouGroup(expert, project, param.getExtExpertRange()));
                recordList.add(expert);
            }
        });
        //获取普通专家
        expertMap.getOrDefault(SouBidExpertLevelEnum.NORMAL.getCode(), new ArrayList<>()).forEach(expert -> {
            if(needComNum.getAndSet(needComNum.get() - 1) > 0) {
                saveGrouList.add(buildExtSouGroup(expert, project, param.getExtExpertRange()));
                recordList.add(expert);
            }
        });

        po.setExpertRecordList(recordList);
        po.setGroupList(saveGrouList);
    }

    protected ExtSouGroup buildExtSouGroup(ExtSouExpertRecord extSouExpertRecord, ExtSouProject project, String range) {
        ExtSouGroup group = new ExtSouGroup();
        group.setProjectId(project.getProjectId());
        group.setExtExpertLevel(extSouExpertRecord.getExpertLevel());
        group.setGroupId(IdGenrator.generate());
        group.setUserId(ObjectUtils.defaultIfNull(extSouExpertRecord.getUserId(), -1L));
        group.setUserName(ObjectUtils.defaultIfNull(extSouExpertRecord.getUserName(), ""));
        group.setFullName(ObjectUtils.defaultIfNull(extSouExpertRecord.getFullName(), ""));
        group.setExtGroupFlag(YesOrNo.NO.getValue());
        group.setExtEvaFlag(YesOrNo.YES.getValue());
        group.setPhone("");
        group.setEmail("");
        group.setPosition("");
        group.setGroupRole(ExtSouGroupRoleEnum.MEMBER.getCode());
        group.setScoreAuth(SouScoreDimensionCodeEnum.SOU_TECH.getCode());
        group.setSortIndex(0);

        extSouExpertRecord.setGroupId(group.getGroupId());
        extSouExpertRecord.setProjectId(project.getProjectId());
        extSouExpertRecord.setExpertRecordId(IdGenrator.generate());
        extSouExpertRecord.setExtractTime(new Date());
        extSouExpertRecord.setExpertRange(range);
        return group;
    }

    @ApiOperation("随机抽取专家后的额外处理")
    public void doHandlerAfterRandomExtractExpert(ApiExtSouExpertRandomExtractDto param, String souType, ExtSouRandomExtractExpertPO po) {
    }

}
