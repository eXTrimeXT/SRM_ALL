package com.midea.cloud.srm.biz.pj.hrorganizationtemp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.hrorganization.service.SccPjOrganizationService;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.mapper.SccPjOrganizationTempMapper;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.service.ISccPjOrganizationTempService;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hrorganizationtemp.SccPjOrganizationTemp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ISccPjOrganizationTempServiceImpl extends ServiceImpl<SccPjOrganizationTempMapper, SccPjOrganizationTemp> implements ISccPjOrganizationTempService {

    @Autowired
    private SccPjOrganizationService sccPjOrganizationService;

    @Override
    public List<SccPjOrganizationTemp> syncHrOrganization(Map<String, Object> param, Long groupId) {
        String orgReqStr = OpenClientUtils.sendHttpGet(OpenClientUtils.TYPE.ORG_LIST, OpenClientUtils.sendHttpGetParam(param));
        JSONObject reqObj = JSON.parseObject(orgReqStr);
        log.info(reqObj.toString());

        //"code":200,"key":"S_0000","message":"请求成功"
        String code = reqObj.getString("code");
        if(!OpenClientConstant.CODE_SUCCESS.equals(code)) {
            throw new BaseException("请求HR组织列表接口失败：" + orgReqStr);
        }

        List<SccPjOrganizationTemp> sccPjOrganizationTempList = new ArrayList<>();

        JSONObject resultObj = reqObj.getJSONObject("result");
        JSONArray jsonArray = resultObj.getJSONArray("rows");

        Iterator<Object> iterator = jsonArray.stream().iterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            JSONObject item = new JSONObject();
            for(String key : jsonObject.keySet()) {
                item.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key.toLowerCase()), jsonObject.get(key));
            }
            SccPjOrganizationTemp sccPjOrganizationTemp = item.toJavaObject(SccPjOrganizationTemp.class);
            sccPjOrganizationTemp.setOrganizationTempId(IdGenrator.generate());
            sccPjOrganizationTemp.setProcessGroupId(groupId);
            sccPjOrganizationTemp.setProcessSerialNum(groupId.toString());
            sccPjOrganizationTemp.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
            sccPjOrganizationTemp.setPreOrganizationId(IdGenrator.generate());
            sccPjOrganizationTempList.add(sccPjOrganizationTemp);
        }
        if (CollectionUtils.isNotEmpty(sccPjOrganizationTempList)) {
            this.saveBatch(sccPjOrganizationTempList);
        }
        return sccPjOrganizationTempList;
    }

    @Override
    public List<SccPjOrganizationTemp> syncAllHrOrganization(Map<String, Object> param) {
        //page=1&size=10&latestUpdateTime=2023-09-13
        Integer page = MapUtils.getInteger(param, "page");
        if(Objects.isNull(page)) {
            page = 1;
        }
        Integer size = MapUtils.getInteger(param, "size");
        if(Objects.isNull(size)) {
            size = 100;
        }
        String latestUpdateTime = MapUtils.getString(param, "latest_update_time");
        if(StringUtils.isBlank(latestUpdateTime)) {
            Calendar calstart = Calendar.getInstance();
            calstart.add(Calendar.DAY_OF_MONTH, -2);
            latestUpdateTime = DateUtil.format(calstart.getTime(), DateUtil.DATE_FORMAT_10);
        }
        param.put("page", page);
        param.put("size", size);
        param.put("latest_update_time", latestUpdateTime);

        List<SccPjOrganizationTemp> sccPjOrganizationTempList = new ArrayList<>();
        Long groupId = IdGenrator.generate();
        while (true) {
            List<SccPjOrganizationTemp> list = syncHrOrganization(param, groupId);
            if(CollectionUtils.isNotEmpty(list)) {
                sccPjOrganizationTempList.addAll(list);
                page++;
                param.put("page", page);
            } else {
                break;
            }
        }
        return sccPjOrganizationTempList;
    }

    @Override
    public List<SccPjOrganization> doPending(Integer level) {

        //查询待处理状态的数据
        PageUtil.startPage(1, 2000);
        LambdaQueryWrapper<SccPjOrganizationTemp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjOrganizationTemp::getProcessStatus, ProcessStatusEnum.PENDING.getCode());
        queryWrapper.orderByAsc(SccPjOrganizationTemp::getOrganizationTempId);

        List<SccPjOrganizationTemp> sccPjOrganizationTempList = this.list(queryWrapper);

        Map<Long, List<SccPjOrganizationTemp>> groupMap = sccPjOrganizationTempList.stream().collect(Collectors.groupingBy(SccPjOrganizationTemp::getProcessGroupId));

        List<SccPjOrganization> sccPjOrganizationList = new ArrayList<>();
        //处理到业务表
        groupMap.keySet().stream().sorted(Comparator.comparingLong(k->k)).forEach(k -> {
            sccPjOrganizationList.addAll(sccPjOrganizationService.toSccPjOrganization(groupMap.get(k)));
        });

        sccPjOrganizationTempList.stream().forEach(sccPjOrganization -> {
            sccPjOrganization.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
            sccPjOrganization.setProcessDate(new Date());
            sccPjOrganization.setProcessMessage(ProcessStatusEnum.COMPLETED.getName());
        });

        this.updateBatchById(sccPjOrganizationTempList);

        return sccPjOrganizationList;
    }

    @Override
    public List<SccPjOrganization> doAllPending() {

        List<SccPjOrganization> sccPjOrganizationList = new ArrayList<>();

        while (true) {
            QueryWrapper<SccPjOrganizationTemp> minWrapper = new QueryWrapper<>();
            minWrapper.eq("PROCESS_STATUS", ProcessStatusEnum.PENDING.getCode());

            long count = this.count(minWrapper);
            if(count <= 0) {
                break;
            }
            sccPjOrganizationList.addAll(doPending(1));
        }
        return sccPjOrganizationList;
    }
}
