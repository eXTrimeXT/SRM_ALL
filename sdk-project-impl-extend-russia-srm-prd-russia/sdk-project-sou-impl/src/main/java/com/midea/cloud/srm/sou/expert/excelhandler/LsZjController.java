package com.midea.cloud.srm.sou.expert.excelhandler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertApplyMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Slf4j
@RestController
@RequestMapping("/external/expert/bu")
public class LsZjController {

    @Resource
    private ExtSouExpertApplyMapper extSouExpertApplyMapper;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private BaseClient baseClient;

    @ApiOperation(value = "专家库设置板块", notes = "专家库设置板块")
    @PostMapping("/setBu")
    public List<Long> setBu(@RequestBody(required = false) List<Long> pList) {
        List<Long> reList = new ArrayList<>();
        log.info("参数===" + JSONObject.toJSONString(pList));
        LambdaQueryWrapper<ExtSouExpertApply> qw = new LambdaQueryWrapper<>();
        qw.notIn(CollectionUtils.isNotEmpty(pList), ExtSouExpertApply::getExpertApplyId, pList);
        List<ExtSouExpertApply> list = extSouExpertApplyMapper.selectList(qw);
        log.info("数量===" + list.size());
        for (ExtSouExpertApply extSouExpert : list) {
            try {
                Long str = setBuInfo(extSouExpert);
                if (str != null) {
                    reList.add(str);
                }
            } catch (Exception e) {
                log.info("出错啦");
            }
        }
        return reList;
    }

    public Long setBuInfo(ExtSouExpertApply e) {
        Long str = e.getExpertApplyId();
        try {
            com.midea.cloud.srm.model.base.organization.entity.Organization oo1 = new com.midea.cloud.srm.model.base.organization.entity.Organization();
            log.info("查询到的组织信息===" + JSONObject.toJSONString(oo1));
            oo1.setOrganizationName(e.getOrgOuName());
            com.midea.cloud.srm.model.base.organization.entity.Organization o1 = baseClient.getOrganization(oo1);
            Organization mo = pjProjectExtClient.getBuOrganizationByOuOrgCode(o1.getOrganizationCode());
            log.info("最后的板块信息===" + JSONObject.toJSONString(mo));
            e.setBuId(mo.getOrganizationId());
            e.setBuCode(mo.getOrganizationCode());
            e.setBuName(mo.getOrganizationName());
            extSouExpertApplyMapper.updateById(e);
        } catch (Exception ex) {
//            return str + "我是分隔符" + ex.getMessage();
            log.info("设置板块信息===" + ex.getMessage());
            return str;
        }
        return null;
    }
}
