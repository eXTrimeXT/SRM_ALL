package com.midea.cloud.srm.biz.pj.hrusertemp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.biz.pj.hrusertemp.mapper.SccPjUserTempMapper;
import com.midea.cloud.srm.biz.pj.hrusertemp.service.ISccPjUserTempService;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjHrUserInfo;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ISccPjUserTempServiceImpl extends ServiceImpl<SccPjUserTempMapper, SccPjUserTemp> implements ISccPjUserTempService {

    @Autowired
    private ISccPjUserService iSccPjUserService;
    @Autowired
    private OpenClientUtils openClientUtils;

    @ApiModelProperty("员工信息")
    @Value("${gwm.url.user-info}")
    private String userInfoUrl;
    @ApiModelProperty("员工信息批量")
    @Value("${gwm.url.user-info-batch}")
    private String userInfoBatchUrl;
    @Value("${gwm.prdAppkey}")
    private String prdAppKey;
    @Value("${gwm.prdSecret}")
    private String prdSecret;

    @Override
    public List<SccPjUserTemp> syncHrUser(Map<String, Object> param, Long groupId) {
        String orgReqStr = OpenClientUtils.sendHttpGet(OpenClientUtils.TYPE.EMPLOYEE_LIST, OpenClientUtils.sendHttpGetParam(param));
        JSONObject reqObj = JSON.parseObject(orgReqStr);

        //"code":200,"key":"S_0000","message":"请求成功"
        String code = reqObj.getString("code");
        if(!OpenClientConstant.CODE_SUCCESS.equals(code)) {
            throw new BaseException("请求HR人员列表接口失败：" + orgReqStr);
        }

        List<SccPjUserTemp> sccPjUserTempList = new ArrayList<>();

        JSONObject resultObj = reqObj.getJSONObject("result");
        JSONArray jsonArray = resultObj.getJSONArray("rows");

        Iterator<Object> iterator = jsonArray.stream().iterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            JSONObject item = new JSONObject();
            for(String key : jsonObject.keySet()) {
                item.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key.toLowerCase()), jsonObject.get(key));
            }
            SccPjUserTemp sccPjUserTemp = item.toJavaObject(SccPjUserTemp.class);
            sccPjUserTemp.setUserTempId(IdGenrator.generate());
            sccPjUserTemp.setProcessGroupId(groupId);
            sccPjUserTemp.setProcessSerialNum(groupId.toString());
            sccPjUserTemp.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
            sccPjUserTempList.add(sccPjUserTemp);
        }
        if (CollectionUtils.isNotEmpty(sccPjUserTempList)) {
            this.saveBatch(sccPjUserTempList);
        }
        return sccPjUserTempList;
    }

    @Override
    public List<SccPjUserTemp> syncAllHrUser(Map<String, Object> param) {
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
        Map<String, Object> req = new HashMap<>(50);
        for(String key : param.keySet()) {
            req.put(key, param.get(key));
        }

        req.put("page", page);
        req.put("size", size);
        req.put("latest_update_time", latestUpdateTime);
        Long groupId = IdGenrator.generate();
        List<SccPjUserTemp> sccPjUserTempList = syncHrUser(req, groupId);

        return sccPjUserTempList;
    }

    @Override
    public List<SccPjUser> doPending(Long groupId) {

        PageUtil.startPage(1, 2000);
        LambdaQueryWrapper<SccPjUserTemp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccPjUserTemp::getProcessStatus, ProcessStatusEnum.PENDING.getCode());
        queryWrapper.eq(!Objects.isNull(groupId), SccPjUserTemp::getProcessGroupId, groupId);
        queryWrapper.orderByAsc(SccPjUserTemp::getUserTempId);

        List<SccPjUserTemp> sccPjUserTempList = this.list(queryWrapper);

        List<SccPjUser> sccPjUserList = iSccPjUserService.toSccPjUser(sccPjUserTempList);

        sccPjUserTempList.stream().forEach(sccPjUserTemp -> {
            sccPjUserTemp.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
            sccPjUserTemp.setProcessMessage(ProcessStatusEnum.COMPLETED.getName());
            sccPjUserTemp.setProcessDate(new Date());
        });
        this.updateBatchById(sccPjUserTempList);
        return sccPjUserList;
    }

    @Override
    public List<SccPjUser> doAllPending() {

        List<SccPjUser> sccPjUserList = new ArrayList<>();

        while (true) {
            PageUtil.startPage(1, 1);
            LambdaQueryWrapper<SccPjUserTemp> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SccPjUserTemp::getProcessStatus, ProcessStatusEnum.PENDING.getCode());
            queryWrapper.orderByAsc(SccPjUserTemp::getProcessGroupId);
            List<SccPjUserTemp> sccPjUserTempList = this.list(queryWrapper);
            if(CollectionUtils.isEmpty(sccPjUserTempList)) {
                break;
            }
            sccPjUserList = doPending(sccPjUserTempList.get(0).getProcessGroupId());
        }

        return sccPjUserList;
    }

    /**
     * 根据员工工号查询员工信息
     */
    @Nullable
    @Override
    public SccPjHrUserInfo getHrUserInfo(String personnelNo) {
        personnelNo = StringUtils.trimToNull(personnelNo);
        AssertUtils.notNull(personnelNo, "缺少personnelNo参数");
        Map<String, Object> param = new HashMap<>(50); {
            param.put("personnel_no", personnelNo);
        }
        String orgReqStr; {
            OpenClient openClient = new OpenClient(prdAppKey, prdSecret);
            orgReqStr = openClient.sendHttpGet(StringUtils.joinWith("", userInfoUrl, OpenClientUtils.sendHttpGetParam(param)));
        }
        JSONObject reqObj = JSON.parseObject(orgReqStr);

        //"code":200,"key":"S_0000","message":"请求成功"
        String code = reqObj.getString("code");
        if(!OpenClientConstant.CODE_SUCCESS.equals(code)) {
            throw new BaseException("请求HR人员列表接口失败：" + orgReqStr);
        }

        List<SccPjUserTemp> sccPjUserTempList = new ArrayList<>();

        JSONObject resultObj = reqObj.getJSONObject("result");
        if (resultObj == null) { return null; }
        JSONObject item = new JSONObject();
        for (String key : resultObj.keySet()) {
            item.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key.toLowerCase()), resultObj.get(key));
        }
        return SouObjectXUtil.convertTargetObj(item, SccPjHrUserInfo.class);
    }

    /**
     * 根据员工工号批量查询员工信息
     */
    @Override
    public Map<String/* 工号 */, SccPjHrUserInfo> listHrUserInfos(Set<String> personnelNos) {
        if (personnelNos == null || personnelNos.isEmpty()) { return Collections.emptyMap(); }

        // 分组处理，hr接口一次最多只能处理100数据
        Function<List<String>, Collection<SccPjHrUserInfo>> function = (list) -> {
            String nos; {
                StringBuilder sb = new StringBuilder(500);
                for (String personnelNo : list) {
                    sb.append(personnelNo).append(",");
                }
                nos = sb.substring(0, sb.length() - 1);
            }
            Map<String, Object> param = new HashMap<>(50); {
                param.put("personnel_nos", nos);
            }
            String orgReqStr; {
                OpenClient openClient = new OpenClient(prdAppKey, prdSecret);
                orgReqStr = openClient.sendHttpGet(StringUtils.joinWith("", userInfoBatchUrl, OpenClientUtils.sendHttpGetParam(param)));
            }
            JSONObject reqObj = JSON.parseObject(orgReqStr);

            //"code":200,"key":"S_0000","message":"请求成功"
            String code = reqObj.getString("code");
            if(!OpenClientConstant.CODE_SUCCESS.equals(code)) {
                throw new BaseException("请求HR人员列表接口失败：" + orgReqStr);
            }

            List<SccPjHrUserInfo> resultList = new ArrayList<>(100);
            JSONArray array = reqObj.getJSONArray("result");
            if (array.isEmpty()) { return Collections.emptyList(); }
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JSONObject item = new JSONObject();
                for (String key : obj.keySet()) {
                    item.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key.toLowerCase()), obj.get(key));
                }
                resultList.add(SouObjectXUtil.convertTargetObj(item, SccPjHrUserInfo.class));
            }
            return resultList;
        };

        // 执行分组查询
        List<SccPjHrUserInfo> resultList = com.midea.cloud.common.utils.CollectionUtils.getListResultByGroup(personnelNos, 100, function);

        return resultList.stream().collect(Collectors.toMap(SccPjHrUserInfo::getPersonnelNo, Function.identity()));
    }

}
